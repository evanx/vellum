/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package vellumdemo.totp;

import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base32;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 *
 * @author evan.summers
 */
public class GenerateTotpSecret {
    static Logr logger = LogrFactory.getLogger(GenerateTotpSecret.class);

    void test() {
        byte[] buffer = new byte[10];
        new SecureRandom().nextBytes(buffer);
        String secret = new String(new Base32().encode(buffer));
        logger.info(secret);
    }

    public static void main(String[] args) {
        new GenerateTotpSecret().test();
    }
}
