package com.bankloan.model;

import com.bankloan.exception.InvalidLoanAmountException;

public class EducationLoan extends Loan {
    public EducationLoan(long customerId, double amount, double rate, int months) throws InvalidLoanAmountException {
        super(customerId, amount, rate, months);
    }
    @Override public String getLoanType() { return "EDUCATION"; }
}
