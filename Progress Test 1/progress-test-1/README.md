# SWT301 – Progress Test 1: Unit Testing với JUnit 5

- **Sinh viên**: Nguyễn Ngọc Thạch
- **MSSV**: DE200063
- **Lớp**: SE20B01
- **Môn học**: SWT301 – Software Testing
- **Package**: `fu.de200063`
- **Môi trường & Công cụ**: JDK 21, Maven 3.9+, JUnit Jupiter 5.10.2, JaCoCo 0.8.11, IntelliJ IDEA

---

## 1. Hướng dẫn chạy kiểm thử

Để chạy toàn bộ bộ kiểm thử (Unit Tests) và tự động sinh báo cáo độ bao phủ mã nguồn (JaCoCo Coverage Report), thực hiện lệnh:

```bash
mvn clean test
```

Xem báo cáo chi tiết trực quan tại:
`target/site/jacoco/index.html`

---

## 2. Kết quả kiểm thử tổng quan

- **Tổng số lượt chạy (Test Invocations)**: **179** (Vượt xa mức yêu cầu tối thiểu $\ge 60$)
- **Kết quả**: **179 Passed**, 0 Failures, 0 Errors, 0 Skipped
- **Tổng số @ParameterizedTest**: 18
- **Nguồn dữ liệu kiểm thử**: Sử dụng đầy đủ và đa dạng cả 4 loại nguồn:
  - `@ValueSource`
  - `@NullAndEmptySource`
  - `@CsvSource`
  - `@MethodSource`

---

## 3. Ma trận truy vết yêu cầu (Traceability Matrix: BR $\rightarrow$ Test Method)

| Mã nghiệp vụ (BR) | Mô tả quy tắc nghiệp vụ | Phương thức kiểm thử tương ứng |
|---|---|---|
| **BR-REG-01** | Kiểm tra bắt buộc các trường không null/blank, dob không ở tương lai | `Register.register_InvalidInputs_ReturnsExpectedResultCode`, `Register.register_Blank*` |
| **BR-REG-02** | Username hợp lệ (5–20 ký tự, bắt đầu chữ cái, `[A-Za-z0-9_]`) | `AccountValidatorTest.isValidUsername_*`, `Register.register_InvalidInputs_ReturnsExpectedResultCode` |
| **BR-REG-03** | Trùng username (tra cứu không phân biệt hoa/thường) | `Register.register_DuplicateUsernameCaseInsensitive_ReturnsDuplicateUsername` |
| **BR-REG-04** | Email hợp lệ (chuẩn `local@domain.tld`, $\le 100$ ký tự, TLD $\ge 2$) | `AccountValidatorTest.isValidEmail_*`, `Register.register_InvalidInputs_ReturnsExpectedResultCode` |
| **BR-REG-05** | Trùng email (tra cứu không phân biệt hoa/thường) | `Register.register_DuplicateEmailCaseInsensitive_ReturnsDuplicateEmail` |
| **BR-REG-06** | Mật khẩu mạnh (8–32 ký tự, đủ 4 nhóm, không chứa username) | `AccountValidatorTest.isValidPassword_*`, `Register.register_InvalidInputs_ReturnsExpectedResultCode` |
| **BR-REG-07** | Mật khẩu xác nhận phải khớp chính xác | `Register.register_InvalidInputs_ReturnsExpectedResultCode` |
| **BR-REG-08** | Tuổi $\ge 18$ (tính tuổi tròn theo ngày động) | `AccountValidatorTest.calculateAge_*`, `Register.register_AgeBoundary_ReturnsExpectedResultCode` |
| **BR-REG-09** | Số điện thoại tùy chọn (hợp lệ 10 số đầu `03/05/07/08/09`, `null` hoặc `""` được chấp nhận) | `AccountValidatorTest.isValidPhone_*`, `Register.register_OptionalPhone*`, `Register.register_InvalidInputs_ReturnsExpectedResultCode` |
| **BR-REG-10** | Đăng ký thành công (sinh salt, hash SHA-256, chuẩn hóa email, lưu trạng thái) | `Register.register_ValidData_ReturnsSuccessAndStoresAccountState` |
| **Priority Order** | Thứ tự ưu tiên bắt buộc: `REG-01 -> 02 -> 04 -> 06 -> 07 -> 08 -> 09 -> 03 -> 05 -> 10` | `Register.register_InvalidInputs_ReturnsExpectedResultCode` (5 ca kiểm thử đa vi phạm) |
| **BR-LOG-01** | Kiểm tra thông tin đăng nhập không được null/blank | `Login.login_BlankUsername_ReturnsInvalidInput`, `Login.login_BlankPassword_ReturnsInvalidInput` |
| **BR-LOG-02** | Đăng nhập đúng thông tin $\rightarrow$ `SUCCESS`, reset bộ đếm sai về 0 | `Login.login_Rule6_CorrectPassword_ReturnsSuccessAndResetsFailedAttempts` |
| **BR-LOG-03** | User không tồn tại hoặc sai mật khẩu đều trả cùng `INVALID_CREDENTIALS` | `Login.login_Rule1_NonExistentUser_ReturnsInvalidCredentials`, `Login.login_Rule1_WrongPassword_ReturnsSameInvalidCredentials` |
| **BR-LOG-04** | Tài khoản bị vô hiệu hóa trả `ACCOUNT_DISABLED` | `Login.login_Rule2_DisabledAccount_ReturnsAccountDisabled` |
| **BR-LOG-05** | Khóa tài khoản khi sai liên tiếp đủ 5 lần | `Login.login_Rule4_FailedAttemptsUnderThreshold_*`, `Login.login_Rule5_FifthFailedAttempt_LocksAccountAndReturnsAccountLocked` |
| **BR-LOG-06** | Đang bị khóa không tăng bộ đếm, trả `ACCOUNT_LOCKED` | `Login.login_Rule3_AlreadyLocked_ReturnsAccountLockedWithoutIncrementingCounter`, `Login.login_CorrectPasswordAfterNFailures` |
| **BR-ADM-01** | Vô hiệu hóa tài khoản (`disableAccount`) | `Admin.disableAccount_ExistingUser_ReturnsSuccessAndChangesStatus`, `Admin.disableAccount_InvalidOrNotFoundUser_ReturnsUserNotFound` |
| **BR-ADM-03** | Mở khóa tài khoản (`unlockAccount`) và đặt lại bộ đếm sai về 0 | `Login.login_AfterAdminUnlock_CounterRestartsAndCanLogin`, `Admin.unlockAccount_*` |

---

## 4. Báo cáo JaCoCo Code Coverage

| Lớp | Line Coverage | Branch Coverage | Yêu cầu tối thiểu | Đánh giá |
|---|---|---|---|---|
| `AccountValidator` | **100.0%** (33/33) | **100.0%** (52/52) | Line $\ge 80\%$, Branch $\ge 70\%$ | Xuất sắc |
| `AccountService` | **96.0%** (72/75) | **98.5%** (65/66) | Line $\ge 80\%$, Branch $\ge 70\%$ | Xuất sắc |
| `PasswordHasher` | **86.7%** (13/15) | **100.0%** (10/10) | N/A (Bonus) | Xuất sắc |
| `Account` | **85.0%** (34/40) | **33.3%** (2/6) | N/A | Đạt |

---

## 5. Bảng thực nghiệm lỗi giả lập (Mutation Testing)

| # | Vị trí / File | Lỗi chèn vào (Mutant) | Kết quả kiểm thử | Ca test phát hiện và Fail (Killed) | Đã hoàn tác |
|---|---|---|---|---|:---:|
| **M1** | `AccountService.java` (`login`) | Đổi `>= MAX_FAILED_ATTEMPTS` $\rightarrow$ `> MAX_FAILED_ATTEMPTS` | Build Failure (6 failures) | `AccountServiceTest$Login.login_Rule5_FifthFailedAttempt_LocksAccountAndReturnsAccountLocked` *(Kỳ vọng ACCOUNT_LOCKED nhưng nhận INVALID_CREDENTIALS)* | ✅ |
| **M2** | `AccountService.java` (`login`) | Bỏ kiểm tra nhánh `if (acc.isLocked())` | Build Failure (4 failures) | `AccountServiceTest$Login.login_Rule3_AlreadyLocked_ReturnsAccountLockedWithoutIncrementingCounter` *(Kỳ vọng ACCOUNT_LOCKED nhưng nhận SUCCESS hoặc sai bộ đếm)* | ✅ |
| **M3** | `AccountValidator.java` | Sửa regex độ dài username từ `{4,19}` $\rightarrow$ `{4,20}` (chấp nhận 21 ký tự) | Build Failure (1 failure) | `AccountValidatorTest.isValidUsername_BoundaryLength_ReturnsExpected` *(Với độ dài 21 ký tự, kỳ vọng false nhưng nhận true)* | ✅ |

---

## 6. Checklist tự đánh giá (Checklist mục 4 của đề bài)

### A. Mã production
- [x] **A1** `mvn clean compile` thành công
- [x] **A2** `AccountValidator` đủ 5 hàm, null trả `false`, không ném exception
- [x] **A3** Mật khẩu băm SHA-256 + salt riêng, không lưu bản rõ
- [x] **A4** `register()` đủ BR-REG-01..10, **đúng thứ tự**
- [x] **A5** `login()`: sai 5 lần thì khóa; đang khóa không tăng bộ đếm; thành công thì đặt bộ đếm về 0
- [x] **A6** `unlockAccount()` mở khóa và đặt `failedAttempts = 0`
- [x] **A7** Username/email không phân biệt hoa/thường (`toLowerCase(Locale.ROOT)`), mật khẩu phân biệt
- [x] **A8** Không dùng `Clock`; không `System.out`, không biến static giữ trạng thái

### B. Mã test
- [x] **B1** $\ge 20$ phương thức test, $\ge 12$ `@ParameterizedTest`, $\ge 60$ lượt chạy (Thực tế: 179 lượt chạy)
- [x] **B2** Dùng đủ `@ValueSource`, `@NullAndEmptySource`, `@CsvSource`, `@MethodSource`
- [x] **B3** Biên: username 4/5/20/21, mật khẩu 7/8/32/33, email 100/101, tuổi 17/18 (qua `calculateAge`)
- [x] **B4** Biên số lần đăng nhập sai 4/5 và test mở khóa
- [x] **B5** $\ge 3$ test thứ tự ưu tiên trong `register()` (Thực tế: 5 ca test)
- [x] **B6** `@Nested` + `@BeforeEach` tạo service mới cho mỗi test
- [x] **B7** Assert cả trạng thái, không `assertTrue(true)`, không `Thread.sleep`
- [x] **B8** Tên test theo mẫu `method_TinhHuong_KetQua`, AAA

### C. Chất lượng và nộp bài
- [x] **C1** `mvn clean test`: 0 failures / errors / skipped
- [x] **C2** JaCoCo Line $\ge 80\%$, Branch $\ge 70\%$ (Line 96–100%, Branch 98–100%)
- [x] **C3** $\ge 3$ lỗi giả lập có ghi lại và đều bị tiêu diệt
- [x] **C4** Lịch sử git $\ge 6$ commit đúng Conventional Commits
- [x] **C5** File zip đúng tên `Lab2_DE200063_NguyenNgocThach.zip`, không có `target/`, `.idea/`, `.vscode/`
