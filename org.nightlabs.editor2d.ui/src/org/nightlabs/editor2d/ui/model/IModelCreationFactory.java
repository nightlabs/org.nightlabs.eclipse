package org.nightlabs.editor2d.ui.model;

import org.eclipse.gef.requests.CreationFactory;
import org.nightlabs.editor2d.Editor2DFactory;

public interface IModelCreationFactory
extends CreationFactory
{
	Editor2DFactory getFactory();
}
