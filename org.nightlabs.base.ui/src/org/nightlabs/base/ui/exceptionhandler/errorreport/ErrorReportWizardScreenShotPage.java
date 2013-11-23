package org.nightlabs.base.ui.exceptionhandler.errorreport;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.util.ImageUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.config.Config;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class ErrorReportWizardScreenShotPage 
extends DynamicPathWizardPage 
{
	private static final Logger logger = Logger.getLogger(ErrorReportWizardScreenShotPage.class);
	
	private Label screenshotImage;
	private Boolean IsSendScreenshotImage = false;
	private Label titleLabel;
	private Button sendScreenShotCheckBox;
	
	public ErrorReportWizardScreenShotPage() {
		super(ErrorReportWizardScreenShotPage.class.getName(), Messages.getString("org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportWizardScreenShotPage.title"));  //$NON-NLS-1$	
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		titleLabel = new Label(page, SWT.WRAP);
		titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		titleLabel.setText(Messages.getString("org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportWizardScreenShotPage.titleLabel"));  //$NON-NLS-1$

		screenshotImage = new Label(page, SWT.WRAP);
		screenshotImage.setLayoutData(new GridData(GridData.FILL_BOTH));
		screenshotImage.setAlignment(SWT.CENTER);

		sendScreenShotCheckBox = new Button(page, SWT.CHECK);
		sendScreenShotCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sendScreenShotCheckBox.setText(Messages.getString("org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportWizardScreenShotPage.sendScreenShotCheckBox.label")); //$NON-NLS-1$
		sendScreenShotCheckBox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				setIsSendScreenshotImage(!getIsSendsScreenshotImage());
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});		    

		ErrorReportSenderCfMod cfMod = Config.sharedInstance().createConfigModule(ErrorReportSenderCfMod.class);
		sendScreenShotCheckBox.setSelection(cfMod.getAttachScreenShotToErrorReport_default());

		sendScreenShotCheckBox.setToolTipText(Messages.getString("org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportWizardScreenShotPage.sendScreenShotCheckBox.tooltip")); //$NON-NLS-1$
		if(!cfMod.getAttachScreenShotToErrorReport_decide())
		{
			sendScreenShotCheckBox.setEnabled(false);
			sendScreenShotCheckBox.setSelection(false);
			sendScreenShotCheckBox.setToolTipText(Messages.getString("org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportWizardScreenShotPage.sendScreenShotCheckBox.noPermissions.tooltip")); //$NON-NLS-1$
		}		

		setIsSendScreenshotImage(sendScreenShotCheckBox.getSelection());

		parent.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				onShow();
			}
		});

		return page;
	}

	@Override 
	public void onShow()
	{
		if(screenshotImage.getSize().x > 0 && screenshotImage.getSize().y > 0 )
		{	
			double imgRatio;

			ErrorReportWizard wizard = (ErrorReportWizard) getWizard();
			ErrorReport errorReport = wizard.getErrorReport();
//			ImageData data = ImageUtil.convertToSWT(errorReport.getErrorScreenshot());
			ImageData data = errorReport.getErrorScreenshot();
			Display display = Display.getCurrent();
			if (display == null) {
				display = Display.getDefault();
			}
			if (display != null) 
			{
				if (data != null) {
					Image image = new Image(display, data);
					Image scaledImage = image;

					if(screenshotImage.getSize().x > screenshotImage.getSize().y)							
						imgRatio = (double)(screenshotImage.getSize().y)  / (double)(data.height);
					else
						imgRatio = (double)(screenshotImage.getSize().x)  / (double)(data.width);

					imgRatio = imgRatio - (imgRatio * 0.1); // decrease image size 10%
					if ((data.width * imgRatio) > 0 && (data.height * imgRatio) > 0) {
						scaledImage = ImageUtil.resize(image,(int)(data.width * imgRatio),(int)(data.height * imgRatio),false);
					}

					image.dispose();

					screenshotImage.setImage(scaledImage);
					screenshotImage.redraw();					
				}
				else {
					logger.error("imageData == null!"); //$NON-NLS-1$
					errorOccured();
				}
			}
			else {
				logger.error("display == null!"); //$NON-NLS-1$
				errorOccured();
			}
		}
	}
	
	public Boolean getIsSendsScreenshotImage() {
		return IsSendScreenshotImage;
	}

	public void setIsSendScreenshotImage(Boolean sendscreenshotImage) {
		this.IsSendScreenshotImage = sendscreenshotImage;
	}

	private void errorOccured() {
		if (titleLabel != null && !titleLabel.isDisposed()) {
			titleLabel.setText(Messages.getString("org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportWizardScreenShotPage.label.text.errorOccured")); //$NON-NLS-1$
		}
		if (sendScreenShotCheckBox != null && !sendScreenShotCheckBox.isDisposed()) {
			sendScreenShotCheckBox.setSelection(false);
			sendScreenShotCheckBox.setEnabled(false);
		}
	}
}
