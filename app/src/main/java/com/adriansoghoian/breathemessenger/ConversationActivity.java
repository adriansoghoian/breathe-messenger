package com.adriansoghoian.breathemessenger;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ConversationActivity extends ActionBarActivity {

    Conversation conversation;
    Contact contact;
    Intent intent;
    TextView contactPINView;
    String contactPIN;
    String messageBuffer = "";
    List<Message> messageList;
    ArrayList<String> messageContents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        TextView contactPINView = (TextView)findViewById(R.id.contactPIN);
        ListView messageListView = (ListView)findViewById(R.id.messageListView);

        intent = this.getIntent();
        contactPIN = intent.getStringExtra("ContactPIN");
        contactPINView.setText(contactPIN);

        contact = Contact.getByPin(contactPIN);
        conversation = Contact.findConversation(contact);

        List<Message> messageList = Conversation.getAllMessages(conversation);
        for (int i = 0; i < messageList.size(); i++) {
            messageBuffer = messageList.get(i).contact.pin + ": ";
            messageBuffer = messageBuffer + messageList.get(i).body;
            messageContents.add(messageBuffer);
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
