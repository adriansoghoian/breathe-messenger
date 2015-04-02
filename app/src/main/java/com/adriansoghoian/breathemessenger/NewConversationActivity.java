package com.adriansoghoian.breathemessenger;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NewConversationActivity extends ActionBarActivity {

    Button startConversation;
    EditText contactName;
    EditText messageBody;
    String name;
    String body;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        final EditText contactName = (EditText)findViewById(R.id.contactName);
        final EditText messageBody = (EditText)findViewById(R.id.messageBody);
        Button startConversation = (Button)findViewById(R.id.startConversation);

        startConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = contactName.getText().toString();
                body = messageBody.getText().toString();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_conversation, menu);
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

//    public class NewConversationTask extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... conversation) {
//
//            try {
//                System.out.println(floor);
//                httpPut();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        private void httpPut() throws IOException {
//            HttpClient httpclient = new DefaultHttpClient();  // sets up the HTTP post
//            HttpPost httppost = new HttpPost("https://blooming-cliffs-4171.herokuapp.com/messages");
//
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); // loads up the request with the floor
//            nameValuePairs.add(new BasicNameValuePair("floor", floor));
//
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            HttpResponse httpResponse = httpclient.execute(httppost);
//
//            httpResponse.getEntity().consumeContent(); // closes out the request once its been made
//        }
//
//    }


}
