package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorConfigFileProvider extends AbstractFileProvider {

	public FCKEditorConfigFileProvider(IFCKEditor editor) {
		super(editor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getFileContents(java.lang.String)
	 */
	@Override
	public InputStream getFileContents(String filename, Properties parms) throws IOException {
		String configContents = 
			"FCKConfig.AutoDetectLanguage = false ;\n"+
	        "FCKConfig.DefaultLanguage = '"+Locale.getDefault().getLanguage()+"' ;\n"+
	        "FCKConfig.SkinPath = '"+getEditor().getBaseUrl()+"/fckeditor-skin/';\n"+
//			"FCKConfig.SkinPath = '"+path+"/fckeditor/editor/skins/office2003/';\n"+
//			"FCKConfig.ToolbarSets[\"Default\"] = [\n"+
//			"                                	['Source','DocProps','-','Save','NewPage','Preview','-','Templates'],\n"+
//			"                                	['Cut','Copy','Paste','PasteText','PasteWord','-','Print','SpellCheck'],\n"+
//			"                                	['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],\n"+
//			"                                	['Form','Checkbox','Radio','TextField','Textarea','Select','Button','ImageButton','HiddenField'],\n"+
//			"                                	'/',\n"+
//			"                                	['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],\n"+
//			"                                	['OrderedList','UnorderedList','-','Outdent','Indent','Blockquote'],\n"+
//			"                                	['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],\n"+
//			"                                	['Link','Unlink','Anchor'],\n"+
//			"                                	['Image','Flash','Table','Rule','Smiley','SpecialChar','PageBreak'],\n"+
//			"                                	'/',\n"+
//			"                                	['Style','FontFormat','FontName','FontSize'],\n"+
//			"                                	['TextColor','BGColor'],\n"+
//			"                                	['FitWindow','ShowBlocks','-','About']\n"+
//			"                                ] ;\n"+
			"FCKConfig.ToolbarSets[\"Default\"] = [\n"+
			"                                	['Source','-','Save','-','Templates'],\n"+
			"                                	['Cut','Copy','Paste','PasteText','PasteWord','-','Print','SpellCheck'],\n"+
			"                                	['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],\n"+
			"                                	'/',\n"+
			"                                	['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],\n"+
			"                                	['OrderedList','UnorderedList','-','Outdent','Indent','Blockquote'],\n"+
			"                                	['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],\n"+
			"                                	['Link','Unlink','Anchor'],\n"+
			"                                	['Image','Table','Rule','Smiley','SpecialChar','PageBreak'],\n"+
			"                                	'/',\n"+
			"                                	['Style','FontFormat','FontName','FontSize'],\n"+
			"                                	['TextColor','BGColor'],\n"+
			"                                	['ShowBlocks']\n"+
			"                                ] ;\n"+
			"FCKConfig.LinkBrowserURL = '"+getEditor().getBaseUrl()+"/nlfinder/nlfinder.html' ;\n"+
			"FCKConfig.ImageBrowserURL = '"+getEditor().getBaseUrl()+"/nlfinder/nlfinder.html?type=Images' ;\n"+
			"FCKConfig.FlashBrowserURL = '"+getEditor().getBaseUrl()+"/nlfinder/nlfinder.html?type=Flash' ;\n"+
			"FCKConfig.LinkUploadURL = '"+getEditor().getBaseUrl()+"/nlfinder/upload.html?command=QuickUpload&type=Files' ;\n"+
			"FCKConfig.ImageUploadURL = '"+getEditor().getBaseUrl()+"/nlfinder/upload.html?command=QuickUpload&type=Images' ;\n"+
			"FCKConfig.FlashUploadURL = '"+getEditor().getBaseUrl()+"/nlfinder/upload.html?command=QuickUpload&type=Flash' ;\n"+			
			"";
		return new ByteArrayInputStream(configContents.getBytes());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath() {
		return "/editorconfig.js";
	}

}
