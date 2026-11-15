package com.bankloan.model;

import com.bankloan.exception.InvalidLoanAmountException;

public abstract class Loan implements Repayable {
    private long loanId;
    private long customerId;
    private double principal;
    private double annualInterestRate;
    private int tenureMonths;
    private String status;

    protected Loan(long customerId, double principal, double annualInterestRate, int tenureMonths)
            throws InvalidLoanAmountException {
        if (principal <= 0 || !Double.isFinite(principal)) {
            throw new InvalidLoanAmountException("Loan amount must be greater than zero.");
        }
        if (annualInterestRate < 0 || !Double.isFinite(annualInterestRate)) {
            throw new InvalidLoanAmountException("Interest rate cannot be negative.");
        }
        if (tenureMonths <= 0) {
            throw new InvalidLoanAmountException("Tenure must be at least one month.");
        }
        this.customerId = customerId;
        this.principal = principal;
        this.annualInterestRate = annualInterestRate;
        this.tenureMonths = tenureMonths;
        this.status = "PENDING";
    }

    public abstract String getLoanType();

    @Override
    public void recordRepayment(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Repayment must be positive.");
    }

    public long getLoanId() { return loanId; }
    public void setLoanId(long loanId) { this.loanId = loanId; }
    public long getCustomerId() { return customerId; }
    public double getPrincipal() { return principal; }
    public double getAnnualInterestRate() { return annualInterestRate; }
    public int getTenureMonths() { return tenureMonths; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
