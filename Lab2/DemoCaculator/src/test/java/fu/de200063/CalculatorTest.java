package fu.de200063;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorTest {
    private final Calculator calculator = new Calculator();

    @Test
    @DisplayName("add(2,3) trả về 5")
    void add_TwoPositiveNumbers_ReturnsSum() {
        int a = 2;
        int b = 3;
        int expected = 5;

        int actual = calculator.add(a, b);

        assertEquals(expected, actual, "2 + 3 phải bằng 5");
    }

    @Test
    @DisplayName("divide(6,3) trả về 2")
    void divide_ValidDivision_ReturnsQuotient() {
        int a = 6;
        int b = 3;

        int result = calculator.divide(a, b);

        assertEquals(2, result);
    }

    @Test
    @DisplayName("divide(10,0) ném IllegalArgumentException")
    void divide_ByZero_ThrowsIllegalArgumentException() {
        int a = 10;
        int b = 0;

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.divide(a, b)
        );

        assertEquals("Cannot divide by zero", ex.getMessage());
    }
}
