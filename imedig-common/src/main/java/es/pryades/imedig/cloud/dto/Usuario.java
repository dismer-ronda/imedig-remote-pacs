package es.pryades.imedig.cloud.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import es.pryades.imedig.cloud.common.ImedigException;
import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.cloud.dto.memo.Contacto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author Dismer Ronda 
*/

@SuppressWarnings({ "unchecked", "rawtypes" })
@EqualsAndHashCode(callSuper=true)
@Data 
public class Usuario extends ImedigDto 
{
	private static final long serialVersionUID = 3377219589058864361L;
	
	public static final int PASS_OK			= 0;
	public static final int PASS_NEW 		= 1;
	public static final int PASS_FORGET 	= 2;
	public static final int PASS_EXPIRY 	= 3;
	public static final int PASS_CHANGED 	= 4;
	public static final int PASS_BLOCKED 	= 5;

	String login;
	String email;
	String pwd;
	Integer cambio;
	Integer intentos;
	Integer estado;
	Integer perfil;
	String nombre;
	String ape1;
	String ape2;
	String contactos; 
	String filtro; 
	String query; 
	String compresion; 
	String titulo; 
	
	boolean local;
	
    /**
	 * Adiciona un contacto al usuario
	 * 
	 * @param tipo Tipo de contacto
	 * @param valor Valor del contacto
	 * @throws ImedigException
	 */
	public void addContacto( String tipo, String valor ) throws Throwable
	{
		String temp = getContactos();
		
		if ( temp == null )
			temp = "[]";
		
    	List contactos = (List) Utils.toArrayList( temp, new TypeToken<ArrayList<Contacto>>() {}.getType() );
    	
    	Contacto contacto = new Contacto();
    	contacto.setTipo( tipo );
    	contacto.setContacto( valor );
    	
    	contactos.add( contacto );

    	setContactos( Utils.toJson( contactos ) );
	}

	/**
	 * Obtiene la lista de un tipo de contactos del usuario 
	 * @param tipo Tipo de contacto
	 * @return Lista de contactos
	 * @throws ImedigException
	 */
	public List getContacto( String tipo ) throws ImedigException
	{
		String temp = getContactos();
		
		if ( temp == null )
			temp = "[]";
		
    	List contactos = (List) Utils.toArrayList( temp, new TypeToken<ArrayList<Contacto>>() {}.getType() );
    	
    	List ret = new ArrayList<String>();
    	
    	for ( Object o : contactos )
    	{
    		Contacto contacto = (Contacto)o;
    		
    		if ( tipo.equals( contacto.getTipo() ) )
    			ret.add( contacto.getContacto() );
    	}
    	
    	return ret;
	}
	
	public String getNombreCompleto()
	{
		return (titulo.isEmpty() ? "" : titulo + " ") + nombre + " " + ape1 + (ape2.isEmpty() ? "" : " " + ape2);
	}
}
