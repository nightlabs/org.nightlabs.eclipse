/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.editor;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.PartStack;
import org.eclipse.ui.internal.WorkbenchPartReference;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.base.io.FileEditorInput;
import org.nightlabs.base.util.RCPUtil;

public class Editor2PerspectiveRegistry 
extends AbstractEPProcessor 
{
	private static final Logger logger = Logger.getLogger(Editor2PerspectiveRegistry.class);
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.editor2perspective"; //$NON-NLS-1$
	
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	private static Editor2PerspectiveRegistry sharedInstance;	
	public static Editor2PerspectiveRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new Editor2PerspectiveRegistry();
		return sharedInstance;
	}	
		
	private Map<String, String> editorID2PerspectiveID = new HashMap<String, String>();
	public String getPerspectiveID(String editorID) {
		checkProcessing();
		return (String) editorID2PerspectiveID.get(editorID);
	}

//	protected Map<String, Set<String>> editorID2PerspectiveIDs = new HashMap<String, Set<String>>();
//	public Set<String> getPerspectiveIDs(String editorID) {
//		checkProcessing();
//		return editorID2PerspectiveIDs.get(editorID);
//	}
		
	private Map<String, Set<String>> perspectiveID2editorIDs = new HashMap<String, Set<String>>();
	public Set<String> getEditorIDs(String perspectiveID) {
		checkProcessing();
		return perspectiveID2editorIDs.get(perspectiveID);
	}
	
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase("registry"))  //$NON-NLS-1$
		{
			String editorID = element.getAttribute("editorID"); //$NON-NLS-1$
			if (!checkString(editorID))
				throw new EPProcessorException("Element registry has to define attribute editorID."); //$NON-NLS-1$
			
			String perspectiveID = element.getAttribute("perspectiveID"); //$NON-NLS-1$
			if (!checkString(perspectiveID))
				throw new EPProcessorException("Element registry has to define attribute perspectiveID."); //$NON-NLS-1$
			
//			Set<String> perspectiveIDs = editorID2PerspectiveIDs.get(editorID);
//			if (perspectiveIDs == null)
//				perspectiveIDs = new HashSet<String>();			
//			perspectiveIDs.add(perspectiveID);
//			editorID2PerspectiveIDs.put(editorID, perspectiveIDs);
			editorID2PerspectiveID.put(editorID, perspectiveID);
			
			Set<String> editorIDs = perspectiveID2editorIDs.get(perspectiveID);
			if (editorIDs == null)
				editorIDs = new HashSet<String>();			
			editorIDs.add(editorID);
			perspectiveID2editorIDs.put(perspectiveID, editorIDs);			
		}
	}
	
	public void openEditor(IEditorInput input, String editorID)
	throws PartInitException, WorkbenchException
	{
	 	IPerspectiveRegistry perspectiveRegistry = PlatformUI.getWorkbench().getPerspectiveRegistry();
//  	IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();  	
  	String perspectiveID = getPerspectiveID(editorID);
  	if (perspectiveID != null) {
  		IPerspectiveDescriptor perspectiveDescriptor = perspectiveRegistry.findPerspectiveWithId(perspectiveID);
  		if (perspectiveDescriptor != null) {
	  		IWorkbench workbench = PlatformUI.getWorkbench();	  	  	
				workbench.showPerspective(perspectiveID, 
				    workbench.getActiveWorkbenchWindow());
				RCPUtil.openEditor(input, editorID);					
  		}
  	}		
	}
	
  /**
   * Opens an Editor (IEditorPart) for the given file, based on the FileExtension of the Editor
   * and the EditorRegistry of the Workbench.
   * @param file The file to open
   * @param saved determines if the created FileEditorInput should be marked as saved or not
   * return true if an editor could be found for this file and the editors opened
   * return false if no editor could be found for this file
   */
  public boolean openFile(File file, boolean saved)
  throws PartInitException
  {
  	if (file == null)
			throw new IllegalArgumentException("Param file must not be null!"); //$NON-NLS-1$

  	IPerspectiveRegistry perspectiveRegistry = PlatformUI.getWorkbench().getPerspectiveRegistry();
  	IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();  	
		IEditorDescriptor editorDescriptor = editorRegistry.getDefaultEditor(file.getName());
		if (editorDescriptor != null) {
			String editorID = editorDescriptor.getId();
	  	String perspectiveID = getPerspectiveID(editorID);
	  	if (perspectiveID != null) {
	  		IPerspectiveDescriptor perspectiveDescriptor = perspectiveRegistry.findPerspectiveWithId(perspectiveID);
	  		if (perspectiveDescriptor != null) {
		  	  try {
			  		IWorkbench workbench = PlatformUI.getWorkbench();	  	  	
						workbench.showPerspective(perspectiveID, 
						    workbench.getActiveWorkbenchWindow());
					} catch (WorkbenchException e) {
						throw new PartInitException("Perspective width ID "+perspectiveID+" could not be opend", e); //$NON-NLS-1$ //$NON-NLS-2$
					}	  			  			
	  		}
	  	}
			FileEditorInput input = new FileEditorInput(file);
			input.setSaved(saved);
			RCPUtil.openEditor(input, editorID);			
			return true;
		}
		return false;
  }
	
  public boolean openFile(File file) 
  throws PartInitException 
  {
  	return openFile(file, true);
  }
  
  public void activate() {
  	checkProcessing();
//  	RCPUtil.getActiveWorkbenchWindow().addPerspectiveListener(perspectiveListener);
  }
  	
//***************************** Only workbenchPartReference.setVisible(true) Variant *****************************  
//  private Set<IEditorReference> hiddenEditorReferences = new HashSet<IEditorReference>();  
//  private IPerspectiveListener perspectiveListener = new PerspectiveAdapter() {
//		@Override
//		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
//			Set<String> editorIDs = perspectiveID2editorIDs.get(perspective.getId());
//			// not editor perspective binding declared, show all hidden editors
//			if (editorIDs == null) {
//				for (IEditorReference editorReference : hiddenEditorReferences) {
//					if (editorReference instanceof WorkbenchPartReference) {
//						WorkbenchPartReference workbenchPartReference = (WorkbenchPartReference) editorReference;
//						workbenchPartReference.setVisible(true);
//					}
//				}				
//				logger.info("No editor perspective bindings declared for perspective "+perspective.getId()+", show all editors and "+hiddenEditorReferences.size()+" hidden editors");
//				hiddenEditorReferences.clear();				
//			}
//			// there exists an editor perspective binding for the activated perspective 
//			else {
//				logger.info("There is a editor perspective binding declared for perspective "+perspective.getId()+"!");
//				// collect all hidden editors and all editors in the current WorkbenchPage
//				Collection<IEditorReference> editorReferences = hiddenEditorReferences;
//				for (int i=0; i<page.getEditorReferences().length; i++) {
//					editorReferences.add(page.getEditorReferences()[i]);
//				}				
//				for (IEditorReference editorReference : editorReferences) {
//					if (editorReference instanceof WorkbenchPartReference) {
//						WorkbenchPartReference workbenchPartReference = (WorkbenchPartReference) editorReference;
//						boolean visible = editorIDs.contains(editorReference.getId());
//						workbenchPartReference.setVisible(visible);						
//						if (!visible) {
//							hiddenEditorReferences.add(editorReference);
//						}				
//						logger.info("visible = "+visible+" for editor "+editorReference.getId());
//					}
//				}
//			}
//		}
//  };
    
// ***************************** Close Variant *****************************
//  private Set<IEditorReference> hiddenEditorReferences = new HashSet<IEditorReference>();  
//  private IPerspectiveListener4 perspectiveListener = new PerspectiveAdapter() {
//		@Override
//		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
//			Set<String> editorIDs = perspectiveID2editorIDs.get(perspective.getId());
//			// not editor perspective binding declared, show all hidden editors
//			if (editorIDs == null) {
//				logger.info("No editor perspective bindings declared for perspective "+perspective.getId()+", show all editors and "+hiddenEditorReferences.size()+" hidden editors");				
//				for (IEditorReference editorReference : hiddenEditorReferences) {
//					if (editorReference instanceof WorkbenchPartReference) {
//						WorkbenchPartReference workbenchPartReference = (WorkbenchPartReference) editorReference;
//						workbenchPartReference.setVisible(true);
//						workbenchPartReference.getPart(true);						
//						try {
//							page.openEditor(editorReference.getEditorInput(), editorReference.getId());
//							logger.info("openEditor for editor "+editorReference.getId());
//						} catch (PartInitException e) {
//							throw new RuntimeException(e);
//						}
//					}
//				}				
//				hiddenEditorReferences.clear();
//				page.getWorkbenchWindow().getShell().layout(true, true);
//			}
//			// there exists an editor perspective binding for the activated perspective 
//			else {
//				logger.info("There is a editor perspective binding declared for perspective "+perspective.getId()+"!");
//				// collect all hidden editors and all editors in the current WorkbenchPage
//				Collection<IEditorReference> editorReferences = hiddenEditorReferences;
//				for (int i=0; i<page.getEditorReferences().length; i++) {
//					editorReferences.add(page.getEditorReferences()[i]);
//				}				
//				for (IEditorReference editorReference : editorReferences) {
//					if (editorReference instanceof WorkbenchPartReference) {
//						WorkbenchPartReference workbenchPartReference = (WorkbenchPartReference) editorReference;
//						boolean visible = editorIDs.contains(editorReference.getId());
//						workbenchPartReference.setVisible(visible);						
//						if (!visible) {
//							hiddenEditorReferences.add(editorReference);
//						}
//						workbenchPartReference.getPane().getStack().remove(workbenchPartReference.getPane());
//						else {
//							if (!workbenchPartReference.getVisible())
//								try {
//									page.openEditor(editorReference.getEditorInput(), editorReference.getId());
//								} catch (PartInitException e) {
//									throw new RuntimeException(e);
//								}
//						}
//						logger.info("visible = "+visible+" for editor "+editorReference.getId());
//					}
//				}
//				page.getWorkbenchWindow().getShell().layout(true, true);				
//				IEditorReference[] editorReferencesToClose = hiddenEditorReferences.toArray(
//						new IEditorReference[hiddenEditorReferences.size()]);
//				page.closeEditors(editorReferencesToClose, false);
//				logger.info("closing "+editorReferencesToClose.length+" editors");
//			}
//		}
//  };
  
  private Map<IEditorReference, Control> editorReference2Control = new HashMap<IEditorReference, Control>();
//  private Map<IEditorReference, PartStack> editorReference2EditorStack = new HashMap<IEditorReference, PartStack>();
  private Set<IEditorReference> hiddenEditorReferences = new HashSet<IEditorReference>();  
  private IPerspectiveListener4 perspectiveListener = new PerspectiveAdapter() {
		@Override
		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			Set<String> editorIDs = perspectiveID2editorIDs.get(perspective.getId());
			// not editor perspective binding declared, show all hidden editors
			if (editorIDs == null) {
				logger.info("No editor perspective bindings declared for perspective "+perspective.getId()+", show all editors and "+hiddenEditorReferences.size()+" hidden editors"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				for (IEditorReference editorReference : hiddenEditorReferences) {
					if (editorReference instanceof WorkbenchPartReference) {
						WorkbenchPartReference workbenchPartReference = (WorkbenchPartReference) editorReference;
//						workbenchPartReference.setVisible(true);		
						
//						PartStack partStack = editorReference2EditorStack.get(workbenchPartReference);
//						if (partStack != null) {
//							partStack.add(workbenchPartReference.getPane());
//						}
						
						Control editorControl = editorReference2Control.get(editorReference);
						editorControl.setVisible(true);
						editorControl.getParent().layout(true, true);
					}
				}	
				hiddenEditorReferences.clear();
//				page.getWorkbenchWindow().getShell().layout(true, true);
			}
			// there exists an editor perspective binding for the activated perspective 
			else {
				logger.info("There is a editor perspective binding declared for perspective "+perspective.getId()+"!"); //$NON-NLS-1$ //$NON-NLS-2$
				// collect all hidden editors and all editors in the current WorkbenchPage
				Collection<IEditorReference> editorReferences = hiddenEditorReferences;
				for (int i=0; i<page.getEditorReferences().length; i++) {
					editorReferences.add(page.getEditorReferences()[i]);
				}				
				for (IEditorReference editorReference : editorReferences) {
					if (editorReference instanceof WorkbenchPartReference) {
						WorkbenchPartReference workbenchPartReference = (WorkbenchPartReference) editorReference;
						boolean visible = editorIDs.contains(editorReference.getId());
//						workbenchPartReference.setVisible(visible);				
												
						if (!visible) {
							hiddenEditorReferences.add(editorReference);
							if (workbenchPartReference.getPane() != null && workbenchPartReference.getPane().getStack() != null) {
								PartStack partStack = workbenchPartReference.getPane().getStack();
								
//								editorReference2EditorStack.put(editorReference, partStack);
//								partStack.remove(workbenchPartReference.getPane());
								
								Control[] tabList = partStack.getTabList(workbenchPartReference.getPane());
								Control editorControl = tabList[0];
								editorControl.setVisible(false);
								editorReference2Control.put(editorReference, editorControl);
								editorControl.getParent().layout(true, true);
							}
						}
						logger.info("visible = "+visible+" for editor "+editorReference.getId()); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
//				page.getWorkbenchWindow().getShell().layout(true, true);				
			}
		}
  };  
  public enum VisibilityMode 
  {
  	EXCLUDE_EDITOR_FROM_PERSPECTIVE,
  	PERSPECTIVE_HIDE_UNBOUND_EDITORS,
  	EDITOR_HIDDEN_IN_UNBOUND_PERSPECTIVES,
  	HIDE_ALL_UNBOUND
  }

}
