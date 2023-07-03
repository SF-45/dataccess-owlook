package space.sadfox.dataccess.action;

public class ActionProviderNotFound extends Exception {

	private static final long serialVersionUID = -4347821738236961383L;

	public ActionProviderNotFound() {
		super();
	}

	public ActionProviderNotFound(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ActionProviderNotFound(String message, Throwable cause) {
		super(message, cause);
	}

	public ActionProviderNotFound(String message) {
		super(message);
	}

	public ActionProviderNotFound(Throwable cause) {
		super(cause);
	}

}
