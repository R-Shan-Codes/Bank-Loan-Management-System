package com.bankloan.dao;

import com.bankloan.database.DatabaseConnection;
import com.bankloan.model.Loan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    public long create(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans(customer_id,loan_type,principal,annual_interest_rate,tenure_months,status,applied_on) VALUES(?,?,?,?,?,'PENDING',CURRENT_DATE)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, loan.getCustomerId());
            ps.setString(2, loan.getLoanType());
            ps.setDouble(3, loan.getPrincipal());
            ps.setDouble(4, loan.getAnnualInterestRate());
            ps.setInt(5, loan.getTenureMonths());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Loan was saved but no generated ID was returned.");
    }

    public void updateStatus(long loanId, String status) throws SQLException {
        if (!List.of("APPROVED", "REJECTED", "CLOSED").contains(status)) {
            throw new IllegalArgumentException("Unsupported loan status.");
        }
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE loans SET status=? WHERE loan_id=?")) {
            ps.setString(1, status);
            ps.setLong(2, loanId);
            if (ps.executeUpdate() == 0) throw new SQLException("Loan not found: " + loanId);
        }
    }

    public List<String[]> findAllSummary() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT l.loan_id,c.full_name,l.loan_type,l.principal,l.annual_interest_rate,l.tenure_months,l.status FROM loans l JOIN customers c ON c.customer_id=l.customer_id ORDER BY l.loan_id DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(new String[]{String.valueOf(rs.getLong(1)), rs.getString(2),
                    rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)});
        }
        return rows;
    }

    public double getApprovedEmi(long loanId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             CallableStatement call = c.prepareCall("{CALL generate_emi_schedule(?)}")) {
            call.setLong(1, loanId);
            try (ResultSet rs = call.executeQuery()) {
                if (rs.next()) return rs.getDouble("monthly_emi");
            }
        }
        throw new SQLException("No approved loan found for ID " + loanId);
    }
}
