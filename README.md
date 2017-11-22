HttpRequest
==========

### What is it ?
A class to make simple Http requests is fast encoded

Look also that example: [click here](https://github.com/jguyet/HttpRequest/blob/master/request/src/main/java/com/http/Main.java)

Look also Test unitari: [click here](https://github.com/jguyet/HttpRequest/blob/master/request/src/test/java/com/http/TestRequest.java)

These run with a model which looks like that:

    public class Main {
        public static void main(String ...args) {
            
			Request r = new Request();
			
			r.setDefaultHeader();//add default header
			
			r.setUrl("http://google.fr/")
			.setGET()
			.execute();
			
			if (r.isSuccess())//Codes
			{
				System.out.println("Test OK");
				
				r.getStatusCode();//return Status code of the response
				
				r.getContent();//Content
				
				r.getCookieStore();//CookieStore
				
			}
			else
			{
				System.out.println("Test Echec");
				
				r.getStatusCode();//return Status code of the response
				
				r.getErrorCode();//return Codes Of Exceptions Request (timeout/ssl etc)
			}
        }
    }

### Author
jguyet
