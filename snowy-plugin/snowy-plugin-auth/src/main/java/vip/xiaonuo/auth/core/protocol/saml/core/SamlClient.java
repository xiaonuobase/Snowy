package vip.xiaonuo.auth.core.protocol.saml.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.impl.XSAnyImpl;
import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.core.impl.StatusCodeBuilder;
import org.opensaml.saml.saml2.core.impl.StatusMessageBuilder;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.impl.ChainingKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.CollectionKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import shade.io.netty.handler.codec.http.HttpMethod;

import javax.xml.namespace.QName;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class SamlClient {
  private static final Logger logger = LoggerFactory.getLogger(SamlClient.class);

  private static final String HTTP_REQ_SAML_PARAM = "SAMLRequest";
  private static final String HTTP_RESP_SAML_PARAM = "SAMLResponse";

  private static boolean initializedOpenSaml = false;
  private final BasicParserPool domParser;

  public enum SamlIdpBinding {
    POST,
    Redirect
  }

  private final String relyingPartyIdentifier;
  private final String assertionConsumerServiceUrl;
  private final String identityProviderUrl;
  private final String responseIssuer;
  private final List<Credential> credentials;
  private Instant now; // used for testing only
  private long notBeforeSkew = 0L;
  private final SamlIdpBinding samlBinding;
  private BasicX509Credential spCredential;
  private List<Credential> additionalSpCredentials = new ArrayList<>();

  /**
   * Returns the url where SAML requests should be posted.
   *
   * @return the url where SAML requests should be posted.
   */
  public String getIdentityProviderUrl() {
    return identityProviderUrl;
  }

  /**
   * Sets the date that will be considered as now. This is only useful for testing.
   *
   * @param now the date to use for now.
   */
  public void setInstantNow(Instant now) {
    this.now = now;
  }

  /**
   * Sets by how much the current time can be before the assertion's notBefore.
   * Used to mitigate clock differences between the identity provider and relying party.
   *
   * @param notBeforeSkew non-negative amount of skew (in milliseconds) to allow between the
   *                      current time and the assertion's notBefore date. Default: 0
   */
  public void setNotBeforeSkew(long notBeforeSkew) {
    if (notBeforeSkew < 0) {
      throw new IllegalArgumentException("Skew must be non-negative");
    }
    this.notBeforeSkew = notBeforeSkew;
  }

  /**
   * Constructs an SAML client using explicit parameters.
   *
   * @param relyingPartyIdentifier      the identifier of the relying party.
   * @param assertionConsumerServiceUrl the url where the identity provider will post back the
   *                                    SAML response.
   * @param identityProviderUrl         the url where the SAML request will be submitted.
   * @param responseIssuer              the expected issuer ID for SAML responses.
   * @param certificates                the list of base-64 encoded certificates to use to validate
   *                                    responses.
   * @param samlBinding                 what type of SAML binding should the client use.
   * @throws SamlException thrown if any error occur while loading the provider information.
   */
  public SamlClient(
      String relyingPartyIdentifier,
      String assertionConsumerServiceUrl,
      String identityProviderUrl,
      String responseIssuer,
      List<X509Certificate> certificates,
      SamlIdpBinding samlBinding)
      throws SamlException {

    ensureOpenSamlIsInitialized();

    if (relyingPartyIdentifier == null) {
      throw new IllegalArgumentException("relyingPartyIdentifier");
    }
    if (identityProviderUrl == null) {
      throw new IllegalArgumentException("identityProviderUrl");
    }
    if (responseIssuer == null) {
      throw new IllegalArgumentException("responseIssuer");
    }
    if (certificates == null || certificates.isEmpty()) {
      throw new IllegalArgumentException("certificates");
    }

    this.relyingPartyIdentifier = relyingPartyIdentifier;
    this.assertionConsumerServiceUrl = assertionConsumerServiceUrl;
    this.identityProviderUrl = identityProviderUrl;
    this.responseIssuer = responseIssuer;
    this.credentials =
        certificates.stream().map(SamlClient::getCredential).collect(Collectors.toList());
    this.samlBinding = samlBinding;
    this.domParser = XMLHelper.createDOMParser();
  }

  /**
   * Constructs an SAML client using explicit parameters.
   *
   * @param relyingPartyIdentifier      the identifier of the relying party.
   * @param assertionConsumerServiceUrl the url where the identity provider will post back the
   *                                    SAML response.
   * @param identityProviderUrl         the url where the SAML request will be submitted.
   * @param responseIssuer              the expected issuer ID for SAML responses.
   * @param certificates                the list of base-64 encoded certificates to use to validate
   *                                    responses.
   * @throws SamlException thrown if any error occur while loading the provider information.
   */
  public SamlClient(
      String relyingPartyIdentifier,
      String assertionConsumerServiceUrl,
      String identityProviderUrl,
      String responseIssuer,
      List<X509Certificate> certificates)
      throws SamlException {

    this(
        relyingPartyIdentifier,
        assertionConsumerServiceUrl,
        identityProviderUrl,
        responseIssuer,
        certificates,
        SamlIdpBinding.POST);
  }

  /**
   * Constructs an SAML client using explicit parameters.
   *
   * @param relyingPartyIdentifier      the identifier of the relying party.
   * @param assertionConsumerServiceUrl the url where the identity provider will post back the
   *                                    SAML response.
   * @param identityProviderUrl         the url where the SAML request will be submitted.
   * @param responseIssuer              the expected issuer ID for SAML responses.
   * @param certificate                 the base-64 encoded certificate to use to validate
   *                                    responses.
   * @throws SamlException thrown if any error occur while loading the provider information.
   */
  public SamlClient(
      String relyingPartyIdentifier,
      String assertionConsumerServiceUrl,
      String identityProviderUrl,
      String responseIssuer,
      X509Certificate certificate)
      throws SamlException {

    this(
        relyingPartyIdentifier,
        assertionConsumerServiceUrl,
        identityProviderUrl,
        responseIssuer,
        Collections.singletonList(certificate),
        SamlIdpBinding.POST);
  }

  /**
   * Decodes and validates an SAML response returned by an identity provider.
   *
   * @param encodedResponse the encoded response returned by the identity provider.
   * @param method The HTTP method used by the request
   *
   * @return An {@link SamlResponse} object containing information decoded from the SAML response.
   * @throws SamlException if the signature is invalid, or if any other error occurs.
   */
  public SamlResponse decodeAndValidateSamlResponse(String encodedResponse, String method)
      throws SamlException {
    //Decode and parse the response
    Response response = (Response) parseResponse(encodedResponse, method);

    // Decode and add the assertion
    try {
      decodeEncryptedAssertion(response);
    } catch (DecryptionException e) {
      throw new SamlException("Cannot decrypt the assertion", e);
    }
    //Validate  the response (Assertion / Signature / Schema)
    ValidatorUtils.validate(response, responseIssuer, credentials, this.now, notBeforeSkew);

    Assertion assertion = response.getAssertions().get(0);
    return new SamlResponse(assertion);
  }

  /**
   * Redirects an {@link HttpServletResponse} to the configured identity provider.
   *
   * @param response   The {@link HttpServletResponse}.
   * @param relayState Optional relay state that will be passed along.
   * @throws IOException   thrown if an IO error occurs.
   * @throws SamlException thrown is an unexpected error occurs.
   */
  public void redirectToIdentityProvider(HttpServletResponse response, String relayState)
      throws IOException, SamlException {
    Map<String, String> values = new HashMap<>();
    values.put("SAMLRequest", getSamlRequest());
    if (relayState != null) {
      values.put("RelayState", relayState);
    }

    BrowserUtils.postUsingBrowser(identityProviderUrl, response, values);
  }

  /**
   * Processes a POST containing the SAML response.
   *
   * @param request the {@link HttpServletRequest}.
   * @return An {@link SamlResponse} object containing information decoded from the SAML response.
   * @throws SamlException thrown is an unexpected error occurs.
   */
  public SamlResponse processPostFromIdentityProvider(HttpServletRequest request)
      throws SamlException {
    String encodedResponse = request.getParameter(HTTP_RESP_SAML_PARAM);
    return decodeAndValidateSamlResponse(encodedResponse, request.getMethod());
  }

  /**
   * Constructs an SAML client using XML metadata obtained from the identity provider. <p> When
   * using Okta as an identity provider, it is possible to pass null to relyingPartyIdentifier and
   * assertionConsumerServiceUrl; they will be inferred from the metadata provider XML.
   *
   * @param relyingPartyIdentifier      the identifier for the relying party.
   * @param assertionConsumerServiceUrl the url where the identity provider will post back the
   *                                    SAML response.
   * @param metadata                    the XML metadata obtained from the identity provider.
   * @return The created {@link SamlClient}.
   * @throws SamlException thrown if any error occur while loading the metadata information.
   */
  public static SamlClient fromMetadata(
      String relyingPartyIdentifier, String assertionConsumerServiceUrl, Reader metadata)
      throws SamlException {
    return fromMetadata(
        relyingPartyIdentifier, assertionConsumerServiceUrl, metadata, SamlIdpBinding.POST);
  }

  /**
   * Constructs an SAML client using XML metadata obtained from the identity provider. <p> When
   * using Okta as an identity provider, it is possible to pass null to relyingPartyIdentifier and
   * assertionConsumerServiceUrl; they will be inferred from the metadata provider XML.
   *
   * @param relyingPartyIdentifier      the identifier for the relying party.
   * @param assertionConsumerServiceUrl the url where the identity provider will post back the
   *                                    SAML response.
   * @param metadata                    the XML metadata obtained from the identity provider.
   * @param samlBinding                 the HTTP method to use for binding to the IdP.
   * @return The created {@link SamlClient}.
   * @throws SamlException thrown if any error occur while loading the metadata information.
   */
  public static SamlClient fromMetadata(
      String relyingPartyIdentifier,
      String assertionConsumerServiceUrl,
      Reader metadata,
      SamlIdpBinding samlBinding)
      throws SamlException {
    return fromMetadata(
        relyingPartyIdentifier, assertionConsumerServiceUrl, metadata, samlBinding, null);
  }

  /**
   * Constructs an SAML client using XML metadata obtained from the identity provider. <p> When
   * using Okta as an identity provider, it is possible to pass null to relyingPartyIdentifier and
   * assertionConsumerServiceUrl; they will be inferred from the metadata provider XML.
   *
   * @param relyingPartyIdentifier      the identifier for the relying party.
   * @param assertionConsumerServiceUrl the url where the identity provider will post back the
   *                                    SAML response.
   * @param metadata                    the XML metadata obtained from the identity provider.
   * @param samlBinding                 the HTTP method to use for binding to the IdP.
   * @param certificates                list of certificates.
   * @return The created {@link SamlClient}.
   * @throws SamlException thrown if any error occur while loading the metadata information.
   */
  public static SamlClient fromMetadata(
      String relyingPartyIdentifier,
      String assertionConsumerServiceUrl,
      Reader metadata,
      SamlIdpBinding samlBinding,
      List<X509Certificate> certificates)
      throws SamlException {

    ensureOpenSamlIsInitialized();

    DOMMetadataResolver metadataResolver = createMetadataResolver(skipBom(metadata));
    EntityDescriptor entityDescriptor = getEntityDescriptor(metadataResolver);

    IDPSSODescriptor idpSsoDescriptor = getIDPSSODescriptor(entityDescriptor);
    SingleSignOnService idpBinding = null;
    if (idpSsoDescriptor.getSingleSignOnServices() != null
        && !idpSsoDescriptor.getSingleSignOnServices().isEmpty()) {
      idpBinding = getIdpBinding(idpSsoDescriptor, samlBinding);
    }

    List<X509Certificate> x509Certificates = getCertificates(idpSsoDescriptor);
    boolean isOkta = entityDescriptor.getEntityID().contains(".okta.com");

    if (relyingPartyIdentifier == null) {
      // Okta's own toolkit uses the entity ID as a relying party identifier, so if we
      // detect that the IDP is Okta let's tolerate a null value for this parameter.
      if (isOkta) {
        relyingPartyIdentifier = entityDescriptor.getEntityID();
      } else {
        throw new IllegalArgumentException("relyingPartyIdentifier");
      }
    }

    if (idpBinding != null && assertionConsumerServiceUrl == null && isOkta) {
      // Again, Okta's own toolkit uses this value for the assertion consumer url, which
      // kinda makes no sense since this is supposed to be a url pointing to a server
      // outside Okta, but it probably just straight ignores this and use the one from
      // it's own config anyway.
      assertionConsumerServiceUrl = idpBinding.getLocation();
    }

    if (certificates != null) {
      // Adding certificates given to this method
      // because some idp metadata file does not embedded signing certificate
      x509Certificates.addAll(certificates);
    }

    String identityProviderUrl;
    if (idpBinding != null) {
      identityProviderUrl = idpBinding.getLocation();
    } else {
      identityProviderUrl = assertionConsumerServiceUrl;
    }
    String responseIssuer = entityDescriptor.getEntityID();

    return new SamlClient(
        relyingPartyIdentifier,
        assertionConsumerServiceUrl,
        identityProviderUrl,
        responseIssuer,
        x509Certificates,
        samlBinding);
  }

  /**
   * Wrap a {@link Reader Reader} to skip a BOM if it is present.
   * OpenSaml won't accept a metadata file if it starts with a BOM.
   * @param metadata The metadata with optional BOM
   * @return A {@link Reader} which will never return a BOM
   */
  private static InputStream skipBom(Reader metadata) throws SamlException {
    try {
      InputStream metadataInputStream =
          IOUtils.toInputStream(IOUtils.toString(metadata), StandardCharsets.UTF_8);

      return BOMInputStream.builder().setInclude(false).setInputStream(metadataInputStream).get();
    } catch (IOException e) {
      throw new SamlException("Couldn't read metadata", e);
    }
  }

  /**
   * Decode Base64, then decode if needed
   * @param encodedResponse a Base64 String with optionally deflated xml
   * @param method The HTTP method used by the request
   * @return A Reader with decoded and inflated xml
   */
  public static Reader decodeAndInflate(String encodedResponse, String method) {
    ByteArrayInputStream afterB64Decode =
        new ByteArrayInputStream(Base64.decodeBase64(encodedResponse));

    if (HttpMethod.GET.name().equals(method)) {
      // If the request was a GET request, the value will have been deflated
      InputStream afterInflate = new InflaterInputStream(afterB64Decode, new Inflater(true));
      return new InputStreamReader(afterInflate, StandardCharsets.UTF_8);
    } else {
      return new InputStreamReader(afterB64Decode, StandardCharsets.UTF_8);
    }
  }

  private synchronized static void ensureOpenSamlIsInitialized() throws SamlException {
    if (!initializedOpenSaml) {
      try {
        InitializationService.initialize();
        initializedOpenSaml = true;
      } catch (Throwable ex) {
        throw new SamlException("Error while initializing the Open SAML library", ex);
      }
    }
  }

  private static DOMMetadataResolver createMetadataResolver(InputStream metadata)
      throws SamlException {
    try {
      BasicParserPool parser = XMLHelper.createDOMParser();
      Document metadataDocument = parser.parse(metadata);
      DOMMetadataResolver resolver = new DOMMetadataResolver(metadataDocument.getDocumentElement());
      resolver.setId(
          "componentId"); // The resolver needs an ID for the initialization to go through.
      resolver.initialize();
      return resolver;
    } catch (ComponentInitializationException | XMLParserException ex) {
      throw new SamlException("Cannot load identity provider metadata", ex);
    }
  }

  private static EntityDescriptor getEntityDescriptor(DOMMetadataResolver metadata)
      throws SamlException {
    List<EntityDescriptor> entityDescriptors = new ArrayList<>();
    metadata.forEach(entityDescriptors::add);
    if (entityDescriptors.size() != 1) {
      throw new SamlException("Bad entity descriptor count: " + entityDescriptors.size());
    }
    return entityDescriptors.get(0);
  }

  private static IDPSSODescriptor getIDPSSODescriptor(EntityDescriptor entityDescriptor)
      throws SamlException {
    IDPSSODescriptor idpssoDescriptor =
        entityDescriptor.getIDPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol");
    if (idpssoDescriptor == null) {
      throw new SamlException("Cannot retrieve IDP SSO descriptor");
    }

    return idpssoDescriptor;
  }

  private static SingleSignOnService getIdpBinding(
      IDPSSODescriptor idpSsoDescriptor, SamlIdpBinding samlBinding) throws SamlException {
    return idpSsoDescriptor
        .getSingleSignOnServices()
        .stream()
        .filter(
            x
                -> x.getBinding()
                    .equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-" + samlBinding.toString()))
        .findAny()
        .orElseThrow(() -> new SamlException("Cannot find HTTP-POST SSO binding in metadata"));
  }

  private static List<X509Certificate> getCertificates(IDPSSODescriptor idpSsoDescriptor)
      throws SamlException {

    List<X509Certificate> certificates;

    try {
      certificates =
          idpSsoDescriptor
              .getKeyDescriptors()
              .stream()
              .filter(x -> x.getUse() == UsageType.SIGNING)
              .flatMap(SamlClient::getDatasWithCertificates)
              .map(SamlClient::getFirstCertificate)
              .collect(Collectors.toList());

    } catch (Exception e) {
      throw new SamlException("Exception in getCertificates", e);
    }

    return certificates;
  }

  private static Stream<X509Data> getDatasWithCertificates(KeyDescriptor descriptor) {
    return descriptor
        .getKeyInfo()
        .getX509Datas()
        .stream()
        .filter(d -> !d.getX509Certificates().isEmpty());
  }

  private static X509Certificate getFirstCertificate(X509Data data) {
    try {
      org.opensaml.xmlsec.signature.X509Certificate cert =
          data.getX509Certificates().stream().findFirst().orElse(null);
      if (cert != null) {
        return KeyInfoSupport.getCertificate(cert);
      }
    } catch (CertificateException e) {
      logger.error("Exception in getFirstCertificate", e);
    }

    return null;
  }

  private static Credential getCredential(X509Certificate certificate) {
    BasicX509Credential credential = new BasicX509Credential(certificate);
    credential.setCRLs(Collections.emptyList());
    return credential;
  }

  /**
   * Decodes and validates an SAML response returned by an identity provider.
   *
   * @param encodedResponse the encoded response returned by the identity provider.
   * @param method The HTTP method used by the request
   * @return An {@link SamlResponse} object containing information decoded from the SAML response.
   * @throws SamlException if the signature is invalid, or if any other error occurs.
   */
  public SamlLogoutResponse decodeAndValidateSamlLogoutResponse(
      String encodedResponse, String method) throws SamlException {
    LogoutResponse logoutResponse = (LogoutResponse) parseResponse(encodedResponse, method);

    ValidatorUtils.validate(logoutResponse, responseIssuer, credentials);

    return new SamlLogoutResponse(logoutResponse.getStatus());
  }

  /**
   * Decodes and validates an SAML logout request send by an identity provider.
   *
   * @param encodedRequest the encoded request send by the identity provider.
   * @param nameID The user to logout
   * @param method The HTTP method used by the request
   * @throws SamlException if the signature is invalid, or if any other error occurs.
   */
  public void decodeAndValidateSamlLogoutRequest(
      String encodedRequest, String nameID, String method) throws SamlException {
    LogoutRequest logoutRequest = (LogoutRequest) parseResponse(encodedRequest, method);

    ValidatorUtils.validate(logoutRequest, responseIssuer, credentials, nameID);
  }

  /**
   * Set service provider keys.
   *
   * @param publicKey  the public key
   * @param privateKey the private key
   * @throws SamlException if publicKey and privateKey don't form a valid credential
   */
  public void setSPKeys(String publicKey, String privateKey) throws SamlException {
    this.spCredential = generateBasicX509Credential(publicKey, privateKey);
  }

  /**
   * generate an X509Credential from the provided key and cert.
   *
   * @param publicKey  the public key
   * @param privateKey the private key
   * @throws SamlException if publicKey and privateKey don't form a valid credential
   */
  private BasicX509Credential generateBasicX509Credential(String publicKey, String privateKey)
      throws SamlException {
    if (publicKey == null || privateKey == null) {
      throw new SamlException("No credentials provided");
    }
    PrivateKey pk = loadPrivateKey(privateKey);
    X509Certificate cert = loadCertificate(publicKey);
    return new BasicX509Credential(cert, pk);
  }

  /**
   * Set service provider keys.
   *
   * @param certificate the certificate
   * @param privateKey the private key
   * @throws SamlException if publicKey and privateKey don't form a valid credential
   */
  public void setSPKeys(X509Certificate certificate, PrivateKey privateKey) throws SamlException {
    if (certificate == null || privateKey == null) {
      throw new SamlException("No credentials provided");
    }
    spCredential = new BasicX509Credential(certificate, privateKey);
  }

  /**
   * Add an additional service provider certificate/key pair for decryption.
   *
   * @param publicKey  the public key
   * @param privateKey the private key
   * @throws SamlException if publicKey and privateKey don't form a valid credential
   */
  public void addAdditionalSPKey(String publicKey, String privateKey) throws SamlException {
    additionalSpCredentials.add(generateBasicX509Credential(publicKey, privateKey));
  }

  /**
   * Add an additional service provider certificate/key pair for decryption.
   *
   * @param certificate the certificate
   * @param privateKey the private key
   */
  public void addAdditionalSPKey(X509Certificate certificate, PrivateKey privateKey) {
    additionalSpCredentials.add(new BasicX509Credential(certificate, privateKey));
  }

  /**
   * Remove all additional service provider decryption certificate/key pairs.
   */
  public void clearAdditionalSPKeys() {
    additionalSpCredentials = new ArrayList<>();
  }

  /**
   * Gets attributes from the IDP Response
   *
   * @param response the response
   * @return the attributes
   */
  public static Map<String, String> getAttributes(SamlResponse response) {
    HashMap<String, String> map = new HashMap<>();
    if (response == null) {
      return map;
    }
    List<AttributeStatement> attributeStatements = response.getAssertion().getAttributeStatements();
    if (attributeStatements == null) {
      return map;
    }

    for (AttributeStatement statement : attributeStatements) {
      for (Attribute attribute : statement.getAttributes()) {
        XMLObject xmlObject = attribute.getAttributeValues().get(0);
        if (xmlObject instanceof XSStringImpl) {
          map.put(attribute.getName(), ((XSStringImpl) xmlObject).getValue());
        } else {
          map.put(attribute.getName(), ((XSAnyImpl) xmlObject).getTextContent());
        }
      }
    }
    return map;
  }

  /** Create a minimal SAML request
   *
   * @param defaultElementName The SomeClass.DEFAULT_ELEMENT_NAME we'll be casting this object into
   * */
  private RequestAbstractType getBasicSamlRequest(QName defaultElementName) {
    RequestAbstractType request = (RequestAbstractType) buildSamlObject(defaultElementName);
    request.setID("z" + UUID.randomUUID()); // ADFS needs IDs to start with a letter

    request.setVersion(SAMLVersion.VERSION_20);
    request.setIssueInstant(Instant.now());

    Issuer issuer = (Issuer) buildSamlObject(Issuer.DEFAULT_ELEMENT_NAME);
    issuer.setValue(relyingPartyIdentifier);
    request.setIssuer(issuer);

    return request;
  }

  /** Convert a SAML request to a base64-encoded String
   *
   * @param request The request to encode
   * @throws SamlException if marshalling the request fails
   * */
  private String marshallAndEncodeSamlObject(RequestAbstractType request, boolean forGetRequest) throws SamlException {
    StringWriter stringWriter;
    try {
      stringWriter = marshallXmlObject(request);
    } catch (MarshallingException e) {
      throw new SamlException("Error while marshalling SAML request to XML", e);
    }

    logger.trace("Issuing SAML request: " + stringWriter);
    byte[] requestBytes = stringWriter.toString().getBytes(StandardCharsets.UTF_8);

    if (forGetRequest) {
      // For GET requests, apply DEFLATE compression before Base64 encoding
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, deflater);
        deflaterOutputStream.write(requestBytes);
        deflaterOutputStream.close();
        return Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
      } catch (IOException e) {
        throw new SamlException("Error while compressing SAML request", e);
      }
    } else {
      // For POST requests, just do Base64 encoding
      return Base64.encodeBase64String(requestBytes);
    }
  }

  /**
   * Builds an encoded SAML request.
   *
   * @return The base-64 encoded SAML request.
   * @throws SamlException thrown if an unexpected error occurs.
   */
  public String getSamlRequest() throws SamlException {
    AuthnRequest request = (AuthnRequest) getBasicSamlRequest(AuthnRequest.DEFAULT_ELEMENT_NAME);

    request.setProtocolBinding(
        "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-" + this.samlBinding.toString());
    request.setDestination(identityProviderUrl);
    request.setAssertionConsumerServiceURL(assertionConsumerServiceUrl);

    NameIDPolicy nameIDPolicy = (NameIDPolicy) buildSamlObject(NameIDPolicy.DEFAULT_ELEMENT_NAME);
    nameIDPolicy.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
    request.setNameIDPolicy(nameIDPolicy);

    signSAMLObject(request);
    // 根据绑定类型决定是否进行压缩
    boolean forGetRequest = SamlIdpBinding.Redirect.name().equals(this.samlBinding.toString());
    return marshallAndEncodeSamlObject(request, forGetRequest);
  }

  /**
   * Gets the encoded logout request.
   *
   * @param nameId the name id
   * @return the logout request
   * @throws SamlException the saml exception
   */
  public String getLogoutRequest(String nameId) throws SamlException {
    LogoutRequest request = (LogoutRequest) getBasicSamlRequest(LogoutRequest.DEFAULT_ELEMENT_NAME);

    NameID nid = (NameID) buildSamlObject(NameID.DEFAULT_ELEMENT_NAME);
    nid.setValue(nameId);
    request.setNameID(nid);

    signSAMLObject(request);
    // 根据绑定类型决定是否进行压缩
    boolean forGetRequest = SamlIdpBinding.Redirect.name().equals(this.samlBinding.toString());
    return marshallAndEncodeSamlObject(request, forGetRequest);
  }
  /**
   * Gets saml logout response.
   *
   * @param status  the status code @See StatusCode.java
   * @return saml logout response
   * @throws SamlException the saml exception
   */
  public String getSamlLogoutResponse(final String status) throws SamlException {
    return getSamlLogoutResponse(status, null);
  }
  /**
   * Gets saml logout response.
   *
   * @param status  the status code @See StatusCode.java
   * @param statMsg the status message
   * @return saml logout response
   * @throws SamlException the saml exception
   */
  public String getSamlLogoutResponse(final String status, final String statMsg)
      throws SamlException {
    LogoutResponse response = (LogoutResponse) buildSamlObject(LogoutResponse.DEFAULT_ELEMENT_NAME);
    response.setID("z" + UUID.randomUUID()); // ADFS needs IDs to start with a letter

    response.setVersion(SAMLVersion.VERSION_20);
    response.setIssueInstant(Instant.now());

    Issuer issuer = (Issuer) buildSamlObject(Issuer.DEFAULT_ELEMENT_NAME);
    issuer.setValue(relyingPartyIdentifier);
    response.setIssuer(issuer);

    //Status
    Status stat = (Status) buildSamlObject(Status.DEFAULT_ELEMENT_NAME);
    StatusCode statCode = new StatusCodeBuilder().buildObject();
    statCode.setValue(status);
    stat.setStatusCode(statCode);
    if (statMsg != null) {
      StatusMessage statMessage = new StatusMessageBuilder().buildObject();
      statMessage.setValue(statMsg);
      stat.setStatusMessage(statMessage);
    }
    response.setStatus(stat);
    //Add a signature into the response
    signSAMLObject(response);

    StringWriter stringWriter;
    try {
      stringWriter = marshallXmlObject(response);
    } catch (MarshallingException ex) {
      throw new SamlException("Error while marshalling SAML request to XML", ex);
    }

    logger.trace("Issuing SAML Logout request: " + stringWriter);

    return Base64.encodeBase64String(stringWriter.toString().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Processes a POST containing the SAML logout request.
   *
   * @param request the {@link HttpServletRequest}.
   * @param nameID the user to log out.
   * @throws SamlException thrown is an unexpected error occurs.
   */
  public void processLogoutRequestPostFromIdentityProvider(
      HttpServletRequest request, String nameID) throws SamlException {
    String encodedResponse = request.getParameter(HTTP_REQ_SAML_PARAM);
    decodeAndValidateSamlLogoutRequest(encodedResponse, nameID, request.getMethod());
  }

  /**
   * Processes a POST containing the SAML response.
   *
   * @param request the {@link HttpServletRequest}.
   * @return An {@link SamlResponse} object containing information decoded from the SAML response.
   * @throws SamlException thrown is an unexpected error occurs.
   */
  public SamlLogoutResponse processPostLogoutResponseFromIdentityProvider(
      HttpServletRequest request) throws SamlException {
    String encodedResponse = request.getParameter(HTTP_RESP_SAML_PARAM);
    return decodeAndValidateSamlLogoutResponse(encodedResponse, request.getMethod());
  }

  /**
   * Redirects an {@link HttpServletResponse} to the configured identity provider.
   *
   * @param response   The {@link HttpServletResponse}.
   * @param relayState Optional relay state that will be passed along.
   * @param nameId the user to log out.
   * @throws IOException   thrown if an IO error occurs.
   * @throws SamlException thrown is an unexpected error occurs.
   */
  public void redirectToIdentityProvider(
      HttpServletResponse response, String relayState, String nameId)
      throws IOException, SamlException {
    Map<String, String> values = new HashMap<>();
    values.put("SAMLRequest", getLogoutRequest(nameId));
    if (relayState != null) {
      values.put("RelayState", relayState);
    }

    BrowserUtils.postUsingBrowser(identityProviderUrl, response, values);
  }
  /**
   * Redirect to identity provider logout.
   *
   * @param response   the response
   * @param statusCode the status code
   * @param statMsg    the stat msg
   * @throws IOException   the io exception
   * @throws SamlException the saml exception
   */
  public void redirectToIdentityProviderLogout(
      HttpServletResponse response, String statusCode, String statMsg)
      throws IOException, SamlException {
    Map<String, String> values = new HashMap<>();
    values.put(HTTP_RESP_SAML_PARAM, getSamlLogoutResponse(statusCode, statMsg));
    BrowserUtils.postUsingBrowser(identityProviderUrl, response, values);
  }

  private static XMLObject buildSamlObject(QName qname) {
    return Objects.requireNonNull(XMLObjectProviderRegistrySupport.getBuilderFactory()
                    .getBuilder(qname))
        .buildObject(qname);
  }

  /**
   * Decode the encrypted assertion.
   *
   * @param response the response
   * @throws DecryptionException the decryption exception
   */
  private void decodeEncryptedAssertion(Response response) throws DecryptionException {
    if (response.getEncryptedAssertions().isEmpty()) {
      return;
    }
    for (EncryptedAssertion encryptedAssertion : response.getEncryptedAssertions()) {
      // Create a decrypter.
      List<KeyInfoCredentialResolver> resolverChain = new ArrayList<>();

      if (spCredential != null) {
        resolverChain.add(new StaticKeyInfoCredentialResolver(spCredential));
      }

      if (!additionalSpCredentials.isEmpty()) {
        resolverChain.add(new CollectionKeyInfoCredentialResolver(additionalSpCredentials));
      }

      Decrypter decrypter =
          new Decrypter(
              null,
              new ChainingKeyInfoCredentialResolver(resolverChain),
              new InlineEncryptedKeyResolver());

      decrypter.setRootInNewDocument(true);

      // Decrypt the assertion.
      Assertion decryptedAssertion = decrypter.decrypt(encryptedAssertion);
      // Add the assertion
      response.getAssertions().add(decryptedAssertion);
    }
  }

  /**
   * Load an X.509 certificate
   * @param filename The path of the certificate
   * */
  private X509Certificate loadCertificate(String filename) throws SamlException {
    try (FileInputStream fis = new FileInputStream(filename);
        BufferedInputStream bis = new BufferedInputStream(fis)) {

      CertificateFactory cf = CertificateFactory.getInstance("X.509");

      return (X509Certificate) cf.generateCertificate(bis);

    } catch (FileNotFoundException e) {
      throw new SamlException("Public key file doesn't exist", e);
    } catch (Exception e) {
      throw new SamlException("Couldn't load public key", e);
    }
  }

  /**
   * Load a PKCS8 key
   * @param filename The path of the key
   * */
  private PrivateKey loadPrivateKey(String filename) throws SamlException {
    try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
      byte[] buf = new byte[(int) raf.length()];
      raf.readFully(buf);
      PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(buf);
      KeyFactory kf = KeyFactory.getInstance("RSA");

      return kf.generatePrivate(kspec);

    } catch (FileNotFoundException e) {
      throw new SamlException("Private key file doesn't exist", e);
    } catch (Exception e) {
      throw new SamlException("Couldn't load private key", e);
    }
  }

  private StringWriter marshallXmlObject(XMLObject object) throws MarshallingException {
    StringWriter stringWriter = new StringWriter();
    Marshaller marshaller =
        XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(object);
      Element dom = null;
      if (marshaller != null) {
          dom = marshaller.marshall(object);
      }
      XMLHelper.writeNode(dom, stringWriter);

    return stringWriter;
  }

  private SAMLObject parseResponse(String encodedResponse, String method) throws SamlException {
    logger.trace("Validating SAML response " + encodedResponse);
    try {
      Document responseDocument = domParser.parse(decodeAndInflate(encodedResponse, method));
      return (SAMLObject)
          Objects.requireNonNull(XMLObjectProviderRegistrySupport.getUnmarshallerFactory()
                          .getUnmarshaller(responseDocument.getDocumentElement()))
              .unmarshall(responseDocument.getDocumentElement());
    } catch (UnmarshallingException | XMLParserException ex) {
      throw new SamlException("Cannot decode xml encoded response", ex);
    }
  }

  /** Sign a SamlObject with default settings.
   * Note that this method is a no-op if spCredential is unset.
   * @param samlObject The object to sign
   *
   * @throws SamlException if {@link SignatureSupport#signObject(SignableXMLObject, SignatureSigningParameters) signObject} fails
   * */
  private void signSAMLObject(SignableSAMLObject samlObject) throws SamlException {
    if (spCredential != null) {
      try {
        // Describe how we're going to sign the request
        SignatureBuilder signer = new SignatureBuilder();
        Signature signature = signer.buildObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setKeyInfo(
            new X509KeyInfoGeneratorFactory().newInstance().generate(spCredential));
        signature.setSigningCredential(spCredential);
        samlObject.setSignature(signature);

        // Actually sign the request
        SignatureSigningParameters signingParameters = new SignatureSigningParameters();
        signingParameters.setSigningCredential(spCredential);
        signingParameters.setSignatureCanonicalizationAlgorithm(
            SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signingParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signingParameters.setKeyInfoGenerator(new X509KeyInfoGeneratorFactory().newInstance());
        SignatureSupport.signObject(samlObject, signingParameters);
      } catch (SecurityException | MarshallingException | SignatureException e) {
        throw new SamlException("Failed to sign request", e);
      }
    }
  }
}
