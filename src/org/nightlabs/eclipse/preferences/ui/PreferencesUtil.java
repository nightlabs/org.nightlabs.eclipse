package org.nightlabs.eclipse.preferences.ui;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Preferences utility methods.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision: 1734 $ - $Date: 2008-01-08 17:02:20 +0100 (Di, 08 Jan 2008) $
 */
public class PreferencesUtil
{
	/**
	 * Deserialize an object from a string.
	 * <p>
	 * This implementation makes use of the {@link XMLDecoder} class,
	 * thus only Java standard types and Beans are supported.
	 * </p>
	 * <p>
	 * <strong>Note:</strong> When using this method from another eclipse
	 * plugin, you might need to add
	 * <strong><code>Eclipse-RegisterBuddy: org.nightlabs.eclipse.preference.ui</code></strong>
	 * to your MANIFEST.MF file.
	 * </p>
	 * @param string The string to deserialize
	 * @param <T> The type to be returned. If the serialized object is
	 * 		not of type T, this implementation returns <code>null</code>
	 * 		and logs the class cast exception.
	 * @return The deserialized Object
	 */
	public static <T> T deserialize(String string)
	{
		// The code "return deserialize(...)" compiles in eclipse but not with Sun compiler.
		// According to http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954 this is a workaround:
		return PreferencesUtil.<T>deserialize(string, PreferencesUtil.class.getClassLoader());
	}

	/**
	 * Deserialize an object from a string.
	 * <p>
	 * This implementation makes use of the {@link XMLDecoder} class,
	 * thus only Java standard types and Beans are supported.
	 * </p>
	 * @param string The string to deserialize
	 * @param classLoader The class loader to use within the XMLDecoder
	 * 		load classes.
	 * @param <T> The type to be returned. If the serialized object is
	 * 		not of type T, this implementation returns <code>null</code>
	 * 		and logs the class cast exception.
	 * @return The deserialized Object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(String string, ClassLoader classLoader)
	{
		if(string == null || string.trim().length() == 0)
			return null;
		ByteArrayInputStream in;
		try {
			in = new ByteArrayInputStream(string.getBytes("UTF-8")); //$NON-NLS-1$
		} catch(UnsupportedEncodingException e) {
			// this should never happen
			throw new RuntimeException(e);
		}
		XMLDecoder dec = new XMLDecoder(in, null, null, classLoader);
		try {
			T result = (T)dec.readObject();
			dec.close();
			return result;
		} catch(ClassCastException e) {
			PreferencesUIPlugin.log(e);
		}
		return null;
	}

	/**
	 * Serialize an object to a string.
	 * <p>
	 * This implementation makes use of the {@link XMLEncoder} class,
	 * thus only Java standard types and Beans are supported.
	 * </p>
	 * <p>
	 * <strong>Note:</strong> When using this method from another eclipse
	 * plugin, you might need to add
	 * <strong><code>Eclipse-RegisterBuddy: org.nightlabs.eclipse.preference.ui</code></strong>
	 * to your MANIFEST.MF file.
	 * </p>
	 * @param object The object to serialize
	 * @return The serialized object as string
	 */
	public static String serialize(Object object)
	{
		if(object == null)
			return null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLEncoder enc = new XMLEncoder(out);
		enc.writeObject(object);
		enc.close();
		try {
			return out.toString("UTF-8"); //$NON-NLS-1$
		} catch(UnsupportedEncodingException e) {
			PreferencesUIPlugin.log(e);
			return null;
		}
	}
}
