package com.requestExample;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author jguyet
 * 
 * default :
 * 
 * port : 443
 * protocol : http
 * proxy : false
 * method : GET
 * 
 * proxy default :
 * 
 * port : 222
 * IP : 127.0.0.1
 * protocol : http
 */
public class Request {
	
	private String				url = "";
	private String				protocol = "http";
	private int					port = 443;
	private boolean				useProxy = false;
	private String				proxyIP = "127.0.0.1";
	private int					proxyPort = 222;
	private String				proxyProtocol = "http";
	private Map<String, String> header = new TreeMap<String, String>();
	private CookieStore 		cookieStore = new BasicCookieStore();
	private boolean				GET = true;
	private boolean				POST = false;
	List<NameValuePair>			params = new ArrayList<NameValuePair>();
	
	private String 				content = null;
	private boolean				success = false;
	private int					statusCode = 0;
	
	private HttpURLConnection	con = null;
	
	/**
	 * Constructor
	 */
	public Request()
	{
		Logger.getLogger(org.apache.http.impl.execchain.RetryExec.class).setLevel(Level.OFF);
	}
	
	/**
	 * Method execute set POST
	 * @return
	 */
	public Request setPost()
	{
		if (GET)
			GET = false;
		POST = true;
		return (this);
	}
	
	/**
	 * Method execute set GET (default)
	 * @return
	 */
	public Request setGET()
	{
		if (POST)
			POST = false;
		GET = true;
		return (this);
	}
	
	/**
	 * active proxy
	 * @param ip
	 * @param port
	 * @return
	 */
	public Request setProxy(String ip, int port)
	{
		this.proxyIP = ip;
		this.proxyPort = port;
		this.useProxy = true;
		return (this);
	}
	
	/**
	 * change protocol connection proxy https
	 * @return
	 */
	public Request setProxyHttps()
	{
		this.proxyProtocol = "https";
		return (this);
	}
	
	/**
	 * change protocol connection proxy http (default)
	 * @return
	 */
	public Request setProxyHttp()
	{
		this.proxyProtocol = "http";
		return (this);
	}
	
	/**
	 * replace Map<String, String> header map
	 * @param h
	 * @return
	 */
	public Request setHeader(Map<String, String> h)
	{
		this.header = h;
		return (this);
	}
	/**
	 * Add key and value into the header
	 * @param key
	 * @param value
	 * @return
	 */
	public Request addHeader(String key, String value)
	{
		this.header.put(key, value);
		return (this);
	}
	
	/**
	 * clean current header
	 * @return
	 */
	public Request clearHeader()
	{
		this.header.clear();
		return (this);
	}
	
	/**
	 * get all cookies
	 * @return
	 */
	public CookieStore getCookieStore()
	{
		return (cookieStore);
	}
	
	/**
	 * replace cookieStore
	 * @param cook
	 * @return
	 */
	public Request setCookieStore(CookieStore cook)
	{
		this.cookieStore = cook;
		return (this);
	}
	
	/**
	 * add one Cookie into current cookieStore
	 * @param cook
	 * @return
	 */
	public Request addCookie(Cookie cook)
	{
		this.cookieStore.addCookie(cook);
		return (this);
	}
	
	/**
	 * clean cookieStore
	 * @return
	 */
	public Request clearCookie()
	{
		this.cookieStore.clear();
		return (this);
	}
	
	/**
	 * set protocol http (default)
	 * @return
	 */
	public Request setProtocolHttp()
	{
		this.protocol = "http";
		return (this);
	}
	
	/**
	 * set protocol https (default)
	 * @return
	 */
	public Request setProtocolHttps()
	{
		this.protocol = "https";
		return (this);
	}
	
	/**
	 * change url
	 * @param url
	 * @return
	 */
	public Request setUrl(String url)
	{
		this.url = url;
		return (this);
	}
	
	/**
	 * change port server web
	 * @param port
	 * @return
	 */
	public Request setPort(int port)
	{
		this.port = port;
		return (this);
	}
	
	/**
	 * replace params into your POST
	 * @param params
	 * @return
	 */
	public Request setParams(List<NameValuePair> params)
	{
		this.params = params;
		return (this);
	}
	
	/**
	 * add one param into your POST
	 * @param params
	 * @return
	 */
	public Request addParam(String key, String value)
	{
		params.add(new BasicNameValuePair(key, value));
		return (this);
	}
	
	/**
	 * clean all params of your POST
	 * @return
	 */
	public Request clearParam()
	{
		params.clear();
		return (this);
	}
	
	/**
	 * return if use proxy
	 * @return
	 */
	public boolean useProxi()
	{
		return (this.useProxy);
	}
	
	/**
	 * return statut code after execute (base 0)
	 * @return
	 */
	public int getStatusCode()
	{
		return (this.statusCode);
	}
	
	/**
	 * if have error on execute !success
	 * @return
	 */
	public boolean isStoppedByError()
	{
		return (!success);
	}
	
	/**
	 * return content after execute
	 * @return
	 */
	public String getContent()
	{
		return (this.content);
	}
	
	/**
	 * Test proxy return false if connection is null
	 * @return
	 */
	public boolean isProxyOK()
	{
		try {
			SocketAddress proxyAddr = new InetSocketAddress(proxyIP, proxyPort);
	        Proxy pr = new Proxy(Proxy.Type.HTTP, proxyAddr);
	        con = (HttpURLConnection) new URL("http://google.com").openConnection(pr);
	        con.setConnectTimeout(30 * 1000);
	        con.setReadTimeout(30 * 1000);
	        con.connect();
	        if (con.usingProxy())
	        {
	        	Thread.sleep(40000);
	        	return (true);
	        }
        }
		catch (Exception e)
		{
			e.printStackTrace();
			return (false);
		}
		finally
		{
			disconnectToProxy();
		}
		return (false);
	}
	
	/**
	 * IN WORKING
	 * @return
	 */
	public boolean ConnectProxy()
	{
		try {
			SocketAddress proxyAddr = new InetSocketAddress(proxyIP, proxyPort);
	        Proxy pr = new Proxy(Proxy.Type.HTTP, proxyAddr);
	        con = (HttpURLConnection) new URL(protocol + "://" + url).openConnection(pr);
	        con.setConnectTimeout(30 * 1000);
	        con.setReadTimeout(30 * 1000);
	        con.connect();
	        if (con.usingProxy())
	        {
	        	Thread.sleep(40000);
	        	System.setProperty("http.proxyHost", proxyIP);
	        	System.setProperty("http.proxyPort", "" + proxyPort);
	        	System.out.println("Connected on " + System.getProperty("http.proxyHost") + ":" + System.getProperty("http.proxyPort"));
	        	return (true);
	        }
        }
		catch (Exception e)
		{
			e.printStackTrace();
			return (false);
		}
		finally
		{
			disconnectToProxy();
		}
		return (false);
	}
	
	/**
	 * IN WORKING
	 * Disconnect proxy
	 */
	public void disconnectToProxy()
	{
		if (con == null)
			return ;
		if (con.usingProxy())
			con.disconnect();
	}
	
	/**
	 * process request
	 */
	public void execute()
	{
		this.success = false;
		this.statusCode = 0;
		RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).build();
	    HttpClientContext context = HttpClientContext.create();
	    context.setCookieStore(cookieStore);
	    
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore).build();
		CloseableHttpResponse response = null;
		try {
            HttpHost target = new HttpHost(url, port, protocol);
            HttpHost proxy = null;
            RequestConfig config = null;

            if (useProxy)
            {
	            proxy = new HttpHost(proxyIP, proxyPort, proxyProtocol);
	
	            config = RequestConfig.custom()
	                    .setProxy(proxy)
	                    .build();
            	//ConnectProxy();
            }
            
            HttpRequest request = null;
            if (GET)
            {
            	request = new HttpGet("/");
            	if (config != null)
                	((HttpGet)request).setConfig(config);
            }
            else if (POST)
            {
            	request = new HttpPost("/");
            	if (config != null)
                	((HttpPost)request).setConfig(config);
				((HttpPost)request).setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            }
           
            for (Entry<String, String> entry : header.entrySet())
            {
            	request.addHeader(entry.getKey(), entry.getValue());
            }
            
            try
            {
            	response = httpclient.execute(target, request, context);
            	HttpEntity entity = response.getEntity();
            	this.cookieStore = context.getCookieStore();
            	this.statusCode = response.getStatusLine().getStatusCode();
    			this.content = EntityUtils.toString(entity, "UTF-8");
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
            finally {
                try {
                	if (response != null)
                		response.close();
                	} catch (IOException e) {}
            }
        } catch (UnsupportedEncodingException e)
		{
        	e.printStackTrace();
        	//error encoding
		}
		finally {
            try {httpclient.close();} catch (IOException e) {}
            
           // if (useProxy)
            //	disconnectToProxy();
        }
		this.success = true;
	}
}
