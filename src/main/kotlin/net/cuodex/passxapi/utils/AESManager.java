package net.cuodex.passxapi.utils;


import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class AESManager {

    private final String ENCRYPTION_TEST = "encryptionTest";

    private Cipher cipher;
    private final String key, iv;

    public AESManager(String key) {
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        String SALT = "WyuZFx5zOy65AsZRGLJcn8OFuGq5LvMI";
        this.key = (key + SALT).substring(0, 32);
        this.iv = this.key.substring(0, 16);
    }

    public String encrypt(String str) {
        try {
            byte[] dataBytes = str.getBytes();
            int plaintextLength = dataBytes.length;
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(this.key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(this.iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return new String(Base64.getEncoder().encode(encrypted));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String str) {
        try {
            byte[] encrypted = Base64.getDecoder().decode(str.getBytes());

            SecretKeySpec keyspec = new SecretKeySpec(this.key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(this.iv.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted);

            return new String(original);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encryptionTest() {
        return encrypt(ENCRYPTION_TEST);
    }

    public boolean checkEncryptionTest(String encrypted) {
        try {
            return decrypt(encrypted).equals(ENCRYPTION_TEST);
        } catch (Exception e) {
            return false;
        }
    }


}