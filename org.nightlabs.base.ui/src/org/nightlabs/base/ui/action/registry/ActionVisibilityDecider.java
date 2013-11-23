package org.nightlabs.base.ui.action.registry;

public class ActionVisibilityDecider {
	/**
	 * Decide whether the current {@link ActionDescriptor} is visible.
	 *
	 * @param actionDescriptor
	 * @return 
	 */
	public boolean isVisible(ActionVisibilityContext actionVisibilityContext, ActionDescriptor actionDescriptor)
	{
		boolean visible = actionDescriptor.isVisible();

		if (visible) {
			ContributionManagerKind kind = actionVisibilityContext.getKind();
			if (ContributionManagerKind.menuBar.equals(kind))
				visible = actionDescriptor.isVisibleInMenubar();
			else if (ContributionManagerKind.contextMenu.equals(kind))
				visible = actionDescriptor.isVisibleInContextmenu();
			else if (ContributionManagerKind.toolBar.equals(kind))
				visible = actionDescriptor.isVisibleInToolbar();
			else if (ContributionManagerKind.coolBar.equals(kind))
				visible = actionDescriptor.isVisibleInToolbar();
			else
				throw new IllegalArgumentException("kind \"" + kind + "\" invalid!"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return visible;
	}
}
