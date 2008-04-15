/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.eclipse.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
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

/**
 * Defines a Dialog with a "twistie" for toggling the expansion-state of a Composite.
 * This class is ment to be subclassed. You have to
 * define a constructor and override {@link #createStaticArea(Composite)} as well as
 * {@link #createExpandableArea(Composite)} to have your own Composites inside the Dialog.
 * <p>
 * Here is an example:
 * <pre>
 * public class MyExpandableDialog extends ExpandableAreaDialog{
 *   public MyExpandableDialog(Shell parent)
 *   {
 *     super(parent);
 *		 setDialogTitle("My Dialog's Title");
 *		 setExpandText("Expand Me");
 *   }
 *
 *   protected Composite createStaticArea(Composite parent)
 *   {
 *     Composite staticArea = super.createStaticArea(parent);
 *     new Label(staticArea, SWT.NONE).setText("The static area");
 *     return staticArea;
 *   }
 *			
 *   protected Composite createExpandableArea(Composite parent)
 *   {
 *     Composite expandableArea = super.createExpandableArea(parent);
 *     new Label(expandableArea, SWT.NONE).setText("The expandable area");
 *     return expandableArea;
 *   }
 * }
 * </pre>
 * </p>
 * <p>
 * You can still override methods from {@link org.eclipse.jface.dialogs.Dialog} in order to provide
 * your own ButtonBar. Please do not override createDialogArea or at least return super(parent).
 * </p>
 * <p>
 * If you provide an implementation of {@link #getDialogBoundsSettings()}, the expansion state
 * of the twistie will be saved in the dialog settings.
 * </p>
 * 
 * @author Alexander Bieber
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @see org.eclipse.ui.forms.widgets.ExpandableComposite
 * @see org.eclipse.jface.dialogs.Dialog
 */
public abstract class ExpandableAreaDialog extends Dialog 
{
	/**
	 * The dialog settings key name for the expansion state of the twistie.
	 */
	private static final String DIALOG_TWISTIE_EXPANSION_STATE = "DIALOG_TWISTIE_EXPANSION_STATE"; //$NON-NLS-1$
	
	private ExpandableAreaComp dialogAreaComp = null;
	private Composite staticArea = null;
	private Composite expandableArea = null;
	
	/**
	 * Create a new ExpandableAreaDialog instance.
	 * @param parent The parent shell
	 */
	public ExpandableAreaDialog(Shell parent)
	{
		super(parent);
	}
	
	private String expandText = ""; //$NON-NLS-1$
	
	/**
	 * @return the expandText.
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
	
	/** Do not override this method.
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		dialogAreaComp = new ExpandableAreaComp();
		return dialogAreaComp.createComposite(parent, this, expandText);
	}
	
	/**
	 * Override this method to return the Composite you want to be visible in the
	 * upper area of the Dialog.
	 * @param parent Add your Composite as child of this
	 */
	protected Composite createStaticArea(Composite parent){
		// create a composite with no margins and standard spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		return composite;
	}

	/**
	 * Override this method to return the Composite you want to be the client of the
	 * ExpandableComposite.
	 * @param parent Add your Composite as child of this
	 */
	protected Composite createExpandableArea(Composite parent){
		// create a composite with no margins and standard spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		return composite;
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
	@Override
	public void create() {
		super.create();
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
			ExpandableComposite expandableComposite = new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);
			expandableComposite.setText(expandText);
			IDialogSettings settings = getDialogBoundsSettings();
			if(settings != null)
				expandableComposite.setExpanded(settings.getBoolean(DIALOG_TWISTIE_EXPANSION_STATE));
			expandableComposite.addExpansionListener(new ExpansionAdapter() {
				/* (non-Javadoc)
				 * @see org.eclipse.ui.forms.events.ExpansionAdapter#expansionStateChanged(org.eclipse.ui.forms.events.ExpansionEvent)
				 */
				@Override
				public void expansionStateChanged(ExpansionEvent e) {
					IDialogSettings settings = getDialogBoundsSettings();
					if(settings != null)
						settings.put(DIALOG_TWISTIE_EXPANSION_STATE, e.getState());
					getComp().layout(true);
					getDialog().doRelayout();
				}
			});
			GridData gd = new GridData(GridData.FILL_BOTH);
			expandableComposite.setLayoutData(gd);

			GridLayout ecLayout = new GridLayout();
			ecLayout.verticalSpacing = 5;
			ecLayout.horizontalSpacing= 5;
			expandableComposite.setLayout(ecLayout);
			
			// dummyComp wraps the expandable Comp one more time.
			// Workaround, otherwise half of first row in expandableArea
			// visible even when collapsed
			Composite dummyComp = new Composite(expandableComposite,SWT.NONE);
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
				expandableComposite.setClient(dummyComp);
			}

			return expComp;
		}
	}
}
