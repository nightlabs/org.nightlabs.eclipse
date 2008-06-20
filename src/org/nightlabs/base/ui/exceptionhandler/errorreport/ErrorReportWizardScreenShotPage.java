package org.nightlabs.base.ui.exceptionhandler.errorreport;

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
import org.nightlabs.base.ui.util.ImageUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.config.Config;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class ErrorReportWizardScreenShotPage extends DynamicPathWizardPage {
	protected Label screenshotImage;
	private Boolean IsSendScreenshotImage = false;


	public ErrorReportWizardScreenShotPage() {

		super(ErrorReportWizardScreenShotPage.class.getName(), "Send an error report"); 
		// TODO Auto-generated constructor stub	
	}

	@Override
	public Control createPageContents(Composite parent) {
		// TODO Auto-generated method stub

		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		Label titleLabel = new Label(page, SWT.WRAP);
		titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		titleLabel.setText("Attach a screenshot of the Error"); 

		screenshotImage = new Label(page, SWT.WRAP);
		screenshotImage.setLayoutData(new GridData(GridData.FILL_BOTH));
		screenshotImage.setAlignment(SWT.CENTER);

		Button sendScreenShotCheckBox = new Button(page, SWT.CHECK);
		sendScreenShotCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sendScreenShotCheckBox.setText("Attach the Above ScreenShot in the Report");
		sendScreenShotCheckBox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				setIsSendscreenshotImage(!getIsSendsScreenshotImage());
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});		    

		ErrorReportSenderCfMod cfMod = Config.sharedInstance().createConfigModule(ErrorReportSenderCfMod.class);
		sendScreenShotCheckBox.setSelection(cfMod.isAttachScreenShotToErrorReport_default());

		sendScreenShotCheckBox.setToolTipText("decides whether to Attach the ScreenShot in the Error Report E-Mail");
		if(!cfMod.isAttachScreenShotToErrorReport_decide())
		{
			sendScreenShotCheckBox.setEnabled(false);
			sendScreenShotCheckBox.setSelection(false);
			sendScreenShotCheckBox.setToolTipText("you dont have enough access rights to use this feature!");
		}		

		setIsSendscreenshotImage(sendScreenShotCheckBox.getSelection());

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
			Image image = new Image(Display.getCurrent(), data);
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

	}
	public Boolean getIsSendsScreenshotImage() {
		return IsSendScreenshotImage;
	}

	public void setIsSendscreenshotImage(Boolean sendscreenshotImage) {
		this.IsSendScreenshotImage = sendscreenshotImage;
	}

}
