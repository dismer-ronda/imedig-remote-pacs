package es.pryades.imedig.wiewer.echo3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nextapp.echo.filetransfer.app.DownloadProvider;

public class DownloadReportProvider implements DownloadProvider 
{
	ByteArrayOutputStream os;
	String fileName;
	
	public DownloadReportProvider( String fileName, ByteArrayOutputStream os ) 
	{ 
		this.os = os;
		this.fileName = fileName;
	}
	
	public String getContentType() 
	{
		return "application/pdf";
	}

	public String getFileName() 
	{
		return fileName;
	}

	public long getSize() 
	{
		return os.size();
	}
	
	public void writeFile( OutputStream out ) throws IOException 
	{ 
		os.writeTo( out );
		
		out.flush();
	}
}


