package org.nightlabs.eclipse.ui.pdfviewer.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.sun.pdfview.PDFFile;

public class PdfSimpleNavigatorComposite extends Composite {

	private Button decreasePageNumberButton;
	private Button increasePageNumberButton;
	private Text currentPageNumberText;
	private Label label;
	private PDFFile pdfFile;
	private int numberOfPages;


	public PdfSimpleNavigatorComposite(Composite parent, int style) {
		super(parent, style);
		numberOfPages = pdfFile.getNumPages();
		// TODO Auto-generated constructor stub

		FillLayout fillLayout = new FillLayout();
		fillLayout.spacing = 1;
		this.setLayout(fillLayout);

		decreasePageNumberButton = new Button(parent, SWT.ARROW_LEFT);
		decreasePageNumberButton.setEnabled(false);

		currentPageNumberText = new Text(parent, SWT.NONE);
		currentPageNumberText.setText(String.valueOf(1));

		label = new Label(parent, SWT.NONE);
		label.setText("/");

		increasePageNumberButton = new Button(parent, SWT.ARROW_RIGHT);
		if (numberOfPages <= 1) {
			increasePageNumberButton.setEnabled(false);
		}


		decreasePageNumberButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int chosenPageNumber = new Integer(currentPageNumberText.getText()).intValue() - 1;
				if (chosenPageNumber >= 1) {
					currentPageNumberText.setText(String.valueOf(chosenPageNumber));		// alternative: (new Integer(x)).toString();
					if (!increasePageNumberButton.isEnabled())
						increasePageNumberButton.setEnabled(true);
					// TODO scrolling to the chosen page

				}
				else {
					decreasePageNumberButton.setEnabled(false);
				}
			}
		});

		increasePageNumberButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int chosenPageNumber = new Integer(currentPageNumberText.getText()).intValue() + 1;
				if (chosenPageNumber <= numberOfPages) {
					currentPageNumberText.setText(String.valueOf(chosenPageNumber));
					if (!decreasePageNumberButton.isEnabled())
						decreasePageNumberButton.setEnabled(true);
					// TODO scrolling to the chosen page


				}
				else {
					increasePageNumberButton.setEnabled(false);
				}
			}
		});

		currentPageNumberText.addVerifyListener(new VerifyListener() {
			@Override
            public void verifyText(VerifyEvent event) {
				event.doit = false;

			    char myChar = event.character;
			    String text = ((Text) event.widget).getText();
			    // TODO test if input is valid


            }
		});


		currentPageNumberText.addModifyListener(new ModifyListener() {
			@Override
            public void modifyText(ModifyEvent event) {
				Text text = (Text) event.widget;
				int chosenPageNumber = new Integer(text.getText()).intValue();

				if (chosenPageNumber == 1 && decreasePageNumberButton.isEnabled())
					decreasePageNumberButton.setEnabled(false);
				if (chosenPageNumber == numberOfPages && increasePageNumberButton.isEnabled())
					increasePageNumberButton.setEnabled(false);

				// TODO scrolling to the chosen page


            }
		});
	}

}
