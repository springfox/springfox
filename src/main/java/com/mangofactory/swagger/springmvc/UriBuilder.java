package com.mangofactory.swagger.springmvc;

public class UriBuilder {

	private StringBuilder sb = new StringBuilder();
	public UriBuilder()
	{
	}
	public UriBuilder(String uri)
	{
		sb.append(uri);
	}
	public UriBuilder append(String segment)
	{
		if (!sb.toString().endsWith("/"))
		{
			sb.append("/");
		}
		if (segment.startsWith("/"))
		{
			sb.append(segment.substring(1));
		} else {
			sb.append(segment);
		}
		return this;
	}
	
	public String toString()
	{
		return sb.toString();
	}
	
}
