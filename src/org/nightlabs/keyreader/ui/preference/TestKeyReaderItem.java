package org.nightlabs.keyreader.ui.preference;

import org.nightlabs.keyreader.KeyReadEvent;
import org.nightlabs.keyreader.KeyReader;
import org.nightlabs.keyreader.KeyReaderErrorEvent;
import org.nightlabs.keyreader.ui.KeyReaderUseCase;

public class TestKeyReaderItem
{
	private KeyReaderUseCase keyReaderUseCase;
	private KeyReader keyReader;
	private volatile KeyReadEvent lastKeyReadEvent = null;
	private volatile KeyReaderErrorEvent lastKeyReaderErrorEvent = null;

	public TestKeyReaderItem(KeyReaderUseCase keyReaderUseCase, KeyReader keyReader) {
		this.keyReaderUseCase = keyReaderUseCase;
		this.keyReader = keyReader;
	}

	public KeyReaderUseCase getKeyReaderUseCase() {
		return keyReaderUseCase;
	}
	
	public KeyReader getKeyReader() {
		return keyReader;
	}
	
	public KeyReaderErrorEvent getLastKeyReaderErrorEvent() {
		return lastKeyReaderErrorEvent;
	}

	public void setLastKeyReadEvent(KeyReadEvent lastKeyReadEvent) {
		this.lastKeyReadEvent = lastKeyReadEvent;
	}
	
	public KeyReadEvent getLastKeyReadEvent() {
		return lastKeyReadEvent;
	}
	
	public void setLastKeyReaderErrorEvent(KeyReaderErrorEvent lastKeyReaderErrorEvent) {
		this.lastKeyReaderErrorEvent = lastKeyReaderErrorEvent;
	}
}
