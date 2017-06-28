package com.alfredbase.http;

import com.alfredbase.javabean.NanoHTTPD.Method;
import com.alfredbase.javabean.NanoHTTPD.Response;

import java.io.IOException;
import java.util.Map;

public interface AlfredHttpHandler {
   public Response doPost(String uri, Method mothod, Map<String, String> params, String body);
   public Response doDesktopPost(String uri, Method mothod, Map<String, String> params, String body);
   public Response doGet(String uri, Method mothod, Map<String, String> params, String body) throws IOException;
   public Response doStaticFile(String uri, Method mothod, Map<String, String> params, String body);

}
