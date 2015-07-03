import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

/**
 * Created by Ongun on 7/3/2015.
 */
public class Encrpyt {

    public static void main(String[] unused) throws Exception {
        byte[] dataBytes = "FC-AA-14-02-42-49".getBytes();

        PrivateKey pkey = decodePrivateKey();
        PublicKey pubkey = decodePublicKey();
        byte[] encBytes = encrypt(dataBytes, pkey);
        BASE64Encoder encoder = new BASE64Encoder();
        System.out.println(encoder.encode(encBytes));
        byte[] mac = decrypt(encBytes, pubkey);

        System.out.println(new String(mac).trim());
        assert (new String(mac).trim().equals(new String(dataBytes)));
    }

    private static byte[] encrypt(byte[] inpBytes, PrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }

    private static byte[] decrypt(byte[] inpBytes, PublicKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }

    private static PrivateKey decodePrivateKey() throws Exception {

        byte[] encoded = Files.readAllBytes(Paths.get("KeyGen", "src", "key.private"));
        String keyStr = new String(encoded);

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] sigBytes2 = decoder.decodeBuffer(keyStr);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                sigBytes2);

        KeyFactory keyFact = KeyFactory.getInstance("RSA");
        return keyFact.generatePrivate(privateKeySpec);
    }
    private static PublicKey decodePublicKey() throws Exception {

        byte[] encoded = Files.readAllBytes(Paths.get("KeyGen", "src", "key.public"));
        String keyStr = new String(encoded);

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] sigBytes2 = decoder.decodeBuffer(keyStr);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes2);

        KeyFactory keyFact = KeyFactory.getInstance("RSA");
        return keyFact.generatePublic(x509KeySpec);
    }
}
