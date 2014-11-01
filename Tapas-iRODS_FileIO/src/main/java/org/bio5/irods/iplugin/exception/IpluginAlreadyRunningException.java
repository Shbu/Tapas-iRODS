package org.bio5.irods.iplugin.exception;

/**
 * Exception caused by iDrop already running.
 * 
 */
public class IpluginAlreadyRunningException extends IpluginException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2435686638718255181L;

	public IpluginAlreadyRunningException() {
	}

	public IpluginAlreadyRunningException(final String message) {
		super(message);
	}

	public IpluginAlreadyRunningException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public IpluginAlreadyRunningException(final Throwable cause) {
		super(cause);
	}
}
