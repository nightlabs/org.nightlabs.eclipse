package org.nightlabs.base.toolkit;

import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Marius Heinzmann [marius<at>NightLabs<dot>de]
 */
public interface IToolkit {
	
	/**
	 * Key for the {@link Control#setData(String, Object)} which should be used with {@link #TABLE_BORDER}
	 * or {@link #TEXT_BORDER} to enforce a border for a certain element.
	 */
	public static final String KEY_DRAW_BORDER = "IToolkit.drawBorder"; //$NON-NLS-1$
	// TODO the above key should use the fully-qualified classname like the line below! I don't change it, because I have other work to do and don't want to break anything.
//	public static final String KEY_DRAW_BORDER = IToolkit.class.getName() + ".drawBorder"; //$NON-NLS-1$

	public static final String TABLE_BORDER = "tableBorder"; //$NON-NLS-1$
	public static final String TEXT_BORDER = "textBorder"; //$NON-NLS-1$


	/**
	 * Adapt the composite to fit the look of the Toolkit.
	 * @param composite the composite to adapt.
	 */
	public void adapt(Composite composite);
	
	/**
	 * Adapts a control to be used in a context that is associated with this
	 * toolkit. This involves adjusting colors and optionally adding handlers to
	 * ensure focus tracking and keyboard management.
	 * 
	 * @param control
	 *            a control to adapt
	 * @param trackFocus
	 *            if <code>true</code>, form will be scrolled horizontally
	 *            and/or vertically if needed to ensure that the control is
	 *            visible when it gains focus. Set it to <code>false</code> if
	 *            the control is not capable of gaining focus.
	 * @param trackKeyboard
	 *            if <code>true</code>, the control that is capable of
	 *            gaining focus will be tracked for certain keys that are
	 *            important to the underlying form (for example, PageUp,
	 *            PageDown, ScrollUp, ScrollDown etc.). Set it to
	 *            <code>false</code> if the control is not capable of gaining
	 *            focus or these particular key event are already used by the
	 *            control.
	 */
	public void adapt(Control control, boolean trackFocus, boolean trackKeyboard);
	
	/**
	 * Checks the given Control and if necessary adds a {@link PaintListener} to the parent, so that the parent
	 * will paint the border for the element.
	 *  
	 * @param element the element to check if a border painter is needed. 
	 * @return <code>true</code> if a {@link PaintListener} has been added to the parent of the given 
	 * 					control, <code>false</code> otherwise.
	 */
	public boolean checkForBorders(Control element);
	
	/**
	 * Returns the bitmask for the border style. The Toolkit can therefore exchange the standard borders,
	 * if the elements use this method to determine the border style.
	 *  
	 * @return the bitmask for the border style.
	 */
	public int getBorderStyle();
	
	/**
	 * Creates a scrolled form widget in the provided parent. If you do not
	 * require scrolling because there is already a scrolled composite up the
	 * parent chain, use 'createForm' instead.
	 * 
	 * @param parent
	 *            the scrolled form parent
	 * @return the form that can scroll itself
	 * @see #createForm
	 */
	public ScrolledForm createScrolledForm(Composite parent);

	/**
	 * Creates a form widget in the provided parent. Note that this widget does
	 * not scroll its content, so make sure there is a scrolled composite up the
	 * parent chain. If you require scrolling, use 'createScrolledForm' instead.
	 * 
	 * @param parent
	 *            the form parent
	 * @return the form that does not scroll
	 * @see #createScrolledForm
	 */
	public Form createForm(Composite parent);

	/**
	 * Creates a section as a part of the form.
	 * 
	 * @param parent
	 *            the section parent
	 * @param sectionStyle
	 *            the section style
	 * @return the section widget
	 */
	public Section createSection(Composite parent, int sectionStyle); 

	/**
	 * Creates a rich text as a part of the form.
	 * 
	 * @param parent
	 *            the rich text parent
	 * @param trackFocus
	 *            if <code>true</code>, the toolkit will monitor focus
	 *            transfers to ensure that the hyperlink in focus is visible in
	 *            the form.
	 * @return the rich text widget
	 */
	public FormText createFormText(Composite parent, boolean trackFocus); 

	/**
	 * Creates a hyperlink as a part of the form. The hyperlink will be added to
	 * the hyperlink group that belongs to this toolkit.
	 * 
	 * @param parent
	 *            the hyperlink parent
	 * @param text
	 *            the text of the hyperlink
	 * @param style
	 *            the hyperlink style
	 * @return the hyperlink widget
	 */
	public Hyperlink createHyperlink(Composite parent, String text, int style); 

//	/**
//	* Creates an image hyperlink as a part of the form. The hyperlink will be
//	* added to the hyperlink group that belongs to this toolkit.
//	* 
//	* @param parent
//	*            the hyperlink parent
//	* @param style
//	*            the hyperlink style
//	* @return the image hyperlink widget
//	*/
//	public ImageHyperlink createImageHyperlink(Composite parent, int style); 
//
//	/**
//	 * Creates an expandable composite as a part of the form.
//	 * 
//	 * @param parent
//	 *            the expandable composite parent
//	 * @param expansionStyle
//	 *            the expandable composite style
//	 * @return the expandable composite widget
//	 */
//	public ExpandableComposite createExpandableComposite(Composite parent, int expansionStyle); 
//
//	/**
//	 * Creates a separator label as a part of the form.
//	 * 
//	 * @param parent
//	 *            the separator parent
//	 * @param style
//	 *            the separator style
//	 * @return the separator label
//	 */
//	public Label createSeparator(Composite parent, int style);
//
//	/**
//	 * Creates a table as a part of the form.
//	 * 
//	 * @param parent
//	 *            the table parent
//	 * @param style
//	 *            the table style
//	 * @return the table widget
//	 */
//	public Table createTable(Composite parent, int style);
//
//	/**
//	 * Creates a text as a part of the form.
//	 * 
//	 * @param parent
//	 *            the text parent
//	 * @param value
//	 *            the text initial value
//	 * @return the text widget
//	 */
//	public Text createText(Composite parent, String value);
//
//	/**
//	 * Creates a text as a part of the form.
//	 * 
//	 * @param parent
//	 *            the text parent
//	 * @param value
//	 *            the text initial value
//	 * @param style
//	 *            the text style
//	 * @return the text widget
//	 */
//	public Text createText(Composite parent, String value, int style);
//
//	/**
//	 * Creates a tree widget as a part of the form.
//	 * 
//	 * @param parent
//	 *            the tree parent
//	 * @param style
//	 *            the tree style
//	 * @return the tree widget
//	 */
//	public Tree createTree(Composite parent, int style);
//
//	/**
//	 * Creates a scrolled page book widget as a part of the form.
//	 * 
//	 * @param parent
//	 *            the page book parent
//	 * @param style
//	 *            the text style
//	 * @return the scrolled page book widget
//	 */
//	public ScrolledPageBook createPageBook(Composite parent, int style);

}
