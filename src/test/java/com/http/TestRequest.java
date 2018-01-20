// package com.http;
//
//
// import com.http.Request;
//
// import junit.framework.Test;
// import junit.framework.TestCase;
// import junit.framework.TestSuite;
//
// class TestRequest extends TestCase implements TestRequestCase{
//
// 	public TestRequest(String name)
// 	{
// 		super(name);
// 	}
//
// 	public static Test suite()
// 	{
// 		return (new TestSuite(TestRequest.class));
// 	}
//
// 	public void testApp() {
// 		Request r = new Request();
//
// 		r.addCookie(null);
// 		r.clearCookie();
// 		r.setProtocolHttps().setProtocolHttp();
// 		r.setGET().setPost().clearHeader().clearParam();
// 		r.getCookieStore();
// 		//test des fonctions
// 		if (r.isStoppedByError() == false)
// 		{
// 			assertTrue(false);
// 			return ;
// 		}
// 		//preparation d'un du header
// 		r.setGET().setUrl("http://www.this-page-intentionally-left-blank.org/")
// 		.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36")
// 		.addHeader("Connection", "keep-alive");
//
// 		//lancement d'une get
// 		r.execute();
//
// 		//si echou�
// 		if (!r.isSuccess())
// 		{
// 			assertTrue(false);
// 			return ;
// 		}
//
// 		//ont garde le contenue
// 		String content = r.getContent();
// 		r.execute();
//
// 		//comparaison des difference entre deux meme requetes
// 		if (!r.isSuccess() || r.getHttpReponse() != null && !r.getContent().equalsIgnoreCase(content))
// 		{
// 			assertTrue(false);
// 			return ;
// 		}
//
// 		//test d'une post bidon
// 		r.setPost()
// 		.addParam("testparam", "okparam")
// 		.execute();
//
// 		//comparaison avec la get de base normlament la meme chose
// 		if (!r.isSuccess() || r.getHttpReponse() != null && !r.getContent().equalsIgnoreCase(content))
// 		{
// 			assertTrue(false);
// 			return ;
// 		}
//
// 		//maintenant test avec un proxy
// 		r.setGET()
// 		.setProxy("91.121.42.68", 80)
// 		.setProxy("91.121.42.68", 80, null, null)
// 		.setProxyHttps()
// 		.setProxyHttp();
//
// 		//verifi le proxy
// 		if (!r.isValideProxy())
// 			System.out.println("Proxy is not valide");
// 		else if (r.useProxi())
// 		{
// 			//proxy valide ont echoue pas le test cars defois ils sont pas vraiment fonctionnel pour autant
// 			System.out.println("valide proxy");
//
// 			//execute notre requete
// 			r.execute();
// 			//si pas pareil que nos ancienne requetes ont dit que c'est pas fonctionnel
// 			if (!r.isSuccess() || r.getHttpReponse() != null && !r.getContent().equalsIgnoreCase(content))
// 			{
// 				System.out.println("Proxy is not valide OR error");
// 			}
// 			else//sinon Excelent proxy !
// 				System.out.println("Proxy OK");
// 		}
// 		//test OK
// 		assertTrue(true);
// 	}
//
//
// }
