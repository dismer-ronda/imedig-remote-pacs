package es.pryades.imedig.cloud.dto;

import java.util.Map;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.ImedigException;

import lombok.Data;

/**
*
* @author Dismer Ronda 
* @since 1.0.0.0
*/

@Data
@SuppressWarnings({ "rawtypes" })
public class Parametros
{
    private static final Logger LOG = Logger.getLogger( Parametros.class );

    Map parametros;

    /**
     * @param codigo Nombre del elemento de configuración a obtener
     * @param def Valor por defecto en caso de producirse un error
     * @return Valor del elemento de configuración o valor por defecto
     */
    public String getString( Integer centro, String codigo, String def ) 
    {
    	try
    	{
			String key = (centro == null) ? codigo : centro + codigo; 

       		return ((Parametro)parametros.get( key )).getValor();
    	}
    	catch ( Throwable e )
    	{
			if ( !(e instanceof ImedigException) )
				new ImedigException( e, LOG, 0 );
    	}
    	
    	return def;
    }

    /**
     * @param codigo Nombre del elemento de configuración a obtener
     * @param def Valor por defecto en caso de producirse un error
     * @return Valor del elemento de configuración o valor por defecto como entero
     */
    public int getInt( Integer centro, String codigo, int def )
    {
    	return Integer.parseInt( getString( centro, codigo, Integer.toString( def ) ) );
    }

    /**
     * @param codigo Nombre del elemento de configuración a obtener
     * @param def Valor por defecto en caso de producirse un error
     * @return Valor del elemento de configuración o valor por defecto como double
     */
    /**
     * @param codigo
     * @param def
     * @return
     */
    public double getDouble( Integer centro, String codigo, double def )
    {
    	return Double.parseDouble( getString( centro, codigo, Double.toString( def ) ) );
    }
}
