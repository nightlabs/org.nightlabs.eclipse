package org.nightlabs.eclipse.ui.pdfviewer.editor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.util.ImageUtil;

public class Test {
/*	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Dimension screenSize = toolkit.getScreenSize();	
	GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	bufferedImageAWT2 = graphicsConfiguration.createCompatibleImage(screenSize.width * BUFFER_SIZE_HORIZONTAL, screenSize.height * BUFFER_SIZE_VERTICAL);
	Graphics2D g2D2 = bufferedImageAWT2.createGraphics();		
	
	coordinateY = 0;
	coordinateY += MARGIN;
	g2D2.drawImage(pdfPages.get(i).getImage(
			convertCoordinatesP2S(pdfPageWidths.get(i)), 
			convertCoordinatesP2S(pdfPageHeights.get(i)) - imagePartHeight,
			new Rectangle2D.Double(0, 0, pdfPageWidths.get(i), pdfPageHeights.get(i) - imagePartHeight), null), 
//		pdfImages.get(i),
		0, 
		coordinateY, 
		imageObserver
		);
	
	coordinateY += convertCoordinatesP2S(pdfPageHeights.get(i)) - imagePartHeight;
	coordinateY += MARGIN;
	i++;
	
	while (coordinateY + pdfPageHeights.get(i) <= rectangleBufferedImage.height) {
//		System.out.println("y-coordinate: "+coordinateY+"; page height of page "+i+": "+pdfPageHeights.get(i)+"; rectangleBufferedImage.height: "+rectangleBufferedImage.height);
		g2D2.drawImage(pdfImages.get(i), 0, coordinateY, imageObserver);
		coordinateY += pdfPageHeights.get(i) + MARGIN;
		if (i + 1  >= pdfPages.size()) {
			nextPageExists = false;
			break;
		}
		i++;
	}
	
	imageData2 = ImageUtil.convertToSWT((BufferedImage) bufferedImageAWT2);
	bufferedImageSWT2 = new Image(event.gc.getDevice(), imageData2);
	
	event.gc.drawImage(	bufferedImageSWT2, 
			0, 0, 
			convertCoordinatesP2S(pdfPageWidthMax), rectangleView.height, 
			(rectangleView.width - convertCoordinatesP2S(pdfPageWidthMax)) / 2, 0, 
			convertCoordinatesP2S(pdfPageWidthMax), rectangleView.height);	
	
	*/
}
