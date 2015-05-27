package com.adriansoghoian.breathemessenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class NewContactActivity extends ActionBarActivity {

    Button addContact;
    String contactPinString;
    NewContactTask newContactTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        final EditText contactPin = (EditText)findViewById(R.id.contactPin);
        Button addContact = (Button)findViewById(R.id.addContact);

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactPinString = contactPin.getText().toString();
                newContactTask = new NewContactTask(getApplicationContext());
                newContactTask.execute(contactPinString);
            }
        });
    }

    public class NewContactTask extends AsyncTask<String, Integer, String> {

        List<NameValuePair> nameValuePairs;
        int rand;
        Context context;
        String secret;

        @Override
        public String doInBackground(String... params) {
            try {
                registerUser(params[0]);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        public NewContactTask(Context c) {
            this.context = c;
        }

        public void registerUser(String pin) throws UnsupportedEncodingException {
            HttpPost httppost = new HttpPost("https://blooming-cliffs-4171.herokuapp.com/user/key");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("pin", pin));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            try {
                HttpResponse response = TorWrapper.getInstance().execute(httppost);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseString = EntityUtils.toString(entity)    ;
                    System.out.println(responseString);
                    JSONObject responseJSON = new JSONObject(responseString);
                    String contactKey = responseJSON.get("key").toString();
                    System.out.println("User fetched with key: " + contactKey);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
