package org.nightlabs.jseditor.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public interface IJSEditor {

	void addFocusListener(FocusListener listener);

	void removeFocusListener(FocusListener listener);

	void addMouseListener(MouseListener listener);

	void removeMouseListener(MouseListener listener);

	void setDocumentText(String text);

	String getDocumentText();

	// This should be in compliance with the current FocusListener settings, which direct this JSEditorComposite's
	// focus to the sourceViewer. Kai 2009-11-19.
	// TODO this should not be used as it reacts only on keys - not when data is pasted from the clipboard via the mouse!
	// Need to use the document listener instead!
	void addKeyListener(KeyListener listener);

	void removeKeyListener(KeyListener listener);

	void addDocumentListener(IDocumentListener documentListener);

	void removeDocumentListener(IDocumentListener documentListener);

	void setEnabled(boolean enabled);

	boolean isEnabled();

	Control getControl();

	Menu createContextMenu(MenuManager menuManager);

}