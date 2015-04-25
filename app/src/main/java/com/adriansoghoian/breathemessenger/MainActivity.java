package com.adriansoghoian.breathemessenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static javax.crypto.Cipher.ENCRYPT_MODE;


public class MainActivity extends ActionBarActivity {

    ArrayList<String> conversationList;
    ArrayAdapter<String> conversationListAdapter;
    boolean dbExists;
    Button new_conversation;
    Cursor c;
    Cursor c_temp;
    HttpClient httpClient;
    int i;
    Intent conversationActivityIntent;
    KeyHandler keyHandler;
    PublicKey publicKey;
    PrivateKey privateKey;
    String pin;
    String status;
    Cryptosaurus cryptosaurus;
    String sqlQuery;
    String recipientID;
    String currentState;
    TextView pinView;
    SQLiteDatabase db;
    ListView conversationListUI;
    RegisterUserTask registerNewUserTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = this.getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        currentState = preferences.getString("status", null);

        keyHandler = new KeyHandler();
        System.out.println("Current state is: " + currentState);

        if (currentState == null) {
            Context context = getApplicationContext();
            publicKey = keyHandler.buildKeys(context);
            createNewUser();
            editor.putString("status", "Preferences already set.");
            editor.commit();
        } else {
            status = "Not first run";
            System.out.println("The app has been run before.");
        }
        Button new_conversation = (Button)findViewById(R.id.new_conversation);
        TextView pinView = (TextView)findViewById(R.id.pin);
        pinView.setText(preferences.getString("pin", null));
        ListView conversationListUI = (ListView)findViewById(R.id.conversationListUI);

        if (status == "Not first run") {
            new refreshMessages(this).execute();
        }
        conversationListUI.setClickable(true);
        conversationListUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Conversation> conversationList = Conversation.getAll();
                Conversation selectedConversation = conversationList.get(position);
                String conversationContactPIN = selectedConversation.contact.pin;
                conversationActivityIntent = new Intent();
                conversationActivityIntent.putExtra("ContactPIN", conversationContactPIN);
                startActivity(new Intent(getApplicationContext(), ConversationActivity.class));
            }
        });

        new_conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewConversationActivity.class));
            }
        });
        try {
            fetchKeys();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cryptosaurus = new Cryptosaurus(publicKey, privateKey);
    }

    public void createNewUser() {
        // Generates a random PIN
        SecureRandom pinGen = new SecureRandom();
        pin = new BigInteger(32, pinGen).toString(16);
        System.out.println("The PIN is: " + pin);
        registerNewUserTask = new RegisterUserTask(this.getApplicationContext());
        registerNewUserTask.execute(pin); // Sends the new PIN to the server, registering the user

        Contact currentUser = new Contact();
        currentUser.name = "You";
        currentUser.pin = pin;
        currentUser.pubKey = publicKey.toString();
        currentUser.save(); // Saves the user's own credential to the local DB for future reference.

        SharedPreferences preferences = this.getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pin", pin);
        editor.putInt("numMessages", 0); // Initializes the counter at 0.
        editor.commit(); // Saves the PIN to the app's Shared Preferences as well.
    }

    public void fetchKeys() throws CertificateException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, IOException {
        publicKey = keyHandler.getPublicKey();
        privateKey = keyHandler.getPrivateKey();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_conversations) {
            return true;
        }
        if (id == R.id.action_contacts) {
            startActivity(new Intent(this, ContactListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class RegisterUserTask extends AsyncTask<String, Integer, String> {

        String pin;
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

        public RegisterUserTask(Context c) {
            this.context = c;
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
                    secret = responseJSON.get("secret").toString();
                    System.out.println("New user created. Secret is: " + secret);
                    SharedPreferences preferences = context.getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("secret", secret);
                    editor.commit();
                    // TODO - check response from server
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class refreshMessages extends AsyncTask<String, Integer, String> {

        Context context;
        String pin;
        String secret;
        String numMessagesBeforeReresh;
        JSONArray messageQueueJSONArray;
        List<Conversation> conversationlist;
        ArrayList<String> conversationNameList = new ArrayList<>();


        @Override
        public String doInBackground(String... params) {
            try {
                fetchMessages(pin, secret);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                System.out.println("Oops! Couldn't refresh messages");
            }
            return "WAHOO";
        }

        protected void onPostExecute(String result) {
            conversationlist = Conversation.getAll();
            for (int i = 0; i < conversationlist.size(); i++) {
                conversationNameList.add(conversationlist.get(i).contact.pin);
            }
            ListView conversationListUI = (ListView)findViewById(R.id.conversationListUI);
            ArrayAdapter<String> conversationListAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, conversationNameList);
            conversationListUI.setAdapter(conversationListAdapter);

        }

        public refreshMessages(Context c) {
            context = c;
            SharedPreferences preferences = c.getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
            pin = preferences.getString("pin", null);
            secret = preferences.getString("secret", null);
            numMessagesBeforeReresh = Integer.toString(preferences.getInt("numMessages", 0));
        }

        public void fetchMessages(String pin, String secret) throws UnsupportedEncodingException {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://blooming-cliffs-4171.herokuapp.com/message/refresh");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("pin", pin));
            nameValuePairs.add(new BasicNameValuePair("secret", secret));
            nameValuePairs.add(new BasicNameValuePair("message_count", numMessagesBeforeReresh));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            try {
                HttpResponse response = httpClient.execute(httppost);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseString = EntityUtils.toString(entity)    ;
                    System.out.println(responseString);
                    JSONObject responseJSON = new JSONObject(responseString);

                    System.out.println(responseJSON.toString());
                    System.out.println("Class is: " + responseJSON.get("messages").getClass());

                    messageQueueJSONArray = responseJSON.getJSONArray("messages");

                    if (messageQueueJSONArray.length() > 0) {
                        ArrayList<String> messageQueue = new ArrayList<>();
                        System.out.println("The number of new messages is: " + messageQueueJSONArray.length());
                        for (int i = 0; i < messageQueueJSONArray.length(); i++) {
                            JSONObject message = messageQueueJSONArray.getJSONObject(i);
                            String senderPIN = message.getString("sender_pin");
                            String messageBody = message.getString("body");
                            messageQueue.add(senderPIN);
                            messageQueue.add(messageBody);
                        }
                        int numMessagesAfterRefresh = Integer.valueOf(numMessagesBeforeReresh) + messageQueueJSONArray.length();
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("numMessages", numMessagesAfterRefresh);
                        editor.commit();
                        updateDB(messageQueue);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDB(ArrayList<String> messages) {
        Contact sender;
        Conversation conversation;
        Message message;

        for (int i = 0; i < messages.size() / 2; i++) {
            // The Message queue is populated in this way: senderPIN1, messagebody1, senderPIN2, messagebody2, etc.
            sender = Contact.getByPin(messages.get(i*2));
            if (sender == null) {
                sender = new Contact();
                sender.pin = messages.get(i*2);
                sender.name = "NAME";
                sender.pubKey = "PUBKEY";
                sender.save();
            }
            conversation = Contact.findConversation(sender);
            if (conversation == null) {
                conversation = new Conversation();
                conversation.contact = sender;
                conversation.save();
            }
            message = new Message();
            message.body = messages.get(i*2 + 1);
            message.contact = sender;
            message.conversation = conversation;
            message.save();
        }
    }

}
