package vip.xiaonuo.auth.core.protocol.saml.core;

import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusDetail;
import org.opensaml.saml.saml2.core.StatusMessage;

/**
 * The type Saml logout response.
 */
public class SamlLogoutResponse {
  private Status status;

  /**
   * Instantiates a new Saml logout response.
   *
   * @param status the status
   */
  public SamlLogoutResponse(Status status) {
    this.status = status;
  }

  /**
   * Is valid boolean.
   *
   * @return the boolean
   */
  public boolean isValid() {
    return StatusCode.SUCCESS.equals(this.status.getStatusCode().getValue());
  }

  /**
   * Is not valid boolean.
   *
   * @return the boolean
   */
  public boolean isNotValid() {
    return !isValid();
  }

  /**
   * Gets status code.
   *
   * @return the status code
   */
  public String getStatusCode() {
    return this.status.getStatusCode().getValue();
  }

  /**
   * Gets message.
   *
   * @return the message
   */
  public String getMessage() {
    StatusMessage statusMessage = status.getStatusMessage();
    return statusMessage == null ? "none" : statusMessage.getValue();
  }

  /**
   * Gets details.
   *
   * @return the details
   */
  public String getDetails() {
    StatusDetail statusDetail = this.status.getStatusDetail();
    return statusDetail == null ? "none" : statusDetail.toString();
  }
}
