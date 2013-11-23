package org.nightlabs.base.ui.property;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * Implementations of this interface can additionally to {@link IPropertyDescriptor}
 * indicate whether they're read-only and they have a clean resource-life-cycle.
 * The latter is essential when using {@link ILabelProvider}s (a label-provider should always
 * be disposed) with {@link Image}s (images <b>must</b> be disposed!).
 * <p>
 * <b>Important:</b> It's urgently recommended to extend {@link XPropertyDescriptor} instead of
 * directly implementing this interface!
 * </p>
 *
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public interface IXPropertyDescriptor extends IPropertyDescriptor
{
	/**
	 * Indicates whether the property is read-only.
	 *
	 * @return <code>true</code> if the property is read-only; <code>false</code> if the user can edit it.
	 * @see #setReadOnly(boolean)
	 */
	public abstract boolean isReadOnly();

	/**
	 * Set the flag whether the user can edit the property.
	 *
	 * @param readOnly <code>true</code> if the property is read-only; <code>false</code> if it can be edited.
	 */
	public abstract void setReadOnly(boolean readOnly);

	/**
	 * This lifecycle callback method is triggered whenever the property descriptor becomes active in the UI.
	 * Note, that this method is called before {@link IPropertyDescriptor#getLabelProvider()} and can therefore
	 * initialise things.
	 * <p>
	 * It's a good idea to create heavy-weight objects in your implementation of this method and to
	 * dispose them in {@link #onDeactivate()}.
	 * </p>
	 *
	 * @see #onDeactivate()
	 */
	public abstract void onActivate();

	/**
	 * This lifecycle callback method is triggered whenever the property descriptor becomes deactivated in the UI.
	 * You should perform clean-up in your implementation of this method: Dispose all objects that you created in
	 * {@link #onActivate()} before.
	 * <p>
	 * It's especially recommended to call {@link ILabelProvider#dispose()} here and to ensure that a new {@link ILabelProvider}
	 * is created either in {@link #onActivate()} or (lazily) in {@link #getLabelProvider()}.
	 * </p>
	 *
	 * @see #onActivate()
	 */
	public abstract void onDeactivate();
}