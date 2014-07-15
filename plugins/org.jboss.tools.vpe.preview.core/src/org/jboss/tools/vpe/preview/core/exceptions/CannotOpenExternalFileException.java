package org.jboss.tools.vpe.preview.core.exceptions;

public class CannotOpenExternalFileException extends Exception {
	private static final long serialVersionUID = -2090569707031789823L;
	
	public CannotOpenExternalFileException(String message) {
		super(message, null);
	}
	
	@Override
	/**
	 * Disable showing stacktrace for this exception.
	 */
	public synchronized Throwable fillInStackTrace() {
		return null;
	}
}
