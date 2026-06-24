package vip.xiaonuo.auth.core.protocol.saml.core;

public class SamlException extends Exception {
  private static final long serialVersionUID = 6911867372179131014L;

  public SamlException(String message) {
    super(message);
  }

  public SamlException(String message, Throwable cause) {
    super(message, cause);
  }
}
