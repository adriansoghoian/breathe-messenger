package com.adriansoghoian.breathemessenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.io.IOException;
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
    int i;
    KeyHandler keyHandler;
    ListView conversationListUI;
    PublicKey publicKey;
    PrivateKey privateKey;
    Cryptosaurus cryptosaurus;
    String sqlQuery;
    String recipientID;
    String currentState;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = this.getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        currentState = preferences.getString("status", null);

        keyHandler = new KeyHandler();
        dbExists = dbExists();

        if (dbExists) {
            System.out.println("The DB exists; this app has been run before.");
        } else {
            Context context = getApplicationContext();
            keyHandler.buildKeys(context);
            editor.putString("status", "Key pair has been generated and saved.");
        }
        buildDB();
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
        sqlQuery = "CREATE TABLE IF NOT EXISTS friends (id INTEGER PRIMARY KEY AUTOINCREMENT, pin VARCHAR(100), name VARCHAR(100));";
        db.execSQL(sqlQuery);

        sqlQuery = "CREATE TABLE IF NOT EXISTS conversations (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "body TEXT, " +
                "friend_id integer, " +
                "FOREIGN KEY(friend_id) REFERENCES friend(id));";
        db.execSQL(sqlQuery);

        sqlQuery = "CREATE TABLE IF NOT EXISTS messages (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                   "body TEXT, " +
                   "conversation_id integer, " +
                   "friend_id integer, " +
                   "FOREIGN KEY(friend_id) REFERENCES friends(id) " +
                   "FOREIGN KEY(conversation_id) REFERENCES conversations(id));";
        db.execSQL(sqlQuery);
        System.out.println("DB created successfully");
    }

    public void fetchKeys() throws CertificateException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, IOException {
        publicKey = keyHandler.getPublicKey();
        privateKey = keyHandler.getPrivateKey();
    }

    public boolean dbExists() {
        db = openOrCreateDatabase("breathe", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor c = null;
        boolean tableExists = false;
        try {
            c = db.query("breathe", null, null, null, null, null, null);
            tableExists = true;
            c.close();
        } catch (Exception e) {
            System.out.println("Nope, no table");
        }
        return tableExists;
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
}
