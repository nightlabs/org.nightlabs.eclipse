package org.nightlabs.eclipse.ui.pdfviewer.extension.action.zoom;

import java.util.ArrayList;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.ToolBar;
import org.nightlabs.base.ui.action.CComboContributionItem;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.IPdfViewerActionOrContributionItem;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerActionRegistry;

public class ZoomContributionItem
extends CComboContributionItem<ZoomLevel>
implements IPdfViewerActionOrContributionItem
{
	private class ZoomLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			ZoomLevel zoomLevel = (ZoomLevel) element;
			return String.valueOf(zoomLevel);
		}
	}

	public ZoomContributionItem() {
		super(ZoomContributionItem.class.getName(), null, null);
		setLabelProvider(new ZoomLabelProvider());
		ArrayList<ZoomLevel> zoomLevels = new ArrayList<ZoomLevel>();
		zoomLevels.add(new ZoomLevel());
		zoomLevels.add(new ZoomLevel());
		zoomLevels.add(new ZoomLevel());
		zoomLevels.add(new ZoomLevel());
		zoomLevels.add(new ZoomLevel());
		zoomLevels.add(new ZoomLevel());
		zoomLevels.add(new ZoomLevel());
		zoomLevels.add(new ZoomLevel());
		setElements(zoomLevels);
	}

	@Override
	public void fill(CoolBar parent, int index) {
		super.fill(parent, index);
//		setSize();
	}

	@Override
	public void fill(ToolBar parent, int index) {
		super.fill(parent, index);
	}

	@Override
	public boolean isEnabled() {
		return pdfViewerActionRegistry.getPdfViewer().getPdfDocument() != null;
	}

	private PdfViewerActionRegistry pdfViewerActionRegistry;

	@Override
	public PdfViewerActionRegistry getPdfViewerActionRegistry() {
		return pdfViewerActionRegistry;
	}

	@Override
	public void init(PdfViewerActionRegistry pdfViewerActionRegistry) {
		this.pdfViewerActionRegistry = pdfViewerActionRegistry;
	}

//	/**
//   * Computes the width required by control
//   * @param control The control to compute width
//   * @return int The width required
//   */
//  @Override
//	protected int computeWidth(Control control)
//  {
//  	return 240;
////  	int width = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
////  	return width;
//  }

}
