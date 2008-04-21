package org.nightlabs.eclipse.ui.dialog;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

/**
 * A helper class for resizable dialogs. An instance of this class stores and 
 * retrieves dialog sizes and locations from a {@link IDialogSettings} object,
 * just as {@link Dialog} does. Additionally, it is possible to provide a
 * resource bundle with initial size and location information. If no dialog
 * settings exist, this information is used to determine the initial size and
 * location of the dialog.
 * <p>
 * Dialog positions are relative to the parent shell if it exists or to the 
 * monitor the dialog appears on. See {@link Dialog}.
 * </p>
 * <p>
 * Using this class as recommended, it renders useless to override the 
 * {@link Dialog#getDialogBoundsSettings()} method. This method should return
 * <code>null</code>.
 * </p>
 * <p>
 * The bundle entries must be in the form:
 * <pre>
 * &lt;dialog identifier/class name&gt;.DialogBounds.x=100
 * &lt;dialog identifier/class name&gt;.DialogBounds.y=150
 * &lt;dialog identifier/class name&gt;.DialogBounds.width=700
 * &lt;dialog identifier/class name&gt;.DialogBounds.height=500
 * </pre>
 * </p>
 * <p>
 * This class should be used within a dialog implementation as follows:
 * <pre>
 * public class MyDialog extends TrayDialog {
 *   private ResizableDialogSupport resizableDialogSupport;
 * 	
 *   public ResizableTrayDialog(Shell shell) {
 *     super(shell);
 *     setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
 *     this.resizableDialogSupport = new ResizableDialogSupport(this, Messages.getBundle());
 *   }
 *   
 *   protected Point getInitialSize() {
 *     Point size = resizableDialogSupport.getInitialSize();
 *     if(size != null)
 *       return size;
 *     return super.getInitialSize();
 *   }
 *   
 *   protected Point getInitialLocation(Point initialSize)
 *   {
 *     Point loc = resizableDialogSupport.getInitialLocation();
 *     if(loc != null)
 *   	   return loc;
 *     return super.getInitialLocation(initialSize);
 *   }
 *   
 *   public boolean close()
 *   {
 *     resizableDialogSupport.saveBounds();
 *     return super.close();
 *   }
 * }
 * </pre>
 * </p>
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ResizableDialogSupport
{
	// dialog store id constants
	private static final String DIALOG_BOUNDS_KEY= "DialogBounds"; //$NON-NLS-1$
	private static final String X= "x"; //$NON-NLS-1$
	private static final String Y= "y"; //$NON-NLS-1$
	private static final String WIDTH= "width"; //$NON-NLS-1$
	private static final String HEIGHT= "height"; //$NON-NLS-1$
	
	/**
	 * The parent dialog. May be null.
	 */
	private Dialog dialog;

	/**
	 * Always a valid non-null identifier value.
	 */
	private String dialogIdentifier;

	/**
	 * The resource bundle.
	 */
	protected ResourceBundle fBundle;
	
	/**
	 * The dialog settings. 
	 */
	private IDialogSettings fSettings;
	
	/**
	 * Track bounds changes.
	 */
	private boolean boundsChanged;
	
	/**
	 * Create a new ResizableDialogSupport instance for the given dialog.
	 * @param dialog The corresponding dialog. May be <code>null</code>.
	 * @param dialogIdentifier The dialog identifier. If this is <code>null</code>
	 * 		and the <code>dialog</code> argument is not <code>null</code>, the dialogs
	 * 		class name is used.
	 * @param dialogSettings The dialog settings. An instance as given by {@link org.eclipse.ui.plugin.AbstractUIPlugin#getDialogSettings()}.
	 * 		If this is <code>null</code>, the dialog setting provided by the {@link DialogPlugin} are used.
	 * @param bundle The resource bundle. May be <code>null</code>.
	 */
	public ResizableDialogSupport(Dialog dialog, String dialogIdentifier, IDialogSettings dialogSettings, ResourceBundle bundle)
	{
		this.dialog = dialog;
		this.fBundle= bundle;
		if(dialogSettings == null)
			this.fSettings = DialogPlugin.getDefault().getDialogSettings();
		else
			this.fSettings= dialogSettings;
		if(dialogIdentifier == null || dialogIdentifier.isEmpty()) {
			if(dialog != null)
				this.dialogIdentifier = dialog.getClass().getName();
			else
				this.dialogIdentifier = getClass().getName();
		} else 
			this.dialogIdentifier = dialogIdentifier;
	}

	/**
	 * Create a new ResizableDialogSupport instance for the given dialog.
	 * This is a convenience constructor for the most common use case in
	 * a dialog implementation.
	 * @param dialog The corresponding dialog.
	 * @param bundle The resource bundle. May be <code>null</code>.
	 */
	public ResizableDialogSupport(Dialog dialog, ResourceBundle bundle)
	{
		this(dialog, null, null, bundle);
	}
	
	private Point getInitialSizeFromBundle()
	{
		if(fBundle != null) {
			int width = getBundleInt(dialogIdentifier+"."+DIALOG_BOUNDS_KEY+"."+WIDTH, 0);
			int height= getBundleInt(dialogIdentifier+"."+DIALOG_BOUNDS_KEY+"."+HEIGHT, 0);
			if(width > 0 && height > 0)
				return new Point(width, height);
		}
		return null;
	}

	private Point getInitialSizeFromDialogSettings()
	{
		if(fSettings != null) {
			IDialogSettings bounds= fSettings.getSection(dialogIdentifier+"."+DIALOG_BOUNDS_KEY); 
			if (bounds != null) {
				int width = 0;
				int height = 0;
				try {
					width= bounds.getInt(WIDTH);
					height= bounds.getInt(HEIGHT);
					if(width > 0 && height > 0)
						return new Point(width, height);
				} catch (NumberFormatException e) {
				}
			}
		}
		return null;
	}

	private Point getInitialLocationFromBundle()
	{
		if(fBundle != null) {
			int x = getBundleInt(dialogIdentifier+"."+DIALOG_BOUNDS_KEY+"."+X, 0);
			int y= getBundleInt(dialogIdentifier+"."+DIALOG_BOUNDS_KEY+"."+Y, 0);
			if(x > 0 && y > 0)
				return new Point(x, y);
		}
		return null;
	}

	private Point getInitialLocationFromDialogSettings()
	{
		if(fSettings != null) {
			IDialogSettings bounds= fSettings.getSection(dialogIdentifier+"."+DIALOG_BOUNDS_KEY); 
			if (bounds != null) {
				try {
					int x= bounds.getInt(X);
					int y= bounds.getInt(Y);
					if(x > 0 && y > 0)
						return new Point(x, y);
				} catch (NumberFormatException e) {
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the initial size for the corresponding dialog.
	 * This method returns <code>null</code> if there is nothing known
	 * about the dialogs size. In this case, a dialog implementation
	 * should use {@link Dialog#getInitialSize()}.
	 * @return The dialogs initial size or <code>null</code> if no
	 * 		initial size is known
	 */
	public Point getInitialSize()
	{
		registerControlListener();
		Point size = getInitialSizeFromDialogSettings();
		if(size == null)
			size = getInitialSizeFromBundle();
		return size;
	}

	/**
	 * Get the initial location for the corresponding dialog.
	 * This method returns <code>null</code> if there is nothing known
	 * about the dialogs size. In this case, a dialog implementation
	 * should use {@link Dialog#getInitialLocation(Point initialSize)}.
	 * @return The dialogs initial location or <code>null</code> if no
	 * 		initial location is known
	 */
	public Point getInitialLocation()
	{
		Point loc = getInitialLocationFromDialogSettings();
		if(loc == null)
			loc = getInitialLocationFromBundle();
		return loc;
	}
	
	private void registerControlListener()
	{
		if(dialog != null) {
			final Shell s= dialog.getShell();
			if (s != null) {
				s.addControlListener(
						new ControlListener() {
							public void controlMoved(ControlEvent e) 
							{
								boundsChanged = true;
								removeControlListener();
							}
							public void controlResized(ControlEvent e) 
							{
								boundsChanged = true;
								removeControlListener();
							}
							private void removeControlListener()
							{
								Shell sh = dialog.getShell();
								if(sh != null)
									sh.removeControlListener(this);
							}
						}
				);
			}
		}		
	}
		
	private int getBundleInt(String key, int defaultValue)
	{
		try {
			if(fBundle == null)
				return defaultValue;
			String value = fBundle.getString(key);
			if(value == null)
				return defaultValue;
			return Integer.parseInt(value);
		} catch(NumberFormatException e) {
		} catch(MissingResourceException e) {
		}
		return defaultValue;
	}

	/**
	 * Save the current dialog bounds if they have changed.
	 * To use this method, the corresponding dialog must be set in the
	 * constructor.
	 */
	public void saveBounds()
	{
		if(dialog == null)
			throw new IllegalStateException("Corresponding dialog is not set when trying to save dialog bounds.");
		Shell sh = dialog.getShell();
		if(sh != null && !sh.isDisposed() && isBoundsChanged())
			saveBounds(sh.getBounds());
	}

	/**
	 * Save new bounds for the corresponding dialog.
	 * @param bounds The bounds to save
	 */
	public void saveBounds(Rectangle bounds) 
	{
		if(fSettings != null) {
			IDialogSettings dialogBounds= fSettings.getSection(dialogIdentifier+"."+DIALOG_BOUNDS_KEY);
			if (dialogBounds == null) {
				dialogBounds= new DialogSettings(dialogIdentifier+"."+DIALOG_BOUNDS_KEY);
				fSettings.addSection(dialogBounds);
			}
			dialogBounds.put(X, bounds.x);
			dialogBounds.put(Y, bounds.y);
			dialogBounds.put(WIDTH, bounds.width);
			dialogBounds.put(HEIGHT, bounds.height);
		}
	}


	/**
	 * Check whether the dialogs bounds have changed since the first call
	 * to {@link #getInitialLocation()}.
	 * @return <code>true</code> if the dialog bounds have changed - 
	 * 		<code>false</code> otherwise
	 */
	public boolean isBoundsChanged()
	{
		return boundsChanged;
	}
}
