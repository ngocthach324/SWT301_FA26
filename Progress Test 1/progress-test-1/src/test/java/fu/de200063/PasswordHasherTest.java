package fu.de200063;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordHasherTest {

    @Test
    void generateSalt_ReturnsRandomHexSalt() {
        String salt1 = PasswordHasher.generateSalt();
        String salt2 = PasswordHasher.generateSalt();

        assertNotNull(salt1);
        assertNotNull(salt2);
        assertEquals(32, salt1.length());
        assertEquals(32, salt2.length());
        assertNotEquals(salt1, salt2);
    }

    @Test
    void hash_ValidInput_ReturnsExpectedHex64() {
        String salt = "1234567890abcdef1234567890abcdef";
        String hash1 = PasswordHasher.hash(salt, "Password@123");
        String hash2 = PasswordHasher.hash(salt, "Password@123");

        assertNotNull(hash1);
        assertEquals(64, hash1.length());
        assertEquals(hash1, hash2);
    }

    @Test
    void hash_NullInput_ReturnsNull() {
        assertNull(PasswordHasher.hash(null, "Password@123"));
        assertNull(PasswordHasher.hash("salt", null));
        assertNull(PasswordHasher.hash(null, null));
    }

    @ParameterizedTest(name = "[{index}] matches password ''{1}'' with expected: {2}")
    @CsvSource({
            "salt123, Password@123, true",
            "salt123, WrongPass@123, false"
    })
    void matches_ValidAndInvalidPasswords(String salt, String inputPassword, boolean expected) {
        String hash = PasswordHasher.hash(salt, "Password@123");
        boolean result = PasswordHasher.matches(salt, inputPassword, hash);
        assertEquals(expected, result);
    }

    @Test
    void matches_NullInputs_ReturnsFalse() {
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash(salt, "Secret@123");

        assertFalse(PasswordHasher.matches(null, "Secret@123", hash));
        assertFalse(PasswordHasher.matches(salt, null, hash));
        assertFalse(PasswordHasher.matches(salt, "Secret@123", null));
        assertFalse(PasswordHasher.matches(null, null, null));
    }
}
