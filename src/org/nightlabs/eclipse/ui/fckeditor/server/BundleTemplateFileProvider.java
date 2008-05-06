package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class BundleTemplateFileProvider extends BundleFileProvider
{
	/**
	 * Create a new BundleTemplateFileProvider instance.
	 * @param editor
	 */
	public BundleTemplateFileProvider(IFCKEditor editor)
	{
		super(editor);
	}

	@Override
	protected String getBundleFilename(String subUri)
	{
		return "/serverfiles/fckeditor-custom" + subUri;
	}
	
	@Override
	public InputStream getFileContents(String subUri, Properties parms) throws IOException
	{
		InputStream in = super.getFileContents(subUri, parms);
		if(in == null)
			return null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		while(true) {
			String line = reader.readLine();
			if(line == null)
				break;
			for (Map.Entry<String, String> e : getReplacements().entrySet())
				line = line.replace("${"+e.getKey()+"}", e.getValue());
			sb.append(line);
			sb.append('\n');
		}
		return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
	}
	
	protected Map<String, String> getReplacements()
	{
		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("baseUrl", getEditor().getBaseUrl());
		replacements.put("editorId", getEditor().getFCKEditorId());
		return replacements;
	}
	
}
