/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.ui.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.nightlabs.concurrent.RWLock;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.notification.SubjectCarrier;

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
	private Map<String, Map<Class<?>, NotificationEvent>> eventsByZone = new HashMap<String, Map<Class<?>, NotificationEvent>>();
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

			for (Iterator<String> itZones = zones.iterator(); itZones.hasNext(); ) {
				zone = itZones.next();

				Map<Class<?>, NotificationEvent> eventsByClass = eventsByZone.get(zone);
				if (eventsByClass == null) {
					eventsByClass = new HashMap<Class<?>, NotificationEvent>();
					eventsByZone.put(zone, eventsByClass);
				}

				for (Iterator<SubjectCarrier> itSubjectCarriers = event.getSubjectCarriers().iterator(); itSubjectCarriers.hasNext(); ) {
					SubjectCarrier carrier = itSubjectCarriers.next();
					for (Iterator<Class<?>> itClasses = carrier.getSubjectClasses().iterator(); itClasses.hasNext(); ) {
						Class<?> clazz = itClasses.next();

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

// That's wrong, IMHO! Commented the whole method out. Marco.
//	/**
//	 * Shouldn't we remove the event also?, @see line - 171 {@link SelectionManager#addNotificationListener(String, Class, NotificationListener)}
//	 * ,Chairat
//	 */
//	@Override
//	public void removeNotificationListener(String zone, Class<?> subjectClass,
//			NotificationListener listener) {
//		super.removeNotificationListener(zone, subjectClass, listener);
//		Map<Class<?>, NotificationEvent> eventsByClass = eventsByZone.get(zone);
//		eventsByClass.remove(subjectClass);
//	}

	/**
	 * @see org.nightlabs.notification.NotificationManager#addNotificationListener(java.lang.String, java.lang.Class, org.nightlabs.notification.NotificationListener)
	 */
	@Override
	public void addNotificationListener(
			String zone, Class<?> subjectClass, final NotificationListener listener)
	{
		super.addNotificationListener(zone, subjectClass, listener);

		eventsByZoneMutex.acquireReadLock();
		try {
			Map<Class<?>, NotificationEvent> eventsByClass = eventsByZone.get(zone);
			if (eventsByClass == null) {
				if (zone != null) {
					eventsByClass = eventsByZone.get(null);
					if (eventsByClass == null)
						return;
				}
				else
					return;
			}

			final NotificationEvent event = eventsByClass.get(subjectClass);
			if (event == null)
				return;

			// Can we call this directly? Does this break any other code? Marco.
			// Please do not switch it back to asynchronous notification, if it works this way, because
			// we have now code that relies on the listener being triggered before this method returns: org.nightlabs.jfire.trade.ui.legalentity.view.LegalEntitySelectionComposite#initGUI()
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
	@Override
	protected NotificationEvent intercept(NotificationEvent event)
	{
		event = super.intercept(event);
		registerEvent(event);
		return event;
	}

}
