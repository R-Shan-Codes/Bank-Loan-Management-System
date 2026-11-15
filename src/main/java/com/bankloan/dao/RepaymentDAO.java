package com.bankloan.dao;

import com.bankloan.database.DatabaseConnection;
import com.bankloan.model.Repayment;
import java.sql.*;

public class RepaymentDAO {
    public void create(Repayment repayment) throws SQLException {
        String sql = "INSERT INTO repayments(loan_id,amount,payment_date,note) VALUES(?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, repayment.getLoanId());
            ps.setDouble(2, repayment.getAmount());
            ps.setDate(3, Date.valueOf(repayment.getPaymentDate()));
            ps.setString(4, repayment.getNote());
            ps.executeUpdate();
        }
    }

    public double getTotalPaid(long loanId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COALESCE(SUM(amount),0) FROM repayments WHERE loan_id=?")) {
            ps.setLong(1, loanId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getDouble(1); }
        }
    }

    public double getOutstandingPrincipal(long loanId) throws SQLException {
        String sql = "SELECT GREATEST(l.principal-COALESCE(SUM(r.amount),0),0) FROM loans l LEFT JOIN repayments r ON r.loan_id=l.loan_id WHERE l.loan_id=? GROUP BY l.loan_id,l.principal";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        throw new SQLException("Loan not found: " + loanId);
    }
}
