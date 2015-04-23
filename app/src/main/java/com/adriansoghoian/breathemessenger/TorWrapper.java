package com.adriansoghoian.breathemessenger;

import android.content.Context;

import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpResponse;

import java.io.IOException;

public class TorWrapper {
    // fields
    private static TorWrapper instance = null;
    private final static String PROXY_HOST = "127.0.0.1";
    private final static int PROXY_PORT = 9050; // 8118 for HTTP, 9050 for SOCKS
    private static DefaultHttpClient client;

    // constructors
    private TorWrapper(){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        client = httpclient;
    }

    // thread safe singleton access
    public static synchronized TorWrapper getInstance(){
        if(instance == null){
            instance = new TorWrapper();
        }
        return instance;
    }

    // execute arbitrary http request via proxy
    public static HttpResponse execute(HttpRequestBase request){
        try{
            return client.execute(request);
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
