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

package org.nightlabs.base.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.composite.ExpandableWrapperComposite;
import org.nightlabs.base.config.DialogCf;
import org.nightlabs.base.config.DialogCfMod;
import org.nightlabs.config.Config;

/**
 * Defines a Dialog with a "twistie" for toggling the expansion-state of a Composite.
 * This class is ment to be subclassed. You have to 
 * define a constructor and override {@link #createStaticArea(Composite)} as well as
 * {@link #createExpandableArea(Composite)} to have your own Composites inside the Dialog.
 * <br/>
 * <br/>
 * Here is a example: 
 * <pre>
 * public class MyExpandableDialog extends ExpandableAreaDialog{
 * 		public MyExpandableDialog(Shell parent)
 *		{
 *			super(parent);
 *			setDialogTitle("My Dialog's Title");
 *			setExpandText("Expand Me");
 *		}
 *
 *		protected Composite createStaticArea(Composite parent) 
 *		{
 *			return new Composite(parent,SWT.NONE);		
 *		}
 *
 *		protected Composite createExpandableArea(Composite parent) 
 *		{
 *			return new Composite(parent,SWT.NONE);		
 *		}
 * }
 * </pre>
 * You can still override methods from {@link org.eclipse.jface.dialogs.Dialog} in order to provide
 * your own ButtonBar. Please do not override createDialogArea or at least return super(parent).
 * 
 * @author Alexander Bieber
 * @see org.eclipse.ui.forms.widgets.ExpandableComposite
 * @see org.eclipse.jface.dialogs.Dialog
 *
 */
public class ExpandableAreaDialog extends Dialog {
	
	
	public ExpandableAreaDialog(Shell parent)
	{
		this(parent,"","",SWT.RESIZE); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public ExpandableAreaDialog(Shell parent, int style)
	{
		this(parent,"","",style); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public ExpandableAreaDialog(Shell parent, String title)
	{
		this(parent,title,"",SWT.RESIZE); //$NON-NLS-1$
	}

	public ExpandableAreaDialog(Shell parent, String title, String expandText)
	{
		this(parent,title,expandText,SWT.RESIZE);
	}
	
	
	public ExpandableAreaDialog(Shell parent, String title, String expandText, int style)
	{
		super(parent);
		setShellStyle(getShellStyle()|style);
		this.dialogTitle = title;
		this.expandText = expandText;
	}	
	
	private String dialogTitle = ""; //$NON-NLS-1$
	private String expandText = ""; //$NON-NLS-1$
	
	/**
	 * @return Returns the dialogTitle.
	 */
	public String getDialogTitle() {
		return dialogTitle;
	}
	/**
	 * @param dialogTitle The dialogTitle to set.
	 */
	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}
	/**
	 * @return Returns the expandText.
	 */
	public String getExpandText() {
		return expandText;
	}
	/**
	 * @param expandText The expandText to set.
	 */
	public void setExpandText(String expandText) {
		this.expandText = expandText;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(dialogTitle);
	}
	
	
	private ExpandableAreaComp dialogAreaComp = null;
	/** Do not override this method.
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		dialogAreaComp = new ExpandableAreaComp();		
		return dialogAreaComp.createComposite(parent,this,expandText);
	}
	
	
	
	private Composite staticArea = null;
	/**
	 * Override this method to return the Composite you want to be visible in the
	 * upper area of the Dialog.
	 * @param parent Add your Composite as child of this
	 */
	protected Composite createStaticArea(Composite parent){
		return new Composite(parent,SWT.NONE);
	}

	private Composite expandableArea = null;
	/**
	 * Override this method to return the Composite you want to be the client of the
	 * ExpandableComposite.
	 * @param parent Add your Composite as child of this
	 */
	protected Composite createExpandableArea(Composite parent){
		return new Composite(parent,SWT.NONE);
	}
	
	
	/**
	 * Returns the expandable area Composite
	 * @see #createExpandableArea(Composite)
	 */
	public Composite getExpandableArea() {
		return expandableArea;
	}
	/**
	 * Returns the static area Composite
	 * @see #createStaticArea(Composite)
	 */
	public Composite getStaticArea() {
		return staticArea;
	}

	/**
	 * Packs, layouts and resizes the Dialog according to
	 * its contents and {@link #getMaxWidth()} and
	 * {@link #getMaxHeight()}.
	 */
	public void doRelayout() {
		if (getShell() != null) {
			getShell().layout();
			getShell().pack();
			Point sizeAfterPack = getShell().getSize();
			Point sizeToSet = new Point(sizeAfterPack.x,sizeAfterPack.y);
			if (sizeAfterPack.x > getMaxWidth())
				sizeToSet.x = getMaxWidth();
			if (sizeAfterPack.y > getMaxHeight())
				sizeToSet.y = getMaxHeight();
			
			getShell().setSize(sizeToSet.x,sizeToSet.y);
			getShell().layout();
		}
	}
	
	/**
	 * Overrides {@link Dialog#create()} and adds a call to {@link #doRelayout()}
	 */
	public void create() {
		super.create();

		DialogCf cf = getDialogCfMod().getDialogCf(getDialogIdentifier());
		if (cf != null) {
			getShell().setLocation(cf.getX(), cf.getY());
			getShell().setSize(cf.getWidth(), cf.getHeight());
		}

		doRelayout();
	}
	
	
	private int maxWidth = -1;
	private int maxHeight = -1;
	
	/**
	 * Returns the maximal Width for the dialog.
	 * Default value is the screens width.
	 */
	public int getMaxWidth() {
		if ( maxWidth > 0 ) {
			return maxWidth;
		}
		else
			return Display.getCurrent().getClientArea().width;
	}
	
	/**
	 * Returns the maximal Height for the dialog.
	 * Default value is the screens height.
	 */
	public int getMaxHeight() {
		if ( maxHeight > 0 ) {
			return maxHeight;
		}
		else
			return Display.getCurrent().getClientArea().height;
	}
	
	/**
	 * Sets the maximum size for the dialog.
	 * @param maxSize
	 */
	public void setMaxSize(Point maxSize) {
		setMaxWidth(maxSize.x);
		setMaxHeight(maxSize.y);
	}
	
	/**
	 * Sets the maximum width for the dialog.
	 * maxHeight is not affected by this method.
	 * @param width
	 */
	public void setMaxWidth(int width) {
		this.maxWidth = width;
	}
	
	/**
	 * Sets the maximum height for the dialog.
	 * maxWidth is not affected by this method.
	 * @param height
	 */
	public void setMaxHeight(int height) {
		this.maxHeight = height;
	}
	
	/**
	 * This class is internally used. 
	 * @author Alexander Bieber
	 */
	private class ExpandableAreaComp {
		
		private Composite expComp = null;
		private Composite getComp(){return expComp;}
		
		private ExpandableAreaDialog dialog = null;
		private ExpandableAreaDialog getDialog(){return dialog;}
		
		/**
		 * Calls {@link ExpandableAreaDialog#createStaticArea(Composite)}, creates an
		 * {@link ExpandableComposite} and adds the result of 
		 * {@link ExpandableAreaDialog#createExpandableArea(Composite)} to it.
		 * An anonymous {@link ExpansionAdapter} is added that handles 
		 * relayouting of the parent Dialog.
		 * @param parent
		 * @param dialog
		 * @param expandText
		 */
		public Composite createComposite(
				Composite parent, 
				ExpandableAreaDialog dialog, 
				String expandText
			)
		{		
			if (dialog == null)
				throw new IllegalArgumentException(this.getClass().getName()+"#createComposite: Parameter dialog can not be null."); //$NON-NLS-1$
			this.dialog = dialog;
			// create the Composite
			expComp = new Composite(parent,SWT.NONE);
			// LayoutData takes care of layouting within the dialog ... 
			GridData myData = new GridData(GridData.FILL_BOTH);
			expComp.setLayoutData(myData);
			
			// Use a TableWrapLayout for the parent
			GridLayout layout = new GridLayout();
			expComp.setLayout(layout);
			
			// create the static Composite
			staticArea = createStaticArea(expComp);
			if (staticArea != null) {
				// take care of its layoutData
				GridData gdStatic = new GridData(GridData.FILL_BOTH);
//				gdStatic.grabExcessHorizontalSpace = true;
//				gdStatic.horizontalAlignment=GridData.FILL_;
				staticArea.setLayoutData(gdStatic);
			}
					
			// the ExpandableComposite
			ExpandableWrapperComposite ec = new ExpandableWrapperComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE|ExpandableComposite.COMPACT);
			ec.removeExpansionListener();
			ec.setText(expandText);
			GridData gd = new GridData(GridData.FILL_BOTH);
			ec.setLayoutData(gd);
			// the ExpansionListener re-packs the dialog
			ec.addExpansionListener(new ExpansionAdapter() {
				public void expansionStateChanged(ExpansionEvent e) {
					getComp().layout(true);
					getDialog().doRelayout();
				}
			});

			GridLayout ecLayout = new GridLayout();
			ecLayout.verticalSpacing = 5;
			ecLayout.horizontalSpacing= 5;
			ec.setLayout(ecLayout);
			
			// dummyComp wraps the expandable Comp one more time.
			// Workaround, otherwise half of first row in expandableArea
			// visible even when collapsed
			Composite dummyComp = new Composite(ec,SWT.NONE);
			gd = new GridData(GridData.FILL_BOTH);				
			dummyComp.setLayoutData(gd);
			GridLayout gl = new GridLayout();
			dummyComp.setLayout(gl);	

			expandableArea = createExpandableArea(dummyComp);
			if (expandableArea != null) {
				// set the LayoutData
				gd = new GridData(GridData.FILL_BOTH);				
				expandableArea.setLayoutData(gd);
				// tell the parent to manage this as expandable client
				ec.setClient(dummyComp);
			}

			return expComp;
		}
		
	}

	protected DialogCfMod getDialogCfMod()
	{
		return (DialogCfMod) Config.sharedInstance().createConfigModule(DialogCfMod.class);
	}

	protected String getDialogIdentifier()
	{
		return this.getClass().getName();
	}

	@Override
	public boolean close()
	{
		getDialogCfMod().createDialogCf(
				getDialogIdentifier(),
				getShell().getLocation().x,
				getShell().getLocation().y,
				getShell().getSize().x,
				getShell().getSize().y);
		return super.close();
	}
}
