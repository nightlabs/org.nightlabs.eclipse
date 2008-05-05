package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;


/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorHTTPD extends NanoHTTPD 
{
	private static final int defaultPort = 8080;

	private static FCKEditorHTTPD nh;

	public static synchronized FCKEditorHTTPD sharedInstance()
	{
		if(nh == null) {
			try
			{
				nh = new FCKEditorHTTPD( defaultPort );
			}
			catch( IOException ioe )
			{
				System.err.println( "Couldn't start server:\n" + ioe );
			}
		}
		return nh;
	}

	private Map<IFCKEditor, List<FileProvider>> fileProviders = new HashMap<IFCKEditor, List<FileProvider>>();
	
	public void addFileProvider(IFCKEditor editor, FileProvider fileProvider)
	{
		fileProviders.get(editor).add(fileProvider);
	}
	
	/**
	 * Create a new MyHTTPD instance.
	 * @param port
	 * @throws IOException
	 */
	public FCKEditorHTTPD(int port) throws IOException {
		super(port);
	}

//	private static String getMimeType(String uri)
//	{
//		String mime = null;
//		int dot = uri.lastIndexOf( '.' );
//		if ( dot >= 0 )
//			mime = (String)theMimeTypes.get( uri.substring( dot + 1 ).toLowerCase());
//		if ( mime == null )
//			mime = MIME_DEFAULT_BINARY;
//		return mime;
//	}
//	
//	/**
//	 * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
//	 */
//	private static Hashtable theMimeTypes = new Hashtable();
//	static
//	{
//		StringTokenizer st = new StringTokenizer(
//			"htm		text/html "+
//			"html		text/html "+
//			"txt		text/plain "+
//			"asc		text/plain "+
//			"xml		text/xml "+
//			"css		text/css "+
//			"js		text/javascript "+
//			"gif		image/gif "+
//			"jpg		image/jpeg "+
//			"jpeg		image/jpeg "+
//			"png		image/png "+
//			"mp3		audio/mpeg "+
//			"m3u		audio/mpeg-url " +
//			"pdf		application/pdf "+
//			"doc		application/msword "+
//			"ogg		application/x-ogg "+
//			"zip		application/octet-stream "+
//			"exe		application/octet-stream "+
//			"class		application/octet-stream " );
//		while ( st.hasMoreTokens())
//			theMimeTypes.put( st.nextToken(), st.nextToken());
//	}
	
	@Override
	public Response serve(String uri, String method, Properties header,	Properties parms) {
		
		String path;
		if(!uri.startsWith("/"))
			throw new IllegalStateException("Illegal URI: "+uri);
		int idx = uri.indexOf('/', 1);
		if(idx == -1)
			throw new IllegalStateException("Illegal URI: "+uri);
		path = uri.substring(0, idx);
		
		IFCKEditor editor = getEditor(path);
		if(editor == null)
			throw new IllegalStateException("No editor");
		
		
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
		/*
		//System.out.println("SERVE: "+uri);
		String urlPrefix = path+"/fckeditor/";
		Response response = null;
		if(uri.startsWith(urlPrefix)) {
			String resource = "fckeditor/"+uri.substring(urlPrefix.length());
			InputStream in = getClass().getResourceAsStream(resource);
			if(in != null)
				response = new Response(HTTP_OK, getMimeType(uri), in);
		} else if(uri.startsWith(path+"/fckeditor-skin/")) {
			String filename = uri.substring((path+"/fckeditor-skin/").length());
			InputStream in = getSkinFileContents(filename, editor);
			if(in != null)
				response = new Response(HTTP_OK, getMimeType(uri), in);
		} else if((path+"/edit.html").equals(uri)){
//			in = getClass().getResourceAsStream("edit.html");
//			if(in != null)
//				response = new Response(HTTP_OK, MIME_HTML, in);
			response = new Response(HTTP_OK, MIME_HTML, getEditContents(path, editor.getEditorInput().getEditorContent().getHtml(), "Loading..."));
		} else if((path+"/save.html").equals(uri)){
			String html = parms.getProperty("FCKeditor_"+path.substring(1));
			if(html == null)
				throw new IllegalStateException("No data to save");
			editor.getEditorInput().getEditorContent().setHtml(html);
			String editContents = getEditContents(path, editor.getEditorInput().getEditorContent().getHtml(), "Saving...");
			response = new Response(HTTP_OK, MIME_HTML, editContents);
		} else if((path+"/sample.css").equals(uri)){
			InputStream in = getClass().getResourceAsStream("sample.css");
			if(in != null)
				response = new Response(HTTP_OK, "text/css", in);
		} else if((path+"/editorconfig.js").equals(uri)){
			String editorConfig = getEditorConfig(path);
			System.out.println("Config:\n"+editorConfig);
			response = new Response(HTTP_OK, "text/javascript", editorConfig);
		}
		*/
		if(response == null) {
			// TODO: logger
			System.err.println("Unable to serve: "+uri);
			response = new Response(HTTP_NOTFOUND, MIME_PLAINTEXT, "Not found");
		}
		return response;
	}
	
	private Map<IFCKEditor, String> pathByEditor = new HashMap<IFCKEditor, String>();
	private Map<String, IFCKEditor> editorByPath = new HashMap<String, IFCKEditor>();
	Random rand = new Random();
	
	private String getRandomPath()
	{
		long l = rand.nextLong();
		return "/"+Long.toHexString(l);
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
		return "http://127.0.0.1:8080"+path;
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
	}
	
//	private static String escapeContents(String contents)
//	{
//		return contents
//				.replace("\\", "\\\\")
//				.replace("'", "\\'")
//				.replace("\n", "\\n")
//				.replace("\r", "");
//	}
//	
//	private static String getEditContents(String path, String contents, String loadingPaneText)
//	{
//		String editContents =
//		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"+
//		"<head>\n"+
//		"        <title>FCKeditor</title>\n"+
//		"        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"+
//		"        <meta name=\"robots\" content=\"noindex, nofollow\" />\n"+
//		"        <link href=\""+path+"/sample.css\" rel=\"stylesheet\" type=\"text/css\" />\n"+
//		"        <script type=\"text/javascript\" src=\""+path+"/fckeditor/fckeditor.js\"></script>\n"+
//		"</head>\n"+
//		"<body>\n";
//		if(loadingPaneText != null)
//			editContents +=
//		"<div id=\"loadingpane\" style=\"position:absolute; top:0px; left:0px; width: 100%; height: 150px; z-index:100; background: white; padding: 5px;\">"+loadingPaneText+"</div>\n";
//		editContents +=
//		"        <form action=\""+path+"/save.html\" method=\"post\">\n"+
//		"                <script type=\"text/javascript\">\n"+
//		"<!--\n"+
//		"var sBasePath = '"+path+"/fckeditor/';\n"+
//		"var dirtyCheckInterval = 0;\n"+
//		"var oFCKeditor = new FCKeditor( 'FCKeditor_"+path.substring(1)+"' ) ;\n"+
//		"oFCKeditor.BasePath     = sBasePath ;\n"+
//		"oFCKeditor.Height       = 100;\n"+
//		"oFCKeditor.Value        = '"+escapeContents(contents)+"';\n"+
//		"oFCKeditor.Config[\"CustomConfigurationsPath\"] = '"+path+"/editorconfig.js';\n"+
//		"oFCKeditor.Create() ;\n"+
//		"function FCKeditor_OnComplete( editorInstance )\n"+
//		"{\n"+
//		"    editorInstance.Commands.GetCommand('FitWindow').Execute();\n"+
//		"    loadingpane = document.getElementById('loadingpane');\n"+
//		"    loadingpane.style.display='none';\n"+
//		"    \n"+
//		"    dirtyCheckInterval = setInterval('dirtyCheck()', 250);\n"+
//		"    \n"+
//		"    \n"+
//		"    \n"+
//		"}\n"+
//		"function dirtyCheck()\n"+
//		"{\n"+
//		"    oEditor = FCKeditorAPI.GetInstance('FCKeditor_"+path.substring(1)+"') ;\n"+
//		"    if(oEditor.IsDirty()) {\n"+
//		"      alert('dirty');\n"+
//		"      clearInterval(dirtyCheckInterval);\n"+
//		"    }\n"+
//		"}\n"+
//		"//-->\n"+
//		"                </script>\n"+
//		"        </form>\n"+
//		"</body>\n"+
//		"</html>\n";
//		return editContents;
//	}
//	
//	private static String getEditorConfig(String path)
//	{
//		return 
//			"FCKConfig.AutoDetectLanguage = false ;\n"+
//	        "FCKConfig.DefaultLanguage = '"+Locale.getDefault().getLanguage()+"' ;\n"+
//	        "FCKConfig.SkinPath = '"+path+"/fckeditor-skin/';\n"+
////			"FCKConfig.SkinPath = '"+path+"/fckeditor/editor/skins/office2003/';\n"+
////			"FCKConfig.ToolbarSets[\"Default\"] = [\n"+
////			"                                	['Source','DocProps','-','Save','NewPage','Preview','-','Templates'],\n"+
////			"                                	['Cut','Copy','Paste','PasteText','PasteWord','-','Print','SpellCheck'],\n"+
////			"                                	['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],\n"+
////			"                                	['Form','Checkbox','Radio','TextField','Textarea','Select','Button','ImageButton','HiddenField'],\n"+
////			"                                	'/',\n"+
////			"                                	['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],\n"+
////			"                                	['OrderedList','UnorderedList','-','Outdent','Indent','Blockquote'],\n"+
////			"                                	['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],\n"+
////			"                                	['Link','Unlink','Anchor'],\n"+
////			"                                	['Image','Flash','Table','Rule','Smiley','SpecialChar','PageBreak'],\n"+
////			"                                	'/',\n"+
////			"                                	['Style','FontFormat','FontName','FontSize'],\n"+
////			"                                	['TextColor','BGColor'],\n"+
////			"                                	['FitWindow','ShowBlocks','-','About']\n"+
////			"                                ] ;\n"+
//			"FCKConfig.ToolbarSets[\"Default\"] = [\n"+
//			"                                	['Source','-','Save','-','Templates'],\n"+
//			"                                	['Cut','Copy','Paste','PasteText','PasteWord','-','Print','SpellCheck'],\n"+
//			"                                	['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],\n"+
//			"                                	'/',\n"+
//			"                                	['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],\n"+
//			"                                	['OrderedList','UnorderedList','-','Outdent','Indent','Blockquote'],\n"+
//			"                                	['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],\n"+
//			"                                	['Link','Unlink','Anchor'],\n"+
//			"                                	['Image','Table','Rule','Smiley','SpecialChar','PageBreak'],\n"+
//			"                                	'/',\n"+
//			"                                	['Style','FontFormat','FontName','FontSize'],\n"+
//			"                                	['TextColor','BGColor'],\n"+
//			"                                	['ShowBlocks']\n"+
//			"                                ] ;\n"+
//			"";
//	}
//	
//	private static InputStream getSkinFileContents(String filename, IFCKEditor editor)
//	{
//		try {
//			String resource = "fckeditor/editor/skins/mk/"+filename;
//			InputStream in = FCKEditorHTTPD.class.getResourceAsStream(resource);
//			if(in != null && "fck_editor.css".equals(filename)) {
//				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//				StringBuilder contents = new StringBuilder();
//				while(true) {
//					String line = reader.readLine();
//					if(line == null)
//						break;
//					contents.append(line.replace("#eeeff2", editor.getWidgetBackgroundColor()));
//				}
//				ByteArrayInputStream in2 = new ByteArrayInputStream(contents.toString().getBytes("UTF-8"));
//				return in2;
//			}
//			return in;
//		} catch(IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
}
