package com.adriansoghoian.breathemessenger;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.Proxy;
import java.net.UnknownHostException;

import info.guardianproject.net.SocksHttpClient;

public class TorWrapper {
    // fields
    private static TorWrapper instance = null;
    private static HttpClient client;
    private final static Proxy.Type proxyType = Proxy.Type.SOCKS; // prefer SOCKS, HTTP leaks!
    private final static String PROXY_HOST = "127.0.0.1";
    private final static int PROXY_PORT = 9050; // 8118 for HTTP, 9050 for SOCKS

    // constructors
    private TorWrapper(){
        // TODO: should start TOR connection / orbot here as well
        HttpClient httpclient = null;

        // set client based on proxy type
        try {
            if (proxyType == Proxy.Type.SOCKS) {
                httpclient = new SocksHttpClient(PROXY_HOST, PROXY_PORT);
            }
            else if(proxyType == Proxy.Type.HTTP){
                httpclient = new DefaultHttpClient();
                HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
                httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            }
        }
        catch(UnknownHostException e){
            e.printStackTrace();
        }

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
