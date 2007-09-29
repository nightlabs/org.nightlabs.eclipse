package org.nightlabs.base.language;

import org.nightlabs.i18n.I18nText;

/**
 * @deprecated Marco: What's this interface good for? I think that's mumpiz ;-)
 */
public interface I18nTextTableListener {
		/**
		 * Update the view to reflect the fact that a I18nText was added 
		 * to the I18nText list
		 * 
		 * @param I18nText
		 */
		public void addI18nText(I18nText I18nText);

		/**
		 * Update the view to reflect the fact that a I18nText was removed 
		 * from the I18nText list
		 * 
		 * @param I18nText
		 */
		public void removeI18nText(I18nText i18nText);

		/**
		 * Update the view to reflect the fact that one of the I18nText
		 * was modified 
		 * 
		 * @param I18nText
		 */
		public void updateI18nText(I18nText i18nText);
	}