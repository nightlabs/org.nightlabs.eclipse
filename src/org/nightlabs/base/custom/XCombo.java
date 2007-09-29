/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleTextAdapter;
import org.eclipse.swt.accessibility.AccessibleTextEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;
import org.nightlabs.base.composite.XComposite;

/**
 * A Custom Implementation of a ComboBox which is able not only to show Strings but also
 * {@link org.eclipse.swt.graphics.Image}s.  
 * 
 * This Composite and its implementation (greate parts are adopted) is created more less
 * like a {@link org.eclipse.swt.custom.CCombo} but
 * internally, it uses a {@link org.eclipse.swt.widgets.Table} instead of a 
 * {@link org.eclipse.swt.widgets.List} for displaying the entries.
 * 
 * @author Daniel.Mazurek [AT] NightLabs [DOT] com
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class XCombo 
extends XComposite
{
	private Label imageLabel;
	private Text text;
	private int visibleItemCount = 5;
	private Shell popup;
	private Button arrow;
	private boolean hasFocus;
	private Listener listener, filter;
	private Color foreground, background;
	private Font font;	
	private Table table;

	private static boolean isGtk()
	{
		String ws = System.getProperty("osgi.ws");
		return "gtk".equals(ws);
	}
	
/**
 * Constructs a new instance of this class given its parent
 * and a style value describing its behaviour and appearance.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together 
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 *
 * @param parent a widget which will be the parent of the new instance (cannot be null)
 * @param style the style of widget to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 * </ul>
 *
 * @see SWT#BORDER
 * @see SWT#READ_ONLY
 * @see SWT#FLAT
 * @see Widget#getStyle()
 */
public XCombo (Composite parent, int style) {
	super (parent, style = checkStyle (style, parent), LayoutMode.NONE, LayoutDataMode.NONE);
//	getGridLayout().horizontalSpacing = 2;
	
	imageLabel = new Label(this, SWT.NONE);
	imageLabel.setBackground(getDefaultBackgroundColor());

	int textStyle = SWT.SINGLE;
	if ((style & SWT.READ_ONLY) != 0) textStyle |= SWT.READ_ONLY;
	if ((style & SWT.FLAT) != 0) textStyle |= SWT.FLAT;
	text = new Text (this, textStyle);
	// Workaround to avoid grey background if style == SWT.READ_ONLY; // TODO why this?! The native combos are grey, if this style is set.
//	if (!isGtk()) { // added by Marco, because it doesn't look good in GTK.
		if (isReadOnly())	
			text.setBackground(getDefaultBackgroundColor());
//	}

	int arrowStyle = SWT.DOWN | SWT.ARROW;
	if ((style & SWT.FLAT) != 0) arrowStyle |= SWT.FLAT;
	
	arrow = new Button (this, arrowStyle);

	listener = new Listener () {
		public void handleEvent (Event event) {
			if (popup == event.widget) {
				popupEvent (event);
				return;
			}
			if (text == event.widget) {
				textEvent (event);
				return;
			}
			if (table == event.widget) {
				listEvent (event);
				return;
			}
			if (arrow == event.widget) {
				arrowEvent (event);
				return;
			}
			if (XCombo.this == event.widget) {
				comboEvent (event);
				return;
			}
			if (getShell () == event.widget) {
				handleFocus (SWT.FocusOut);
			}
		}
	};
	filter = new Listener() {
		public void handleEvent(Event event) {
			Shell shell = ((Control)event.widget).getShell ();
			if (shell == XCombo.this.getShell ()) {
				handleFocus (SWT.FocusOut);
			}
		}
	};
	
	int [] comboEvents = {SWT.Dispose, SWT.Move, SWT.Resize};
	for (int i=0; i<comboEvents.length; i++) this.addListener (comboEvents [i], listener);
	
	int [] textEvents = {SWT.KeyDown, SWT.KeyUp, SWT.Modify, SWT.MouseDown, SWT.MouseUp, SWT.Traverse, SWT.FocusIn};
	for (int i=0; i<textEvents.length; i++) text.addListener (textEvents [i], listener);
	
	int [] arrowEvents = {SWT.Selection, SWT.FocusIn};
	for (int i=0; i<arrowEvents.length; i++) arrow.addListener (arrowEvents [i], listener);
	
	addControlListener(new ControlAdapter() {
	
		@Override
		public void controlResized(ControlEvent e) {
			XCombo.this.controlResized(e);
		}
	
	});
	
	createPopup(null, -1);
	initAccessible();
}

protected void controlResized(ControlEvent e) {
	internalLayout(false);
}

// Custom Method to determine default bg Color of a text to avoid grey bg if style == SWT.READ_ONLY
private Color getDefaultBackgroundColor() {
	Text t = new Text(this, SWT.NONE);
	Color bg = t.getBackground();
	t.dispose();
	return bg;
}

// Custom Method to which avoid the selecting of the text if the style == SWT.READ_ONLY
private void textSelectAll()  
{
	if (! isReadOnly())
		text.selectAll ();
}

private boolean isReadOnly() {
	return (getStyle() & SWT.READ_ONLY) != 0;
}

static int checkStyle (int style, Composite parent) {
	int mask = XComposite.getBorderStyle(parent) | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
	return style | mask;
}

/**
 * Adds the argument to the end of the receiver's table.
 *
 * @param image the new item image
 * @param text the new item name
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #add(Image, String, int)
 */
public void add (Image image, String text) {
	add(image, text, -1);
}

/**
 * Adds the argument to the receiver's table at the given
 * zero-relative index.
 * <p>
 * Note: To add an item at the end of the list, use the
 * result of calling <code>getItemCount()</code> as the
 * index or use <code>add(String)</code>.
 * </p>
 *
 * @param image the new item image
 * @param text the new item name
 * @param index the index for the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #add(Image, String)
 */
public void add (Image image, String text, int index) {
	checkWidget();
	if (text == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	if (index < 0)
		index = table.getItemCount();
	TableItem item = new TableItem(table, getStyle(), index);
	item.setImage(image);
	item.setText(text);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver's text is modified, by sending
 * it one of the messages defined in the <code>ModifyListener</code>
 * interface.
 *
 * @param listener the listener which should be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see ModifyListener
 * @see #removeModifyListener
 */
public void addModifyListener (ModifyListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Modify, typedListener);
}
/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver's selection changes, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 * <p>
 * <code>widgetSelected</code> is called when the combo's list selection changes.
 * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text area.
 * </p>
 *
 * @param listener the listener which should be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SelectionListener
 * @see #removeSelectionListener
 * @see SelectionEvent
 */
public void addSelectionListener(SelectionListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Selection,typedListener);
	addListener (SWT.DefaultSelection,typedListener);
}

// TODO: This is used to ship around an event dispatching order problem. see arrowEvent and popupEvent
//private long popupTime = 0;

void arrowEvent (Event event) {
	switch (event.type) {
		case SWT.FocusIn: {
			handleFocus (SWT.FocusIn);
			break;
		}
		case SWT.Selection: {
			if (!shellJustDeactivated)
				dropDown (!isDropped ());
			
			shellJustDeactivated = false;
			break;
		}
	}
}
/**
 * Sets the selection in the receiver's text field to an empty
 * selection starting just before the first character. If the
 * text field is editable, this has the effect of placing the
 * i-beam at the start of the text.
 * <p>
 * Note: To clear the selected items in the receiver's list, 
 * use <code>deselectAll()</code>.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #deselectAll
 */
public void clearSelection () {
	checkWidget ();
	text.clearSelection ();
	table.deselectAll ();
}

void comboEvent (Event event) {
	switch (event.type) {
		case SWT.Dispose:
			if (popup != null && !popup.isDisposed ()) {
				table.removeListener (SWT.Dispose, listener);
				popup.dispose ();
			}
			Shell shell = getShell ();
			shell.removeListener (SWT.Deactivate, listener);
			Display display = getDisplay ();
			display.removeFilter (SWT.FocusIn, filter);
			popup = null;  
			text = null;  
			table = null;  
			arrow = null;
			break;
		case SWT.Move:
			dropDown (false);
			break;
		case SWT.Resize:
			internalLayout (false);
			break;
	}
}

//public Point computeSize (int wHint, int hHint, boolean changed) {
//	checkWidget ();
//	int width = 0, height = 0;
//	TableItem[] items = list.getItems ();
//	int textWidth = 0;
//	GC gc = new GC (text);
//	int spacer = gc.stringExtent (" ").x; //$NON-NLS-1$
//	for (int i = 0; i < items.length; i++) {
//		textWidth = Math.max (gc.stringExtent (items[i]).x, textWidth);
//	}
//	gc.dispose();
//	Point textSize = text.computeSize (SWT.DEFAULT, SWT.DEFAULT, changed);
//	Point arrowSize = arrow.computeSize (SWT.DEFAULT, SWT.DEFAULT, changed);
//	Point listSize = list.computeSize (wHint, SWT.DEFAULT, changed);
//	int borderWidth = getBorderWidth ();
//	
//	height = Math.max (hHint, Math.max (textSize.y, arrowSize.y) + 2*borderWidth);
//	width = Math.max (wHint, Math.max (textWidth + 2*spacer + arrowSize.x + 2*borderWidth, listSize.x));
//	return new Point (width, height);
//}
public Point computeSize (int wHint, int hHint, boolean changed) 
{
	checkWidget ();
	int width = 0, height = 0;

	Point maxImageSize = new Point(0, 0);
	TableItem[] items = getItems();

	int textWidth = 0;
	GC gc = new GC (text);
	int spacer = gc.stringExtent (" ").x; //$NON-NLS-1$
	for (int i = 0; i < items.length; i++) {
		TableItem item = items[i];
		item.getText();
		textWidth = Math.max(gc.stringExtent(items[i].getText()).x, textWidth);

		Image img = item.getImage();
		maxImageSize.x = Math.max(maxImageSize.x, img == null ? 0 : img.getBounds().width);
		maxImageSize.y = Math.max(maxImageSize.y, img == null ? 0 : img.getBounds().height);
	}	
	gc.dispose();
	Point textSize = text.computeSize (SWT.DEFAULT, SWT.DEFAULT, changed);
	Point arrowSize = arrow.computeSize (SWT.DEFAULT, SWT.DEFAULT, changed);
	
	// TODO workaround to change the size of the XCombo
	if (isGtk()) { // added by Marco, because it doesn't look good in GTK.
		Text t = new Text(this, SWT.BORDER);
		t.setText("ABCxyz");
		arrowSize.y = t.getSize().y;
		t.dispose();
//		arrowSize.y -= 5;
	}
	
	Point listSize = table.computeSize (wHint, SWT.DEFAULT, changed);
	int borderWidth = getBorderWidth ();

	int imageTextSpace = maxImageSize.x == 0 ? 0 : imageTextSpaceConst;

	height = Math.max (hHint, Math.max (textSize.y, arrowSize.y) + 2*borderWidth);
//	width = Math.max (wHint, Math.max (textWidth + 2*spacer + arrowSize.x + 2*borderWidth, listSize.x));
//	width += maxImageSize.x + imageTextSpace;
	width = Math.max (wHint, Math.max (maxImageSize.x + imageTextSpace + textWidth + 2*spacer + arrowSize.x + 2*borderWidth, listSize.x));
	return new Point (width, height);
}

void createPopup(TableItem[] items, int selectionIndex) {		
		// create shell and list
		popup = new Shell (getShell (), SWT.NO_TRIM | SWT.ON_TOP);
//		shellJustDeactivated = false;
		int style = getStyle ();
		int listStyle = SWT.SINGLE | SWT.V_SCROLL;
		if ((style & SWT.FLAT) != 0) listStyle |= SWT.FLAT;
		if ((style & SWT.RIGHT_TO_LEFT) != 0) listStyle |= SWT.RIGHT_TO_LEFT;
		if ((style & SWT.LEFT_TO_RIGHT) != 0) listStyle |= SWT.LEFT_TO_RIGHT;
		table = new Table (popup, listStyle);
		if (font != null) table.setFont (font);
		if (foreground != null) table.setForeground (foreground);
//		if (background != null) table.setBackground (background); // always keep the backround of the table white!
		
		int [] popupEvents = {SWT.Close, SWT.Paint, SWT.Deactivate};
		for (int i=0; i<popupEvents.length; i++) popup.addListener (popupEvents [i], listener);
		int [] listEvents = {SWT.MouseUp, SWT.Selection, SWT.Traverse, SWT.KeyDown, SWT.KeyUp, SWT.FocusIn, SWT.Dispose};
		for (int i=0; i<listEvents.length; i++) table.addListener (listEvents [i], listener);
		
//		if (items != null) list.setItems (items);
		if (selectionIndex != -1) table.setSelection (selectionIndex);
}
/**
 * Deselects the item at the given zero-relative index in the receiver's 
 * table.  If the item at the index was already deselected, it remains
 * deselected. Indices that are out of range are ignored.
 *
 * @param index the index of the item to deselect
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselect (int index) {
	checkWidget ();
	table.deselect (index);
}
/**
 * Deselects all selected items in the receiver's table.
 * <p>
 * Note: To clear the selection in the receiver's text field,
 * use <code>clearSelection()</code>.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #clearSelection
 */
public void deselectAll () {
	checkWidget ();
	table.deselectAll ();
}

void dropDown (boolean drop) {
	if (drop == isDropped ()) return;
	if (!drop) {
		popup.setVisible (false);
		if (!isDisposed ()&& arrow.isFocusControl()) {
			setFocus();
		}
		return;
	}

	if (getShell() != popup.getParent ()) {
		TableItem[] items = table.getItems ();
		int selectionIndex = table.getSelectionIndex ();
		table.removeListener (SWT.Dispose, listener);
		popup.dispose();
		popup = null;
		table = null;
		createPopup (items, selectionIndex);
	}
	
	Point size = getSize ();
	int itemCount = table.getItemCount ();
	itemCount = (itemCount == 0) ? visibleItemCount : Math.min(visibleItemCount, itemCount);
	int itemHeight = table.getItemHeight () * itemCount;
	Point listSize = table.computeSize (SWT.DEFAULT, itemHeight, false);
	// Since the Scrollbar widths are always added (thanks to Windoof compatibility) we need to prune 
	// of the unnecessary space by ignoring the listSize.y and simply use the calculated height
//	int scrollbarWidth = 0;
//	ScrollBar bar;
//	if (table.getItemCount() <= visibleItemCount) {
//		bar = table.getVerticalBar();
//		if (bar != null)
//			scrollbarWidth = bar.getSize().x;
//	}
	table.setBounds (1, 1, Math.max (size.x - 2, listSize.x), itemHeight);

	int index = table.getSelectionIndex ();
	if (index != -1) table.setTopIndex (index);
	Display display = getDisplay ();
	Rectangle listRect = table.getBounds ();
	Rectangle parentRect = display.map (getParent (), null, getBounds ());
	Point comboSize = getSize ();
	Rectangle displayRect = getMonitor ().getClientArea ();
	int width = Math.max (comboSize.x, listRect.width + 2);
	int height = listRect.height + 2;
	int x = parentRect.x;
	int y = parentRect.y + comboSize.y;
	if (y + height > displayRect.y + displayRect.height) y = parentRect.y - height;
	popup.setBounds (x, y, width, height);
	popup.setVisible (true);
	table.setFocus ();
}

/* 
 * Return the Label immediately preceding the receiver in the z-order, 
 * or null if none. 
 */
Label getAssociatedLabel () {
	Control[] siblings = getParent ().getChildren ();
	for (int i = 0; i < siblings.length; i++) {
		if (siblings [i] == XCombo.this) {
			if (i > 0 && siblings [i-1] instanceof Label) {
				return (Label) siblings [i-1];
			}
		}
	}
	return null;
}

public Control [] getChildren () {
	checkWidget();
	return new Control [0];
}

/**
 * Gets the editable state.
 *
 * @return whether or not the reciever is editable
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @since 3.0
 */
public boolean getEditable () {
	checkWidget ();
	return text.getEditable();
}

/**
 * Returns the item at the given, zero-relative index in the
 * receiver's table. Throws an exception if the index is out
 * of range.
 *
 * @param index the index of the item to return
 * @return the item at the given index
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public TableItem getItem (int index) {
	checkWidget();
	return table.getItem (index);
}

/**
 * Returns the number of items contained in the receiver's table.
 *
 * @return the number of items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getItemCount () {
	checkWidget ();
	return table.getItemCount ();
}

/**
 * Returns the height of the area which would be used to
 * display <em>one</em> of the items in the receiver's table.
 *
 * @return the height of one item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getItemHeight () {
	checkWidget ();
	return table.getItemHeight ();
}

/**
 * Returns an array of <code>TableItem</code>s which are the items
 * in the receiver's table. 
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its list of items, so modifying the array will
 * not affect the receiver. 
 * </p>
 *
 * @return the items in the receiver's table
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public TableItem [] getItems () {
	checkWidget ();
	return table.getItems ();
}

char getMnemonic (String string) {
	int index = 0;
	int length = string.length ();
	do {
		while ((index < length) && (string.charAt (index) != '&')) index++;
		if (++index >= length) return '\0';
		if (string.charAt (index) != '&') return string.charAt (index);
		index++;
	} while (index < length);
 	return '\0';
}

/**
 * Returns a <code>Point</code> whose x coordinate is the start
 * of the selection in the receiver's text field, and whose y
 * coordinate is the end of the selection. The returned values
 * are zero-relative. An "empty" selection as indicated by
 * the the x and y coordinates having the same value.
 *
 * @return a point representing the selection start and end
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Point getSelection () {
	checkWidget ();
	return text.getSelection ();
}

/**
 * Returns the zero-relative index of the item which is currently
 * selected in the receiver's table, or -1 if no item is selected.
 *
 * @return the index of the selected item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getSelectionIndex () {
	checkWidget ();
	return table.getSelectionIndex ();
}

public int getStyle () {
	int style = super.getStyle ();
	style &= ~SWT.READ_ONLY;
	if (!text.getEditable()) style |= SWT.READ_ONLY; 
	return style;
}

/**
 * Returns a string containing a copy of the contents of the
 * receiver's text field.
 *
 * @return the receiver's text
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getText () {
	checkWidget ();
	return text.getText ();
}

/**
 * Returns the height of the receivers's text field.
 *
 * @return the text height
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getTextHeight () {
	checkWidget ();
	return text.getLineHeight ();
}

/**
 * Returns the maximum number of characters that the receiver's
 * text field is capable of holding. If this has not been changed
 * by <code>setTextLimit()</code>, it will be the constant
 * <code>Combo.LIMIT</code>.
 * 
 * @return the text limit
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getTextLimit () {
	checkWidget ();
	return text.getTextLimit ();
}

/**
 * Gets the number of items that are visible in the drop
 * down portion of the receiver's list.
 *
 * @return the number of items that are visible
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @since 3.0
 */
public int getVisibleItemCount () {
	checkWidget ();
	return visibleItemCount;
}

void handleFocus (int type) {
	if (isDisposed ()) return;
	switch (type) {
		case SWT.FocusIn: {
			if (hasFocus) return;
//			if (getEditable ()) text.selectAll ();
			if (getEditable ()) textSelectAll();			
			hasFocus = true;
//			Shell shell = getShell ();
//			shell.removeListener (SWT.Deactivate, listener);
//			shell.addListener (SWT.Deactivate, listener);
//			Display display = getDisplay ();
//			display.removeFilter (SWT.FocusIn, filter);
//			display.addFilter (SWT.FocusIn, filter);
			Event e = new Event ();
			notifyListeners (SWT.FocusIn, e);
			break;
		}
		case SWT.FocusOut: {
			if (!hasFocus) return;
			Control focusControl = getDisplay ().getFocusControl ();
			if (focusControl == arrow || focusControl == table || focusControl == text) return;
			hasFocus = false;
//			Shell shell = getShell ();
//			shell.removeListener(SWT.Deactivate, listener);
//			Display display = getDisplay ();
//			display.removeFilter (SWT.FocusIn, filter);
			Event e = new Event ();
			notifyListeners (SWT.FocusOut, e);
			break;
		}
	}
}

/**
 * Searches the receiver's table starting at the first item
 * (index 0) until an item is found that is equal to the 
 * argument, and returns the index of that item. If no item
 * is found, returns -1.
 *
 * @param item the search item
 * @return the index of the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int indexOf (TableItem item) {
	checkWidget ();
	if (item == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	return table.indexOf (item);
}

/**
 * Searches the receiver's table starting at the given, 
 * zero-relative index until an item is found that is equal
 * to the argument, and returns the index of that item. If
 * no item is found or the starting index is out of range,
 * returns -1.
 *
 * @param column the search column
 * @return the index of the column
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int indexOf (TableColumn column) {
	checkWidget ();
	if (column == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	return table.indexOf (column);
}

void initAccessible() {
	AccessibleAdapter accessibleAdapter = new AccessibleAdapter () {
		public void getName (AccessibleEvent e) {
			String name = null;
			Label label = getAssociatedLabel ();
			if (label != null) {
				name = stripMnemonic (label.getText());
			}
			e.result = name;
		}
		public void getKeyboardShortcut(AccessibleEvent e) {
			String shortcut = null;
			Label label = getAssociatedLabel ();
			if (label != null) {
				String text = label.getText ();
				if (text != null) {
					char mnemonic = getMnemonic (text);
					if (mnemonic != '\0') {
						shortcut = "Alt+"+mnemonic; //$NON-NLS-1$
					}
				}
			}
			e.result = shortcut;
		}
		public void getHelp (AccessibleEvent e) {
			e.result = getToolTipText ();
		}
	};
	getAccessible ().addAccessibleListener (accessibleAdapter);
	text.getAccessible ().addAccessibleListener (accessibleAdapter);
	table.getAccessible ().addAccessibleListener (accessibleAdapter);
	
	arrow.getAccessible ().addAccessibleListener (new AccessibleAdapter() {
		public void getName (AccessibleEvent e) {
			e.result = isDropped () ? SWT.getMessage ("SWT_Close") : SWT.getMessage ("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		public void getKeyboardShortcut (AccessibleEvent e) {
			e.result = "Alt+Down Arrow"; //$NON-NLS-1$
		}
		public void getHelp (AccessibleEvent e) {
			e.result = getToolTipText ();
		}
	});

	getAccessible().addAccessibleTextListener (new AccessibleTextAdapter() {
		public void getCaretOffset (AccessibleTextEvent e) {
			e.offset = text.getCaretPosition ();
		}
	});
	
	getAccessible().addAccessibleControlListener (new AccessibleControlAdapter() {
		public void getChildAtPoint (AccessibleControlEvent e) {
			Point testPoint = toControl (e.x, e.y);
			if (getBounds ().contains (testPoint)) {
				e.childID = ACC.CHILDID_SELF;
			}
		}
		
		public void getLocation (AccessibleControlEvent e) {
			Rectangle location = getBounds ();
			Point pt = toDisplay (location.x, location.y);
			e.x = pt.x;
			e.y = pt.y;
			e.width = location.width;
			e.height = location.height;
		}
		
		public void getChildCount (AccessibleControlEvent e) {
			e.detail = 0;
		}
		
		public void getRole (AccessibleControlEvent e) {
			e.detail = ACC.ROLE_COMBOBOX;
		}
		
		public void getState (AccessibleControlEvent e) {
			e.detail = ACC.STATE_NORMAL;
		}

		public void getValue (AccessibleControlEvent e) {
			e.result = getText ();
		}
	});

	text.getAccessible ().addAccessibleControlListener (new AccessibleControlAdapter () {
		public void getRole (AccessibleControlEvent e) {
			e.detail = text.getEditable () ? ACC.ROLE_TEXT : ACC.ROLE_LABEL;
		}
	});

	arrow.getAccessible ().addAccessibleControlListener (new AccessibleControlAdapter() {
		public void getDefaultAction (AccessibleControlEvent e) {
			e.result = isDropped () ? SWT.getMessage ("SWT_Close") : SWT.getMessage ("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	});
}

boolean isDropped () {
	return popup.getVisible ();
}

public boolean isFocusControl () {
	checkWidget();
	if (text.isFocusControl () || arrow.isFocusControl () || table.isFocusControl () || popup.isFocusControl ()) {
		return true;
	} 
	return super.isFocusControl ();
}

void internalLayout (boolean changed) {
//	if (isDropped ()) dropDown (false); why was this here? (Marius)
	Rectangle rect =  getClientArea ();
	int width = rect.width;
	int height = rect.height;
	Point arrowSize = arrow.computeSize (SWT.DEFAULT, height, changed);

	Image img = imageLabel.getImage();
	int imageWidth = img == null ? 0 : img.getBounds().width;
	int imageHeight = img == null ? 0 : img.getBounds().height;

	int imageTextSpace = imageWidth == 0 ? 0 : imageTextSpaceConst;

	imageLabel.setBounds(0, (height - imageHeight) / 2, imageWidth, imageHeight);
	text.setBounds (imageWidth + imageTextSpace, 0, width - arrowSize.x - imageWidth - imageTextSpace, height);
	arrow.setBounds (width - arrowSize.x, 0, arrowSize.x, arrowSize.y);
}

private static int imageTextSpaceConst = 4;

void listEvent (Event event) {
	switch (event.type) {
		case SWT.Dispose:
			if (getShell () != popup.getParent ()) {
				TableItem[] items = table.getItems ();
				int selectionIndex = table.getSelectionIndex ();
				popup = null;
				table = null;
				createPopup (items, selectionIndex);
			}
			break;
		case SWT.FocusIn: {
			handleFocus (SWT.FocusIn);
			break;
		}
		case SWT.MouseUp: {
			if (event.button != 1) return;
			dropDown (false);
			break;
		}
		case SWT.Selection: {
			int index = table.getSelectionIndex ();
			if (index == -1) return;
			TableItem tableItem = table.getItem(index);
			int oldImgWidth = imageLabel.getImage() == null ? 0 : imageLabel.getImage().getBounds().width;
			int newImgWidth = tableItem.getImage() == null ? 0 : tableItem.getImage().getBounds().width;
			imageLabel.setImage(tableItem.getImage());
			text.setText (tableItem.getText());
//			text.selectAll ();
			textSelectAll();			
			table.setSelection (index);
			Event e = new Event ();
			e.time = event.time;
			e.stateMask = event.stateMask;
			e.doit = event.doit;
			notifyListeners (SWT.Selection, e);
			event.doit = e.doit;

			if (oldImgWidth != newImgWidth)
				layout(true, true);

			break;
		}
		case SWT.Traverse: {
			switch (event.detail) {
				case SWT.TRAVERSE_RETURN:
				case SWT.TRAVERSE_ESCAPE:
				case SWT.TRAVERSE_ARROW_PREVIOUS:
				case SWT.TRAVERSE_ARROW_NEXT:
					event.doit = false;
					break;
			}
			Event e = new Event ();
			e.time = event.time;
			e.detail = event.detail;
			e.doit = event.doit;
			e.character = event.character;
			e.keyCode = event.keyCode;
			notifyListeners (SWT.Traverse, e);
			event.doit = e.doit;
			event.detail = e.detail;
			break;
		}
		case SWT.KeyUp: {		
			Event e = new Event ();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners (SWT.KeyUp, e);
			break;
		}
		case SWT.KeyDown: {
			if (event.character == SWT.ESC) { 
				// Escape key cancels popup list
				dropDown (false);
			}
			if ((event.stateMask & SWT.ALT) != 0 && (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN)) {
				dropDown (false);
			}
			if (event.character == SWT.CR) {
				// Enter causes default selection
				dropDown (false);
				Event e = new Event ();
				e.time = event.time;
				e.stateMask = event.stateMask;
				notifyListeners (SWT.DefaultSelection, e);
			}
			// At this point the widget may have been disposed.
			// If so, do not continue.
			if (isDisposed ()) break;
			Event e = new Event();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners(SWT.KeyDown, e);
			break;
			
		}
	}
}

/**
 * This boolean's purpose is to tell whether  the last event for the {@link #popup}-Shell was of type
 * {@link SWT#Deactivate}. It is used in the method {@link #arrowEvent(Event)} to avoid a problem
 * ocurring when the user tries to close the popup by clicking on the arrow button, that renders the
 * popup uncloseable. The cause for this is that first the shell's Deactivate event closes the drop
 * down and then the arrow button receives the Selection event which causes the popup to be opened
 * again. The workaround is now to skip the opening of the drop down in {@link #arrowEvent(Event)}
 * if <code>shellJustDeactivated == true</code>.
 */
boolean shellJustDeactivated = false;

void popupEvent(Event event) {
	shellJustDeactivated = false;
	switch (event.type) {
		case SWT.Paint:
			// draw black rectangle around list
			Rectangle listRect = table.getBounds();
			Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
			event.gc.setForeground(black);
			event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height + 1);
			break;
		case SWT.Close:
			event.doit = false;
			dropDown (false);
			break;
		case SWT.Deactivate:
			dropDown (false);
			shellJustDeactivated = true;
			break;
	}
}

public void redraw () {
	super.redraw();
	text.redraw();
	arrow.redraw();
	if (popup.isVisible()) table.redraw();
}

public void redraw (int x, int y, int width, int height, boolean all) {
	super.redraw(x, y, width, height, true);
}

/**
 * Removes the item from the receiver's table at the given
 * zero-relative index.
 *
 * @param index the index for the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void remove (int index) {
	checkWidget();
	table.remove (index);
}

/**
 * Removes the items from the receiver's table which are
 * between the given zero-relative start and end 
 * indices (inclusive).
 *
 * @param start the start of the range
 * @param end the end of the range
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if either the start or end are not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void remove (int start, int end) {
	checkWidget();
	table.remove (start, end);
}

/**
 * Searches the receiver's table starting at the first item
 * until an item is found that text is equal to the argument, 
 * and removes that item from the list.
 *
 * @param string the item to remove
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the string is not found in the list</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void remove (String string) {
	checkWidget();
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	table.remove (getTableItemIndex(string));
}

protected TableItem getTableItem(String text) 
{
	int itemCount = table.getItemCount();
	for (int i=0; i<itemCount-1; i++) 
	{
		TableItem item = table.getItem(i);
		if (item.getText().equals(text))
			return item;
	}
	return null;	
}

protected int getTableItemIndex(String text) 
{
	int itemCount = table.getItemCount();
	for (int i=0; i<itemCount-1; i++) 
	{
		TableItem item = table.getItem(i);
		if (item.getText().equals(text))
			return i;
	}
	return -1;	
}

/**
 * Removes all of the items from the receiver's table and clear the
 * contents of receiver's text field.
 * <p>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void removeAll () {
	checkWidget();
	imageLabel.setImage(null);
	text.setText (""); //$NON-NLS-1$
	table.removeAll ();
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the receiver's text is modified.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see ModifyListener
 * @see #addModifyListener
 */
public void removeModifyListener (ModifyListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	removeListener(SWT.Modify, listener);	
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the receiver's selection changes.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SelectionListener
 * @see #addSelectionListener
 */
public void removeSelectionListener (SelectionListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	removeListener(SWT.Selection, listener);
	removeListener(SWT.DefaultSelection,listener);	
}

/**
 * Selects the item at the given zero-relative index in the receiver's 
 * table.  If the item at the index was already selected, it remains
 * selected. Indices that are out of range are ignored.
 *
 * @param index the index of the item to select
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void select (int index) {
	checkWidget();
	if (index == -1) {
		imageLabel.setImage(null);
		table.deselectAll ();
		text.setText (""); //$NON-NLS-1$
		return;
	}
	if (0 <= index && index < table.getItemCount()) {
		if (index != getSelectionIndex()) {
			TableItem tableItem = table.getItem (index);

			int oldImgWidth = imageLabel.getImage() == null ? 0 : imageLabel.getImage().getBounds().width;
			int newImgWidth = tableItem.getImage() == null ? 0 : tableItem.getImage().getBounds().width;
			imageLabel.setImage(tableItem.getImage());

			text.setText (tableItem.getText());
//			text.selectAll ();
			textSelectAll();
			table.select (index);
			table.showSelection ();

			if (oldImgWidth != newImgWidth)
				layout(true, true);
		}
	}
}

public void setBackground (Color color) {
	super.setBackground(color);
	background = color;
	if (text != null) text.setBackground(color);
	if (table != null) table.setBackground(color);
	if (arrow != null) arrow.setBackground(color);
}

/**
 * Sets the editable state.
 *
 * @param editable the new editable state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @since 3.0
 */
public void setEditable (boolean editable) {
	checkWidget ();
	text.setEditable(editable);
}

public void setEnabled (boolean enabled) {
//	super.setEnabled(enabled);
//	if (popup != null) popup.setVisible (false);
	if (text != null) text.setEnabled(enabled);
	if (arrow != null) arrow.setEnabled(enabled);
}
public boolean setFocus () {
	checkWidget();
	if (getEditable())
		return text.setFocus ();
	
	return false;
}

public void setFont (Font font) {
	super.setFont (font);
	this.font = font;
	text.setFont (font);
	table.setFont (font);
	internalLayout (true);
}

public void setForeground (Color color) {
	super.setForeground(color);
	foreground = color;
	if (text != null) text.setForeground(color);
	if (table != null) table.setForeground(color);
	if (arrow != null) arrow.setForeground(color);
}

/**
 * Sets the text and the image of the item in the receiver's table at the given
 * zero-relative index. This is equivalent
 * to <code>remove</code>'ing the old item at the index, and then
 * <code>add</code>'ing the new item at that index.
 *
 * @param index the index for the item
 * @param image the image for the item
 * @param string the new text for the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setItem (int index, Image image, String text) {
	checkWidget();
	remove(index);
	add(image, text, index);
}

/**
 * Sets the layout which is associated with the receiver to be
 * the argument which may be null.
 * <p>
 * Note : No Layout can be set on this Control because it already
 * manages the size and position of its children.
 * </p>
 *
 * @param layout the receiver's new layout or null
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
// Don't allow to set Layout, since own layout is done by internalLayout(boolean)
//public void setLayout (Layout layout) {
//	checkWidget ();
//	return;
//}
//

/**
 * Sets the selection in the receiver's text field to the
 * range specified by the argument whose x coordinate is the
 * start of the selection and whose y coordinate is the end
 * of the selection. 
 *
 * @param selection a point representing the new selection start and end
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSelection (Point selection) {
	checkWidget();
	if (selection == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	text.setSelection (selection.x, selection.y);
}

/**
 * Sets the contents of the receiver's text field to the
 * given string.
 * <p>
 * Note: The text field in a <code>Combo</code> is typically
 * only capable of displaying a single line of text. Thus,
 * setting the text to a string containing line breaks or
 * other special characters will probably cause it to 
 * display incorrectly.
 * </p>
 *
 * @param string the new text
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setText (String string) {
	checkWidget();
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
//	int index = table.indexOf (getTableItem(string));
	TableItem tableItem = getTableItem(string);
	int index = -1;
	if (tableItem != null)		
		index = table.indexOf (getTableItem(string));	
	if (index == -1) {
		table.deselectAll ();
		text.setText (string);
		return;
	}
	text.setText (string);
//	text.selectAll ();
	textSelectAll();	
	table.setSelection (index);
	table.showSelection ();
	internalLayout(false);
}

/**
 * Sets the maximum number of characters that the receiver's
 * text field is capable of holding to be the argument.
 *
 * @param limit new text limit
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_CANNOT_BE_ZERO - if the limit is zero</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setTextLimit (int limit) {
	checkWidget();
	text.setTextLimit (limit);
}

public void setToolTipText (String string) {
	checkWidget();
	super.setToolTipText(string);
	arrow.setToolTipText (string);
	text.setToolTipText (string);		
}

public void setVisible (boolean visible) {
	super.setVisible(visible);
	if (!visible) popup.setVisible(false);
}

/**
 * Sets the number of items that are visible in the drop
 * down portion of the receiver's table.
 *
 * @param count the new number of items to be visible
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @since 3.0
 */
public void setVisibleItemCount (int count) {
	checkWidget ();
	if (count < 0) return;
	visibleItemCount = count;
}

String stripMnemonic (String string) {
	int index = 0;
	int length = string.length ();
	do {
		while ((index < length) && (string.charAt (index) != '&')) index++;
		if (++index >= length) return string;
		if (string.charAt (index) != '&') {
			return string.substring(0, index-1) + string.substring(index, length);
		}
		index++;
	} while (index < length);
 	return string;
}

void textEvent (Event event) {
	switch (event.type) {
		case SWT.FocusIn: {
			handleFocus (SWT.FocusIn);
			break;
		}
		case SWT.KeyDown: {
			if (event.character == SWT.CR) {
				dropDown (false);
				Event e = new Event ();
				e.time = event.time;
				e.stateMask = event.stateMask;
				notifyListeners (SWT.DefaultSelection, e);
			}
			//At this point the widget may have been disposed.
			// If so, do not continue.
			if (isDisposed ()) break;
			
			if (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN) {
				event.doit = false;
				if ((event.stateMask & SWT.ALT) != 0) {
					boolean dropped = isDropped ();
//					text.selectAll ();
					textSelectAll();					
					if (!dropped) setFocus ();
					dropDown (!dropped);
					break;
				}

				int oldIndex = getSelectionIndex ();
				if (event.keyCode == SWT.ARROW_UP) {
					select (Math.max (oldIndex - 1, 0));
				} else {
					select (Math.min (oldIndex + 1, getItemCount () - 1));
				}
				if (oldIndex != getSelectionIndex ()) {
					Event e = new Event();
					e.time = event.time;
					e.stateMask = event.stateMask;
					notifyListeners (SWT.Selection, e);
				}
				//At this point the widget may have been disposed.
				// If so, do not continue.
				if (isDisposed ()) break;
			}
			
			// Further work : Need to add support for incremental search in 
			// pop up list as characters typed in text widget
						
			Event e = new Event ();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners (SWT.KeyDown, e);
			break;
		}
		case SWT.KeyUp: {
			Event e = new Event ();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners (SWT.KeyUp, e);
			break;
		}
		case SWT.Modify: {
			table.deselectAll ();
			Event e = new Event ();
			e.time = event.time;
			notifyListeners (SWT.Modify, e);
			break;
		}
		case SWT.MouseDown: {
			if (event.button != 1) return;
			if (text.getEditable ()) return;
			boolean dropped = isDropped ();
//			text.selectAll ();
			textSelectAll();			
			if (!dropped) setFocus ();
			dropDown (!dropped);
			break;
		}
		case SWT.MouseUp: {
			if (event.button != 1) return;
			if (text.getEditable ()) return;
//			text.selectAll ();
			textSelectAll();			
			break;
		}
		case SWT.Traverse: {		
			switch (event.detail) {
				case SWT.TRAVERSE_RETURN:
				case SWT.TRAVERSE_ARROW_PREVIOUS:
				case SWT.TRAVERSE_ARROW_NEXT:
					// The enter causes default selection and
					// the arrow keys are used to manipulate the list contents so
					// do not use them for traversal.
					event.doit = false;
					break;
			}
			
			Event e = new Event ();
			e.time = event.time;
			e.detail = event.detail;
			e.doit = event.doit;
			e.character = event.character;
			e.keyCode = event.keyCode;
			notifyListeners (SWT.Traverse, e);
			event.doit = e.doit;
			event.detail = e.detail;
			break;
		}
	}
}
}

