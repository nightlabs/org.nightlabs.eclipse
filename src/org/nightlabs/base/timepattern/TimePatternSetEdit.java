package org.nightlabs.base.timepattern;

import java.util.Collection;

import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternSet;

public interface TimePatternSetEdit
{
	void setTimePatternSet(TimePatternSet timePatternSet);
	TimePatternSet getTimePatternSet();

	void createTimePattern();
	void removeSelectedTimePatterns();
	void removeTimePatterns(Collection<TimePattern> timePatterns);
	void removeTimePattern(TimePattern timePattern);

	void addTimePatternSetModifyListener(TimePatternSetModifyListener listener);
	void removeTimePatternSetModifyListener(TimePatternSetModifyListener listener);
}
