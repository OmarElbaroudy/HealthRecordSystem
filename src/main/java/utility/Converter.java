package utility;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.util.Base64;

public class Converter {
    public static String encodeAESKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static SecretKey decodeAESKey(String key){
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static String encode(byte[] initVect){
        return Base64.getEncoder().encodeToString(initVect);
    }

    public static byte[] decode(String initVect){
        return Base64.getDecoder().decode(initVect);
    }

    public static BigInteger getECKeyFromString(String key){
        return new BigInteger(key, 16);
    }

    public static String getECKeyAsString(BigInteger key){
        return key.toString(16);
    }
}
