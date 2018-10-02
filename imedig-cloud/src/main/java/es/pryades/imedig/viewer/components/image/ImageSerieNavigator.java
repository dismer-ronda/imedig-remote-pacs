package es.pryades.imedig.viewer.components.image;

import es.pryades.imedig.viewer.datas.ImageData;

public interface ImageSerieNavigator
{
	boolean containImagesSeries(); 

	boolean hasPriorImageSerie();  

	ImageData getFirstImageSerie();
	
	ImageData getPreviousImageSerie();
	
	boolean hasNextImageSerie();  
	
	ImageData getNextImageSerie();
	
	ImageData getLastImageSerie();
}
