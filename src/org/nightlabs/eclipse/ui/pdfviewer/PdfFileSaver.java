package org.nightlabs.eclipse.ui.pdfviewer;



public class PdfFileSaver {

//	public static void savePdfAs(String pathName, IProgressMonitor monitor) throws IOException {
//
//		monitor.beginTask("Saving PDF file", 100);
//		File file = new File(pathName);
//		try {
//			// TODO do also consider URIs and input streams
//			FileInputStream fileInputStream = new FileInputStream(PdfFileLoader.getAbsolutePath());
//			try {
//				FileOutputStream fileOutputStream = new FileOutputStream(file, true);
//				try {
//					byte[] buffer = new byte[0xFFFF];
//					for (int len; (len = fileInputStream.read(buffer)) != -1;)
//						fileOutputStream.write(buffer, 0, len);
//
//				}
//				finally {
//					fileOutputStream.close();
//				}
//			}
//			finally {
//				fileInputStream.close();
//			}
//		}
//		finally {
//			monitor.done();
//		}
//	}
}
