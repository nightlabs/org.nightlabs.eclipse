package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;


/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorHTTPD extends NanoHTTPD
{
	private static final int minPort = 1024;
	private static final int maxPort = 65535;
	//private static final int defaultPort = 8080;
	private static final int maxPortTries = 10;
	private int port;

	private static FCKEditorHTTPD sharedInstance;

	public static FCKEditorHTTPD sharedInstance() throws IOException
	{
		synchronized(FCKEditorHTTPD.class) {
			if(sharedInstance == null)
				createInstance();
			return sharedInstance;
		}
	}

	private static void createInstance() throws IOException
	{
		Random rand = new Random();
		IOException ex = null;
		for(int i=0; i<=maxPortTries; i++) {
			int port = rand.nextInt(maxPort - minPort + 1) + minPort;
			try {
				sharedInstance = new FCKEditorHTTPD(port);
				Activator.log("Using port "+port); //$NON-NLS-1$
				break;
			} catch(IOException e) {
				sharedInstance = null;
				ex = e;
				Activator.warn("Port "+port+" unusable: "+e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		if(sharedInstance == null)
			throw new IOException(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorHTTPD.bindErrorText"), ex); //$NON-NLS-1$
	}

	private Map<IFCKEditor, List<FileProvider>> fileProviders = new HashMap<IFCKEditor, List<FileProvider>>();

	public void addFileProvider(IFCKEditor editor, FileProvider fileProvider)
	{
		fileProviders.get(editor).add(fileProvider);
	}

	/**
	 * Create a new MyHTTPD instance.
	 */
	public FCKEditorHTTPD(int port) throws IOException
	{
		// InetAddress.getLocalHost() results in binding tcp6 only... :-(
		super(port, InetAddress.getByName("127.0.0.1")); //InetAddress.getLocalHost()); //$NON-NLS-1$
		this.port = port;
	}

	@Override
	public Response serve(String uri, String method, Properties header,	Properties parms)
	{
		// TODO: remove debug
		System.out.println("URI: "+uri); //$NON-NLS-1$
		String path;
		if(!uri.startsWith("/")) //$NON-NLS-1$
			throw new IllegalStateException("Illegal URI: "+uri); //$NON-NLS-1$
		int idx = uri.indexOf('/', 1);
		if(idx == -1)
			throw new IllegalStateException("Illegal URI: "+uri); //$NON-NLS-1$
		path = uri.substring(0, idx);

		IFCKEditor editor = getEditor(path);
		if(editor == null)
			throw new IllegalStateException("No editor"); //$NON-NLS-1$

		Response response = null;
		for (FileProvider fileProvider : fileProviders.get(editor)) {
			String subUri = uri.substring(path.length());
			String providerPath = fileProvider.getPath();
			if(subUri.startsWith(providerPath)) {
				InputStream in = null;
				try {
					in = fileProvider.getFileContents(subUri, parms);
				} catch (IOException e) {
					// TODO: logger
					e.printStackTrace();
				}
				if(in != null) {
					response = new Response(
							HTTP_OK,
							fileProvider.getContentType(subUri),
							in);
					break;
				}
			}
		}
		if(response == null) {
			Activator.warn("Unable to serve: "+uri); //$NON-NLS-1$
			response = new Response(HTTP_NOTFOUND, MIME_PLAINTEXT, "Not found"); //$NON-NLS-1$
		}
		return response;
	}

	private Map<IFCKEditor, String> pathByEditor = new HashMap<IFCKEditor, String>();
	private Map<String, IFCKEditor> editorByPath = new HashMap<String, IFCKEditor>();
	Random rand = new Random();

	private String getRandomPath()
	{
		long l = rand.nextLong();
		return "/"+Long.toHexString(l); //$NON-NLS-1$
	}

	public synchronized void addEditor(IFCKEditor editor)
	{
		String path = getRandomPath();
		pathByEditor.put(editor, path);
		editorByPath.put(path, editor);
		fileProviders.put(editor, new ArrayList<FileProvider>());
	}

	public synchronized String getPath(IFCKEditor editor)
	{
		return pathByEditor.get(editor);
	}

	public String getUrl(IFCKEditor editor)
	{
		String path = getPath(editor);
		if(path == null)
			return null;
		return "http://127.0.0.1:"+port+path; //$NON-NLS-1$
	}

	public synchronized IFCKEditor getEditor(String path)
	{
		return editorByPath.get(path);
	}

	public synchronized void removeEditor(IFCKEditor editor)
	{
		String url = pathByEditor.remove(editor);
		if(url != null)
			editorByPath.remove(url);
		fileProviders.remove(editor);
		synchronized(FCKEditorHTTPD.class) {
			if(pathByEditor.isEmpty()) {
				//System.out.println("Shutting down.");
				shutdown();
				sharedInstance = null;
			}
		}
	}
}
