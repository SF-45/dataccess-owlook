package space.sadfox.dataccess.dataccess;

public class ParserException extends Exception {

  private static final long serialVersionUID = -2519364882381750347L;

  public ParserException() {
    super();
  }

  public ParserException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ParserException(String message, Throwable cause) {
    super(message, cause);
  }

  public ParserException(String message) {
    super(message);
  }

  public ParserException(Throwable cause) {
    super(cause);
  }


}
