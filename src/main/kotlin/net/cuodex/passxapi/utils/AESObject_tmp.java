package net.cuodex.passxapi.utils;

import net.cuodex.passxapi.PassxApiApplication;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESObject_tmp {

    public static final String ENCRYPTION_TEST = "encryptionTest", SALT = "WyuZFx5zOy65AsZRGLJcn8OFuGq5LvMIWyuZFx5zOy65AsZRGLJcn8OFuGq5LvMI";

    private Cipher cipher;
    private String key;
    private final String customSalt;

    public AESObject_tmp(String key) {
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            PassxApiApplication.LOGGER.error("Error whiles initialising cipher: " + e.getMessage());
        }
        this.key = key;
        this.customSalt = generateSalt();
    }

    public String encrypt(String str) {
        return encrypt(str, customSalt);
    }

    public String encrypt(String str, String s) {

        str = UmlautHelper.replaceUmlauts(str);
        String key = (this.key + s).substring(0, 32);
        String iv = key.substring(0, 16);

        try {
            byte[] dataBytes = str.getBytes();
            int plaintextLength = dataBytes.length;
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);


            return new String(Base64.getEncoder().encode(encrypted));

        } catch (Exception e) {
            PassxApiApplication.LOGGER.error("Error whiles encrypting: " + e.getMessage());
            return null;
        }
    }

    public String decrypt(String str) {

        String key = (this.key + customSalt).substring(0, 32), iv = key.substring(0, 16);
        try {
            byte[] encrypted = Base64.getDecoder().decode(str.getBytes());
            System.out.println(Arrays.toString(encrypted));


            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted);
            String originalString = new String(original);
            System.out.println(originalString);

            return UmlautHelper.reverseUmlauts(originalString);

        } catch (Exception e) {
            PassxApiApplication.LOGGER.error("Error whiles decrypting: " + e.getMessage());
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

    private String generateSalt() {
        return encrypt(SALT, SALT);
    }


}