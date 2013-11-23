/**
 * 
 */
package org.nightlabs.base.ui.action.registry;

public class ActionVisibilityContext {
	public ActionVisibilityContext(ContributionManagerKind kind) {
		this.kind = kind;
	}

	private ContributionManagerKind kind;
	public ContributionManagerKind getKind() {
		return kind;
	}
}