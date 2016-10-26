package com.requestExample;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main {

	public static void main(String[] args)
	{
		Logger.getLogger(org.apache.http.impl.execchain.RetryExec.class).setLevel(Level.OFF);
		
		Map<String, String> header = new TreeMap<String, String>();
		
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
		header.put("Accept-Language", "fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4");
		header.put("Accept-Encoding", "gzip, deflate, sdch");
		header.put("Accept", "text/css,*/*;q=0.1");
		Request r = new Request();
		
		r.setUrl("http://google.com")
			.setProxy("46.105.214.133", 3128)
			.execute();
		
		if (r.isSuccess())
		{
			//content
			System.out.println(r.getContent());
			//cookie
			System.out.println(r.getCookieStore().getCookies().toString());
			//status
			System.out.println("Success " + r.getStatusCode());
		}
	}
}
