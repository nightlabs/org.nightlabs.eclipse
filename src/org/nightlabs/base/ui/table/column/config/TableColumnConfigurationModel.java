package org.nightlabs.base.ui.table.column.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class for {@link TableColumnConfigurator}.
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class TableColumnConfigurationModel {

	private Map<String, Boolean> columnIDToVisibilityState;
	private Map<String, String> columnIDToColumnText;	// fix mapping, independent of visibility/ordering! e.g. 0->ID,1->Customer,...
	private Map<String, Integer> columnIDToColumnWidth;
	private Map<String, Integer> columnIDToColumnIdx;	// fix mapping, independent of visibility/ordering! e.g. 0->0,1->1,...
	private List<String> columnIDsOrder;	// the order the columns appear in the table (i.e. the "visible" order), e.g. Customer,ID,...
	private List<String> columnIDsHidden;

	public TableColumnConfigurationModel() {
		columnIDToVisibilityState = new HashMap<String, Boolean>();
		columnIDToColumnText = new HashMap<String, String>();
		columnIDToColumnWidth = new HashMap<String, Integer>();
		columnIDToColumnIdx = new HashMap<String, Integer>();
		columnIDsOrder = new ArrayList<String>();
		columnIDsHidden = new ArrayList<String>();
	}

	public Map<String, Boolean> getColumnIDToVisibilityState() {
		return columnIDToVisibilityState;
	}

	public Map<String, String> getColumnIDToColumnText() {
		return columnIDToColumnText;
	}

	public List<String> getColumnIDsOrder() {
		return columnIDsOrder;
	}

	public List<String> getColumnIDsHidden() {
		return columnIDsHidden;
	}

	public Map<String, Integer> getColumnIDToColumnWidth() {
		return columnIDToColumnWidth;
	}
	
	public Map<String, Integer> getColumnIDToColumnIdx() {
		return columnIDToColumnIdx;
	}
}
