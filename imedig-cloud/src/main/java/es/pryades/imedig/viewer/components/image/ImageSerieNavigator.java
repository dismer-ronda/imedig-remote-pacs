package es.pryades.imedig.viewer.components.image;

import es.pryades.imedig.viewer.datas.ImageData;

public interface ImageSerieNavigator
{
	boolean containFrames(); 
	
	ImageData getPreviousImageSerie();
	
	ImageData getNextImageSerie();
}
