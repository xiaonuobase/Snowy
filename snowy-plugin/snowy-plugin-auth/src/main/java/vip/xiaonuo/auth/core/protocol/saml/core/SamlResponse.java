package vip.xiaonuo.auth.core.protocol.saml.core;

import org.opensaml.saml.saml2.core.Assertion;

public class SamlResponse {
  private Assertion assertion;

  public SamlResponse(Assertion assertion) {
    this.assertion = assertion;
  }

  /**
   * Retrieves the {@link Assertion} for the SAML response.
   *
   * @return The assertion for the SAML response.
   */
  public Assertion getAssertion() {
    return assertion;
  }

  /**
   * Retrieves the Name ID from the SAML response. This is normally the name of the authenticated
   * user.
   *
   * @return The Name ID from the SAML response.
   */
  public String getNameID() {
    return assertion.getSubject().getNameID().getValue();
  }
}
