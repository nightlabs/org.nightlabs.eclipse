package org.eclipse.swt.widgets;


public class FileDialog extends Dialog {
	public FileDialog(Shell aParent, int aStyle) {
		super(aParent, aStyle);
	}

	public FileDialog(Shell aParent) {
		super(aParent);
	}


	public void setFilterExtensions(String[] extensions) {

	}

	public void setFilterNames(String[] extensions) {

	}

	private String filterPath;
	public void setFilterPath(String path) {
		this.filterPath = path;
	}


	public String open() {
		return null;
	}

	public void setFileName(String fileName) {
	}

	public String getFileName() {
		return null;
	}
	public String[] getFileNames() {
		return null;
	}
	public String getFilterPath() {
		return filterPath;
	}
}
