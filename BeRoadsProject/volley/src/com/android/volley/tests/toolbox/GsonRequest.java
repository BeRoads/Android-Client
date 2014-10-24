package com.android.volley.tests.toolbox;

import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
 
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
 
/**
* Volley adapter for JSON requests that will be parsed into Java objects by Gson.
*/
public class GsonRequest<T> extends Request<T> {
    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

	private final Gson gson = new Gson();
	private final Class<T> clazz;
	private final Map<String, String> headers;
	private final Listener<T> listener;
    private final Type type;

    /**
	* Make a GET request and return a parsed object from JSON.
	*
	* @param url URL of the request to make
	* @param clazz Relevant class object, for Gson's reflection
	* @param headers Map of request headers
	*/
	public GsonRequest(String url, Class<T> clazz, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		this.clazz = clazz;
        this.type = null;
		this.headers = headers;
		this.listener = listener;
	}

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param type Relevant type object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url, Type type, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.type = type;
        this.clazz = null;
        this.headers = headers;
        this.listener = listener;
    }

    /**
     * Make a GET, POST, PUT and Delete request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int method,String url, Class<T> clazz, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.type = null;
        this.headers = headers;
        this.listener = listener;
    }

    /**
     * Make a GET, POST, PUT and Delete request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param type Relevant type object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int method,String url, Type type, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.type = type;
        this.clazz = null;
        this.headers = headers;
        this.listener = listener;
    }
 
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String,String> headers2 = new HashMap<String, String>();

        headers2.put("Accept",PROTOCOL_CONTENT_TYPE);
        headers2.put("Content-type",PROTOCOL_CONTENT_TYPE);

        return headers != null ? headers : headers2;
	}
 
	@Override
	protected void deliverResponse(T response) {
		listener.onResponse(response);
	}
 
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(
				response.data, HttpHeaderParser.parseCharset(response.headers));

            T entityResponse = null;

            if (clazz != null){
                entityResponse = gson.fromJson(json, clazz);
            } else if (type != null){
                entityResponse = gson.fromJson(json, type);
            } else {
                VolleyLog.e("Type or clazz must be not null");
            }

            return Response.success(
				entityResponse, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}