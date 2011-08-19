package org.nightlabs.base.ui.print.pref;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.print.PrintUtil;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterConfigurationCfMod;

/**
 * {@link PreferencePage} extension used for setting PDFDocumentPrinter-specific properties.
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class PDFDocumentPrinterPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    /**
     * Attribute used for persisting whether PDFPage-specific page dimensions shall be used instead of
     * printer-related ones.
     */
	public static final String ATTRIBUTE_NAME_PDF_PAGE_DIMENSIONS_USAGE_STATE = "pdfPageDimensionsUsageState"; //$NON-NLS-1$

	/**
	 * Use case ID for PDF printing.
	 * TODO do not hard-code use case ID here
	 */
	private static final String PRINTER_USE_CASE_ID
		= "org.nightlabs.eclipse.ui.pdfviewer.extension.printerUseCase"; //$NON-NLS-1$

    /**
     * Checkbox for setting the value of the attribute
     * {@link PDFDocumentPrinterPreferencePage#ATTRIBUTE_NAME_PDF_PAGE_DIMENSIONS_USAGE_STATE}.
     */
	private Button pdfPageDimensionsUsageStateButton;

	/**
	 * The printer configuration used for PDF printing. This is read out via the {@link PrinterConfigurationCfMod}.
	 */
	private PrinterConfiguration printerConfig;

	/**
	 * The constructor.
	 */
	public PDFDocumentPrinterPreferencePage() {
		super();
	}

	@Override
	protected Control createContents(final Composite parent) {
		final Group pdfPageDimensionsUsageStatePreferencesGroup = createGroup(parent, 1);
		pdfPageDimensionsUsageStatePreferencesGroup.setText(Messages.getString(
			"org.nightlabs.base.ui.print.pref.PDFDocumentPrinterPreferencePage.pdfPageDimensionsUsageStatePreferencesGroup.text")); //$NON-NLS-1$

		pdfPageDimensionsUsageStateButton = new Button(pdfPageDimensionsUsageStatePreferencesGroup, SWT.CHECK);
		pdfPageDimensionsUsageStateButton.setText(Messages.getString(
			"org.nightlabs.base.ui.print.pref.PDFDocumentPrinterPreferencePage.pdfPageDimensionsUsageStateFieldEditor.text")); //$NON-NLS-1$

		boolean selectionState = false;
		if (printerConfig != null && printerConfig.getAttributes().containsKey(ATTRIBUTE_NAME_PDF_PAGE_DIMENSIONS_USAGE_STATE)) {
			selectionState = (Boolean) printerConfig.getAttributes().get(ATTRIBUTE_NAME_PDF_PAGE_DIMENSIONS_USAGE_STATE);
		}
		pdfPageDimensionsUsageStateButton.setSelection(selectionState);

		return pdfPageDimensionsUsageStatePreferencesGroup;
	}

	@Override
	public void init(final IWorkbench arg0) {
		printerConfig = PrintUtil.getPrinterConfigurationFor(PRINTER_USE_CASE_ID);
	}

	@Override
	public boolean performOk() {
		if (printerConfig != null) {
			printerConfig.getAttributes().put(ATTRIBUTE_NAME_PDF_PAGE_DIMENSIONS_USAGE_STATE,
				pdfPageDimensionsUsageStateButton.getSelection());
			PrinterConfigurationCfMod.setPrinterConfiguration(PRINTER_USE_CASE_ID, printerConfig);
		}

		return super.performOk();
	}

	/**
	 * Helper method for creating and layouting a Group composite.
	 * @param parent The parent composite to be used.
	 * @param numColumns The amount of columns to be used.
	 * @return the Group
	 */
	public static Group createGroup(final Composite parent, final int numColumns) {
		final Group group = new Group(parent, SWT.NULL);

		final GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		group.setLayout(layout);

		final GridData data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		group.setLayoutData(data);

		return group;
	}
}
