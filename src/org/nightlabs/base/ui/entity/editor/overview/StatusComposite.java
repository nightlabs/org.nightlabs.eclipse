package org.nightlabs.base.ui.entity.editor.overview;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class StatusComposite extends XComposite 
{
	private IOverviewPageStatusProvider statusProvider;
	private ProgressMonitorPart progressMonitorPart;
	private Label messageLabel;
	private Label statusImageLabel;
	
	/**
	 * @param parent
	 * @param style
	 * @param statusProvider
	 */
	public StatusComposite(Composite parent, int style, IOverviewPageStatusProvider statusProvider) {
		super(parent, style);
		this.statusProvider = statusProvider;
		setLayout(new GridLayout(2, false));
		statusImageLabel = getToolkit().createLabel(this, "", SWT.NONE);
		GridData statusImageData = new GridData(48, 48);
		statusImageLabel.setLayoutData(statusImageData);
		messageLabel = getToolkit().createLabel(this, "", SWT.NONE);
		refresh();
	}

	public void refresh() {
		if (statusProvider != null) {
			statusProvider.resolveStatus(new NullProgressMonitor());
			IStatus status = statusProvider.getStatus();
			if (status != null) {
				Image statusImage = null;
				int code = status.getSeverity();
				switch (code) {
					case IStatus.OK:
						statusImage = SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
								OverviewSection.class, "OK", "48x48", ImageFormat.png);
						break;
					case IStatus.ERROR:
						statusImage = SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
								OverviewSection.class, "Error", "48x48", ImageFormat.png);
						break;
					case IStatus.WARNING:
						statusImage = SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
								OverviewSection.class, "Warning", "48x48", ImageFormat.png);
						break;										
				}
				if (statusImage != null)
					statusImageLabel.setImage(statusImage);	
				
				messageLabel.setText(status.getMessage());				
			}
		}		
	}
}
