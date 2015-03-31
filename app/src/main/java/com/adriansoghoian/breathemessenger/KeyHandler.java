package com.adriansoghoian.breathemessenger;

/**
 * Created by adrian on 3/27/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.x500.X500Principal;


public class KeyHandler {

    KeyPairGenerator keyGenerator;
    KeyPair keyPair;
    PublicKey publicKey;
    PrivateKey privateKey;
    KeyStore keyStore;
    String algorithmType = "RSA";
    String keyStoreType;
    String test;
    int eValue = 79;
    int keySize = 1024;
    Context context;
    Calendar calendar;


    public void buildKeys(Context context) {
        Calendar notBefore = Calendar.getInstance();
        Calendar notAfter = Calendar.getInstance();
        notAfter.add(Calendar.YEAR, 1);

        try {
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias("RSA Keys")
                    .setKeyType(algorithmType)
                    .setKeySize(keySize)
                    .setSubject(new X500Principal("CN=test"))
                    .setStartDate(notBefore.getTime())
                    .setEndDate(notAfter.getTime())
                    .setSerialNumber(BigInteger.ONE)
                    .build();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithmType, "AndroidKeyStore");
            keyPairGenerator.initialize(spec);
            keyPair = keyPairGenerator.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Oops1");
            e.printStackTrace();

        } catch (NoSuchProviderException e) {
            System.out.println("Oops2");
            e.printStackTrace();

        } catch (InvalidAlgorithmParameterException e) {
            System.out.println("Oops3");
            e.printStackTrace();
        }
    }

    public KeyHandler() {
    }

    public PrivateKey getPrivateKey() throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry("RSA Keys", null);
        privateKey = keyEntry.getPrivateKey();
        return privateKey;
    }

    public PublicKey getPublicKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry("RSA Keys", null);
        publicKey = keyEntry.getCertificate().getPublicKey();
        return publicKey;
    }

}
