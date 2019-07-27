package com.paul.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * 响应处理工具类
 */
public class HttpUtil {
	
	private static String CONTENT_TYPE = "Content-Type";
	private static String CONTENT_LENGTH = "Conteng-Length";
	
	/**
	 * 输出纯Json字符串
	 */
	public static FullHttpResponse constructJSON(String json){
		return constuct(json, "text/x-json;charset=UTF-8",
				HttpResponseStatus.OK);
	}
	
	/**
	 * 输出纯字符串
	 */
	public static FullHttpResponse constructText(String text) {
		return constuct(text, "text/plain;charset=UTF-8",
				HttpResponseStatus.OK);
	}
	
	/**
	 * 输出纯XML
	 */
	public static FullHttpResponse constructXML(String xml) {
		return constuct(xml, "text/xml;charset=UTF-8",
				HttpResponseStatus.OK);
	}
	
	/**
	 * 输出纯HTML
	 */
	public static FullHttpResponse constructHTML(String html) {
		return constuct(html, "text/html;charset=UTF-8",
				HttpResponseStatus.OK);
	}
	
	public static FullHttpResponse getErroResponse(){
		return constuct("Server error", "text/plain;charset=UTF-8"
		,HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}
	
	public static FullHttpResponse getNotFoundResponse(){
		return constuct("Request not found", "text/plain;charset=UTF-8",HttpResponseStatus.NOT_FOUND);
	}
	
	/**
	 * response输出
	 * @param text
	 * @param contentType
	 */
	public static FullHttpResponse constuct(String text, String contentType,HttpResponseStatus status){
		if(text == null){
			text = "";
		}
		ByteBuf byteBuf = Unpooled.wrappedBuffer(text.getBytes());
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
		response.headers().add(CONTENT_TYPE, contentType);
		response.headers().add(CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
		return response;
	}

}
