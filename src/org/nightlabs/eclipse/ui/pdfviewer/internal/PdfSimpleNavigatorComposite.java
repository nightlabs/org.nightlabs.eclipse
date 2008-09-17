package org.nightlabs.eclipse.ui.pdfviewer.internal;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfSimpleNavigator;

import com.sun.pdfview.PDFFile;

public class PdfSimpleNavigatorComposite extends Composite {

	private PdfSimpleNavigator pdfSimpleNavigator;
	private Button gotoFirstPageButton;
	private Button decreasePageNumberButton;
	private Button increasePageNumberButton;
	private Button gotoLastPageButton;
	private Text currentPageNumberText;
	private Label label;
	private PDFFile pdfFile;
	private PdfDocument pdfDocument;
	private int numberOfPages;
	private ModifyListenerImpl modifyListenerImpl;

//	public PdfSimpleNavigatorComposite(Composite parent, int style, PDFFile pdfFile, PdfDocument pdfDocument) {
	public PdfSimpleNavigatorComposite(Composite parent, PdfSimpleNavigator pdfSimpleNavigator) {
		super(parent, SWT.NONE);
		this.pdfSimpleNavigator = pdfSimpleNavigator;

		numberOfPages = 0;
		// TODO Auto-generated constructor stub

		RowLayout rowLayout = new RowLayout();
		this.setLayout(rowLayout);

		gotoFirstPageButton = new Button(this, SWT.ARROW | SWT.UP);
		decreasePageNumberButton = new Button(this, SWT.ARROW | SWT.LEFT);
		currentPageNumberText = new Text(this, SWT.BORDER);
		modifyListenerImpl = new ModifyListenerImpl();
		currentPageNumberText.addModifyListener(modifyListenerImpl);
		label = new Label(this, SWT.NONE);
		increasePageNumberButton = new Button(this, SWT.ARROW | SWT.RIGHT);
		gotoLastPageButton = new Button(this, SWT.ARROW | SWT.DOWN);

		setInitialControlValues();

		gotoFirstPageButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int oldPageNumber = new Integer(currentPageNumberText.getText()).intValue();
				gotoPage(oldPageNumber, 1);
			}
		});

		decreasePageNumberButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int oldPageNumber = new Integer(currentPageNumberText.getText()).intValue();
				gotoPage(oldPageNumber, oldPageNumber - 1);
			}
		});

		increasePageNumberButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int oldPageNumber = new Integer(currentPageNumberText.getText()).intValue();
				gotoPage(oldPageNumber, oldPageNumber + 1);
			}
		});

		gotoLastPageButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int oldPageNumber = new Integer(currentPageNumberText.getText()).intValue();
				gotoPage(oldPageNumber, numberOfPages);
			}
		});

//		currentPageNumberText.addVerifyListener(new VerifyListener() {
//			@Override
//            public void verifyText(VerifyEvent event) {
//				event.doit = false;
//
//			    char myChar = event.character;
//			    String text = ((Text) event.widget).getText();
//			    // TODO test if input is valid
//
//
//            }
//		});

		currentPageNumberText.addKeyListener(new KeyListener() {
			@Override
            public void keyPressed(KeyEvent e) {
            }
			@Override
            public void keyReleased(KeyEvent e) {
            }
		});
	}

	private void gotoPage(int oldPageNumber, int chosenPageNumber) {
		currentPageNumberText.removeModifyListener(modifyListenerImpl);

		if (chosenPageNumber >= 1 && chosenPageNumber <= numberOfPages) {
			currentPageNumberText.setText(String.valueOf(chosenPageNumber));

			if (chosenPageNumber == 1) {
				decreasePageNumberButton.setEnabled(false);
				if (numberOfPages > 1 && !increasePageNumberButton.isEnabled()){
					increasePageNumberButton.setEnabled(true);
				}
			}
			if (chosenPageNumber == numberOfPages) {
				increasePageNumberButton.setEnabled(false);
				if (numberOfPages > 1 && !decreasePageNumberButton.isEnabled()) {
					decreasePageNumberButton.setEnabled(true);
				}
			}
			if (chosenPageNumber > 1 && chosenPageNumber < numberOfPages) {
				if (!decreasePageNumberButton.isEnabled()) {
					decreasePageNumberButton.setEnabled(true);
				}
				if (!increasePageNumberButton.isEnabled()) {
					increasePageNumberButton.setEnabled(true);
				}
			}

			scrollToPage(chosenPageNumber);

		}
		else {
			currentPageNumberText.setText(String.valueOf(oldPageNumber));
			scrollToPage(numberOfPages);
		}
		currentPageNumberText.addModifyListener(modifyListenerImpl);
	}

	private void scrollToPage(int chosenPageNumber) {
		// TODO test page number for validity
		if (chosenPageNumber > 0) {
			Rectangle2D chosenPageBounds = pdfDocument.getPageBounds(chosenPageNumber);
			pdfSimpleNavigator.getPdfViewer().setViewOrigin(new Point2D.Double(chosenPageBounds.getMinX(), chosenPageBounds.getMinY()));
		}
	}

	private void setInitialControlValues() {
		decreasePageNumberButton.setEnabled(false);
		currentPageNumberText.setText(String.valueOf(0));
		label.setText(" / 0");
		increasePageNumberButton.setEnabled(false);
	}

	private void setControlValues() {
		if (pdfFile != null) {
			numberOfPages = pdfFile.getNumPages();
		}
		if (numberOfPages >= 1) {
			currentPageNumberText.setText(String.valueOf(1));
			currentPageNumberText.setTextLimit((Integer.toString(numberOfPages)).length());
			label.setText(" / " + numberOfPages);
			boolean morePages = numberOfPages > 1 ? true : false;
			increasePageNumberButton.setEnabled(morePages);
		}
	}

	public PdfDocument getPdfDocument() {
		return pdfDocument;
	}

	public void setPdfDocument(PdfDocument pdfDocument) {
		this.pdfDocument = pdfDocument;
		this.pdfFile = pdfDocument != null ? pdfDocument.getPdfFile() : null;

		setControlValues();
	}

	public PDFFile getPdfFile() {
		return pdfFile;
	}

	private class ModifyListenerImpl implements ModifyListener {
		@Override
        public void modifyText(ModifyEvent event) {
			Text text = (Text) event.widget;
			// TODO activate verify listener for checking validity of input in general
			if (!(text.getText().equals(""))) {
				int chosenPageNumber = new Integer(text.getText()).intValue();
				// TODO do not use highest page number as fix old value but send real old value instead
				gotoPage(numberOfPages, chosenPageNumber);
			}
        }
	};


}
