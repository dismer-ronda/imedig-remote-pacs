package es.pryades.imedig.cloud.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.log4j.Logger;

public class WebServiceRequest implements Serializable 
{
	private static final long serialVersionUID = 3987324766332000135L;
	private static final Logger LOG = Logger.getLogger( WebServiceRequest.class );

    public int code;
	public String url;
	public String method;
	public String response;
	public String body; 
	public HashMap<String, String> headers;

	public WebServiceRequest( String url, int code, String method, String response, String body, HashMap<String, String> headers )
	{
		this.code = code;
		this.url = url;
		this.method = method;
		this.response = response;
		this.body = body;
		this.headers = headers;
	}
	
	public void readInputStreamAsString( InputStream in, ByteArrayOutputStream resp ) throws IOException 
	{
	   BufferedInputStream bis = new BufferedInputStream( in );
	   
	   int result = bis.read();
	   
	   while( result != -1 ) 
	   {
	     byte b = (byte)result;
	     resp.write(b);
	     result = bis.read();
	   }
	}
	
	/*@SuppressWarnings("unused")
	private void configureSSLHandling(HttpClient hc) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException 
	{
		KeyStore trustStore  = KeyStore.getInstance( KeyStore.getDefaultType() );
		
		URL url = Thread.currentThread().getContextClassLoader().getResource( "indigo.keystore" );
		
        FileInputStream instream = new FileInputStream( url.getPath() ); 
        
        try 
        {
            trustStore.load( instream, Settings.TRUST_KEY.toCharArray() );
        } 
        finally 
        {
            try 
            { 
            	instream.close(); 
            } 
            catch ( Exception ignore ) 
            {
            }
        }

        SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
        hc.getConnectionManager().getSchemeRegistry().register( new Scheme("https", 443, socketFactory) );
 	}
 */
	
	
  	public int HttpExecute( HttpRequestBase method, ByteArrayOutputStream resp, HttpHost proxy ) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException
   	{
  		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial( new File( Settings.HOME_dir + "/conf/indigo.keystore" ), Settings.TRUST_KEY.toCharArray() ).build();
  		
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory( sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        
  		int port = Utils.getPortFromUrl( url );
  		String protocol = Utils.getProtocolFromUrl( url );
  		String host = Utils.getHostFromUrl( url );
  		
  		LOG.info( "protocol=" + protocol + " host=" + host + " port=" + port );
  		
  		HttpHost target = new HttpHost( host, port, protocol );
  		
  		try
  		{
  			CloseableHttpResponse response = httpclient.execute( target, method );
  		
  			try
  			{
	  			StatusLine ret = response.getStatusLine();
	  			
	  			HttpEntity entity = response.getEntity();
	  	    	
	  	    	if ( entity != null ) 
	  	    	{
	  	    	    InputStream is = entity.getContent();
	  	    	    
	  	    	    readInputStreamAsString( is, resp );
	  	    	}
	  	    	
	  			return ret.getStatusCode();
  			}
  			finally
  			{
  				response.close();
  			}
  		}
  		finally
  		{
  			httpclient.close();
  		}
  		
  		/*
    	HttpParams httpParams = new BasicHttpParams();

    	SSLContext sslContext = SSLContext.getInstance("SSL");

    	// set up a TrustManager that trusts everything
    	sslContext.init(null, new TrustManager[] { new X509TrustManager() {
    	            public X509Certificate[] getAcceptedIssuers() {
    	                    return null;
    	            }

    	            public void checkClientTrusted(X509Certificate[] certs,
    	                            String authType) {
    	            }

    	            public void checkServerTrusted(X509Certificate[] certs,
    	                            String authType) {
    	            }
    	} }, new SecureRandom());

    	SSLSocketFactory sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
    	
    	SchemeRegistry schemeRegistry = new SchemeRegistry();

    	schemeRegistry.register(new Scheme("https", 443, sf));
    	schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory() ));

    	// apache HttpClient version >4.2 should use BasicClientConnectionManager
    	ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
    	HttpClient httpclient = new DefaultHttpClient(cm);
    	//HttpClient httpclient = new DefaultHttpClient( httpParams );
    	//configureSSLHandling( httpclient );

    	HttpConnectionParams.setConnectionTimeout( httpParams, 10000 );
    	HttpConnectionParams.setSoTimeout( httpParams, 10000 );
    	
    	if ( proxy != null )
    		httpclient.getParams().setParameter( ConnRoutePNames.DEFAULT_PROXY,proxy);

    	HttpResponse response = httpclient.execute( method );
    	
		int ret = response.getStatusLine().getStatusCode();
		
		HttpEntity entity = response.getEntity();
    	
    	if ( entity != null ) 
    	{
    	    InputStream is = entity.getContent();
    	    
    	    readInputStreamAsString( is, resp );
    	}

		httpclient.getConnectionManager().shutdown();
		*/
  		
   	}

  	public boolean Execute( String token, ByteArrayOutputStream resp, HttpHost proxy ) throws Throwable
	{
		try
		{
			HttpRequestBase method = null;

			StringEntity entity = !this.body.isEmpty() ? new StringEntity( this.body ) : null;
		
			String uri = Utils.getUriFromUrl( url );
			
			if ( token != null )
			{
				if ( uri.contains( "?" ) )
				{
					if ( !uri.contains("token") )
						uri += "&token=" + token;
				}
				else
					uri += "?token=" + token;
			}
			
	  		LOG.info( "uri=" + uri );
			
			if ( "POST".equals( this.method ) )
			{
				method = new HttpPost( new URI( uri ) );
				
				if ( entity != null )
					((HttpPost)method).setEntity( entity );
			}
			else if ( "PUT".equals( this.method ) )
			{
				method = new HttpPut( new URI( uri ) );
	
				if ( entity != null )
					((HttpPut)method).setEntity( entity );
			}
			else if ( "DELETE".equals( this.method ) )
				method = new HttpDelete( new URI( uri ) );
			else if ( "HEAD".equals( this.method ) )
				method = new HttpHead( new URI( uri ) );
			else if ( "OPTIONS".equals( this.method ) )
				method = new HttpOptions( new URI( uri ) );
			else
				method = new HttpGet( new URI( uri ) );
			
			if ( proxy != null )
				method.setConfig( RequestConfig.custom().setProxy(proxy).build() );
			
			Set<Entry<String,String>> set = headers.entrySet();
			
			for ( Entry<String, String> header : set )
				method.addHeader( header.getKey().replace( ":", "" ), header.getValue() );
	
			int code = HttpExecute( method, resp, proxy );
			
			boolean ret = code == this.code;
			
			if ( ret && response != null && !response.isEmpty() )
				ret = resp.toString().contains( response ); 
			
			return ret;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			throw e;
		}
	}
}