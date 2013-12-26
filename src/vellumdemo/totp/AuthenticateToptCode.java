/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package vellumdemo.totp;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Strings;

/**
 * from
 * http://thegreyblog.blogspot.com/2011/12/google-authenticator-using-it-in-your.html
 *
 * @author evan.summers
 */
public class AuthenticateToptCode {

    static Logr logger = LogrFactory.getLogger(AuthenticateToptCode.class);
    String secret = "OVEK7TIJ3A3DM3M6";
    String user = "evanx";
    String host = "beethoven";

    long testTimeIndex = 45064605;
    int testCode = 111070;

    public static String getQRBarcodeURL(String user, String host, String secret) {
        String chl = "otpauth%3A%2F%2Ftotp%2F" + user + '@' + host + "%3Fsecret%3D" + secret;
        System.out.println(Strings.decodeUrl(chl));
        return "http://chart.apis.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=" + chl;
    }
     
    void test() throws Exception {
        System.out.println("time: " + getTimeIndex());
        System.out.println("code: " +  getCode(secret, getTimeIndex()));
        System.out.println("codes: " + getCodeList(secret, getTimeIndex(), 6));
        System.out.println("verify: " + verifyCode(secret, testCode, testTimeIndex, 6));
    }

    public static boolean verifyCode(String secret, int code, long timeIndex, int variance) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        for (int i = -variance; i <= variance; i++) {
            if (getCode(decodedKey, timeIndex + i) == code) {
                return true;
            }
        }
        return false;
    }

    public static long getTimeIndex() {
        return System.currentTimeMillis()/1000/30;
    }

    private static List<Long> getCodeList(String secret, long timeIndex, int variance) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        List<Long> list = new ArrayList();
        for (int i = -variance; i <= variance; i++) {
            list.add(getCode(new Base32().decode(secret), timeIndex + i));
        }
        return list;
    }
    
    private static long getCode(String secret, long timeIndex) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        return getCode(new Base32().decode(secret), timeIndex);
    }

    private static long getCode(byte[] secret, long timeIndex) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signKey = new SecretKeySpec(secret, "HmacSHA1");
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(timeIndex);
        byte[] timeBytes = buffer.array();
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(timeBytes);
        int offset = hash[19] & 0xf;
        long truncatedHash = hash[offset] & 0x7f;
        for (int i = 1; i < 4; i++) {
            truncatedHash <<= 8;
            truncatedHash |= hash[offset + i] & 0xff;
        }
        return (truncatedHash %= 1000000);
    }

    public static void main(String[] args) {
        try {
            new AuthenticateToptCode().test();
        } catch (Exception e) {
            logger.warn(e);
        }
    }
}
