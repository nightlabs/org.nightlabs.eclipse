package org.nightlabs.eclipse.compatibility;

public class NotAvailableInRAPException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NotAvailableInRAPException() {
		this("not available in RAP");
	}
	
	public NotAvailableInRAPException(String message) {
		super(message);
	}

}
