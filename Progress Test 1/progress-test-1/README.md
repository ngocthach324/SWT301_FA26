# SWT301 – Progress Test 1: Unit Testing với JUnit 5

- **Môn học**: SWT301 (Software Testing)
- **MSSV**: DE200063
- **Môi trường**: JDK 21, Maven 3.9+, JUnit Jupiter 5.10.2, JaCoCo 0.8.11
- **Package**: `fu.de200063`

---

## 1. Hướng dẫn chạy kiểm thử

Chạy toàn bộ test suite và sinh báo cáo JaCoCo:
```bash
mvn clean test
```

Xem báo cáo JaCoCo tại:
`target/site/jacoco/index.html`

---

## 2. Báo cáo JaCoCo Code Coverage (TODO-8)

| Lớp | Line Coverage | Branch Coverage | Yêu cầu tối thiểu | Đánh giá |
|---|---|---|---|---|
| `AccountValidator` | **100.0%** (33/33) | **100.0%** (52/52) | Line $\ge 80\%$, Branch $\ge 70\%$ | Xuất sắc |
| `AccountService` | **96.0%** (72/75) | **98.5%** (65/66) | Line $\ge 80\%$, Branch $\ge 70\%$ | Xuất sắc |
| `PasswordHasher` | **86.7%** (13/15) | **100.0%** (10/10) | N/A (Bonus) | Đạt |
| `Account` | **85.0%** (34/40) | **33.3%** (2/6) | N/A | Đạt |

> **Tổng quan**: Toàn bộ dự án có **179/179 test invocations passed** (0 failures, 0 errors, 0 skipped). Cả `AccountValidator` và `AccountService` đều vượt xa tiêu chí bắt buộc.

---

## 3. Bảng thực nghiệm lỗi giả lập (Mutation Testing - TODO-8)

| # | Vị trí / File | Lỗi chèn vào (Mutant) | Kết quả kiểm thử | Ca test phát hiện và Fail (Killed) | Đã hoàn tác |
|---|---|---|---|---|:---:|
| **M1** | `AccountService.java` (`login`) | Đổi `>= MAX_FAILED_ATTEMPTS` $\rightarrow$ `> MAX_FAILED_ATTEMPTS` | Build Failure (6 failures) | `AccountServiceTest$Login.login_Rule5_FifthFailedAttempt_LocksAccountAndReturnsAccountLocked` *(Kỳ vọng ACCOUNT_LOCKED nhưng nhận INVALID_CREDENTIALS)* | ✅ |
| **M2** | `AccountService.java` (`login`) | Bỏ kiểm tra nhánh `if (acc.isLocked())` | Build Failure (4 failures) | `AccountServiceTest$Login.login_Rule3_AlreadyLocked_ReturnsAccountLockedWithoutIncrementingCounter` *(Kỳ vọng ACCOUNT_LOCKED nhưng nhận SUCCESS hoặc sai bộ đếm)* | ✅ |
| **M3** | `AccountValidator.java` | Sửa regex độ dài username từ `{4,19}` $\rightarrow$ `{4,20}` (chấp nhận 21 ký tự) | Build Failure (1 failure) | `AccountValidatorTest.isValidUsername_BoundaryLength_ReturnsExpected` *(Với độ dài 21 ký tự, kỳ vọng false nhưng nhận true)* | ✅ |

> **Kết luận**: Toàn bộ 3 đột biến đều bị bộ test phát hiện ngay lập tức (Mutation Score: 100%). Mã nguồn đã được hoàn tác sạch sẽ và an toàn.
