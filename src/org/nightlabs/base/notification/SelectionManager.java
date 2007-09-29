/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.notification.SubjectCarrier;
import org.nightlabs.util.RWLock;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class SelectionManager extends NotificationManager
{
	private static SelectionManager _sharedInstance = null;

	public static SelectionManager sharedInstance()
	{
		if (_sharedInstance == null)
			_sharedInstance = new SelectionManager();

		return _sharedInstance;
	}

	protected SelectionManager()
	{
	}

	/**
	 * key: String zone<br/>
	 * value: Map {<br/>
	 *		key: Class searchClass<br/>
	 *		value: NotificationEvent event<br/>
	 * }
	 */
	private Map<String, Map<Class, NotificationEvent>> eventsByZone = new HashMap<String, Map<Class,NotificationEvent>>();
	private RWLock eventsByZoneMutex = new RWLock("eventsByZoneMutex"); //$NON-NLS-1$

	protected void registerEvent(NotificationEvent event)
	{
		eventsByZoneMutex.acquireWriteLock();
		try {
			String zone = event.getZone();
			Set<String> zones = new HashSet<String>();
			if (zone != null) {
				zones.add(null);
				zones.add(zone);
			}
			else {
				zones.add(null);
				zones.addAll(eventsByZone.keySet());
			}

			for (Iterator itZones = zones.iterator(); itZones.hasNext(); ) {
				zone = (String)itZones.next();

				Map<Class, NotificationEvent> eventsByClass = eventsByZone.get(zone);
				if (eventsByClass == null) {
					eventsByClass = new HashMap<Class, NotificationEvent>();
					eventsByZone.put(zone, eventsByClass);
				}

				for (Iterator itSubjectCarriers = event.getSubjectCarriers().iterator(); itSubjectCarriers.hasNext(); ) {
					SubjectCarrier carrier = (SubjectCarrier) itSubjectCarriers.next();
					for (Iterator itClasses = carrier.getSubjectClasses().iterator(); itClasses.hasNext(); ) {
						Class clazz = (Class) itClasses.next();
						
						if (carrier.isInheritanceIgnored())
							eventsByClass.put(clazz, event);
						else {
							do {
								eventsByClass.put(clazz, event);
								clazz = clazz.getSuperclass();
							} while (clazz != null);
						}
					}
				} // for (Iterator itSubjectCarriers = event.getSubjectCarriers().iterator(); itSubjectCarriers.hasNext(); ) {

			} // for (Iterator itZones = zones.iterator(); itZones.hasNext(); ) {

//			for (Iterator it = event.getSubjects().iterator(); it.hasNext(); ) {
//				Object subject = it.next();
//				Class clazz = subject.getClass();
//				do {
//					eventsByClass.put(clazz, event);
//					clazz = clazz.getSuperclass();
//				} while (clazz != null);
//			}
//			for (Iterator it = event.getSubjectClassesToClear().iterator(); it.hasNext(); ) {
//				Class clazz = (Class) it.next();
//				do {
//					eventsByClass.put(clazz, event);
//					clazz = clazz.getSuperclass();
//				} while (clazz != null);
//			}

		} finally {
			eventsByZoneMutex.releaseLock();
		}
	}

	/**
	 * @see org.nightlabs.notification.NotificationManager#addNotificationListener(java.lang.String, java.lang.Class, org.nightlabs.notification.NotificationListener)
	 */
	public void addNotificationListener(
			String zone, Class subjectClass, final NotificationListener listener)
	{
		super.addNotificationListener(zone, subjectClass, listener);

		eventsByZoneMutex.acquireReadLock();
		try {
			Map eventsByClass = (Map) eventsByZone.get(zone);
			if (eventsByClass == null) {
				if (zone != null) {
					eventsByClass = (Map) eventsByZone.get(null);
					if (eventsByClass == null)
						return;
				}
				else
					return;
			}

			final NotificationEvent event = (NotificationEvent) eventsByClass.get(subjectClass);
			if (event == null)
				return;

			// Can we call this directly? Does this break any other code? Marco.
			// Please do not switch it back to asynchronous notification, if it works this way, because
			// we have now code that relies on the listener being triggered before this method returns: org.nightlabs.jfire.trade.legalentity.view.LegalEntitySelectionComposite#initGUI()
			SelectionManager.this.notify(event, listener);
//			Display.getDefault().asyncExec(
//					new Runnable() {
//						public void run()
//						{
//							SelectionManager.this.notify(event, listener);
//						}
//					});
		} finally {
			eventsByZoneMutex.releaseLock();
		}
	}

	/**
	 * @see org.nightlabs.notification.NotificationManager#intercept(org.nightlabs.notification.NotificationEvent)
	 */
	protected NotificationEvent intercept(NotificationEvent event)
	{
		event = super.intercept(event);
		registerEvent(event);
		return event;
	}

}
