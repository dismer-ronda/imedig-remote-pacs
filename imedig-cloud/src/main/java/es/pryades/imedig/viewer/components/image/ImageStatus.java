package es.pryades.imedig.viewer.components.image;

import java.awt.Rectangle;

import lombok.Getter;

@Getter
public class ImageStatus {
	private Rectangle irect;
	private double windowWidth;
	private double windowCenter;
	private int frame;
		
	public ImageStatus(Rectangle irect, double windowCenter, double windowWidth, int frame ){
		this.irect = irect;
		
		this.windowCenter = windowCenter;
		this.windowWidth = windowWidth;
			
		this.frame = frame;
	}
	
}
