package org.nightlabs.base.exceptionhandler.errorreport;

public class CauseEffectThrowablePair {
	private Throwable cause;
	private Throwable effect;
	
	public CauseEffectThrowablePair(Throwable effect, Throwable cause) {
		this.cause = cause;
		this.effect = effect;
	}

	public Throwable getCauseThrowable() {
		return cause;
	}
	
	public Throwable getEffectThrowable() {
		return effect;
	}
}
