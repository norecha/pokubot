import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Ongun on 7/3/2015.
 */
public class GenerateKeys {

    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();

        System.out.println(encodeKey(priv));
        System.out.println();
        System.out.println(encodeKey(pub));
    }

    public static String encodeKey(Key key) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(key.getEncoded());
    }
}
