package fu.de200063;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountValidatorTest {

    @ParameterizedTest(name = "[{index}] valid username: {0}")
    @ValueSource(strings = {"alice", "Alice_01", "Z____", "user_123", "a1234"})
    void isValidUsername_Valid_ReturnsTrue(String username) {
        assertTrue(AccountValidator.isValidUsername(username));
    }

    @ParameterizedTest(name = "[{index}] invalid username: {0}")
    @ValueSource(strings = {"ab_1", "1alice", "_alice", "ali ce", "alice!", "alice-01", "user@name", "user#name"})
    void isValidUsername_InvalidFormat_ReturnsFalse(String username) {
        assertFalse(AccountValidator.isValidUsername(username));
    }

    @ParameterizedTest(name = "[{index}] null/empty/blank username: ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void isValidUsername_NullAndEmpty_ReturnsFalse(String username) {
        assertFalse(AccountValidator.isValidUsername(username));
    }

    @ParameterizedTest(name = "[{index}] username length {0} -> expected {1}")
    @MethodSource("usernameLengths")
    void isValidUsername_BoundaryLength_ReturnsExpected(int length, boolean expected) {
        assertEquals(expected, AccountValidator.isValidUsername("a".repeat(length)));
    }

    static Stream<Arguments> usernameLengths() {
        return Stream.of(
                Arguments.of(4, false),
                Arguments.of(5, true),
                Arguments.of(6, true),
                Arguments.of(19, true),
                Arguments.of(20, true),
                Arguments.of(21, false)
        );
    }

    @ParameterizedTest(name = "[{index}] email ''{0}'' -> {1}")
    @CsvSource({
            "valid@example.com, true",
            "user.name+tag@sub.domain.org, true",
            "user@sub.domain.co.uk, true",
            "plainaddress, false",
            "@missingusername.com, false",
            "username@.com, false",
            "username@com, false",
            "username@domain..com, false",
            "username@domain.c, false",
            "username@domain.toolongtld, true"
    })
    void isValidEmail_Partitions_ReturnsExpected(String email, boolean expected) {
        assertEquals(expected, AccountValidator.isValidEmail(email));
    }

    @ParameterizedTest(name = "[{index}] null/empty/blank email: ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void isValidEmail_NullAndEmpty_ReturnsFalse(String email) {
        assertFalse(AccountValidator.isValidEmail(email));
    }

    @ParameterizedTest(name = "[{index}] email length {0} -> expected {1}")
    @MethodSource("emailLengths")
    void isValidEmail_BoundaryLength_ReturnsExpected(int length, boolean expected) {
        String email = "a".repeat(length - 5) + "@b.co";
        assertEquals(expected, AccountValidator.isValidEmail(email));
    }

    static Stream<Arguments> emailLengths() {
        return Stream.of(
                Arguments.of(99, true),
                Arguments.of(100, true),
                Arguments.of(101, false)
        );
    }

    @ParameterizedTest(name = "[{index}] {3}")
    @CsvSource(delimiter = '|', value = {
            "Secret@123    | alice_01 | true  | valid password",
            "secret@123    | alice_01 | false | missing uppercase",
            "SECRET@123    | alice_01 | false | missing lowercase",
            "Secret@abc    | alice_01 | false | missing digit",
            "Secret1234    | alice_01 | false | missing special char",
            "'Secret @123' | alice_01 | false | contains space",
            "Secret#123~   | alice_01 | false | contains disallowed char",
            "Xalice_01@1   | alice_01 | false | contains username lowercase",
            "XALICE_01@1   | alice_01 | false | contains username uppercase",
            "Xalice_01@1   |          | true  | username null",
            "Xalice_01@1   | '   '    | true  | username blank"
    })
    void isValidPassword_Partitions_ReturnsExpected(String password, String username, boolean expected, String desc) {
        assertEquals(expected, AccountValidator.isValidPassword(password, username));
    }

    @ParameterizedTest(name = "[{index}] null/empty/blank password: ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void isValidPassword_NullAndEmpty_ReturnsFalse(String password) {
        assertFalse(AccountValidator.isValidPassword(password, "alice"));
    }

    @ParameterizedTest(name = "[{index}] password length {0} -> expected {1}")
    @MethodSource("passwordLengths")
    void isValidPassword_BoundaryLength_ReturnsExpected(int length, boolean expected) {
        String password = "Aa1!" + "a".repeat(length - 4);
        assertEquals(expected, AccountValidator.isValidPassword(password, "alice"));
    }

    static Stream<Arguments> passwordLengths() {
        return Stream.of(
                Arguments.of(7, false),
                Arguments.of(8, true),
                Arguments.of(32, true),
                Arguments.of(33, false)
        );
    }

    @ParameterizedTest(name = "[{index}] valid phone: {0}")
    @ValueSource(strings = {"0312345678", "0512345678", "0712345678", "0812345678", "0912345678"})
    void isValidPhone_ValidPrefixes_ReturnsTrue(String phone) {
        assertTrue(AccountValidator.isValidPhone(phone));
    }

    @ParameterizedTest(name = "[{index}] invalid phone: {0}")
    @ValueSource(strings = {"0112345678", "0212345678", "0412345678", "0612345678", "031234567", "03123456789", "031234567a", "abcdefghij", "+84912345678"})
    void isValidPhone_InvalidFormat_ReturnsFalse(String phone) {
        assertFalse(AccountValidator.isValidPhone(phone));
    }

    @ParameterizedTest(name = "[{index}] null/empty/blank phone: ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void isValidPhone_NullAndEmpty_ReturnsFalse(String phone) {
        assertFalse(AccountValidator.isValidPhone(phone));
    }

    @ParameterizedTest(name = "[{index}] dob: {0}, today: {1} -> {2} years")
    @CsvSource({
            "2008-09-28, 2026-09-28, 18",
            "2008-09-29, 2026-09-28, 17",
            "2008-02-29, 2026-02-28, 17",
            "2008-02-29, 2026-03-01, 18",
            "2026-09-28, 2026-09-28, 0"
    })
    void calculateAge_Boundaries_ReturnsExpected(LocalDate dob, LocalDate today, int expected) {
        assertEquals(expected, AccountValidator.calculateAge(dob, today));
    }

    @Test
    void calculateAge_NullInputs_ReturnsZero() {
        assertEquals(0, AccountValidator.calculateAge(null, LocalDate.now()));
        assertEquals(0, AccountValidator.calculateAge(LocalDate.now(), null));
    }
}
