package com.requestExample;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
 * port : 80
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
	private int					port = 80;
	private boolean				useProxy = false;
	private String				proxyIP = "127.0.0.1";
	private int					proxyPort = 222;
	private String				proxyProtocol = "http";
	private String				proxyUsername = null;
	private String				proxyPassword = "";
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
	 * active proxy witch credential
	 * @param ip
	 * @param port
	 * @param username
	 * @param password
	 * @return
	 */
	public Request setProxy(String ip, int port, String username, String password)
	{
		this.proxyIP = ip;
		this.proxyPort = port;
		this.proxyUsername = username;
		this.proxyPassword = password;
		this.useProxy = true;
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
		if (url.split("://").length > 1)
		{
			if (url.split("://")[0].equalsIgnoreCase("http"))
				this.setProtocolHttp();
			else if (url.split("://")[0].equalsIgnoreCase("https"))
				this.setProtocolHttps();
			url = trimStringByString(url.split("://")[1], "/");
		}
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
	 * check statusCode and if error detected
	 * @return
	 */
	public boolean isSuccess()
	{
		if (isStoppedByError()
				|| getStatusCode() != HttpStatus.SC_OK
				&& getStatusCode() != HttpStatus.SC_ACCEPTED
				&& getStatusCode() != HttpStatus.SC_CREATED
				&& getStatusCode() != HttpStatus.SC_RESET_CONTENT
				&& getStatusCode() != HttpStatus.SC_PARTIAL_CONTENT)
			return false;
		return true;
	}
	
	/**
	 * Test proxy return false if connection is null
	 * @return
	 */
	public boolean isValideProxy()
	{
		try {
			SocketAddress proxyAddr = new InetSocketAddress(proxyIP, proxyPort);
	        Proxy pr = new Proxy(Proxy.Type.HTTP, proxyAddr);
	        System.setProperty("http.proxyHost", proxyIP);
        	System.setProperty("http.proxyPort", "" + proxyPort);
	        con = (HttpURLConnection) new URL(protocol + "://" + url).openConnection(pr);
	        con.setConnectTimeout(30 * 1000);
	        con.setReadTimeout(30 * 1000);
	        con.connect();
	        if (con.usingProxy())
	        {
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
			if (con == null)
				return (false);
			if (con.usingProxy())
				con.disconnect();
		}
		return (false);
	}
	
	/**
	 * process request
	 */
	@SuppressWarnings("deprecation")
	public void execute()
	{
		this.success = false;
		this.statusCode = 0;
		//cookies
		RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).build();
	    HttpClientContext context = HttpClientContext.create();
	    context.setCookieStore(cookieStore);
	    //credentials
	    CredentialsProvider credsProvider = null;
	    if (proxyUsername != null)
	    {
		    Credentials credentials = new UsernamePasswordCredentials(proxyUsername,proxyPassword);
		    AuthScope authScope = new AuthScope(proxyIP, proxyPort);
		    credsProvider = new BasicCredentialsProvider();
		    credsProvider.setCredentials(authScope, credentials);
	    }
	    //------------------builder--------------------//
	    HttpClientBuilder builder = HttpClients.custom();
	    builder.setDefaultRequestConfig(globalConfig);
	    builder.setDefaultCookieStore(cookieStore);
	    if (credsProvider != null)
	    	builder.setDefaultCredentialsProvider(credsProvider);
	    //--------------------------------------------//
		CloseableHttpClient httpclient = builder.build();
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
            }
            
            HttpRequest request = null;
            if (GET)
            {
            	request = new HttpGet("/");
            	if (config != null)
            	{
                	((HttpGet)request).setConfig(config);
                	request.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            	}
            }
            else if (POST)
            {
            	request = new HttpPost("/");
            	if (config != null)
            	{
                	((HttpPost)request).setConfig(config);
                	request.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            	}
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
    			this.success = true;
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            	this.success = false;
            }
            finally
            {
                try {
                	if (response != null)
                		response.close();
                	} catch (IOException e) {}
            }
        } catch (UnsupportedEncodingException e)
		{
        	e.printStackTrace();
        	//error encoding
        	this.success = false;
		}
		finally
		{
            try {httpclient.close();} catch (IOException e) {}
        }
	}
	
	private String trimStringByString(String text, String trimBy)
	{
	    int beginIndex = 0;
	    int endIndex = text.length();

	    while (text.substring(beginIndex, endIndex).startsWith(trimBy))
	    {
	        beginIndex += trimBy.length();
	    } 

	    while (text.substring(beginIndex, endIndex).endsWith(trimBy))
	    {
	        endIndex -= trimBy.length();
	    }

	    return text.substring(beginIndex, endIndex);
	}
}
