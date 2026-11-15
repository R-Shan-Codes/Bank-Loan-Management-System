package com.bankloan.service;

import com.bankloan.dao.CustomerDAO;
import com.bankloan.dao.LoanDAO;
import com.bankloan.dao.RepaymentDAO;
import com.bankloan.exception.InvalidLoanAmountException;
import com.bankloan.model.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class LoanService {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final LoanDAO loanDAO = new LoanDAO();
    private final RepaymentDAO repaymentDAO = new RepaymentDAO();

    public long registerCustomer(String name, String email, String phone, String address) throws SQLException {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || address.isBlank())
            throw new IllegalArgumentException("All customer fields are required.");
        return customerDAO.create(new Customer(name.trim(), email.trim(), phone.trim(), address.trim()));
    }

    public long applyLoan(String type, long customerId, double amount, double rate, int months)
            throws SQLException, InvalidLoanAmountException {
        Loan loan = switch (type) {
            case "HOME" -> new HomeLoan(customerId, amount, rate, months);
            case "CAR" -> new CarLoan(customerId, amount, rate, months);
            case "EDUCATION" -> new EducationLoan(customerId, amount, rate, months);
            default -> throw new IllegalArgumentException("Select a valid loan type.");
        };
        return loanDAO.create(loan);
    }

    public void recordRepayment(long loanId, double amount, String note) throws SQLException {
        if (amount <= 0) throw new IllegalArgumentException("Payment must be greater than zero.");
        double outstanding = repaymentDAO.getOutstandingPrincipal(loanId);
        if (amount > outstanding) throw new IllegalArgumentException("Payment exceeds outstanding principal.");
        repaymentDAO.create(new Repayment(loanId, amount, LocalDate.now(), note));
    }

    public CustomerDAO customers() { return customerDAO; }
    public LoanDAO loans() { return loanDAO; }
    public RepaymentDAO repayments() { return repaymentDAO; }
}
