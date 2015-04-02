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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
    KeyHandler keyHandler;
    ListView conversationListUI;
    PublicKey publicKey;
    PrivateKey privateKey;
    String pin;
    Cryptosaurus cryptosaurus;
    String sqlQuery;
    String recipientID;
    String currentState;
    SQLiteDatabase db;
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

        System.out.println("We're in the create method!");

        if (currentState == null) {
            buildDB();
            Context context = getApplicationContext();
            keyHandler.buildKeys(context);
            createNewUser();
            editor.putString("status", "success");
            editor.putInt("messageCount", 0);
            editor.commit();
        } else {
            System.out.println("The app has been run before.");
        }
//        String test = preferences.getString("pin", null);
//        System.out.println("The PIN is: " + test);
        assembleView(this);
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

    public void assembleView(Context context) {
        ListView conversationListUI = (ListView)findViewById(R.id.conversationListUI);
        ArrayList<String> conversationList = new ArrayList<String>();
        ArrayAdapter<String> conversationListAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, conversationList);
        Button new_conversation = (Button)findViewById(R.id.new_conversation);

        db = openOrCreateDatabase("breathe", Context.MODE_WORLD_READABLE, null);
        c = db.rawQuery("SELECT * FROM conversations;", null);

        while (c.moveToNext()) {
            recipientID = c.getString(1);
            String contactLookup = "SELECT * FROM contacts WHERE id = " + recipientID + ";";
            c_temp = db.rawQuery(contactLookup, null);
            conversationList.add(c_temp.getString(2));
        }
        conversationListUI.setAdapter(conversationListAdapter);

        new_conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewConversationActivity.class));
            }
        });
    }

    public void buildDB() {
        SQLiteDatabase db = openOrCreateDatabase("breathe", Context.MODE_WORLD_WRITEABLE, null);
        sqlQuery = "CREATE TABLE IF NOT EXISTS friends (id INTEGER PRIMARY KEY AUTOINCREMENT, pin VARCHAR(100), pubkey VARCHAR(100), name VARCHAR(100));";
        db.execSQL(sqlQuery);

        sqlQuery = "CREATE TABLE IF NOT EXISTS conversations (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "friend_id integer, " +
                "FOREIGN KEY(friend_id) REFERENCES friend(id));";
        db.execSQL(sqlQuery);

        sqlQuery = "CREATE TABLE IF NOT EXISTS messages (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                   "body TEXT, " +
                   "conversation_id integer, " +
                   "friend_id integer, " +
                   "from_me VARCHAR(100), " +
                   "message_count TEXT, " +
                   "FOREIGN KEY(friend_id) REFERENCES friends(id) " +
                   "FOREIGN KEY(conversation_id) REFERENCES conversations(id));";
        db.execSQL(sqlQuery);
        System.out.println("DB created successfully");
    }

    public void createNewUser() {
        Random pinGenerator = new Random();
        int rand = pinGenerator.nextInt(10000000);
        pin = String.valueOf(rand);
        registerNewUserTask = new RegisterUserTask(this.getApplicationContext());
        registerNewUserTask.execute(pin);

        SharedPreferences preferences = this.getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pin", pin);
        System.out.println("Your PIN is: " + pin);
        editor.commit();
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
}
