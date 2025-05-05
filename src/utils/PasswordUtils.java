package utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt.
 */
public class PasswordUtils {
    /**
     * Hash a plaintext password using BCrypt.
     * @param plainPassword the plaintext password
     * @return the hashed password
     */
    public static String hashPassword(String plainPassword) {
        // gensalt's log_rounds parameter determines the complexity
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(plainPassword, salt);
    }

    /**
     * Verify a plaintext password against a hashed password.
     * @param plainPassword the plaintext password
     * @param hashedPassword the stored hashed password
     * @return true if match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid hash provided for comparison");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
