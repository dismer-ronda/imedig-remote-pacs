package es.pryades.imedig.cloud.dto.viewer;

import lombok.Data;

import es.pryades.imedig.cloud.common.Utils;

@Data 
public class ImageHeader 
{
	private String StudyDate;
	private String StudyTime;
	private String AccessionNumber;
	private String StudyID;
	private String StudyInstanceUID;
	private String ReferringPhysicianName;
	private String PatientName;
	private String PatientID;
	private String IssuerOfPatientID;
	private String PatientBirthDate;
	private String PatientSex;
	private String ModalitiesInStudy;
	
	private String SeriesInstanceUID; 
	private String Modality;
	private String SeriesNumber;

	private String SOPInstanceUID;
	private String SOPClassUID;
	private String InstanceNumber;

	private int Columns;
	private int Rows;
	private int BitsAllocated;
	private int BitsStored;
	private int HighBit;
	private int SamplesPerPixel;
	private int PixelRepresentation;
	private String PixelSpacing;
	private double SliceThickness;
	private String PhotometricInterpretation;
	private int PlanarConfiguration;
	private double RescaleSlope;
	private double RescaleIntercept;
	private String WindowWidth;
	private String WindowCenter;
	private int NumberOfFrames;

	static public ImageHeader getImageHeader( String text ) throws Throwable
	{
		return (ImageHeader)Utils.toPojo( text, ImageHeader.class, true );
	}
}
