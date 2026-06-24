package vip.xiaonuo.auth.core.protocol.saml.core;

import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.*;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.security.cert.X509Certificate;


public class MetadataUtils {

	private static final Logger logger = LoggerFactory.getLogger(SamlClient.class);

	public static String generateSpMetadata(String entityId, String assertionConsumerServiceURL, String logoutServiceURL) {
		return generateSpMetadata(entityId, assertionConsumerServiceURL, logoutServiceURL, null);
	}

	@SuppressWarnings("ALL")
	public static String generateSpMetadata(String entityId, String assertionConsumerServiceURL, String singleLogoutServiceURL, X509Certificate certificate) {
		try {
			InitializationService.initialize();

			EntityDescriptor spEntityDescriptor = createSAMLObject(EntityDescriptor.class);
			if (spEntityDescriptor == null) {
				return null;
			}
			spEntityDescriptor.setEntityID(entityId);
			SPSSODescriptor spSSODescriptor = createSAMLObject(SPSSODescriptor.class);
			if (spSSODescriptor == null) {
				return null;
			}

			spSSODescriptor.setWantAssertionsSigned(false);
			spSSODescriptor.setAuthnRequestsSigned(false);

			if (certificate != null) {

				spSSODescriptor.setWantAssertionsSigned(true);
				spSSODescriptor.setAuthnRequestsSigned(true);

				X509KeyInfoGeneratorFactory keyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
				keyInfoGeneratorFactory.setEmitEntityCertificate(true);
				KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();

				KeyDescriptor encKeyDescriptor = createSAMLObject(KeyDescriptor.class);
				if (encKeyDescriptor == null) {
					return null;
				}

				encKeyDescriptor.setUse(UsageType.ENCRYPTION);

				Credential credential = new BasicX509Credential(certificate);

				try {
					encKeyDescriptor.setKeyInfo(keyInfoGenerator.generate(credential));
				}
				catch (Exception e) {
					logger.error("Error while creating credentials", e);
				}
				spSSODescriptor.getKeyDescriptors().add(encKeyDescriptor);

				KeyDescriptor signKeyDescriptor = createSAMLObject(KeyDescriptor.class);
				if (signKeyDescriptor == null) {
					return null;
				}

				signKeyDescriptor.setUse(UsageType.SIGNING); // Set usage

				try {
					signKeyDescriptor.setKeyInfo(keyInfoGenerator.generate(credential));
				}
				catch (SecurityException e) {
					logger.error("Error while creating credentials", e);
				}
				spSSODescriptor.getKeyDescriptors().add(signKeyDescriptor);
			}

			SingleLogoutService singleLogoutService = createSAMLObject(SingleLogoutService.class);
			if (singleLogoutService == null) {
				return null;
			}
			singleLogoutService.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
			singleLogoutService.setLocation(singleLogoutServiceURL);
			spSSODescriptor.getSingleLogoutServices().add(singleLogoutService);

			NameIDFormat nameIDFormat = createSAMLObject(NameIDFormat.class);
			if (nameIDFormat == null) {
				return null;
			}

			nameIDFormat.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
			spSSODescriptor.getNameIDFormats().add(nameIDFormat);

			AssertionConsumerService assertionConsumerService = createSAMLObject(AssertionConsumerService.class);
			if (assertionConsumerService == null) {
				return null;
			}
			assertionConsumerService.setIndex(1);
			assertionConsumerService.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);

			assertionConsumerService.setLocation(assertionConsumerServiceURL);
			spSSODescriptor.getAssertionConsumerServices().add(assertionConsumerService);

			spSSODescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

			spEntityDescriptor.getRoleDescriptors().add(spSSODescriptor);

			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Marshaller out = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(spEntityDescriptor);
			out.marshall(spEntityDescriptor, document);

			TransformerFactory transformerfactory = TransformerFactory.newInstance();
			transformerfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			Transformer transformer = transformerfactory.newTransformer();
			StringWriter stringWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(stringWriter);
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.transform(source, streamResult);
			stringWriter.close();

			return stringWriter.toString();
		}
		catch (Exception e) {
			logger.error("Error while generation SP metadata", e);
			return null;
		}

	}

	public static <T> T createSAMLObject(final Class<T> clazz) {
		XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();

		QName defaultElementName = null;
		try {
			defaultElementName = (QName) clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
		}
		catch (Exception e) {
			logger.error("Error while creating SAML object", e);
			return null;
		}
		T object = (T) builderFactory.getBuilder(defaultElementName).buildObject(defaultElementName);

		return object;
	}

	/**
	 * 生成IDP元数据
	 * @param entityId IDP实体ID
	 * @param ssoServiceURL 单点登录服务URL
	 * @param certificate 用于签名和加密的证书
	 * @return IDP元数据XML字符串
	 */
	@SuppressWarnings("ALL")
	public static String generateIdpMetadata(String entityId, String ssoServiceURL, X509Certificate certificate) {
		try {
			InitializationService.initialize();

			// 创建实体描述符
			EntityDescriptor idpEntityDescriptor = createSAMLObject(EntityDescriptor.class);
			if (idpEntityDescriptor == null) {
				return null;
			}
			idpEntityDescriptor.setEntityID(entityId);

			// 创建IDP SSO描述符
			IDPSSODescriptor idpSSODescriptor = createSAMLObject(IDPSSODescriptor.class);
			if (idpSSODescriptor == null) {
				return null;
			}

			// 设置支持的协议
			idpSSODescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

			// 如果提供了证书，添加密钥描述符
			if (certificate != null) {
				X509KeyInfoGeneratorFactory keyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
				keyInfoGeneratorFactory.setEmitEntityCertificate(true);
				KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();

				Credential credential = new BasicX509Credential(certificate);

				// 添加签名密钥描述符
				KeyDescriptor signKeyDescriptor = createSAMLObject(KeyDescriptor.class);
				if (signKeyDescriptor == null) {
					return null;
				}
				signKeyDescriptor.setUse(UsageType.SIGNING);
				try {
					signKeyDescriptor.setKeyInfo(keyInfoGenerator.generate(credential));
				} catch (Exception e) {
					logger.error("Error while creating signing key info", e);
				}
				idpSSODescriptor.getKeyDescriptors().add(signKeyDescriptor);

				// 添加加密密钥描述符
				KeyDescriptor encKeyDescriptor = createSAMLObject(KeyDescriptor.class);
				if (encKeyDescriptor == null) {
					return null;
				}
				encKeyDescriptor.setUse(UsageType.ENCRYPTION);
				try {
					encKeyDescriptor.setKeyInfo(keyInfoGenerator.generate(credential));
				} catch (Exception e) {
					logger.error("Error while creating encryption key info", e);
				}
				idpSSODescriptor.getKeyDescriptors().add(encKeyDescriptor);
			}

			// 添加单点登录服务 (HTTP-Redirect绑定)
			SingleSignOnService ssoServiceRedirect = createSAMLObject(SingleSignOnService.class);
			if (ssoServiceRedirect == null) {
				return null;
			}
			ssoServiceRedirect.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
			ssoServiceRedirect.setLocation(ssoServiceURL);
			idpSSODescriptor.getSingleSignOnServices().add(ssoServiceRedirect);

			// 添加单点登录服务 (HTTP-POST绑定)
			SingleSignOnService ssoServicePost = createSAMLObject(SingleSignOnService.class);
			if (ssoServicePost == null) {
				return null;
			}
			ssoServicePost.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
			ssoServicePost.setLocation(ssoServiceURL);
			idpSSODescriptor.getSingleSignOnServices().add(ssoServicePost);

			// 添加NameID格式
			NameIDFormat nameIDFormat1 = createSAMLObject(NameIDFormat.class);
			if (nameIDFormat1 == null) {
				return null;
			}
			nameIDFormat1.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
			idpSSODescriptor.getNameIDFormats().add(nameIDFormat1);

			NameIDFormat nameIDFormat2 = createSAMLObject(NameIDFormat.class);
			if (nameIDFormat2 == null) {
				return null;
			}
			nameIDFormat2.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
			idpSSODescriptor.getNameIDFormats().add(nameIDFormat2);

			NameIDFormat nameIDFormat3 = createSAMLObject(NameIDFormat.class);
			if (nameIDFormat3 == null) {
				return null;
			}
			nameIDFormat3.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
			idpSSODescriptor.getNameIDFormats().add(nameIDFormat3);

			// 将IDP SSO描述符添加到实体描述符
			idpEntityDescriptor.getRoleDescriptors().add(idpSSODescriptor);

			// 将XML对象转换为字符串
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Marshaller out = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(idpEntityDescriptor);
			out.marshall(idpEntityDescriptor, document);

			TransformerFactory transformerfactory = TransformerFactory.newInstance();
			transformerfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			Transformer transformer = transformerfactory.newTransformer();
			StringWriter stringWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(stringWriter);
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.transform(source, streamResult);
			stringWriter.close();

			return stringWriter.toString();
		} catch (Exception e) {
			logger.error("Error while generating IDP metadata", e);
			return null;
		}
	}

	// 为方便调用，提供不带证书的重载方法
	public static String generateIdpMetadata(String entityId, String ssoServiceURL) {
		return generateIdpMetadata(entityId, ssoServiceURL, null);
	}
}
