package com.http;

import com.weoohh.http.Request;

public class JsonYoutubeParser {

	private static String getScopContent(String scop)
	{
		int start = 0;
		int end = 0;
		int braket = 0;
		boolean first_braket = false;
		for (int i = 0; i < scop.length(); i++)
		{
			if (scop.charAt(i) != '{' && scop.charAt(i) != '}')
				continue ;
			if (scop.charAt(i) == '{' && first_braket == false)
			{
				start = i + 1;
				first_braket = true;
			}
			if (scop.charAt(i) == '{')
				braket++;
			if (scop.charAt(i) == '}')
				braket--;
			if (braket == 0)
			{
				end = i;
				break ;
			}
		}
		return scop.substring(start, end).trim();
	}
	
	public static void main(String[] args) {
		String url = "https://www.youtube.com/watch?v=zGqdRKkphqc";
		
		Request r = new Request();
		
		r.setUrl(url)
		.setProtocolHttps()
		.setDefaultHeader();
		
		r.setGET();
		r.execute();
		
		try {
			String json = "{" + getScopContent("{" + r.getContent().split("\\\"videoDetails\\\"\\:\\{")[1]) + "}";

			System.out.print(json);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
