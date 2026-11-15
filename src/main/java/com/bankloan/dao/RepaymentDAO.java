package com.bankloan.dao;

import com.bankloan.database.DatabaseConnection;
import com.bankloan.model.Repayment;
import java.sql.*;

public class RepaymentDAO {
    public void create(Repayment repayment) throws SQLException {
        String sql = "INSERT INTO repayments(loan_id,amount,payment_date,note) VALUES(?,?,?,?)";
        try (Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,repayment.getLoanId()); ps.setDouble(2,repayment.getAmount());
            ps.setDate(3,Date.valueOf(repayment.getPaymentDate())); ps.setString(4,repayment.getNote()); ps.executeUpdate();
        }
    }

    public double getTotalPaid(long loanId) throws SQLException {
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT COALESCE(SUM(amount),0) FROM repayments WHERE loan_id=?")) {
            ps.setLong(1,loanId); try(ResultSet rs=ps.executeQuery()){rs.next();return rs.getDouble(1);}
        }
    }

    public double getOutstandingPrincipal(long loanId) throws SQLException {
        String sql="SELECT l.principal,l.annual_interest_rate,l.tenure_months,COALESCE(SUM(r.amount),0) paid FROM loans l LEFT JOIN repayments r ON r.loan_id=l.loan_id WHERE l.loan_id=? GROUP BY l.loan_id,l.principal,l.annual_interest_rate,l.tenure_months";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,loanId);
            try(ResultSet rs=ps.executeQuery()) {
                if(!rs.next()) throw new SQLException("Loan not found: "+loanId);
                double principal=rs.getDouble("principal"), rate=rs.getDouble("annual_interest_rate")/1200.0;
                int months=rs.getInt("tenure_months");
                double emi=rate==0 ? principal/months : principal*rate*Math.pow(1+rate,months)/(Math.pow(1+rate,months)-1);
                double balance=principal, paid=rs.getDouble("paid");
                // Allocate recorded payments to successive scheduled instalments. This is a transparent
                // amortisation estimate; the database retains each actual payment independently.
                for(int i=0;i<months && paid>0.000001 && balance>0.000001;i++) {
                    double interest=balance*rate;
                    double due=Math.min(balance+interest,emi);
                    double applied=Math.min(paid,due);
                    double principalPart=Math.max(0,applied-interest);
                    balance=Math.max(0,balance-principalPart);
                    paid-=applied;
                }
                return Math.round(balance*100.0)/100.0;
            }
        }
    }
}