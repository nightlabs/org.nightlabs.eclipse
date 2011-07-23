/**
 * 
 */
package org.nightlabs.base.ui.composite;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 *
 */
public class ExclusiveExpandBar 
{
	private static final Logger logger = Logger.getLogger(ExclusiveExpandBar.class);
	
	public static void enableFor(final ExpandBar expandBar) {
		ExpandListener expandListener = new ExpandListener() {
			 
            @Override
            public void itemExpanded(ExpandEvent e) {
                adjustExclusiveItemHeight(expandBar);
            }
 
            @Override
            public void itemCollapsed(ExpandEvent e) {
            	adjustExclusiveItemHeight(expandBar);
            }
        };
        expandBar.addExpandListener(expandListener);
		
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				adjustExclusiveItemHeight(expandBar);
			}
		};
		expandBar.addListener(SWT.Resize, listener);
	}
	
	private static void adjustExclusiveItemHeight(final ExpandBar expandBar) {
		Runnable runnable = new Runnable() {
			public void run() 
			{
				if (!expandBar.isDisposed()) {
					ExpandItem[] items = expandBar.getItems();
					Rectangle area = expandBar.getClientArea();
					int spacing = expandBar.getSpacing();
					
					int headers = 0;
					int expanded = 0;
					for (ExpandItem expandItem : items) {
						Integer headerHeight = (Integer) expandItem.getData("headerHeight");
						if (headerHeight == null) {
							headerHeight = expandItem.getHeaderHeight();
							expandItem.setData("headerHeight", headerHeight);
						}
						headers += headerHeight; 
						if (expandItem.getExpanded()) {
							expanded++;
						}
					}
					logger.debug("Area height " + area.height);
					logger.debug("headers " + headers);
					logger.debug("expanded " + expanded);
					
					area.height -= (items.length + 1)*spacing + headers;
					
					if (expanded == 0) {
						area.height = 0;
					} else {
						area.height = area.height / expanded;
					}
					
					logger.debug("Expanded items height: " + area.height);
					
					for (ExpandItem expandItem : items) {
						if (expandItem.getExpanded()) {
							logger.debug("Setting item height " + expandItem + ": " + area.height);
							expandItem.setHeight(area.height);
						} else {
							logger.debug("Setting item height " + expandItem + ": 0");
							expandItem.setHeight(0);
						}
					}					
				}
			}
		};
		expandBar.getDisplay().asyncExec(runnable);
//		runnable.run();
	}

}
