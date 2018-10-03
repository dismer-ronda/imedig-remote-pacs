package es.pryades.imedig.viewer.datas;

import java.io.Serializable;

import es.pryades.imedig.cloud.dto.viewer.Image;
import es.pryades.imedig.cloud.dto.viewer.SeriesTree;
import es.pryades.imedig.cloud.dto.viewer.StudyTree;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageData implements Serializable {

	private static final long serialVersionUID = -6848920850764992213L;
	
	private StudyTree study;
	private SeriesTree series;
	private Image image;
	
	public String studyId(){
		return image.getStudyInstanceUID();
	}
	
	public String serieId(){
		return image.getSeriesInstanceUID();
	}

	public String imagenId(){
		return image.getSOPInstanceUID();
	}

	@Override
	public boolean equals(Object obj){
		if (obj == null) return false;
		if (!(obj instanceof ImageData)) return false;

		String imId = imagenId();
		
		if ( imId == null ) return false;
		
		ImageData data = (ImageData)obj;
		return imId.equals( data.imagenId() );
	}
}
