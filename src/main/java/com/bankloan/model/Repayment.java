package com.bankloan.model;

import java.time.LocalDate;

public class Repayment {
    private long repaymentId;
    private long loanId;
    private double amount;
    private LocalDate paymentDate;
    private String note;

    public Repayment(long loanId, double amount, LocalDate paymentDate, String note) {
        this.loanId = loanId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.note = note;
    }
    public long getRepaymentId() { return repaymentId; }
    public long getLoanId() { return loanId; }
    public double getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getNote() { return note; }
}
