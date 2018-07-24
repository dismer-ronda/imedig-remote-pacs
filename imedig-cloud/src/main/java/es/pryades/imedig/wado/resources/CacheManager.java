package es.pryades.imedig.wado.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import es.pryades.imedig.cloud.common.Utils;
import es.pryades.imedig.core.common.Settings;

/**
 * 
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */

public class CacheManager extends Thread
{
    private static final Logger LOG = Logger.getLogger( CacheManager.class );
    
    static CacheManager instance = null;
    HashMap<String,CacheElement> cache = new HashMap<String,CacheElement>();
    boolean goOn = true;
    Boolean stopLock = Boolean.FALSE;
    long hits = 0;
    long total = 0; 
    
	public class CacheElement 
	{
		String fileName;
		long fileSize;
		String UID;
		Long lastUse;
		  
		public CacheElement( String UID )
		{
			this.UID = UID;
			
			fileName = null;
			fileSize = 0;
			
			notifyUse();
		}
		
		public final void notifyUse()
		{
			lastUse = new Long( new Date().getTime() );
		}
		
		public boolean expired()
		{
			if ( lastUse != null )
				return (new Date()).getTime() - lastUse >= Settings.Cache_TTL * Utils.ONE_MINUTE;
			
			return false;
		}
		
		public boolean exists()
		{
			if ( fileName != null )
				return (new File( fileName )).exists();
			
			return false;
		}
		
		public void delete()
		{
			if ( fileName != null )
				(new File( fileName )).delete();
		}

		public String filename()
		{
			return fileName;
		}

		public void filename( String fileName )
		{
			if ( fileName != null )
			{
				if ( exists() )
					delete();
			
				this.fileName = fileName;
				
				File f = new File( fileName );
			
				fileSize = f.length();

				LOG.info( "Filename updated for cache element " + UID );
			}
		}
		
		public String toString()
		{
			return UID + ": " + fileName + "," + fileSize + "," + new Date( lastUse );
		}
		
	};
	
	public CacheManager()
	{
		super();
	}

	public static void Init()
	{
		instance = new CacheManager();

		instance.init();
		instance.start();
	}
	
	public void Stop()
	{
		synchronized ( stopLock )
		{
			goOn = false;
		}
	}
	
	public static CacheManager getInstance()
	{
		return instance;
	}
	
	public CacheElement addElement( String UID )
	{
		synchronized ( cache )
		{
			CacheElement lck = cache.get( UID );
			
			total++;
	        
			if ( lck == null )
			{
				lck = new CacheElement( UID );
				
				cache.put( UID, lck );

				LOG.info( "Created a new cache element for " + UID );
			}
			else
			{
				lck.notifyUse();

				hits++;
			}

			return lck;
		}
	}
	
	public void delElement( String UID )
	{
		synchronized ( cache )
		{
			CacheElement lck = cache.get( UID );

			if ( lck != null )
			{
				lck.delete();

				cache.remove( UID );
			}
		}
	}
	
	public void init()
	{
		File dir = new File( Settings.Cache_Dir );
		 
	    String[] fileNames = dir.list();
	     
	    for ( String fileName : fileNames )
		{
	    	String [] uid = fileName.split( "-" );
	    	
	    	if ( uid.length == 2 )
	    	{
		    	CacheElement cel = addElement( uid[0] );
		    	
		    	cel.filename( Settings.Cache_Dir + "/" + fileName );

		    	LOG.info( "Initialized cache element from directory " + cel );
	    	}
	    }
	}
	
	public boolean exists( String UID )
	{
		boolean ret = false;
		
		synchronized ( cache )
		{
			CacheElement lck = cache.get( UID );

			ret =  lck != null && lck.exists(); 
		}
		
		return ret; 
	}
	
	public String getFile( String UID )
	{
		String ret = null;
		
		synchronized ( cache )
		{
			CacheElement lck = cache.get( UID );

			if ( lck != null )
				ret = lck.fileName;
		}
		
		return ret;
	}
	
	public void run()
	{
		LOG.info( "Cache processing started" );
		
		boolean stop = false;
		
		while ( !stop )
		{
			Utils.Sleep( Settings.Cache_CheckPeriod * Utils.ONE_MINUTE );

			synchronized ( stopLock )
			{
				stop = !goOn;
			}

			if ( ! stop )
			{
				List<CacheElement> lcks = null; 
				
				long lockTime = System.currentTimeMillis();
				
				synchronized ( cache )
				{
					lcks = new ArrayList<CacheElement>( cache.values() );
				}
				
				long elapsed = System.currentTimeMillis() - lockTime;
			
				Comparator<CacheElement> comparator = new Comparator<CacheElement>() 
				{
					public int compare( CacheElement a, CacheElement b ) 
					{
						return (int)(a.lastUse - b.lastUse);
					}
		        };
				
				Collections.sort( lcks, comparator );
	
				long sizeUsed = 0;
				
				for ( CacheElement lck : lcks )
				{
					synchronized ( lck )
					{
						sizeUsed += lck.fileSize;
					}
				}
	
				long used = sizeUsed/Utils.ONE_MB;
				
				LOG.info( "Cache use is " + used + " of " + Settings.Cache_MaxSize + " MB. Locked for processing during " + elapsed + " ms." + " Number of accesses " + total + ". Hits " + hits );
						
				for ( CacheElement lck : lcks )
				{
					synchronized ( lck )
					{
						if ( lck.expired() || sizeUsed > Settings.Cache_MaxSize * Utils.ONE_MB )
						{
							if ( !lck.expired() )
								sizeUsed -= lck.fileSize;
							
							LOG.info( "Removing cache element " + lck.UID + " because of " + (lck.expired() ? "element expiration" : "cache overload") );
							
							delElement( lck.UID );
							
							Utils.Sleep( 100 );
						}
					}
				}
			}
		}

		LOG.info( "Cache processing finished" );
	}
}
