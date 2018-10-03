package es.pryades.imedig.cloud.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.pryades.imedig.cloud.dto.viewer.Image;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import es.pryades.imedig.viewer.datas.ImageData;

public class StudyUtils implements Serializable
{
	private static final long serialVersionUID = -8538117679737742604L;

	public static List<ImageData> readStudyImagesData(StudyTree study){
		List<ImageData> result = new ArrayList<ImageData>();
		
		for (SeriesTree series : study.getSeriesList()) {

			List<Image> imageList = series.getImageList();

			for (Image image : imageList) {
				ImageData data = new ImageData(); 
				data.setImage(image);
				data.setSeries(series);
				data.setStudy(study);
				result.add( data );
			}
		}
		
		return result;
	}
	
	public static List<String> getSeriesUID(StudyTree study){
		List<String> result = new ArrayList<>();
		
		for (SeriesTree series : study.getSeriesList()) {
			result.add( series.getSeriesData().getSeriesInstanceUID() );
		}
		
		return result;
	}
	
	public static List<ImageData> readSeriesImageData(String serieUID, StudyTree study){
		List<ImageData> result = new ArrayList<ImageData>();
		
		for (SeriesTree series : study.getSeriesList()) {
			
			if (!serieUID.equals( series.getSeriesData().getSeriesInstanceUID() )) continue;

			List<Image> imageList = series.getImageList();

			for (Image image : imageList) {
				ImageData data = new ImageData(); 
				data.setImage(image);
				data.setSeries(series);
				data.setStudy(study);
				result.add( data );
			}
		}
		
		return result;
	}
	
	public static SeriesTree getSerie(String serieUID, StudyTree study){
		for (SeriesTree serie : study.getSeriesList()) {
			if (serieUID.equals( serie.getSeriesData().getSeriesInstanceUID() )) 
				return serie;
		}
		
		return null;
	}
}
