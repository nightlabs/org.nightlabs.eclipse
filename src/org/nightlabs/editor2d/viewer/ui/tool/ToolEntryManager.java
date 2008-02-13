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
	public ToolEntryManager(IViewer viewer)
	{
		super();
		this.viewer = viewer;
		toolManager = new ToolManager(viewer);
	}

	protected IViewer viewer = null;
	public IViewer getViewer() {
		return viewer;
	}
	public void setViewer(IViewer viewer) {
		this.viewer = viewer;
		toolManager.setViewer(viewer);
	}
	
	protected ToolManager toolManager = null;
	public ToolManager getToolManager() {
		return toolManager;
	}
	
	protected Map<IToolEntry, ITool> toolEntry2Tool = new HashMap<IToolEntry, ITool>();
	
	protected List<IToolEntry> toolEntries = new ArrayList<IToolEntry>();
	public void addToolEntry(IToolEntry toolEntry)
	{
		toolEntries.add(toolEntry);
		toolManager.addTool(toolEntry.getTool());
		toolEntry2Tool.put(toolEntry, toolEntry.getTool());
	}
	
	public List<IToolEntry> getToolEntries() {
		return toolEntries;
	}
	
	protected IToolEntry defaultEntry = null;
	public void setDefaultToolEntry(IToolEntry toolEntry) {
		defaultEntry = toolEntry;
	}
	public IToolEntry getDefaultEntry()
	{
		if (defaultEntry == null)
			return toolEntries.get(0);
		
		return defaultEntry;
	}
	
	protected IToolEntry activeEntry = null;
	public void setActiveToolEntry(IToolEntry toolEntry)
	{
		activeEntry = toolEntry;
		ITool tool = toolEntry2Tool.get(activeEntry);
		getToolManager().setActiveTool(tool);
	}
	public IToolEntry getActiveToolEntry() {
		return activeEntry;
	}
	
	public void createToolsComposite(Composite parent)
	{
		for (Iterator<IToolEntry> it = getToolEntries().iterator(); it.hasNext(); )
		{
			IToolEntry toolEntry = it.next();
//			Button toolButton = new Button(parent, SWT.TOGGLE);
			Button toolButton = new Button(parent, SWT.PUSH);
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
	
	protected Map toolButton2ToolEntry = new HashMap();
	protected Map toolEntry2ToolButton = new HashMap();
	protected SelectionListener toolSelectionListener = new SelectionListener()
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
	
}
