package org.bio5.irods.iplugin.exception;

/**
 */
public class IpluginException extends Exception {

	/**   
	 * 
	 */
	private static final long serialVersionUID = 5893379469123450760L;

	public IpluginException(final Throwable cause) {
		super(cause);
	}

	public IpluginException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public IpluginException(final String message) {
		super(message);
	}

	public IpluginException() {
	}
}
