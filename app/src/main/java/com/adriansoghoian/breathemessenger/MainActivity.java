package com.adriansoghoian.breathemessenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static javax.crypto.Cipher.ENCRYPT_MODE;


public class MainActivity extends ActionBarActivity {

    String currentState;
    TextView pubkeyDisplay;
    KeyHandler keyGenerator;
    PublicKey publicKey;
    PrivateKey privateKey;
    KeyPair keyPair;
    KeyStore keyStore;
    Context context;
    EditText plainText;
    TextView cipherText;
    Button encryptButton;
    String ptxt;
    String ctxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assembleView();

        SharedPreferences preferences = this.getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        currentState = preferences.getString("status", null);

        if (currentState == null) {
            setUpKeys();
            editor.putString("status", "Key pair has been generated and saved.");
            System.out.println("Hello, keys have been set.");
            System.out.println(publicKey.getAlgorithm());
            System.out.println(publicKey);
        } else {
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry("RSA Keys", null);
                publicKey = keyEntry.getCertificate().getPublicKey();
                privateKey = keyEntry.getPrivateKey();
            } catch (KeyStoreException e) {
                System.out.println("Couldn't initialize keystore.");
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            }

        }

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ptxt = plainText.getText().toString();
                try {
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                    ctxt = cipher.doFinal(ptxt.getBytes()).toString();
                    System.out.println("Cipher text: " + ctxt);
                } catch (NoSuchAlgorithmException e) {
                    System.out.println("Oooops1");
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    System.out.println("Oooops2");
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    System.out.println("Oooops3");
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    System.out.println("Oooops4");
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    System.out.println("Oooops5");
                    e.printStackTrace();
                }
            }
        });

    }

    public void assembleView() {
        pubkeyDisplay = (TextView) findViewById(R.id.pubkey);
        plainText = (EditText) findViewById(R.id.plaintext);
        cipherText = (TextView) findViewById(R.id.ciphertext);
        encryptButton = (Button) findViewById(R.id.encryptbutton);
    }

    public void setUpKeys() {
        Context context = getApplicationContext();
        keyGenerator = new KeyHandler(context);
        keyPair = keyGenerator.getKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
