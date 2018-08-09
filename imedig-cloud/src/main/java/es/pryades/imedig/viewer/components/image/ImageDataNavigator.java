package es.pryades.imedig.viewer.components.image;

import es.pryades.imedig.viewer.datas.ImageData;

public interface ImageDataNavigator
{
	ImageData getPriorImageData();
	
	ImageData getNextImageData();
}
