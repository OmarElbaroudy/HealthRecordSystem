package utility;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

public class Encrypt {
    private static final String AES = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";

    public static String createAESKey(){
        SecureRandom securerandom = new SecureRandom();
        KeyGenerator keygenerator = null;

        try {
            keygenerator = KeyGenerator.getInstance(AES);
        } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
        }

        assert keygenerator != null;
        keygenerator.init(256, securerandom);
        return Converter.encodeAESKey(keygenerator.generateKey());
    }

    public static String createInitializationVector() {
        byte[] initializationVector = new byte[16];
        SecureRandom secureRandom = new SecureRandom();

        secureRandom.nextBytes(initializationVector);
        return Converter.encode(initializationVector);
    }

    public static String doAESEncryption(
            String plainText, String secretKey, String initializationVector) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);

            IvParameterSpec ivParameterSpec =
                    new IvParameterSpec(Converter.decode(initializationVector));

            cipher.init(Cipher.ENCRYPT_MODE,
                    Converter.decodeAESKey(secretKey), ivParameterSpec);

            return Converter.encode(cipher.doFinal(plainText.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doAESDecryption(
            String cipherText, String secretKey, String initializationVector) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);

            IvParameterSpec ivParameterSpec =
                    new IvParameterSpec(Converter.decode(initializationVector));

            cipher.init(Cipher.DECRYPT_MODE,
                    Converter.decodeAESKey(secretKey), ivParameterSpec);

            byte[] result = cipher.doFinal(Converter.decode(cipherText));
            return new String(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

