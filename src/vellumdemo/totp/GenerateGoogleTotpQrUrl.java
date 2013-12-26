/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package vellumdemo.totp;

import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Strings;

/**
 * from
 * http://thegreyblog.blogspot.com/2011/12/google-authenticator-using-it-in-your.html
 *
 * @author evan.summers
 */
public class GenerateGoogleTotpQrUrl {
    static Logr logger = LogrFactory.getLogger(GenerateGoogleTotpQrUrl.class);

    String secret = "OVEK7TIJ3A3DM3M6";
    String user = "evanx";
    String host = "beethoven";

    void test() throws Exception {
        System.out.println(getQRBarcodeOtpAuthURL(user, host, secret));
        System.out.println(Strings.decodeUrl(getQRBarcodeURLQuery(user, host, secret)));
        System.out.println(getQRBarcodeURL(user, host, secret));
    }
    
    public static String getQRBarcodeURL(String user, String host, String secret) {
        return "https://chart.googleapis.com/chart?" + getQRBarcodeURLQuery(user, host, secret);
    }

    public static String getQRBarcodeURLQuery(String user, String host, String secret) {
        return "chs=200x200&chld=M%7C0&cht=qr&chl=" + 
                Strings.encodeUrl(getQRBarcodeOtpAuthURL(user, host, secret));
    }
   
    public static String getQRBarcodeOtpAuthURL(String user, String host, String secret) {
        return String.format("otpauth://totp/%s@%s&secret=%s", user, host, secret);
    }
    
    public static void main(String[] args) {
        try {
            new GenerateGoogleTotpQrUrl().test();
        } catch (Exception e) {
            logger.warn(e);
        }
    }
}
