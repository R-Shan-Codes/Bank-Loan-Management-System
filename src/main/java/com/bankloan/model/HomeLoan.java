package com.bankloan.model;

import com.bankloan.exception.InvalidLoanAmountException;

public class HomeLoan extends Loan {
    public HomeLoan(long customerId, double amount, double rate, int months) throws InvalidLoanAmountException {
        super(customerId, amount, rate, months);
    }
    @Override public String getLoanType() { return "HOME"; }
}
