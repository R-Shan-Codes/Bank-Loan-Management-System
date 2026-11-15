package com.bankloan.model;

import com.bankloan.exception.InvalidLoanAmountException;

public class CarLoan extends Loan {
    public CarLoan(long customerId, double amount, double rate, int months) throws InvalidLoanAmountException {
        super(customerId, amount, rate, months);
    }
    @Override public String getLoanType() { return "CAR"; }
}
