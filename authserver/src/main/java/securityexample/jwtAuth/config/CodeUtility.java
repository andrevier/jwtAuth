package securityexample.jwtAuth.config;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

// Utility to generate OTP.
public final class CodeUtility {
    private CodeUtility() {}

    public static String generateCode() {
        String code;

        try {
            SecureRandom random = SecureRandom.getInstanceStrong();

            int c = random.nextInt(9000) + 1000;

            code = String.valueOf(c);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Problem when generate random code.");
        }
        return code;
    }
    
}
