/**
 * 
 */
package org.nightlabs.base.ui.composite;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.SharedImages;

/**
 * Composite for displaying messages of a certain type.
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 */
public class MessageComposite 
extends XComposite 
{
	public enum MessageType {
		INFO,
		ERROR,
		WARNING
	}
	
	private String message = "";
	private Label label;
	private Image image;
	private Label imageLabel; 
	private MessageType messageType;
	
	/**
	 * @param parent
	 * @param style
	 */
	public MessageComposite(Composite parent, int style, String message, MessageType messageType) {
		super(parent, style);
		this.message = message;
		this.messageType = messageType;
		
		setLayout(new GridLayout(2, false));
		imageLabel = new Label(this, SWT.NONE);
		label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		setMessage(message, messageType);
		
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (image != null)
					image.dispose();
			}
		});
	}
	
	private Image getImageForMessageType(MessageType messageType) 
	{
		String key = null;
		switch (messageType) {
			case ERROR:
				key = ISharedImages.IMG_OBJS_ERROR_TSK;
				break;
			case WARNING:
				key = ISharedImages.IMG_OBJS_WARN_TSK;
				break;
			case INFO:
				key = ISharedImages.IMG_OBJS_INFO_TSK;
				break;				
		}
//		image = new SharedImages().getImage(key);
//		return image;
		ImageDescriptor desc = new SharedImages().getImageDescriptor(key);
		return desc.createImage();
	}
	
	public void setMessage(String message, MessageType messageType) 
	{
		if (image != null)
			image.dispose();
		
		image = getImageForMessageType(messageType);
		imageLabel.setImage(image);
		label.setText(message);
	}
	
	public String getMessage() {
		return message;	
	}
	
	public MessageType getMessageType() {
		return messageType;
	}
}
