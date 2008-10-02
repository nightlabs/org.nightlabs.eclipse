package org.nightlabs.eclipse.ui.pdfviewer.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.nightlabs.eclipse.ui.pdfviewer.resource.Messages;

/**
 * Utility methods used throughout the PDF viewer plug-in. All these methods have
 * been copied from the classes <code>org.nightlabs.util.Util</code>,
 * <code>org.nightlabs.util.IOUtil</code>,
 * <code>org.nightlabs.base.ui.util.RCPUtil</code> in order to avoid a dependency
 * on the plug-in <code>org.nightlabs.base.ui</code> just for these few methods.
 *
 * @author marco schulze - marco at nightlabs dot de
 * @author frederik löser - frederik at nightlabs dot de
 */
public final class Util {
	private Util() { }

	/**
	 * Check two objects for equality.
	 * <p>
	 * This method is a convenience reducing code:
	 * <code>obj0 == obj1 || (obj0 != null && obj0.equals(obj1))</code>
	 * will be replaced by <code>Utils.equals(obj0, obj1)</code>
	 * and you do not need to worry about <code>null</code> anymore.
	 * <p>
	 * Additionally if you pass two arrays to this method
	 * (whose equals method only checks for equality [TODO doesn't this mean "identity" instead of "equality"?])
	 * this method will consult {@link Arrays#equals(Object[], Object[])}
	 * for equality of the parameters instead, of course after a <code>null</code> check.
	 *
	 * @param obj0 One object to check for equality
	 * @param obj1 The other object to check for equality
	 * @return <code>true</code> if both objects are <code>null</code> or
	 * 		if they are equal or if both objects are Object arrays
	 *    and equal according to {@link Arrays#equals(Object[], Object[])} -
	 *    <code>false</code> otherwise
	 */
	public static boolean equals(Object obj0, Object obj1)
	{
		if (obj0 instanceof Object[] && obj1 instanceof Object[])
			return obj0 == obj1 || Arrays.equals((Object[])obj0, (Object[])obj1);
		return obj0 == obj1 || (obj0 != null && obj0.equals(obj1));
	}

	/**
	 * Check two <code>long</code>s for equality.
	 * <p>
	 * In order to provide the same API for <code>Object</code> and <code>long</code>
	 * which both are often used as IDs, it is recommended to use this method
	 * instead of writing <code>id0 == id1</code>.
	 * </p>
	 * <p>
	 * To write <code>id0 == id1</code> is considered more error-prone if refactorings happen:
	 * Imagine, you create an object with a long unique id. Later on, you decide that a String id
	 * is better. You won't recognize that some old code <code>id0 == id1</code> is broken.
	 * When using this method instead, the compiler will automatically switch to {@link #equals(Object, Object)}
	 * and a correct result will be calculated.
	 * </p>
	 * <p>
	 * Even though Java 5 (and higher) implicitely converts simple datatypes to their corresponding object
	 * (e.g. <code>long</code> to <code>java.lang.Long</code>), it's unnecessary to perform this conversion
	 * and better to have this method instead.
	 * </p>
	 *
	 * @param l0 One long to check.
	 * @param l1 The other long to check.
	 * @return the result of: <code>l0 == l1</code>.
	 * @see #equals(Object, Object)
	 */
	public static boolean equals(long l0, long l1)
	{
		return l0 == l1;
	}
	/**
	 * Check two <code>int</code>s for equality.
	 * <p>
	 * This method does the same for <code>int</code>s as {@link #equals(long, long)}
	 * does for <code>long</code>s.
	 * </p>
	 *
	 * @param i0 One int to check.
	 * @param i1 The other int to check.
	 * @return the result of: <code>i0 == i1</code>.
	 * @see #equals(long, long)
	 */
	public static boolean equals(int i0, int i1)
	{
		return i0 == i1;
	}

	/**
	 * @param l The long number for which to calculate the hashcode.
	 * @return the same as new Long(l).hashCode() would do, but
	 * 		without the overhead of creating a Long instance.
	 */
	public static int hashCode(long l)
	{
		return (int)(l ^ (l >>> 32));
	}

	/**
	 * Get a hash code for an object. This method also handles
	 * <code>null</code>-Objects.
	 * @param obj An object or <code>null</code>
	 * @return <code>0</code> if <code>obj == null</code> -
	 * 		<code>obj.hashCode()</code> otherwise
	 */
	public static int hashCode(Object obj)
	{
		return obj == null ? 0 : obj.hashCode();
	}


//	/**
//	 * Returns the active WorkbenchWindow's active page.
//	 * @return The active WorkbenchWindow's active page.
//	 */
//	public static IWorkbenchWindow getActiveWorkbenchWindow() {
//		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//	}
//
//	/**
//	 * Returns the active WorkbenchWindow's active page.
//	 * @return The active WorkbenchWindow's active page.
//	 */
//	public static IWorkbenchPage getActiveWorkbenchPage() {
//		IWorkbenchWindow window = getActiveWorkbenchWindow();
//		return window == null ? null : window.getActivePage();
//	}


//	/**
//	 * If there is an open editor for the given <code>input</code>,
//	 * it will be closed. If no editor can be found, this
//	 * method is a no-op.
//	 *
//	 * @param input The input specifying the editor to close.
//	 * @param save Whether or not to save - this is passed to {@link IWorkbenchPage#closeEditor(IEditorPart, boolean)}.
//	 * @return <code>true</code>, if no open editor was found or if it has been successfully closed.
//	 *		<code>false</code>, if the open editor for the given <code>input</code> was not closed (e.g. because
//	 *		the user cancelled closing in case <code>save == true</code>).
//	 */
//	public static boolean closeEditor(IEditorInput input, boolean save) {
//		IWorkbenchPage page = getActiveWorkbenchPage();
//		IEditorPart editor = page.findEditor(input);
//		if (editor == null)
//			return true;
//
//		return page.closeEditor(editor, save);
//	}

	private static File tempDir = null;

	/**
	 * Get the temporary directory.
	 * <p>
	 * Note, that you should better use {@link #getUserTempDir()} in many situations
	 * since there is solely one global temp directory in GNU/Linux and you might run into permissions trouble
	 * and other collisions when using the global temp directory with a hardcoded static subdir.
	 * </p>
	 *
	 * @return The temporary directory.
	 * @see #getUserTempDir()
	 */
	public static File getTempDir()
	{
		if(tempDir == null)
	    tempDir = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
		return tempDir;
	}


	/**
	 * Transfer data between streams.
	 * @param in The input stream
	 * @param out The output stream
	 * @param inputOffset How many bytes to skip before transferring
	 * @param inputLen How many bytes to transfer. -1 = all
	 * @return The number of bytes transferred
	 * @throws IOException if an error occurs.
	 */
	public static long transferStreamData(InputStream in, OutputStream out, long inputOffset, long inputLen)
	throws java.io.IOException
	{
		int bytesRead;
		int transferred = 0;
		byte[] buf = new byte[4096];

		//skip offset
		if(inputOffset > 0)
			if(in.skip(inputOffset) != inputOffset)
				throw new IOException("Input skip failed (offset "+inputOffset+")"); //$NON-NLS-1$ //$NON-NLS-2$

		while (true) {
			if(inputLen >= 0)
				bytesRead = in.read(buf, 0, (int)Math.min(buf.length, inputLen-transferred));
			else
				bytesRead = in.read(buf);

			if (bytesRead <= 0)
				break;

			out.write(buf, 0, bytesRead);

			transferred += bytesRead;

			if(inputLen >= 0 && transferred >= inputLen)
				break;
		}
		out.flush();
		return transferred;
	}

	/**
	 * Transfer all available data from an {@link InputStream} to an {@link OutputStream}.
	 * <p>
	 * This is a convenience method for <code>transferStreamData(in, out, 0, -1)</code>
	 * @param in The stream to read from
	 * @param out The stream to write to
	 * @return The number of bytes transferred
	 * @throws IOException In case of an error
	 */
	public static long transferStreamData(InputStream in, OutputStream out)
	throws java.io.IOException
	{
		return transferStreamData(in, out, 0, -1);
	}

//	/**
//	 * Opens an editor with the given input and editorID and returns it.
//	 *
//	 * @param input The editors input
//	 * @param editorID The editors id
//	 * @return The editor opened
//	 * @throws PartInitException
//	 */
//	public static IEditorPart openEditor(IEditorInput input, String editorID)
//	throws PartInitException
//	{
//		return getActiveWorkbenchPage().openEditor(input, editorID);
//	}
//
//	/**
//	 * Opens an editor with the given input and editorID and returns it.
//	 *
//	 * @param input The editors input
//	 * @param editorID The editors id
//	 * @return The editor opened
//	 * @throws PartInitException
//	 */
//	public static IEditorPart openEditor(IEditorInput input, String editorID, boolean activate)
//	throws PartInitException
//	{
//		return getActiveWorkbenchPage().openEditor(input, editorID, activate);
//	}
}
