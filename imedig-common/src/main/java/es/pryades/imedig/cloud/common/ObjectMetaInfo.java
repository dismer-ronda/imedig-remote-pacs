package es.pryades.imedig.cloud.common;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * 
 * 
 * @author Dismer Ronda
 *
 */
public class ObjectMetaInfo implements Serializable
{
	private static final long serialVersionUID = -2356144973959726700L;

	//###########################################################################################//
	//#										PROPERTIES										   	#//
	//###########################################################################################//

	private static final Logger LOG = Logger.getLogger( ObjectMetaInfo.class ); 
	private Class<?> objClass;
	
	private List<Field> attributes;
	private List<Method> methods;
	
	private List<String> attributesNames;
	private HashMap<String,Object> attributesValues;
	private HashMap<String,Class<?>>  attributesTypes;
    
	//###########################################################################################//
	//#										CONSTRUCTOR(S)									   	#//
	//###########################################################################################//

	public ObjectMetaInfo(Class<?> objClass) {
		this.objClass = objClass;	
		this.attributesValues = new HashMap<String, Object>();
		this.retreiveObjectMetaInfo();
	}
	//###########################################################################################//
	//#										ACCESS METHODS  								   	#//
	//###########################################################################################//
	
	
	/**
	 * get
	 * @return
	 */
	public List<Field> getAttributes() {
		return attributes;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * get
	 * @return
	 */
	public List<Method> getMethods() {
		return methods;
	}
	
//------------------------------------------------------------------------------------------------------------	
	/**
	 * get
	 * @return
	 */
	public List<String> getAttributesNames() {
		return attributesNames;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * get
	 * @return
	 */
	public List<String> getAttributesNamesArratString() {
		
		return attributesNames;

	}
	
//------------------------------------------------------------------------------------------------------------	
	/**
	 * get
	 * @return
	 */
	public HashMap<String, Object> getAttributesValues() {
		return attributesValues;
	}

	/**
	 * get
	 * @return
	 */
	public HashMap<String, Class<?>> getAttributesTypes() {
		return attributesTypes;
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * get
	 * @return
	 */
	public Class<?> getObjClass() {
		return objClass;
	}

	/**
	 * set
	 * @param objElem
	 */
	public void setObjClass(Class<?> objElem) {
		this.objClass = objElem;
	}

	
	//###########################################################################################//
	//#										OTHER METHODS  								   		#//
	//###########################################################################################//
	

	/**
	 * @author Dismer Ronda Bernardo
	 * 
	 * <p>
	 * 		<b>Description:<b> 
	 * </p>
	 * 
	 * @param objClass
	 * @return
	 * @throws ImedigException
	 */
	private void retreiveObjectMetaInfo()
    {
	
		try 
		{
			Field[] objParentFields = objClass.getSuperclass().getDeclaredFields();		
			Field[] objFields = objClass.getDeclaredFields();
			List<Field> resultFields = new ArrayList<Field>();
			
			Method[] objMethods = objClass.getDeclaredMethods();
					
			this.attributes= Arrays.asList(objFields);
			this.methods=Arrays.asList(objMethods);
			
			HashMap<String,Class<?>>  objAttTypes = new HashMap<String, Class<?>>();
			List<String> objAttNames = new ArrayList<String>();
			
			for(Field fieldItem: objParentFields)
			{
				Integer modifiers =  fieldItem.getModifiers();
				
				//se ignoran los campos final y estaticos
				if( !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers))
				{
					fieldItem.setAccessible(true);
					resultFields.add(fieldItem);
					objAttNames.add(fieldItem.getName());	
					objAttTypes.put(fieldItem.getName(),fieldItem.getType());		
				}
			}
			
			for(Field fieldItem: objFields)
			{
				Integer modifiers =  fieldItem.getModifiers();
				
				//se ignoran los campos final y estaticos
				if( !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers))
				{
					fieldItem.setAccessible(true);
					resultFields.add(fieldItem);
					objAttNames.add(fieldItem.getName());	
					objAttTypes.put(fieldItem.getName(),fieldItem.getType());		
				}
			}
			
			this.attributes = resultFields; 
			this.attributesTypes = objAttTypes;
			this.attributesNames = objAttNames;
			
		}
		catch ( Throwable e )
		{ 
    		e.printStackTrace();
		}
    }
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * @author Dismer Ronda Bernardo
	 * 
	 * <p>
	 * 		<b>Description:<b> 
	 * </p>
	 * 
	 * @param objClass
	 * @return
	 * @throws ImedigException
	 */
	public void addObjectvaluesToMetaInfo(Object obj)throws ImedigException
    {
		try 
		{
			if (obj.getClass().equals(this.objClass)) 
			{
				if(this.attributesValues == null){
					this.attributesValues = new HashMap<String, Object>();
				}
				this.attributesValues.clear();
				
				for(Field fieldItem: this.attributes)
				{
					Integer modifiers =  fieldItem.getModifiers();
					
					//se ignoran los campos final y estaticos
					if( !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers))
					{
						fieldItem.setAccessible(true);
						this.attributesValues.put(fieldItem.getName(), fieldItem.get(obj));
					}
				}				
			}
			
			
		}
		catch ( Throwable e )
		{ 
    		e.printStackTrace();

    		if ( e instanceof ImedigException )
				throw (ImedigException)e;
    		else
    			throw new ImedigException( e, LOG, ImedigException.UNKNOWN );
		}
    }
	
//------------------------------------------------------------------------------------------------------------	
	/**
	 * 
	 * @return
	 */
	public List<String> getObjValuesToArrayString()
	{
		List<String> objValuesString = new ArrayList<String>();
		
		for(String attName:this.attributesNames){
			objValuesString.add(this.attributesValues.get(attName).toString());
		}
		
		return objValuesString;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 	
	 * @return
	 */
	public Object createObjectFromMetaInfo()throws ImedigException
	{
		Object object = null;
		
		try
		{
			object = this.objClass.newInstance();
			
			this.updateObjectValuesFromMetaInfo(object);

		} catch (InstantiationException e) 
		{
			throw new ImedigException(e.getCause(), LOG, ImedigException.INITIALIZATION_ERROR);
		} 
		catch (IllegalAccessException e)
		{
			throw new ImedigException(e.getCause(), LOG, ImedigException.INITIALIZATION_ERROR);
		}
		
		return object;
	}
	
//------------------------------------------------------------------------------------------------------------	
	/**
	 * 
	 * @param object
	 */
	public void updateObjectValuesFromMetaInfo(Object object) throws ImedigException
	{
		if(object.getClass().equals(this.objClass)){
			try 
			{
				Field[] objParentFields = objClass.getSuperclass().getDeclaredFields();	
				Field[] objFields = object.getClass().getDeclaredFields();
				
				for(Field fieldItem: objParentFields)
				{
					Integer modifiers =  fieldItem.getModifiers();
					
					//se ignoran los campos final y estaticos
					if( !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers))
					{
						fieldItem.setAccessible(true);
						fieldItem.set(object,this.attributesValues.get(fieldItem.getName()));	
					}
				}
				
				for(Field fieldItem: objFields)
				{
					Integer modifiers =  fieldItem.getModifiers();
										
					//se ignoran los campos final y estaticos
					if( !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers))
					{
						fieldItem.setAccessible(true);
						fieldItem.set(object,this.attributesValues.get(fieldItem.getName()));
					}
				}	
			}
			catch ( Exception e )
			{ 
	    		if ( e instanceof ImedigException )
					throw (ImedigException)e;
	    		else
	    			throw new ImedigException( e, null, ImedigException.JSON_STRING_ERROR );
			}
		}else{
			throw new ImedigException(new Exception( "TableImedig Initialization error!"), LOG, ImedigException.COMPATIBILITY_ERROR);
		}
		
	}
	
//------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------

}
