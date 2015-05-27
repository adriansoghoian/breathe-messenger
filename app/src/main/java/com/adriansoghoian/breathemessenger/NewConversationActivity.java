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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NewConversationActivity extends ActionBarActivity {


    Contact currentUser;
    Conversation newConversation;
    Message newMessage;
    Button startConversation;
    EditText contactName;
    EditText messageBody;
    String name;
    String body;
    String recipientPin;
    String senderPin;
    NewConversationTask newConversationTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = this.getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        senderPin = preferences.getString("pin", null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        final EditText contactName = (EditText)findViewById(R.id.contactName);
        final EditText messageBody = (EditText)findViewById(R.id.messageBody);
        Button startConversation = (Button)findViewById(R.id.startConversation);

        startConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipientPin = contactName.getText().toString();
                body = messageBody.getText().toString();

                currentUser = Contact.getCurrentUser();
                Conversation newConversation = new Conversation();

                Message newMessage = new Message();
                newConversation.contact = Contact.getByPin(recipientPin);

                System.out.println("Recipient Pin: " + recipientPin);
                System.out.println(Contact.getByPin(recipientPin).pubKey);
                newConversation.save();

                System.out.println("conversation PIN: " + newConversation.contact.pin);

                newMessage.contact = currentUser;
                newMessage.body = body;
                newMessage.conversation = newConversation;
                System.out.println("new message body: " + newMessage.body);
                System.out.println("conversation PIN: " + newConversation.contact.pin);
                newMessage.save();

                newConversationTask = new NewConversationTask();
                newConversationTask.execute(recipientPin, body, senderPin);
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

    public class NewConversationTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... conversation) {

            try {
                String recipientPin = conversation[0];
                String messageBody = conversation[1];
                String senderPin = conversation[2];
                sendMessage(recipientPin, messageBody, senderPin);
                System.out.println("we've just sent the message");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void sendMessage(String recipientPin, String messageBody, String senderPin) throws IOException {
            HttpPost httpPost = new HttpPost("https://blooming-cliffs-4171.herokuapp.com/message/send");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); // loads up the request with the floor
            nameValuePairs.add(new BasicNameValuePair("pin", recipientPin));
            nameValuePairs.add(new BasicNameValuePair("sender_pin", senderPin)); // TODO - encrypt
            nameValuePairs.add(new BasicNameValuePair("body", messageBody)); // TODO - encrypt
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            try {
                HttpResponse httpResponse = TorWrapper.getInstance().execute(httpPost);
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    String responseString = EntityUtils.toString(entity)    ;
                    System.out.println(responseString);
                }
                httpResponse.getEntity().consumeContent(); // closes out the request once its been made
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
