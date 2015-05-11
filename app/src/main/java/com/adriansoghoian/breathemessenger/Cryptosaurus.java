package com.adriansoghoian.breathemessenger;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by adrian on 3/30/15.
 */
public class Cryptosaurus {

    PublicKey publicKey;
    PrivateKey privateKey;
    String ctxt;
    String ptxt;
    byte[] ctxtRaw;
    byte[] ptxtRaw;

    public Cryptosaurus(PublicKey pk, PrivateKey sk) {
        publicKey = pk;
        privateKey = sk;
    }

    public String encrypt(String plaintext) {
        ptxt = plaintext;
        ctxt = "Oops - weren't able to encrypt.";
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            ctxtRaw = cipher.doFinal(ptxt.getBytes());
            Cipher cipher2 = Cipher.getInstance("RSA");
            cipher2.init(Cipher.DECRYPT_MODE, privateKey);
            ptxtRaw = cipher2.doFinal(ctxtRaw);
            String ptxt = new String(ptxtRaw);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("OOpz1");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            System.out.println("OOpz2");
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            System.out.println("OOpz3");
            e.printStackTrace();
        } catch (BadPaddingException e) {
            System.out.println("OOpz4");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println("OOpz5");
            e.printStackTrace();
        }
        return ptxt;
    }

}
