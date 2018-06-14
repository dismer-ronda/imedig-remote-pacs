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
}
