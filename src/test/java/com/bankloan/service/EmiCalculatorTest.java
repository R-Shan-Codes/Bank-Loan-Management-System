package com.bankloan.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmiCalculatorTest {
    @Test
    void calculatesZeroInterestEmi() {
        assertEquals(1000.0, EmiCalculator.calculate(12000, 0, 12), 0.0001);
    }

    @Test
    void calculatesReducingBalanceEmi() {
        assertEquals(8791.59, EmiCalculator.calculate(100000, 10, 12), 0.02);
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> EmiCalculator.calculate(0, 10, 12));
        assertThrows(IllegalArgumentException.class, () -> EmiCalculator.calculate(1000, -1, 12));
        assertThrows(IllegalArgumentException.class, () -> EmiCalculator.calculate(1000, 10, 0));
    }
}
