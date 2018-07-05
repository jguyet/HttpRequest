 package com.http;


 import static org.junit.jupiter.api.Assertions.assertEquals;

 import org.junit.Test;

public class TestRequest {

	@Test
 	public void testStatusOK() {
 		System.out.println("-------- testStatusOK");
 		Request r = new Request();
 		
 		r.setGET().setUrl("https://www.google.com").setProtocolHttps().setDefaultHeader();
 		r.execute();
 		assertEquals(200, r.getStatusCode());
 	}
	
	@Test
 	public void testValidHttpContent() {
 		System.out.println("-------- testValidHttpContent");
 		Request r = new Request();
 		
 		r.setGET().setUrl("http://www.google.com").setProtocolHttp().setDefaultHeader();
 		r.execute();
 		assertEquals(false, r.getContent().isEmpty());//https://stackoverflow.com/
 	}
	
	@Test
 	public void testValidHttpsContent() {
 		System.out.println("-------- testValidHttpsContent");
 		Request r = new Request();
 		
 		r.setGET().setUrl("https://stackoverflow.com/").setProtocolHttps().setDefaultHeader();
 		r.execute();
 		assertEquals(false, r.getContent().isEmpty());
 	}
	
 	@Test
 	public void testRedirection() {
 		System.out.println("-------- testRedirection");
 		Request r = new Request();
 		
 		r.setGET().setUrl("http://www.google.com").setProtocolHttp().setDefaultHeader();
 		r.execute();
 		String url = r.getUrl();
 		r.execute();
 		assertEquals(true, r.getUrl() == url);
 	}
 	
 	@Test
 	public void testProxyValid() {
 		System.out.println("-------- testProxyValid");
 		Request r = new Request();
 		r.setProxy("152.45.42.8", 150).setProxyHttp();
 		assertEquals(false, r.isValideProxy());
 	}
 }
