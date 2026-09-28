package fu.de200063;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountServiceTest {

    private static final String USER = "alice_01";
    private static final String EMAIL = "alice@example.com";
    private static final String PASS = "Secret@123";
    private static final String PHONE = "0912345678";
    private static final LocalDate DOB = LocalDate.now().minusYears(20);

    AccountService service;

    @BeforeEach
    void setUp() {
        service = new AccountService();
    }

    @Nested
    class Register {

        @Test
        void register_ValidData_ReturnsSuccessAndStoresAccountState() {
            ResultCode result = service.register(USER, "Alice@Example.COM", PASS, PASS, DOB, PHONE);

            assertEquals(ResultCode.SUCCESS, result);
            Optional<Account> opt = service.findByUsername(USER);
            assertTrue(opt.isPresent());

            Account acc = opt.get();
            assertEquals(USER, acc.getUsername());
            assertEquals("alice@example.com", acc.getEmail());
            assertEquals(AccountStatus.ACTIVE, acc.getStatus());
            assertEquals(0, acc.getFailedAttempts());
            assertFalse(acc.isLocked());
            assertNotNull(acc.getSalt());
            assertNotNull(acc.getCurrentPasswordHash());
            assertNotEquals(PASS, acc.getCurrentPasswordHash());
            assertEquals(DOB, acc.getDateOfBirth());
            assertEquals(PHONE, acc.getPhone());
        }

        @Test
        void register_OptionalPhoneNull_ReturnsSuccess() {
            ResultCode result = service.register("user_null_phone", "nullphone@example.com", PASS, PASS, DOB, null);
            assertEquals(ResultCode.SUCCESS, result);
        }

        @Test
        void register_OptionalPhoneEmpty_ReturnsSuccess() {
            ResultCode result = service.register("user_empty_phone", "emptyphone@example.com", PASS, PASS, DOB, "");
            assertEquals(ResultCode.SUCCESS, result);
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("fu.de200063.AccountServiceTest#invalidRegisterInputs")
        void register_InvalidInputs_ReturnsExpectedResultCode(String desc, String u, String e, String p, String c,
                                                              LocalDate dob, String phone, ResultCode expected) {
            ResultCode result = service.register(u, e, p, c, dob, phone);
            assertEquals(expected, result);
            if (u != null) {
                assertTrue(service.findByUsername(u).isEmpty());
            }
        }

        @ParameterizedTest(name = "[{index}] Null/blank username: ''{0}''")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        void register_BlankUsername_ReturnsInvalidInput(String username) {
            assertEquals(ResultCode.INVALID_INPUT, service.register(username, EMAIL, PASS, PASS, DOB, PHONE));
        }

        @ParameterizedTest(name = "[{index}] Null/blank email: ''{0}''")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        void register_BlankEmail_ReturnsInvalidInput(String email) {
            assertEquals(ResultCode.INVALID_INPUT, service.register(USER, email, PASS, PASS, DOB, PHONE));
        }

        @ParameterizedTest(name = "[{index}] Null/blank password: ''{0}''")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        void register_BlankPassword_ReturnsInvalidInput(String password) {
            assertEquals(ResultCode.INVALID_INPUT, service.register(USER, EMAIL, password, password, DOB, PHONE));
        }

        @ParameterizedTest(name = "[{index}] Duplicate username case-insensitive: {0}")
        @ValueSource(strings = {"alice_01", "ALICE_01", "Alice_01"})
        void register_DuplicateUsernameCaseInsensitive_ReturnsDuplicateUsername(String dupUser) {
            assertEquals(ResultCode.SUCCESS, service.register(USER, EMAIL, PASS, PASS, DOB, PHONE));
            ResultCode result = service.register(dupUser, "other@example.com", PASS, PASS, DOB, PHONE);
            assertEquals(ResultCode.DUPLICATE_USERNAME, result);
        }

        @ParameterizedTest(name = "[{index}] Duplicate email case-insensitive: {0}")
        @ValueSource(strings = {"alice@example.com", "ALICE@EXAMPLE.COM", "Alice@Example.Com"})
        void register_DuplicateEmailCaseInsensitive_ReturnsDuplicateEmail(String dupEmail) {
            assertEquals(ResultCode.SUCCESS, service.register(USER, EMAIL, PASS, PASS, DOB, PHONE));
            ResultCode result = service.register("other_user", dupEmail, PASS, PASS, DOB, PHONE);
            assertEquals(ResultCode.DUPLICATE_EMAIL, result);
        }

        @ParameterizedTest(name = "[{index}] today - {0} years + {1} days -> {2}")
        @CsvSource({
                "18, 0, SUCCESS",
                "18, 1, UNDERAGE",
                "0, 1, INVALID_INPUT",
                "19, 0, SUCCESS"
        })
        void register_AgeBoundary_ReturnsExpectedResultCode(int yearsAgo, int plusDays, ResultCode expected) {
            LocalDate dob = LocalDate.now().minusYears(yearsAgo).plusDays(plusDays);
            assertEquals(expected, service.register(USER, EMAIL, PASS, PASS, dob, null));
        }
    }

    static Stream<Arguments> invalidRegisterInputs() {
        return Stream.of(
                Arguments.of("BR-REG-01 dob null", USER, EMAIL, PASS, PASS, null, PHONE, ResultCode.INVALID_INPUT),
                Arguments.of("BR-REG-01 dob in future", USER, EMAIL, PASS, PASS, LocalDate.now().plusDays(1), PHONE, ResultCode.INVALID_INPUT),
                Arguments.of("BR-REG-02 invalid username start with digit", "1alice", EMAIL, PASS, PASS, DOB, PHONE, ResultCode.INVALID_USERNAME),
                Arguments.of("BR-REG-02 invalid username short", "abc", EMAIL, PASS, PASS, DOB, PHONE, ResultCode.INVALID_USERNAME),
                Arguments.of("BR-REG-02 invalid username special char", "alice!", EMAIL, PASS, PASS, DOB, PHONE, ResultCode.INVALID_USERNAME),
                Arguments.of("BR-REG-04 invalid email missing at", "plainaddress", "plainaddress", PASS, PASS, DOB, PHONE, ResultCode.INVALID_EMAIL),
                Arguments.of("BR-REG-04 invalid email single char tld", USER, "user@domain.c", PASS, PASS, DOB, PHONE, ResultCode.INVALID_EMAIL),
                Arguments.of("BR-REG-06 weak password missing digit", USER, EMAIL, "Secret@abc", "Secret@abc", DOB, PHONE, ResultCode.WEAK_PASSWORD),
                Arguments.of("BR-REG-06 weak password missing special", USER, EMAIL, "Secret1234", "Secret1234", DOB, PHONE, ResultCode.WEAK_PASSWORD),
                Arguments.of("BR-REG-06 password contains username", USER, EMAIL, "Xalice_01@1", "Xalice_01@1", DOB, PHONE, ResultCode.WEAK_PASSWORD),
                Arguments.of("BR-REG-07 password mismatch", USER, EMAIL, PASS, "Different@123", DOB, PHONE, ResultCode.PASSWORD_MISMATCH),
                Arguments.of("BR-REG-08 underage", USER, EMAIL, PASS, PASS, LocalDate.now().minusYears(17), PHONE, ResultCode.UNDERAGE),
                Arguments.of("BR-REG-09 invalid phone bad prefix", USER, EMAIL, PASS, PASS, DOB, "0123456789", ResultCode.INVALID_PHONE),
                Arguments.of("BR-REG-09 invalid phone whitespace", USER, EMAIL, PASS, PASS, DOB, "   ", ResultCode.INVALID_PHONE),
                Arguments.of("Priority 1: username invalid + email invalid -> INVALID_USERNAME", "1alice", "bademail", PASS, PASS, DOB, PHONE, ResultCode.INVALID_USERNAME),
                Arguments.of("Priority 2: email invalid + weak password -> INVALID_EMAIL", USER, "bademail", "weak", "weak", DOB, PHONE, ResultCode.INVALID_EMAIL),
                Arguments.of("Priority 3: weak password + password mismatch -> WEAK_PASSWORD", USER, EMAIL, "weak", "mismatch", DOB, PHONE, ResultCode.WEAK_PASSWORD),
                Arguments.of("Priority 4: password mismatch + underage -> PASSWORD_MISMATCH", USER, EMAIL, PASS, "Other@123", LocalDate.now().minusYears(17), PHONE, ResultCode.PASSWORD_MISMATCH),
                Arguments.of("Priority 5: underage + invalid phone -> UNDERAGE", USER, EMAIL, PASS, PASS, LocalDate.now().minusYears(17), "0123456789", ResultCode.UNDERAGE)
        );
    }
}
