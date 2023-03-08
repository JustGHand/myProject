package com.pw.baseutils.retrypool;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.ParseError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 14-8-24
 * Time: 10:02
 */
public class Request extends StringRequest {

    private static final String TAG = Request.class.getName();

    private static Map<String, String> mHeader = new HashMap<String, String>();

    private ResponseListener responseListener;

    public interface ResponseListener{
        void onResponse(int statusCode);
    }
    /**
     * 设置访问自己服务器时必须传递的参数，密钥等
     */
    static {
        mHeader.put("charset", "utf-8");
    }

    private Map<String, String> params;
    private boolean compress;
    private String url;
    private String body;

    public Request(int method, String url, String body,Map<String,String> header, Response.Listener<String> listener, Response.ErrorListener errorListener, boolean compress) {
        super(method, url, listener, errorListener);
        this.body = body;
        this.compress = compress;
        this.url = url;
        mHeader.putAll(header);
        if (compress) {
            mHeader.put("Accept-Encoding", "gzip");
        }
        mHeader.put("content-type", "application/json;charset=UTF-8");
        setShouldCache(false);
    }

    public Request(int method, String url, String body,ResponseListener responseListener, Response.Listener<String> listener, Response.ErrorListener errorListener, boolean compress) {
        super(method, url, listener, errorListener);
        this.responseListener = responseListener;
        this.body = body;
        this.compress = compress;
        this.url = url;
        if (compress) {
            mHeader.put("Accept-Encoding", "gzip");
        }
        mHeader.put("content-type", "application/json;charset=UTF-8");
        setShouldCache(false);
    }

    private String encodeParameters(Map<String, String> params) {
        try {
            StringBuilder encodedParams = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                encodedParams.append('&');
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public byte[] getPostBody() throws AuthFailureError {
        return getBody();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (!TextUtils.isEmpty(body)) {
            try {
                return body.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return super.getBody();
            }
        }
        return super.getBody();
    }

    @Override
    public String getUrl() {
        try {
//            if (Method.GET == getMethod() && getParams() != null && getParams().size() > 0) {
            if (getParams() != null && getParams().size() > 0) {
                final String url = this.url + "?" + encodeParameters(getParams());
                return url;
            }
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        return super.getUrl();
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeader;
    }

    // parse the gzip response using a GZIPInputStream
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        if (responseListener != null) {
            responseListener.onResponse(response.statusCode);
        }
        String output = null;
        try {
            output = getRealString(response.data);
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
        return Response.success(output, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        super.deliverResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }

    private int getShort(byte[] data) {
        return (int) ((data[0] << 8) | data[1] & 0xFF);
    }

    private String getRealString(byte[] data) throws IOException {
        byte[] h = new byte[2];
        h[0] = (data)[0];
        h[1] = (data)[1];
        int head = getShort(h);
        boolean t = head == 0x1f8b;
        InputStream in;
        StringBuilder sb = new StringBuilder();
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        if (t) {
            in = new GZIPInputStream(bis);
        } else {
            in = bis;
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }
}
