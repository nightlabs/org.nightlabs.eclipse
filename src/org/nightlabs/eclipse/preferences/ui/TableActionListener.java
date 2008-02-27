package org.nightlabs.eclipse.preferences.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.nightlabs.eclipse.preferences.ui.SimpleOptionsConfigurationBlock.Key;

/**
 * An action listener for tables added with 
 * {@link OptionsConfigurationBlock#addTable(org.eclipse.swt.widgets.Composite, String, Key, int, TableActionListener, int, int)}.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision: 1734 $ - $Date: 2008-01-08 17:02:20 +0100 (Di, 08 Jan 2008) $
 */
public interface TableActionListener
{
	/**
	 * Add entries to the table.
	 * @param tableViewer The target table viewer.
	 * @param key The preference key.
	 * @param event The original event.
	 */
	void add(TableViewer tableViewer, Key key, SelectionEvent event);
	
	/**
	 * Remove entries from the table.
	 * @param tableViewer The target table viewer.
	 * @param key The preference key.
	 * @param event The original event.
	 */
	void remove(TableViewer tableViewer, Key key, SelectionEvent event);
	
	/**
	 * Edit table entries.
	 * @param tableViewer The target table viewer.
	 * @param key The preference key.
	 * @param event The original event.
	 */
	void edit(TableViewer tableViewer, Key key, SelectionEvent event);
}