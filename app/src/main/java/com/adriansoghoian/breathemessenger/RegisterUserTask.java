package com.adriansoghoian.breathemessenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by adrian on 3/31/15.
 */
public class RegisterUserTask extends AsyncTask<String, Integer, String> {

    String pin;
    List<NameValuePair> nameValuePairs;
    int rand;
    Context context;

    @Override
    public String doInBackground(String... params) {
        try {
            registerUser(params[0]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerUser(String pin) throws UnsupportedEncodingException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://blooming-cliffs-4171.herokuapp.com/user/create");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("pin", pin));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        try {
            HttpResponse response = httpClient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseString = EntityUtils.toString(entity)    ;
                System.out.println(responseString);
                JSONObject responseJSON = new JSONObject(responseString);
                // TODO - check response from server
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
