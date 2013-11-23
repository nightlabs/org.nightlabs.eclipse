package org.nightlabs.jseditor.ui.rcp.editor.contentassist;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContentAssistantVariable {
	private List<String> wordList = new ArrayList<String>();
	public abstract void preparedWordList();
	public List<String> getWordList(){
		return wordList;
	}
}
