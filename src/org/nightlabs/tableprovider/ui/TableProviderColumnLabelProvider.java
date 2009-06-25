package org.nightlabs.tableprovider.ui;

import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableProviderColumnLabelProvider extends ColumnLabelProvider {

	private Collection<TableProvider<?, ?>> tableProviders;
//	private String type;
	private Set<String> types;
	private String scope;

	public TableProviderColumnLabelProvider(Collection<TableProvider<?, ?>> tableProviders, Set<String> types, String scope) {
		this.tableProviders = tableProviders;
//		this.type = type;
		this.types = types;
		this.scope = scope;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element)
	{
		for (TableProvider provider : tableProviders) {
			if (scope != null && provider.isCompatible(element, scope)) {
				return provider.getText(types, element, scope);
			}
		}
		return "";
	}

}
