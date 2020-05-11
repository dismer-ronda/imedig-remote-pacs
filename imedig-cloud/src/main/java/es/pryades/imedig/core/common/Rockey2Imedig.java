package es.pryades.imedig.core.common;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface Rockey2Imedig extends Library 
{
	Rockey2Imedig INSTANCE = (Rockey2Imedig) Native.loadLibrary( "ImedigLicense", Rockey2Imedig.class);

	public int isPresent( int[] hid, byte[] code );
	public int writeCode( byte[] code );
}
