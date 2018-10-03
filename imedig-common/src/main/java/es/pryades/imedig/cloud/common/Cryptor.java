package es.pryades.imedig.cloud.common;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public class Cryptor  implements Serializable
{
	private static final long serialVersionUID = 4761359473199525885L;
	
	private static final String algorithm = "DESede";

	
	public static byte[] getKey( String password ) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest md = MessageDigest.getInstance( "md5" );
		  
	    byte[] digestOfPassword = md.digest(Base64.decodeBase64( password.getBytes("utf-8") ) );
	      
	    byte[] key = Arrays.copyOf( digestOfPassword, 24 );
	      
	    for ( int j = 0, k = 16; j < 8; ) 
	    	key[k++] = key[j++];
	      
	    return key;
	}
	
    public static byte [] encrypt( byte code[], String password ) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
    	byte[] key = getKey( password );
    
    	SecretKeyFactory skf = SecretKeyFactory.getInstance( algorithm );
       
    	DESedeKeySpec kspec = new DESedeKeySpec( key );
       
		SecretKey ks = skf.generateSecret( kspec );
		  
		Cipher cipher = Cipher.getInstance( algorithm );
		     
		cipher.init( Cipher.ENCRYPT_MODE, ks);
			       
		return cipher.doFinal( code );
    }
    
    public static byte [] decrypt( byte code[], String password ) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
    	byte[] key = getKey( password );
       
    	SecretKeyFactory skf = SecretKeyFactory.getInstance( algorithm );
       
    	DESedeKeySpec kspec = new DESedeKeySpec( key );
       
    	SecretKey ks = skf.generateSecret( kspec );
      
    	Cipher cipher = Cipher.getInstance( algorithm );
         
    	cipher.init( Cipher.DECRYPT_MODE, ks);
    	       
    	return cipher.doFinal( code );
    }
}
