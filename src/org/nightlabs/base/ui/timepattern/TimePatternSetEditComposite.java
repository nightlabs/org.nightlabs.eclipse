package org.nightlabs.base.ui.timepattern;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.timepattern.builder.TimePatternSetBuilderWizard;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternSet;

/**
 * A Composite that lets the user view and edit a {@link TimePatternSet}. The Composite will present
 * a {@link TimePatternSetComposite} along with a link that will launch the
 * {@link TimePatternSetBuilderWizard} as well as Buttons to add {@link TimePattern}s to the set or
 * delete selected patterns.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class TimePatternSetEditComposite extends XComposite implements TimePatternSetEdit {

	private TimePatternSetComposite timePatternSetComposite;
	private String caption;

	/**
	 * Create a new {@link TimePatternSetEditComposite} with a default {@link LayoutDataMode} of
	 * {@link LayoutDataMode#GRID_DATA_HORIZONTAL}.
	 * 
	 * @param parent The parent of the new Composite.
	 * @param style The style of the new Composite.
	 * @param caption The caption of the new Composite, or <code>null</code> or an empty String if
	 *            no caption should be displayed.
	 */
	public TimePatternSetEditComposite(Composite parent, int style, String caption) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		this.caption = caption;
		createContents();
	}

	/**
	 * Create a new {@link TimePatternSetEditComposite}..
	 * 
	 * @param parent The parent of the new Composite.
	 * @param style The style of the new Composite.
	 * @param layoutDataMode The {@link LayoutDataMode} for the new Composite.
	 * @param caption The caption of the new Composite, or <code>null</code> or an empty String if
	 *            no caption should be displayed.
	 */
	public TimePatternSetEditComposite(Composite parent, int style, LayoutDataMode layoutDataMode, String caption) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER, layoutDataMode);
		this.caption = caption;
		createContents();
	}

	/**
	 * Used internally to create the Composites contents. Called from constructor.
	 */
	protected void createContents() {
		getGridLayout().makeColumnsEqualWidth = false;
		getGridLayout().numColumns = 4;

		Label captionLabel = new Label(this, SWT.WRAP);
		if (caption != null)
			captionLabel.setText(caption);
		captionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Hyperlink buildWizardLink = new Hyperlink(this, SWT.WRAP);
		// buildWizardLink.setLayoutData(new GridData());
		// buildWizardLink.setText("Build with wizard");
		Hyperlink buildWizardLink = getToolkit().createHyperlink(this, "Build with wizard", SWT.NONE);
		buildWizardLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (TimePatternSetBuilderWizard.open(getShell(), timePatternSetComposite.getTimePatternSet())) {
					timePatternSetComposite.refresh();
					timePatternSetComposite.fireTimePatternSetModifyEvent();
				}
			}
		});

		Button addPattern = new Button(this, SWT.PUSH);
		addPattern.setText("+");
		addPattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				timePatternSetComposite.createTimePattern();
			}
		});

		Button removePattern = new Button(this, SWT.PUSH);
		removePattern.setText("-");
		removePattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				timePatternSetComposite.removeSelectedTimePatterns();
			}
		});

		timePatternSetComposite = new TimePatternSetComposite(this, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 4;
		int rowMargin = 8; // A guess of table row margins (bottom and top)
		gd.heightHint =
		// 4 rows (including header)
		(RCPUtil.getFontHeight(timePatternSetComposite.getControl()) + rowMargin) * 4
		// + some extra pixels for the header margins
		+ 3;
		timePatternSetComposite.setLayoutData(gd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTimePatternSetModifyListener(TimePatternSetModifyListener listener) {
		timePatternSetComposite.addTimePatternSetModifyListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createTimePattern() {
		timePatternSetComposite.createTimePattern();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TimePatternSet getTimePatternSet() {
		return timePatternSetComposite.getTimePatternSet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeSelectedTimePatterns() {
		timePatternSetComposite.removeSelectedTimePatterns();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTimePattern(TimePattern timePattern) {
		timePatternSetComposite.removeTimePattern(timePattern);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTimePatternSetModifyListener(TimePatternSetModifyListener listener) {
		timePatternSetComposite.removeTimePatternSetModifyListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTimePatterns(Collection<TimePattern> timePatterns) {
		timePatternSetComposite.removeTimePatterns(timePatterns);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimePatternSet(TimePatternSet timePatternSet) {
		timePatternSetComposite.setTimePatternSet(timePatternSet);
	}
}
