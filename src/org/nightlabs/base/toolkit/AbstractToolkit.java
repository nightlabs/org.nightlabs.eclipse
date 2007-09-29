/**
 * 
 */
package org.nightlabs.base.toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nightlabs.base.custom.XCombo;

/**
 * @author Marius Heinzmann [marius<at>NightLabs<dot>de]
 */
public abstract class AbstractToolkit extends FormToolkit
	implements IToolkit 
{

	protected class TextBorderPainter implements PaintListener {
		private Control drawBorders;
		
		public TextBorderPainter(Control child) {
			drawBorders = child;
		}
		
		public void paintControl(PaintEvent event) {
			Composite parent = (Composite) event.widget;
			if (drawBorders.getParent() != parent)
				throw new RuntimeException("This PaintListener is configured to draw a border for: "+drawBorders //$NON-NLS-1$
						+" , but is NOT a listener of the parent of that control!"); //$NON-NLS-1$
			
			if (!parent.isVisible() || !drawBorders.isVisible())
				return;

			if (drawBorders instanceof Hyperlink)
				return;


				GC gc = event.gc;
				paintTextBorder(drawBorders, gc, getColors().getBorderColor());
		}
		
	}
	
	protected class TableBorderPainter implements PaintListener {
		private Control drawBorders;
		
		public TableBorderPainter(Control child) {
			drawBorders = child;
		}

		public void paintControl(PaintEvent event) {
			Composite parent = (Composite) event.widget;
			if (drawBorders.getParent() != parent)
				throw new RuntimeException("This PaintListener is configured to draw a border for: "+drawBorders //$NON-NLS-1$
						+" , but is NOT a listener of the parent of that control!"); //$NON-NLS-1$
			
			if (!parent.isVisible() || !drawBorders.isVisible())
				return;

			if (drawBorders instanceof Hyperlink)
				return;


			paintTableBorder(drawBorders, event.gc, getColors().getBorderColor());
		}
		
	}

	/**
	 * @param display
	 */
	public AbstractToolkit(Display display) {
		super(display);
	}

	/**
	 * @param colors
	 */
	public AbstractToolkit(FormColors colors) {
		super(colors);
	}

	protected abstract void paintTextBorder(Control child, GC gc, Color color);
	
	protected abstract void paintTableBorder(Control child, GC gc, Color color);
	
	public boolean checkForBorders(Control element) {
		// skip own border painting if native borders are fine
		if ((getBorderStyle() & SWT.BORDER) != 0)
			return false;
		
		Composite parent = element.getParent();
		
		if (parent == null) // kann das passieren?
			return false;
		
		Object flag = element.getData(IToolkit.KEY_DRAW_BORDER);
		// The flags to force a border and to continue drawing borders for child elements.
		boolean textBorder = false;
		boolean tableBorder = false;
		if (flag != null) {
			if (flag.equals(Boolean.FALSE))
				return false;
			if (flag.equals(IToolkit.TABLE_BORDER))
				tableBorder = true;
			else if (flag.equals(IToolkit.TEXT_BORDER))
				textBorder = true;
		}
			
		boolean listenerAdded = false;
		if (textBorder || element instanceof Text || element instanceof CCombo) { // element instanceof Spinner ||
			parent.addPaintListener( new TextBorderPainter(element) );
//		 if textBorder is set -> return false -> continue drawing borders for children
			listenerAdded = ! textBorder; 
		}
		else if (tableBorder || element instanceof Table || element instanceof Tree 
							|| element instanceof Spinner || element instanceof List) 
		{ 
			parent.addPaintListener( new TableBorderPainter(element) );
//		 if tableBorder is set -> return false -> continue drawing borders for children
			listenerAdded = ! tableBorder;
		}
		else if (element instanceof XCombo) { // element instanceof CComboComposite || 
			parent.addPaintListener( new TableBorderPainter(element) );
			listenerAdded = true;
		}

		listenerAdded |= checkAdditionalTypesForBorders(element);
		
		if (listenerAdded) {
			checkPrerequisites(element);
		}
		
		return listenerAdded;
	}

	/**
	 * Everytime {@link #checkForBorders(Control)} adds a PaintListener to a Control, this method 
	 * is called so that subclasses can ensure all prerequisites are met for their changes in the 
	 * layout / style.
	 *  
	 * @param child the Control which is getting a border.
	 */
	protected abstract void checkPrerequisites(Control child);
	
	/**
	 * Here you can check for additional own Widgets and if for this widgets flat borders should be set
	 * then add a {@link PaintListener} to its parent, which does that. <br>
	 * There are already 2 preconfigured ones available {@link TextBorderPainter} and 
	 * {@link TableBorderPainter}.
	 * 
	 * @param control the control, which to check whether borders shall be drawn for it.
	 * @return <code>true</code> iff a {@link PaintListener} has been added to the parent of the Control
	 * 					drawing its borders.
	 */
	protected abstract boolean checkAdditionalTypesForBorders(Control control);
}
