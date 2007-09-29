package org.nightlabs.base.timepattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.table.AbstractTableComposite;
import org.nightlabs.base.table.TableLabelProvider;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternFormatException;
import org.nightlabs.timepattern.TimePatternSet;
import org.nightlabs.util.CollectionUtil;

public class TimePatternSetComposite
extends AbstractTableComposite<TimePattern>
implements TimePatternSetEdit
{
	protected static class TimePatternSetContentProvider implements IStructuredContentProvider
	{
		private TimePatternSetComposite timePatternSetComposite;

		public TimePatternSetContentProvider(TimePatternSetComposite timePatternSetComposite)
		{
			this.timePatternSetComposite = timePatternSetComposite;
		}

		@SuppressWarnings("unchecked") //$NON-NLS-1$
		public Object[] getElements(Object inputElement)
		{
			TimePatternSet timePatternSet = (TimePatternSet) inputElement;
			if (timePatternSet == null)
				return new TimePattern[] {};

			List<TimePattern> timePatterns = new ArrayList<TimePattern>(timePatternSet.getTimePatterns());

			// cannot sort by ID, because the abstract class does not have an id
			Comparator<TimePattern> c = timePatternSetComposite.getTimePatternComparator();
			if (c != null)
				Collections.sort(timePatterns, c);

			return CollectionUtil.collection2TypedArray(timePatterns, TimePattern.class, false);
		}

		public void dispose()
		{
			// nothing to do
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			// nothing to do // TODO really?
		}
	}

	protected static class TimePatternSetLabelProvider extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			TimePattern timePattern = (TimePattern) element;
			switch (columnIndex) {
				case 0:
					return timePattern.getYear();
				case 1:
					return timePattern.getMonth();
				case 2:
					return timePattern.getDay();
				case 3:
					return timePattern.getDayOfWeek();
				case 4:
					return timePattern.getHour();
				case 5:
					return timePattern.getMinute();
				default:
					return ""; //$NON-NLS-1$
			}
		}
	}

	public static final String COLUMN_PROPERTY_YEAR = "year"; //$NON-NLS-1$
	public static final String COLUMN_PROPERTY_MONTH = "month"; //$NON-NLS-1$
	public static final String COLUMN_PROPERTY_DAY = "day"; //$NON-NLS-1$
	public static final String COLUMN_PROPERTY_DAY_OF_WEEK = "dayOfWeek"; //$NON-NLS-1$
	public static final String COLUMN_PROPERTY_HOUR = "hour"; //$NON-NLS-1$
	public static final String COLUMN_PROPERTY_MINUTE = "minute"; //$NON-NLS-1$

	public static class TimePatternSetCellModifier
	implements ICellModifier
	{
		private TimePatternSetComposite timePatternSetComposite;

		public TimePatternSetCellModifier(TimePatternSetComposite timePatternSetComposite)
		{
			this.timePatternSetComposite = timePatternSetComposite;
		}

		public boolean canModify(Object element, String property)
		{
			return
					COLUMN_PROPERTY_YEAR.equals(property) ||
					COLUMN_PROPERTY_MONTH.equals(property) ||
					COLUMN_PROPERTY_DAY.equals(property) ||
					COLUMN_PROPERTY_DAY_OF_WEEK.equals(property) ||
					COLUMN_PROPERTY_HOUR.equals(property) ||
					COLUMN_PROPERTY_MINUTE.equals(property);
		}

		public Object getValue(Object element, String property)
		{
			TimePattern tp = (TimePattern) element;

			if (COLUMN_PROPERTY_YEAR.equals(property))
				return tp.getYear();

			if (COLUMN_PROPERTY_MONTH.equals(property))
				return tp.getMonth();

			if (COLUMN_PROPERTY_DAY.equals(property))
				return tp.getDay();

			if (COLUMN_PROPERTY_DAY_OF_WEEK.equals(property))
				return tp.getDayOfWeek();

			if (COLUMN_PROPERTY_HOUR.equals(property))
				return tp.getHour();

			if (COLUMN_PROPERTY_MINUTE.equals(property))
				return tp.getMinute();

			return null;
		}

		public void modify(Object element, String property, Object value)
		{
			TableItem tableItem = (TableItem)element;
			TimePattern tp = (TimePattern)tableItem.getData();

			try {
				if (COLUMN_PROPERTY_YEAR.equals(property))
					tp.setYear((String)value);
				else if (COLUMN_PROPERTY_MONTH.equals(property))
					tp.setMonth((String)value);
				else if (COLUMN_PROPERTY_DAY.equals(property))
					tp.setDay((String)value);
				else if (COLUMN_PROPERTY_DAY_OF_WEEK.equals(property))
					tp.setDayOfWeek((String)value);
				else if (COLUMN_PROPERTY_HOUR.equals(property))
					tp.setHour((String)value);
				else if (COLUMN_PROPERTY_MINUTE.equals(property))
					tp.setMinute((String)value);
			} catch (TimePatternFormatException x) {
				throw new RuntimeException(x); // TODO nice message and reset the previous state (i.e. update the table, becaue the TimePattern does not accept invalid values)!
			}

			// TODO do we need to refresh the table?!
			// update(element... or update(tp ????
			timePatternSetComposite.getTableViewer().update(tp, new String[] { property });
			timePatternSetComposite.fireTimePatternSetModifyEvent();
		}
	}

	protected void fireTimePatternSetModifyEvent()
	{
		Object[] listeners = timePatternSetModifyListeners.getListeners();
		if (listeners.length < 1)
			return;

		TimePatternSetModifyEvent event = new TimePatternSetModifyEvent(this);

		for (Object listener : listeners) {
			TimePatternSetModifyListener l = (TimePatternSetModifyListener) listener;
			l.timePatternSetModified(event);
		}
	}

	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.base.timepattern.TimePatternSetComposite.tableColumnYear")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.base.timepattern.TimePatternSetComposite.tableColumnMonth")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.base.timepattern.TimePatternSetComposite.tableColumnDay")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.base.timepattern.TimePatternSetComposite.tableColumnDayOfWeek")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.base.timepattern.TimePatternSetComposite.tableColumnHour")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.base.timepattern.TimePatternSetComposite.tableColumnMinute")); //$NON-NLS-1$

//		table.setLayout(new WeightedTableLayout(new int[] {10, 10, 10, 10, 10, 10}));
		TableLayout l = new TableLayout();
		l.addColumnData(new ColumnWeightData(1, 50));
		l.addColumnData(new ColumnWeightData(1, 50));
		l.addColumnData(new ColumnWeightData(1, 50));
		l.addColumnData(new ColumnWeightData(1, 50));
		l.addColumnData(new ColumnWeightData(1, 50));
		l.addColumnData(new ColumnWeightData(1, 50));
		table.setLayout(l);
		
		

		tableViewer.setColumnProperties(new String[] {
				COLUMN_PROPERTY_YEAR,
				COLUMN_PROPERTY_MONTH,
				COLUMN_PROPERTY_DAY,
				COLUMN_PROPERTY_DAY_OF_WEEK,
				COLUMN_PROPERTY_HOUR,
				COLUMN_PROPERTY_MINUTE
		});
		tableViewer.setCellEditors(new CellEditor[] {
				new TextCellEditor(table),
				new TextCellEditor(table),
				new TextCellEditor(table),
				new TextCellEditor(table),
				new TextCellEditor(table),
				new TextCellEditor(table)
		});

		tableViewer.setCellModifier(new TimePatternSetCellModifier(this));
	}

	private Comparator<TimePattern> timePatternComparator = null;

	/**
	 * The timePatternComparator is per default <code>null</code>. You can set it in order
	 * to sort the {@link TimePattern}s.
	 *
	 * @param timePatternComparator
	 */
	public void setTimePatternComparator(
			Comparator<TimePattern> timePatternComparator)
	{
		this.timePatternComparator = timePatternComparator;
	}
	public Comparator<TimePattern> getTimePatternComparator()
	{
		return timePatternComparator;
	}

	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TimePatternSetContentProvider(this));
		tableViewer.setLabelProvider(new TimePatternSetLabelProvider());
	}

	public TimePatternSetComposite(Composite parent, int style)
	{
		super(parent, style);
	}

	private TimePatternSet timePatternSet;
	
	@Override
	public void setInput(Object input)
	{
		if (!(input instanceof TimePatternSet))
			throw new IllegalArgumentException("input must be an instance of TimePatternSet, but is: " + input); //$NON-NLS-1$

		setTimePatternSet((TimePatternSet) input);
	}

	public void setTimePatternSet(TimePatternSet timePatternSet)
	{
		this.timePatternSet = timePatternSet;
		super.setInput(timePatternSet);
	}
	public TimePatternSet getTimePatternSet()
	{
		return timePatternSet;
	}

	private ListenerList timePatternSetModifyListeners = new ListenerList();

	public void addTimePatternSetModifyListener(TimePatternSetModifyListener listener)
	{
		timePatternSetModifyListeners.add(listener);
	}

	public void removeTimePatternSetModifyListener(TimePatternSetModifyListener listener)
	{
		timePatternSetModifyListeners.remove(listener);
	}

	public void createTimePattern()
	{
		timePatternSet.createTimePattern();
		refresh();
		fireTimePatternSetModifyEvent();
	}

	public void removeSelectedTimePatterns()
	{
		ArrayList<TimePattern> timePatterns = new ArrayList<TimePattern>(getSelectedElements());
		removeTimePatterns(timePatterns);
	}

	public void removeTimePatterns(Collection<TimePattern> timePatterns)
	{
		boolean fireEvent = false;
		for (TimePattern timePattern : timePatterns) {
			if (timePatternSet.removeTimePattern(timePattern))
				fireEvent = true;
		}

		if (fireEvent) {
			refresh();
			fireTimePatternSetModifyEvent();
		}
	}

	public void removeTimePattern(TimePattern timePattern)
	{
		if (timePatternSet.removeTimePattern(timePattern)) {
			refresh();
			fireTimePatternSetModifyEvent();
		}
	}
}
