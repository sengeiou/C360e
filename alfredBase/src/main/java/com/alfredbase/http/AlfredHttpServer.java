package com.alfredbase.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.alfredbase.utils.LogUtil;


public class AlfredHttpServer extends NanoHTTPD implements AlfredHttpHandler{

	public AlfredHttpServer(int port) {
		super(port);
	}

    @Override 
    public Response serve(IHTTPSession session) {
    	Map<String, String> body = new HashMap<String, String>();
        Method method = session.getMethod();
        String uri = session.getUri();
        String apiName = uri.substring(1);
        Map<String, String> params = session.getParms();
        LogUtil.d(method + " '" + uri + "' ", params.toString());
        try {
			session.parseBody(body);
        } catch (IOException e) {
			e.printStackTrace();
			return getInternalErrorResponse("");
		} catch (ResponseException e) {
			return getInternalErrorResponse("");
		}
        if (Method.POST.equals(method)){
        	return this.doPost(apiName, method, params, body.get("postData"));
        }else if(Method.GET.equals(method)){
        	
        }
        return new NanoHTTPD.Response("");
    }
    
    protected Response getNotFoundResponse() {
        return createResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
            "Error 404, file not found.");
    }

    protected Response getJsonResponse(String data) {
        return createResponse(Response.Status.OK, NanoHTTPD.MIME_JSON,
        		data);
    }
    
    protected Response getForbiddenResponse(String s) {
        return createResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: "
            + s);
    }

    protected Response getInternalErrorResponse(String s) {
        return createResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT,
            "INTERNAL ERRROR: " + s);
    }
    
    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, InputStream message) {
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, String message) {
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

	@Override
	public Response doPost(String uri, Method mothod,
			Map<String, String> params, String body) {
		
		return getForbiddenResponse("Not Support yet");
	}

	@Override
	public Response doGet(String uri, Method mothod,
			Map<String, String> params, String body) {

		return getForbiddenResponse("Not Support yet");
	}

	@Override
	public Response doStaticFile(String uri, Method mothod,
			Map<String, String> params, String body) {
		// TODO Auto-generated method stub
		return getForbiddenResponse("Not Support yet");
	}
    
}
