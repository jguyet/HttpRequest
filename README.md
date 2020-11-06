HttpRequest
==========
[![Build Status](https://travis-ci.org/jguyet/HttpRequest.svg?branch=master)](https://travis-ci.org/jguyet/HttpRequest)  

## Installation :

## MAVEN :

````xml
<dependencies>
...
	<dependency>
		<groupId>com.weoohh</groupId>
		<artifactId>request</artifactId>
		<version>1.0.0</version>
    </dependency>
...
</dependencies>
````
## Documentation :

#### GET request

````java
import com.weoohh.http.Request;

public class Main {

    public static void main(String[] args) {
        Request r = new Request();

        r.setUrl("http://www.google.com/")
			.setGet()
			.execute();

        System.out.println(r.getStatusCode());
    }
}
````

#### POST request

use HttpEntity 

````java
import com.weoohh.http.Request;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class Main {

    public static void main(String[] args) {
        Request r = new Request();
        HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);

        r.setUrl("http://www.google.com/")
			.setPost()
			.setHttpEntity(jsonEntity)
			.execute();

        System.out.println(r.getStatusCode());
    }
}
````

## Example :

### What is it ?
A class to make simple Http requests is fast encoded

Look also that example: [click here](https://github.com/jguyet/HttpRequest/blob/master/request/src/main/java/com/http/Main.java)

Look also Test unitari: [click here](https://github.com/jguyet/HttpRequest/blob/master/request/src/test/java/com/http/TestRequest.java)

These run with a model which looks like that:

````java
    public class Main {
        public static void main(String ...args) {
            
			Request r = new Request();
			
			r.setDefaultHeader();//add default header
			
			r.setUrl("http://www.google.com/")
			.setGET()
			.execute();
			
			if (r.isSuccess())// Codes == 200 || 202 || 201 || 205 || 206 || 302
			{
				System.out.println("Test OK");
				
				r.getStatusCode();//return Status code of the response
				
				r.getContent();//Content
				
				r.getCookieStore();//CookieStore
				
			}
			else
			{
				System.out.println("Test KO");
				
				r.getStatusCode();//return Status code of the response
				
				r.getErrorCode();//return Codes Of Exceptions Request (timeout/ssl etc)
			}
        }
    }
````

### Author
jguyet in 2017
