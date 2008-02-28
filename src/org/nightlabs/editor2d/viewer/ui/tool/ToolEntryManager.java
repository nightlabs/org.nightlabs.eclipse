/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.editor2d.viewer.ui.IViewer;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class ToolEntryManager
{
	private IToolEntry activeEntry = null;
	private IToolEntry defaultEntry = null;
	private ToolManager toolManager = null;
	private IViewer viewer = null;
	private Map<IToolEntry, ITool> toolEntry2Tool = new HashMap<IToolEntry, ITool>();	
	private List<IToolEntry> toolEntries = new ArrayList<IToolEntry>();	
	private Map<Button, IToolEntry> toolButton2ToolEntry = new HashMap<Button, IToolEntry>();
	private Map<IToolEntry, Button> toolEntry2ToolButton = new HashMap<IToolEntry, Button>();
	private Map<ITool, IToolEntry> tool2ToolEntry = new HashMap<ITool, IToolEntry>();
	private SelectionListener toolSelectionListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent arg0) {
			widgetSelected(arg0);
		}
		public void widgetSelected(SelectionEvent arg0) {
			Button b = (Button) arg0.getSource();
			IToolEntry toolEntry = (IToolEntry) toolButton2ToolEntry.get(b);
			setActiveToolEntry(toolEntry);
		}
	};

	public ToolEntryManager(IViewer viewer)
	{
		super();
		this.viewer = viewer;
		toolManager = new ToolManager(this);
	}

	public IViewer getViewer() {
		return viewer;
	}
	public void setViewer(IViewer viewer) {
		this.viewer = viewer;
		toolManager.setViewer(viewer);
	}
	
	public ToolManager getToolManager() {
		return toolManager;
	}
	
	public void addToolEntry(IToolEntry toolEntry)
	{
		toolEntries.add(toolEntry);
		toolManager.addTool(toolEntry.getTool());
		toolEntry2Tool.put(toolEntry, toolEntry.getTool());
		tool2ToolEntry.put(toolEntry.getTool(), toolEntry);
	}
	
	public List<IToolEntry> getToolEntries() {
		return toolEntries;
	}
	
	public void setDefaultToolEntry(IToolEntry toolEntry) {
		defaultEntry = toolEntry;
		ITool tool = toolEntry2Tool.get(defaultEntry);
		getToolManager().setDefaultTool(tool);
	}
	
	public IToolEntry getDefaultEntry()
	{
		if (defaultEntry == null)
			return toolEntries.get(0);
		
		return defaultEntry;
	}
	
	public void setActiveToolEntry(IToolEntry toolEntry)
	{
		if (activeEntry != null) {
			Button oldActiveButton = toolEntry2ToolButton.get(activeEntry);
			if (oldActiveButton != null) {
				oldActiveButton.setSelection(false);
			}
		}
		
		activeEntry = toolEntry;
		ITool tool = toolEntry2Tool.get(activeEntry);
		getToolManager().setActiveTool(tool);
		Button button = toolEntry2ToolButton.get(activeEntry);
		button.setSelection(true);
	}
	public IToolEntry getActiveToolEntry() {
		return activeEntry;
	}
	
	public void createToolsComposite(Composite parent)
	{
		for (Iterator<IToolEntry> it = getToolEntries().iterator(); it.hasNext(); )
		{
			IToolEntry toolEntry = it.next();
			Button toolButton = new Button(parent, SWT.TOGGLE);
			GridData toolData = new GridData(GridData.FILL_HORIZONTAL);
			toolButton.setLayoutData(toolData);
			if (toolEntry.getName() != null)
				toolButton.setText(toolEntry.getName());
			if (toolEntry.getImage() != null)
				toolButton.setImage(toolEntry.getImage());
			if (toolEntry.getToolTipText() != null)
				toolButton.setToolTipText(toolEntry.getToolTipText());
			
			toolButton2ToolEntry.put(toolButton, toolEntry);
			toolEntry2ToolButton.put(toolEntry, toolButton);
			toolButton.addSelectionListener(toolSelectionListener);
		}
	}
		
	public IToolEntry getToolEntry(ITool tool) {
		return tool2ToolEntry.get(tool);
	}
}
