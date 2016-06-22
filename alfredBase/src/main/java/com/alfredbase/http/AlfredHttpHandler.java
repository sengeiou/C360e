package com.alfredbase.http;

import java.util.Map;

import com.alfredbase.http.NanoHTTPD.Method;
import com.alfredbase.http.NanoHTTPD.Response;

public interface AlfredHttpHandler {
   public Response doPost(String uri, Method mothod, Map<String, String> params, String body);
   public Response doGet(String uri, Method mothod, Map<String, String> params, String body);
   public Response doStaticFile(String uri, Method mothod, Map<String, String> params, String body);

}
