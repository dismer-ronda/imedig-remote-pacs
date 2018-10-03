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
* @author dismer.ronda 
* @since 1.0.0.0
*/

@SuppressWarnings({ "unchecked", "rawtypes" })
@EqualsAndHashCode(callSuper=true)
@Data
public class Centro extends ImedigDto 
{
	private static final long serialVersionUID = -43846107733811466L;
	
	Integer orden;
	String descripcion;
	String direccion;
	String contactos;
	String coordenadas;
	Integer imagen;
	String nombre;
	Integer horario;
	Integer moneda;
	String ip;
	Integer puerto;
	String serie;
	
    /**
	 * Adiciona un contacto al centro
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
	 * Obtiene la lista de un tipo de contactos del centro 
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
}
