package org.nightlabs.jseditor.ui;

import java.util.EventObject;

public class DocumentEvent extends EventObject
{
	private static final long serialVersionUID = 1L;

	public DocumentEvent(IJSEditor source) {
		super(source);
	}

	@Override
	public IJSEditor getSource() {
		return (IJSEditor) super.getSource();
	}
}
