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
        if (!List.of("APPROVED", "REJECTED", "CLOSED").contains(status))
            throw new IllegalArgumentException("Unsupported loan status.");
        String sql = status.equals("APPROVED")
                ? "UPDATE loans SET status=?, next_due_date=COALESCE(next_due_date, DATE_ADD(CURRENT_DATE, INTERVAL 1 MONTH)) WHERE loan_id=? AND status='PENDING'"
                : "UPDATE loans SET status=? WHERE loan_id=? AND status IN ('PENDING','APPROVED')";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status); ps.setLong(2, loanId);
            if (ps.executeUpdate() == 0) throw new SQLException("Loan not found or status transition is not allowed: " + loanId);
        }
    }

    public List<String[]> findAllSummary() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT l.loan_id,c.full_name,l.loan_type,l.principal,l.annual_interest_rate,l.tenure_months,l.status FROM loans l JOIN customers c ON c.customer_id=l.customer_id ORDER BY l.loan_id DESC";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(new String[]{String.valueOf(rs.getLong(1)),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7)});
        }
        return rows;
    }

    public List<String[]> findDueLoans(int daysAhead) throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT l.loan_id,c.full_name,l.next_due_date FROM loans l JOIN customers c ON c.customer_id=l.customer_id WHERE l.status='APPROVED' AND l.next_due_date<=DATE_ADD(CURRENT_DATE, INTERVAL ? DAY) ORDER BY l.next_due_date";
        try (Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setInt(1, daysAhead);
            try(ResultSet rs=ps.executeQuery()) {
                while(rs.next()) rows.add(new String[]{String.valueOf(rs.getLong(1)),rs.getString(2),rs.getDate(3).toString()});
            }
        }
        return rows;
    }

    public void advanceDueDate(long loanId) throws SQLException {
        String sql = "UPDATE loans SET next_due_date=DATE_ADD(next_due_date, INTERVAL 1 MONTH) WHERE loan_id=? AND status='APPROVED'";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,loanId);
            if(ps.executeUpdate()==0) throw new SQLException("Approved loan not found: "+loanId);
        }
    }

    public String findStatus(long loanId) throws SQLException {
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT status FROM loans WHERE loan_id=?")) {
            ps.setLong(1,loanId);
            try(ResultSet rs=ps.executeQuery()) { if(rs.next()) return rs.getString(1); }
        }
        throw new SQLException("Loan not found: "+loanId);
    }

    public double getApprovedEmi(long loanId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection(); CallableStatement call = c.prepareCall("{CALL generate_emi_schedule(?)}")) {
            call.setLong(1, loanId);
            try (ResultSet rs = call.executeQuery()) { if (rs.next()) return rs.getDouble("monthly_emi"); }
        }
        throw new SQLException("No approved loan found for ID " + loanId);
    }
}