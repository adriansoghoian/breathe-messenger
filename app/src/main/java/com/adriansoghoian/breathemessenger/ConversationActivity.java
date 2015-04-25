package com.adriansoghoian.breathemessenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ConversationActivity extends ActionBarActivity {

    ArrayAdapter<String> messageListAdapter;
    Button sendMessage;
    Conversation conversation;
    Contact contact;
    EditText newMessageEditText;
    Intent intent;
    Message newMessage;
    String contactPIN;
    String selfPIN;
    String messageBuffer = "";
    List<Message> messageList;
    ListView messageListView;
    ArrayList<String> messageContents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ListView messageListView = (ListView)findViewById(R.id.messageListView);
        Button sendMessage = (Button)findViewById(R.id.sendMessage);
        final EditText newMessageEditText = (EditText)findViewById(R.id.newMessage);
        SharedPreferences preferences = this.getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
        selfPIN = preferences.getString("pin", null);

        intent = this.getIntent();
        contactPIN = intent.getStringExtra("ContactPIN");
        getSupportActionBar().setTitle(contactPIN);

        System.out.println("The PIN for the other person in this convo is: " + contactPIN);
        contact = Contact.getByPin(contactPIN);
        conversation = Contact.findConversation(contact);

        List<Message> messageList = Conversation.getAllMessages(conversation);
        for (int i = 0; i < messageList.size(); i++) {
            messageBuffer = messageList.get(i).contact.pin + ": ";
            messageBuffer = messageBuffer + messageList.get(i).body;
            messageContents.add(messageBuffer);
        }
        final ArrayAdapter<String> messageListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, messageContents);
        messageListView.setAdapter(messageListAdapter);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMessageBody = newMessageEditText.getText().toString();
                NewMessageTask newMessageTask = new NewMessageTask();
                newMessageTask.execute(selfPIN, contactPIN, newMessageBody);
            }
        });
    }

    public class NewMessageTask extends AsyncTask<String, String, String> {
        String selfPIN;
        String contactPIN;
        String newMessageBody;

        @Override
        protected String doInBackground(String... conversation) {
            try {
                selfPIN = conversation[0];
                contactPIN = conversation[1];
                newMessageBody = conversation[2];
                sendMessage(selfPIN, contactPIN, newMessageBody);
                System.out.println("We've send a new message to: " + contactPIN + " with message: " + newMessageBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            newMessage = new Message();
            newMessage.body = newMessageBody;
            newMessage.contact = contact;
            newMessage.conversation = conversation;
            newMessage.save();

            String messageBuffer = "";
            messageBuffer = contactPIN + ": " + newMessageBody;
            messageContents.add(messageBuffer);
            messageListView.setAdapter(messageListAdapter);
        }

        private void sendMessage(String selfPIN, String contactPIN, String newMessageBody) throws IOException {
            HttpClient httpClient = new DefaultHttpClient();  // sets up the HTTP post
            HttpPost httpPost = new HttpPost("https://blooming-cliffs-4171.herokuapp.com/message/send");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); // loads up the request with the floor
            nameValuePairs.add(new BasicNameValuePair("pin", contactPIN));
            nameValuePairs.add(new BasicNameValuePair("senderPIN", selfPIN)); // TODO - encrypt
            nameValuePairs.add(new BasicNameValuePair("body", newMessageBody)); // TODO - encrypt
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
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
