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
    private final static Proxy.Type proxyType = Proxy.Type.HTTP;
    private final static String PROXY_HOST = "localhost"; // 127.0.0.1 (Home Sweet Home)
    private final static int PROXY_PORT = 8118; // 8118 for HTTP, 9050 for SOCKS

    // constructors
    private TorWrapper(){
        // TODO: should start TOR connection here, otherwise must be running orbot
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
        System.out.println(client.getClass());
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
