package es.pryades.imedig.viewer.datas;

import es.pryades.imedig.cloud.dto.viewer.Image;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageData{
	private StudyTree study;
	private SeriesTree series;
	private Image image;
	
	public String studyId(){
		return study.getStudyData().getStudyID();
	}
	
	public String serieId(){
		return series.getSeriesData().getSeriesInstanceUID();
	}

	public String imagenId(){
		return image.getSOPInstanceUID();
	}

	@Override
	public boolean equals(Object obj){
		if (obj == null) return false;
		if (!(obj instanceof ImageData)) return false;

		ImageData data = (ImageData)obj;
		
		return studyId().equals( data.studyId() ) && serieId().equals( data.serieId() ) && imagenId().equals( data.imagenId() );
	}
}
