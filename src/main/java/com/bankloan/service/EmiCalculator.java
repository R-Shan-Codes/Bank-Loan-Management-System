package com.bankloan.service;

public final class EmiCalculator {
    private EmiCalculator() { }

    public static double calculate(double principal, double annualRate, int months) {
        if (principal <= 0 || annualRate < 0 || months <= 0) {
            throw new IllegalArgumentException("Principal and tenure must be positive; rate cannot be negative.");
        }
        double monthlyRate = annualRate / 1200.0;
        if (monthlyRate == 0) return principal / months;
        double factor = Math.pow(1 + monthlyRate, months);
        return principal * monthlyRate * factor / (factor - 1);
    }
}
