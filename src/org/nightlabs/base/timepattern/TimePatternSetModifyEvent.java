package org.nightlabs.base.timepattern;

import java.util.EventObject;

public class TimePatternSetModifyEvent
extends EventObject
{
	private static final long serialVersionUID = 1L;

	public TimePatternSetModifyEvent(TimePatternSetEdit timePatternSetEdit)
	{
		super(timePatternSetEdit);
	}

	/**
	 * @return Returns the same as <code>getSource()</code> - just for convenience without
	 *		casting.
	 */
	public TimePatternSetEdit geTimePatternSetEdit() {
		return (TimePatternSetEdit) getSource();
	}
}
