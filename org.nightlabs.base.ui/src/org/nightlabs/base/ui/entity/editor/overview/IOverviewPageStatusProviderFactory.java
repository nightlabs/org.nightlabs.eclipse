package org.nightlabs.base.ui.entity.editor.overview;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public interface IOverviewPageStatusProviderFactory 
{
	/**
	 * Returns a newly created instance of {@link IOverviewPageStatusProvider}.
	 * @return a newly created instance of {@link IOverviewPageStatusProvider}.
	 */
	IOverviewPageStatusProvider createStatusProvider();
}
