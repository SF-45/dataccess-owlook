package space.sadfox.dataccess.dataccess;

public class ParseError extends Exception {

  private static final long serialVersionUID = -2519364882381750347L;

  public ParseError() {
    super();
  }

  public ParseError(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ParseError(String message, Throwable cause) {
    super(message, cause);
  }

  public ParseError(String message) {
    super(message);
  }

  public ParseError(Throwable cause) {
    super(cause);
  }


}
