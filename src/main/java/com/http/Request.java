package com.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLException;

import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;


/**
 * Description : Browser simulation <br>
 * @author jguyet<br>
 * <br>
 * default :<br>
 * <br>
 * protocol : http<br>
 * proxy : false<br>
 * method : GET<br>
 * <br>
 * proxy default :<br>
 * <br>
 * port : 222<br>
 * IP : 127.0.0.1<br>
 * protocol : http
 */
public class Request {
	
	//private static final Logger	LOGGER = LoggerFactory.getLogger(Request.class);
	
	/**
	 * Internal Errors
	 */
	private static final int	NO_ERROR					= 0;
	private static final int	ERROR_UNDEFIEND				= 1;
	private static final int	ERROR_CLIENT_PROTOCOL 		= 2;
	private static final int	ERROR_SSL					= 3;
	private static final int	ERROR_RESPONSE_TIME_OUT		= 4;
	private static final int	ERROR_SOCKET_TIME_OUT		= 5;
	private static final int	ERROR_CONNECTION_TIME_OUT	= 4;
	
	private String				url							= "";
	private String				protocol					= "http";
	private boolean				useProxy					= false;
	private String				proxyIP						= "127.0.0.1";
	private int					proxyPort					= 222;
	private String				proxyProtocol				= "http";
	private String				proxyUsername				= null;
	private String				proxyPassword				= "";
	private Map<String, String> header						= new TreeMap<String, String>();
	private CookieStore 		cookieStore					= new BasicCookieStore();
	private boolean				GET							= true;
	private boolean				POST						= false;
	List<NameValuePair>			params						= new ArrayList<NameValuePair>();
	private HttpEntity			httpEntity					= null;
	
	private String 				content						= null;
	private HttpResponse		HttpResponse				= null;
	private int					statusCode					= 0;
	
	private int					timeout						= 60000;
	
	private String				referer						= null;
	
	private HttpURLConnection	con							= null;
	private HttpClientContext	context						= null;
	
	private int					internalErrorCode			= Request.NO_ERROR;
	
	/**
	 * Constructor
	 */
	public Request() {
		org.apache.log4j.Logger.getLogger(org.apache.http.client.protocol.ResponseProcessCookies.class).setLevel(Level.OFF);
		org.apache.log4j.Logger.getLogger(org.apache.http.impl.execchain.RetryExec.class).setLevel(Level.OFF);
	}
	
	/**
	 * Method execute set POST
	 * @return
	 */
	public Request setPost() {
		if (GET)
			GET = false;
		POST = true;
		return (this);
	}
	
	/**
	 * Method execute set GET (default)
	 * @return
	 */
	public Request setGET() {
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
	public Request setProxy(String ip, int port, String username, String password) {
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
	public Request setProxy(String ip, int port) {
		this.proxyIP = ip;
		this.proxyPort = port;
		this.useProxy = true;
		return (this);
	}
	
	/**
	 * change protocol connection proxy https
	 * @return
	 */
	public Request setProxyHttps() {
		this.proxyProtocol = "https";
		return (this);
	}
	
	/**
	 * change protocol connection proxy http (default)
	 * @return
	 */
	public Request setProxyHttp() {
		this.proxyProtocol = "http";
		return (this);
	}
	
	/**
	 * replace Map<String, String> header map
	 * @param h
	 * @return
	 */
	public Request setHeader(Map<String, String> h) {
		if (h.containsKey("Referer"))
			referer = null;
		this.header = h;
		return (this);
	}
	
	/**
	 * Add key and value into the header
	 * @param key
	 * @param value
	 * @return
	 */
	public Request addHeader(String key, String value) {
		if (key.equalsIgnoreCase("Referer"))
			referer = null;
		if (this.header.containsKey(key))
			this.header.remove(key);
		this.header.put(key, value);
		return (this);
	}
	
	public Map<String, String> getHeader() {
		return this.header;
	}
	
	/**
	 * remove key and value into the header
	 * @param key
	 * @return
	 */
	public Request removeHeader(String key) {
		if (this.header.containsKey(key))
			this.header.remove(key);
		return (this);
	}
	
	/**
	 * clean current header
	 * @return
	 */
	public Request clearHeader() {
		this.header.clear();
		return (this);
	}
	
	/**
	 * get all cookies
	 * @return
	 */
	public CookieStore getCookieStore() {
		return (cookieStore);
	}
	
	/**
	 * replace cookieStore
	 * @param cook
	 * @return
	 */
	public Request setCookieStore(CookieStore cook) {
		this.cookieStore = cook;
		return (this);
	}
	
	/**
	 * add one Cookie into current cookieStore
	 * @param cook
	 * @return
	 */
	public Request addCookie(Cookie cook) {
		this.cookieStore.addCookie(cook);
		return (this);
	}
	
	/**
	 * clean cookieStore
	 * @return
	 */
	public Request clearCookie() {
		this.cookieStore.clear();
		return (this);
	}
	
	/**
	 * set protocol http (default)
	 * @return
	 */
	public Request setProtocolHttp() {
		this.protocol = "http";
		return (this);
	}
	
	/**
	 * set protocol https (default)
	 * @return
	 */
	public Request setProtocolHttps() {
		this.protocol = "https";
		return (this);
	}
	
	/**
	 * change url syntax = http://website.com
	 * @param url
	 * @return
	 */
	public Request setUrl(String url) {
		this.url = url;
		return (this);
	}
	
	public String getUrl() {
		return (this.url);
	}
	
	public String getReferer() {
		return (this.referer);
	}
	
	public void setReferer(String url) {
		this.referer = url;
	}
	
	/**
	 * replace params into your POST
	 * @param params
	 * @return
	 */
	public Request setParams(List<NameValuePair> params) {
		this.params = params;
		return (this);
	}
	
	/**
	 * add one param into your POST
	 * @param params
	 * @return
	 */
	public Request addParam(String key, String value) {
		params.add(new BasicNameValuePair(key, value));
		return (this);
	}
	
	/**
	 * clean all params of your POST
	 * @return
	 */
	public Request clearParam() {
		params.clear();
		return (this);
	}
	
	public Request setHttpEntity(HttpEntity e) {
		httpEntity = e;
		return (this);
	}
	
	/**
	 * return if use proxy
	 * @return
	 */
	public boolean useProxi() {
		return (this.useProxy);
	}
	
	/**
	 * return statut code after execute (base 0)
	 * @return
	 */
	public int getStatusCode() {
		return (this.statusCode);
	}
	
	/**
	 * return internal error code
	 * @return
	 */
	public int getInternalError() {
		return (this.internalErrorCode);
	}
	
	/**
	 * Set TimeOut (default : 60000millis)
	 * @param millis
	 */
	public void setTimeOut(int millis) {
		this.timeout = millis;
	}
	
	/**
	 * Return timeOut on millisecondes
	 * @return
	 */
	public int getTimeOut() {
		return (this.timeout);
	}
	
	public void setDefaultHeader() {
		Map<String, String> header = new TreeMap<String, String>();

		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
		header.put("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3");
		header.put("Accept-Encoding", "gzip, deflate, br");
		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.put("Connection", "Keep-Alive");
		this.setHeader(header);
	}
	
	/**
	 * return content after execute
	 * @return
	 */
	public String getContent() {
		return (this.content);
	}
	
	public HttpResponse getHttpReponse() {
		return (this.HttpResponse);
	}
	
	public boolean isInternalError() {
		return (internalErrorCode != Request.NO_ERROR);
	}
	
	/**
	 * check statusCode and if error detected
	 * @return
	 */
	public boolean isSuccess() {
		if (internalErrorCode != Request.NO_ERROR
				|| getStatusCode() != HttpStatus.SC_OK
				&& getStatusCode() != HttpStatus.SC_ACCEPTED
				&& getStatusCode() != HttpStatus.SC_CREATED
				&& getStatusCode() != HttpStatus.SC_RESET_CONTENT
				&& getStatusCode() != HttpStatus.SC_PARTIAL_CONTENT
				&& getStatusCode() != HttpStatus.SC_MOVED_TEMPORARILY)
			return false;
		return true;
	}
	
	/**
	 * Test proxy return false if connection is null
	 * @return
	 */
	public boolean isValideProxy() {
		try {
			SocketAddress proxyAddr = new InetSocketAddress(proxyIP, proxyPort);
	        Proxy pr = new Proxy(Proxy.Type.HTTP, proxyAddr);
	        System.setProperty("http.proxyHost", proxyIP);
        	System.setProperty("http.proxyPort", "" + proxyPort);
	        con = (HttpURLConnection) new URL((url.contains("http") ? url : protocol + "://" + url)).openConnection(pr);
	        con.setConnectTimeout(2000);
	        con.setReadTimeout(2000);
	        con.connect();
	        if (con.usingProxy()) {
	        	return (true);
	        }
        }
		catch (Exception e) {
			e.printStackTrace();
			return (false);
		}
		finally {
			if (con == null)
				return (false);
			if (con.usingProxy())
				con.disconnect();
		}
		return (false);
	}
	
	private CloseableHttpClient getClient() {
		RequestConfig globalConfig = RequestConfig.custom()
		.setConnectTimeout(this.timeout)
		.setConnectionRequestTimeout(this.timeout)
		.setSocketTimeout(this.timeout)
		.setCookieSpec(CookieSpecs.DEFAULT)
		.setRedirectsEnabled(false)
		.build();
	    context = HttpClientContext.create();
	    //credentials
	    CredentialsProvider credsProvider = null;
	    if (proxyUsername != null) {
		    Credentials credentials = new UsernamePasswordCredentials(proxyUsername,proxyPassword);
		    AuthScope authScope = new AuthScope(proxyIP, proxyPort);
		    credsProvider = new BasicCredentialsProvider();
		    credsProvider.setCredentials(authScope, credentials);
	    }
		HttpClientBuilder builder = HttpClients.custom();
		
		builder.setDefaultRequestConfig(globalConfig);
	    builder.setDefaultCookieStore(cookieStore);
	    if (credsProvider != null)
	    	builder.setDefaultCredentialsProvider(credsProvider);
	    //--------------------------------------------//
		CloseableHttpClient httpclient = builder.build();
		return httpclient;
	}
	
	private boolean sendPost()
	{
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			httpclient = getClient();
			HttpPost httpPost = new HttpPost(url);

			HttpHost proxy = null;
	        RequestConfig config = null;
	        
	        for (Entry<String, String> entity : this.header.entrySet()) {
	        	httpPost.setHeader(entity.getKey(), entity.getValue());
	        }
	
	        if (useProxy) {
	            proxy = new HttpHost(proxyIP, proxyPort, proxyProtocol);
	            config = RequestConfig.custom()
	            		.setSocketTimeout(this.timeout)
	                    .setConnectTimeout(this.timeout)
	                    .setConnectionRequestTimeout(this.timeout)
	                    .setProxy(proxy)
	                    .build();
	        }
	        
	        if (config != null) {
	        	httpPost.setConfig(config);
	    	}
	        
	        if (httpEntity != null) {
	        	httpPost.setEntity(httpEntity);
	        }
	        else {
	        	httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
	        }
	        try {
				response = httpclient.execute(httpPost, context);
				this.HttpResponse = response;
				content = EntityUtils.toString(response.getEntity(), "UTF-8");
            	this.statusCode = response.getStatusLine().getStatusCode();
            	if (response.getHeaders("Location").length >= 1) {
            		String redirection = response.getHeaders("Location")[0].getValue();
            		this.setUrl(redirection);
            	}
			}
	        catch (ClientProtocolException e)  {
	        	if (this.proxyProtocol.equalsIgnoreCase("http")) {
	        		this.setProxyHttps();
	        		return sendPost();
	        	}
	        	
	        	this.internalErrorCode = ERROR_CLIENT_PROTOCOL;
	        }
	        catch (SSLException e) {
	        	this.internalErrorCode = ERROR_SSL;
	        	//LOGGER.debug("SSL Unrecognized");
	        }
	        catch (ConnectTimeoutException e) {
	        	this.internalErrorCode = ERROR_CONNECTION_TIME_OUT;
	        	//LOGGER.debug("Http Connection Timeout");
	        }
	        catch (NoHttpResponseException e) {
	        	this.internalErrorCode = ERROR_RESPONSE_TIME_OUT;
	        	//LOGGER.debug("Http Response Timeout");
	        }
	        catch (SocketTimeoutException e) {
	        	this.internalErrorCode = ERROR_SOCKET_TIME_OUT;
	        	//LOGGER.debug("Connection Timeout");
	        }
	        catch (SocketException e) {
	        	this.internalErrorCode = ERROR_UNDEFIEND;
	        }
	        catch (Exception e) {
	        	e.printStackTrace();
	        	this.internalErrorCode = ERROR_UNDEFIEND;
	        }
	        finally {
	            try {
	            	if (response != null)
	            		response.close();
	            } catch (IOException e) {}
	        }
		}
		catch (Exception e) {
			e.printStackTrace();
			this.internalErrorCode = ERROR_UNDEFIEND;
			//logger.error("error in openSession",e);
		}
		finally {
            try {
            	if (httpclient != null)
            		httpclient.close();
            } catch (IOException e) {}
        }
		clearParam();
		httpEntity = null;
		return this.isInternalError();
	}
	
	
	private boolean sendGet()
	{
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			httpclient = getClient();
			
			HttpGet httpGet = new HttpGet(url);
			
			HttpHost proxy = null;
	        RequestConfig config = null;
	        
	        for (Entry<String, String> entity : this.header.entrySet()) {
	        	httpGet.setHeader(entity.getKey(), entity.getValue());
	        }
	
	        if (useProxy) {
	            proxy = new HttpHost(proxyIP, proxyPort, proxyProtocol);
	            config = RequestConfig.custom()
	            		.setSocketTimeout(this.timeout)
	                    .setConnectTimeout(this.timeout)
	                    .setConnectionRequestTimeout(this.timeout)
	                    .setProxy(proxy)
	                    .build();
	        }
	        
	        if (config != null) {
	        	httpGet.setConfig(config);
	    	}
	        try {
				response = httpclient.execute(httpGet, context);
				this.HttpResponse = response;
				content = EntityUtils.toString(response.getEntity(), "UTF-8");
				if (response.getHeaders("Location").length >= 1) {
            		String redirection = response.getHeaders("Location")[0].getValue();
            		this.setUrl(redirection);
            	}
            	this.statusCode = response.getStatusLine().getStatusCode();
			}
	        catch (ClientProtocolException e) {
	        	if (this.proxyProtocol.equalsIgnoreCase("http")) {
	        		this.setProxyHttps();
	        		return sendGet();
	        	}
	        	this.internalErrorCode = ERROR_CLIENT_PROTOCOL;
	        }
	        catch (SSLException e) {
	        	this.internalErrorCode = ERROR_SSL;
	        	//LOGGER.debug("SSL Unrecognized");
	        }
	        catch (ConnectTimeoutException e) {
	        	this.internalErrorCode = ERROR_CONNECTION_TIME_OUT;
	        	//LOGGER.debug("Http Connection Timeout");
	        }
	        catch (NoHttpResponseException e) {
	        	this.internalErrorCode = ERROR_RESPONSE_TIME_OUT;
	        	//LOGGER.debug("Http Response Timeout");
	        }
	        catch (SocketTimeoutException e) {
	        	this.internalErrorCode = ERROR_SOCKET_TIME_OUT;
	        	//LOGGER.debug("Connection Timeout");
	        }
	        catch (SocketException e) {
	        	this.internalErrorCode = Request.ERROR_UNDEFIEND;
	        }
	        catch (Exception e) {
	        	e.printStackTrace();
	        	this.internalErrorCode = Request.ERROR_UNDEFIEND;
	        }
	        finally {
	            try {
	            	if (response != null)
	            		response.close();
	            } catch (IOException e) {}
	        }
		}
		catch (Exception e) {
			this.internalErrorCode = Request.ERROR_UNDEFIEND;
		}
		finally {
            try {
            	if (httpclient != null)
            		httpclient.close();
            } catch (IOException e) {}
        }
		return this.isInternalError();
	}
	
	/**
	 * process request
	 */
	public void execute()
	{
		this.internalErrorCode = Request.NO_ERROR;
		generateheader();
		if (!url.contains("http"))
			url = protocol + "://" + url;
		if (POST) {
			sendPost();
		}
		else if (GET) {
			sendGet();
		}
	}
	
	private void generateheader()
	{
		String host = url;
		
		host = host.replace("https://", "").replace("http://", "");
		host = host.split("\\?")[0];
		host = host.split("/")[0];
		header.put("Host", host);
		if (referer != null)
			header.put("referer", referer);
		referer = host;
	}
}
