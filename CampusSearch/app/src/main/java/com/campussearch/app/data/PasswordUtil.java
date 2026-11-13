package com.campussearch.app.data;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Passwords are never stored as plain text: salted PBKDF2 hash only. */
public final class PasswordUtil {
    private PasswordUtil() { }

    public static String newSalt() {
        byte[] b = new byte[16];
        new SecureRandom().nextBytes(b);
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static String hash(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                    Base64.decode(salt, Base64.NO_WRAP), 10000, 256);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.encodeToString(f.generateSecret(spec).getEncoded(), Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String password, String salt, String expectedHash) {
        return hash(password, salt).equals(expectedHash);
    }
}
