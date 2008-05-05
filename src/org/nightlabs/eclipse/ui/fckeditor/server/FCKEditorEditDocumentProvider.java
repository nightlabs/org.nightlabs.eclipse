package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorEditDocumentProvider extends AbstractFileProvider 
{
	public FCKEditorEditDocumentProvider(IFCKEditor editor) {
		super(editor);
	}

	protected String getLoadingPaneText()
	{
		return "Loading...";
	}
	
	protected String getFCKEditorId()
	{
		return "FCKeditor_"+getEditor().getBaseUrl();
	}
	
	private static String escapeContents(String contents)
	{
		if(contents == null)
			return "";
		return contents
				.replace("\\", "\\\\")
				.replace("'", "\\'")
				.replace("\n", "\\n")
				.replace("\r", "");
	}
	
	@Override
	public InputStream getFileContents(String filename, Properties parms) {
		String loadingPaneText = getLoadingPaneText();
		String editorId = getFCKEditorId();
		String contents = getEditor().getEditorInput().getEditorContent().getHtml();
		String escapedContents = escapeContents(contents);

		String editContents =
			"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"+
			"<head>\n"+
			"        <title>FCKeditor</title>\n"+
			"        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"+
			"        <meta name=\"robots\" content=\"noindex, nofollow\" />\n"+
			"        <link href=\""+getEditor().getBaseUrl()+"/style.css\" rel=\"stylesheet\" type=\"text/css\" />\n"+
			"        <script type=\"text/javascript\" src=\""+getEditor().getBaseUrl()+"/fckeditor/fckeditor.js\"></script>\n"+
			"</head>\n"+
			"<body style=\"overflow:hidden\">\n";
			if(loadingPaneText != null)
				editContents +=
			"<div id=\"loadingpane\" style=\"position:absolute; top:0px; left:0px; width: 100%; height: 150px; z-index:10000; background: white; padding: 5px;\">"+loadingPaneText+"</div>\n";
			editContents +=
			"        <form action=\""+getEditor().getBaseUrl()+"/save.html\" method=\"post\">\n"+
			"                <script type=\"text/javascript\">\n"+
			"<!--\n"+
			"var sBasePath = '"+getEditor().getBaseUrl()+"/fckeditor/';\n"+
			"var dirtyCheckInterval = 0;\n"+
			"var oFCKeditor = new FCKeditor( '"+editorId+"' ) ;\n"+
			"oFCKeditor.BasePath     = sBasePath ;\n"+
			"oFCKeditor.Height       = 100;\n"+
			"oFCKeditor.Value        = '"+escapedContents+"';\n"+
			"oFCKeditor.Config[\"CustomConfigurationsPath\"] = '"+getEditor().getBaseUrl()+"/editorconfig.js';\n"+
			"oFCKeditor.Create() ;\n"+
			"function dirtyCheck()\n"+
			"{\n"+
			"    oEditor = FCKeditorAPI.GetInstance('"+editorId+"') ;\n"+
			"    if(oEditor.IsDirty()) {\n"+
			"      clearInterval(dirtyCheckInterval);\n"+
		    "var xhr;\n"+
		    "try {  xhr = new ActiveXObject('Msxml2.XMLHTTP');   }\n"+
		    "catch (e)\n"+ 
		    "{\n"+
		    "    try {   xhr = new ActiveXObject('Microsoft.XMLHTTP');    }\n"+
		    "    catch (e2)\n"+ 
		    "    {\n"+
		    "      try {  xhr = new XMLHttpRequest();     }\n"+
		    "      catch (e3) {  xhr = false;   }\n"+
		    "    }\n"+
		    " }\n"+
		    " \n"+		  
		    "xhr.open('GET', '"+getEditor().getBaseUrl()+"/markdirty.xml', true);\n"+ 
		    "xhr.send(null);\n"+ 			
			"\n"+
			"      //alert('dirty');\n"+
			"    }\n"+
			"}\n"+
			"function FCKeditor_OnComplete( editorInstance )\n"+
			"{\n"+
			"    editorInstance.Commands.GetCommand('FitWindow').Execute();\n"+
			"    loadingpane = document.getElementById('loadingpane');\n"+
			"    loadingpane.style.display='none';\n"+
			"    editorInstance.Focus();\n"+
			"    \n"+
			"    dirtyCheckInterval = setInterval('dirtyCheck()', 250);\n"+
			"    \n"+
			"    \n"+
			"    \n"+
			"}\n"+
			"//-->\n"+
			"                </script>\n"+
			"        </form>\n"+
			"</body>\n"+
			"</html>\n";
			
			return new ByteArrayInputStream(editContents.getBytes());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath() 
	{
		return "/edit.html";
	}
}
