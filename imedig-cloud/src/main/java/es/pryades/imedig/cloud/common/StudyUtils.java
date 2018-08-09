package es.pryades.imedig.cloud.common;

import java.util.ArrayList;
import java.util.List;

import es.pryades.imedig.cloud.dto.viewer.Image;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import es.pryades.imedig.viewer.datas.ImageData;

public class StudyUtils
{
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
}
