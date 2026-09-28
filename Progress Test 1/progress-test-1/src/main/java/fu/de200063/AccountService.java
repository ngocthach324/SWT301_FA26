package fu.de200063;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class AccountService {
    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final int PASSWORD_HISTORY_SIZE = 3;
    public static final int MIN_AGE = 18;

    private final Map<String, Account> accounts;
    private final Map<String, String> emails;

    public AccountService() {
        this.accounts = new HashMap<>();
        this.emails = new HashMap<>();
    }

    public ResultCode register(String username, String email, String password,
                               String confirmPassword, LocalDate dateOfBirth, String phone) {
        LocalDate today = LocalDate.now();
        if (isBlank(username) || isBlank(email) || isBlank(password) || isBlank(confirmPassword)
                || dateOfBirth == null || dateOfBirth.isAfter(today)) {
            return ResultCode.INVALID_INPUT;
        }
        if (!AccountValidator.isValidUsername(username)) {
            return ResultCode.INVALID_USERNAME;
        }
        if (!AccountValidator.isValidEmail(email)) {
            return ResultCode.INVALID_EMAIL;
        }
        if (!AccountValidator.isValidPassword(password, username)) {
            return ResultCode.WEAK_PASSWORD;
        }
        if (!password.equals(confirmPassword)) {
            return ResultCode.PASSWORD_MISMATCH;
        }
        if (AccountValidator.calculateAge(dateOfBirth, today) < MIN_AGE) {
            return ResultCode.UNDERAGE;
        }
        if (phone != null && !phone.isEmpty()) {
            if (!AccountValidator.isValidPhone(phone)) {
                return ResultCode.INVALID_PHONE;
            }
        }
        if (accounts.containsKey(key(username))) {
            return ResultCode.DUPLICATE_USERNAME;
        }
        if (emails.containsKey(key(email))) {
            return ResultCode.DUPLICATE_EMAIL;
        }

        String salt = PasswordHasher.generateSalt();
        String passwordHash = PasswordHasher.hash(salt, password);
        String normalizedEmail = email.toLowerCase(Locale.ROOT);
        Account account = new Account(username, normalizedEmail, dateOfBirth, phone, salt, passwordHash);

        accounts.put(key(username), account);
        emails.put(key(email), key(username));

        return ResultCode.SUCCESS;
    }

    public ResultCode login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return ResultCode.INVALID_INPUT;
        }
        Account acc = accounts.get(key(username));
        if (acc == null) {
            return ResultCode.INVALID_CREDENTIALS;
        }
        if (acc.getStatus() == AccountStatus.DISABLED) {
            return ResultCode.ACCOUNT_DISABLED;
        }
        if (acc.isLocked()) {
            return ResultCode.ACCOUNT_LOCKED;
        }

        if (!PasswordHasher.matches(acc.getSalt(), password, acc.getCurrentPasswordHash())) {
            acc.incrementFailedAttempts();
            if (acc.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                acc.lock();
                return ResultCode.ACCOUNT_LOCKED;
            }
            return ResultCode.INVALID_CREDENTIALS;
        }

        acc.resetFailedAttempts();
        return ResultCode.SUCCESS;
    }

    public ResultCode changePassword(String username, String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("TODO");
    }

    public TokenResult requestPasswordReset(String email) {
        throw new UnsupportedOperationException("TODO");
    }

    public ResultCode resetPassword(String token, String newPassword) {
        throw new UnsupportedOperationException("TODO");
    }

    public ResultCode disableAccount(String username) {
        if (isBlank(username)) {
            return ResultCode.USER_NOT_FOUND;
        }
        Optional<Account> acc = findByUsername(username);
        if (acc.isEmpty()) {
            return ResultCode.USER_NOT_FOUND;
        }
        acc.get().setStatus(AccountStatus.DISABLED);
        return ResultCode.SUCCESS;
    }

    public ResultCode unlockAccount(String username) {
        if (isBlank(username)) {
            return ResultCode.USER_NOT_FOUND;
        }
        Optional<Account> acc = findByUsername(username);
        if (acc.isEmpty()) {
            return ResultCode.USER_NOT_FOUND;
        }
        acc.get().unlock();
        return ResultCode.SUCCESS;
    }

    public Optional<Account> findByUsername(String username) {
        if (isBlank(username)) {
            return Optional.empty();
        }
        return Optional.ofNullable(accounts.get(key(username)));
    }

    public boolean isLocked(String username) {
        if (isBlank(username)) {
            return false;
        }
        Account acc = accounts.get(key(username));
        return acc != null && acc.isLocked();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String key(String s) {
        return s.toLowerCase(Locale.ROOT);
    }
}
