package com.bankloan.service;

import com.bankloan.dao.CustomerDAO;
import com.bankloan.dao.LoanDAO;
import com.bankloan.dao.RepaymentDAO;
import com.bankloan.exception.InvalidLoanAmountException;
import com.bankloan.model.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class LoanService {
    private final CustomerDAO customerDAO=new CustomerDAO();
    private final LoanDAO loanDAO=new LoanDAO();
    private final RepaymentDAO repaymentDAO=new RepaymentDAO();

    public long registerCustomer(String name,String email,String phone,String address) throws SQLException {
        if(name==null||email==null||phone==null||address==null||name.isBlank()||email.isBlank()||phone.isBlank()||address.isBlank())
            throw new IllegalArgumentException("All customer fields are required.");
        return customerDAO.create(new Customer(name.trim(),email.trim(),phone.trim(),address.trim()));
    }

    public long applyLoan(String type,long customerId,double amount,double rate,int months) throws SQLException,InvalidLoanAmountException {
        if(customerId<=0) throw new IllegalArgumentException("Enter a valid customer ID.");
        Loan loan=switch(type) {
            case "HOME" -> new HomeLoan(customerId,amount,rate,months);
            case "CAR" -> new CarLoan(customerId,amount,rate,months);
            case "EDUCATION" -> new EducationLoan(customerId,amount,rate,months);
            default -> throw new IllegalArgumentException("Select a valid loan type.");
        };
        return loanDAO.create(loan);
    }

    public void changeLoanStatus(long loanId,String status) throws SQLException {
        if(loanId<=0) throw new IllegalArgumentException("Enter a valid loan ID.");
        loanDAO.updateStatus(loanId,status);
    }

    public void recordRepayment(long loanId,double amount,String note) throws SQLException {
        if(loanId<=0||amount<=0||!Double.isFinite(amount)) throw new IllegalArgumentException("Enter a valid loan ID and payment amount.");
        if(!"APPROVED".equals(loanDAO.findStatus(loanId))) throw new IllegalArgumentException("Repayments can only be recorded for approved loans.");
        double outstanding=repaymentDAO.getOutstandingPrincipal(loanId);
        if(amount>outstanding*1.05) throw new IllegalArgumentException("Payment exceeds the estimated outstanding balance.");
        repaymentDAO.create(new Repayment(loanId,amount,LocalDate.now(),note==null?"":note));
        loanDAO.advanceDueDate(loanId);
    }

    public CustomerDAO customers(){return customerDAO;}
    public LoanDAO loans(){return loanDAO;}
    public RepaymentDAO repayments(){return repaymentDAO;}
}