package org.nightlabs.base.ui.exceptionhandler.errorreport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;

public class ErrorReportWizardScreenShotPage extends DynamicPathWizardPage {


	protected Label screenshotImage;
	
	
	public ErrorReportWizardScreenShotPage() {
		
		super(ErrorReportWizardScreenShotPage.class.getName(), "ScreenShot"); 
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public Control createPageContents(Composite parent) {
		// TODO Auto-generated method stub
		
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		screenshotImage = new Label(page, SWT.WRAP);
		screenshotImage.setLayoutData(new GridData(GridData.FILL_BOTH));

		Button sendScreenShotCheckBox = new Button(page, SWT.CHECK);
		sendScreenShotCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sendScreenShotCheckBox.setText("Attach the Above ScreenShot in the Report");
		
		return page;
	}
	
	
	  @Override
		
	 
	  
	  public void onShow()
	  {
		  
			ErrorReportWizard wizard = (ErrorReportWizard) getWizard();
			ErrorReport errorReport = wizard.getErrorReport();

			ImageData data = RCPUtil.convertToSWT(errorReport.getErrorScreenshot());				
			Image image = new Image(Display.getCurrent(), data);
			
			image = RCPUtil.resize(image,screenshotImage.getSize().x,screenshotImage.getSize().y);
			
			screenshotImage.setImage(image);
		    
	  }
	
	
	
}
