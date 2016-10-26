package com.requestExample;

import org.apache.http.HttpStatus;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main {

	public static void main(String[] args)
	{
		Logger.getLogger(org.apache.http.impl.execchain.RetryExec.class).setLevel(Level.OFF);
		
		Request r = new Request();
		
		r.setUrl("www.google.com")
			.setPort(80)
			.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:17.0) Gecko/20100101 Firefox/17.0")
			.execute();
		
		if (r.isStoppedByError() || r.getStatusCode() != HttpStatus.SC_OK)
		{
			//Error
		}
		else
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
