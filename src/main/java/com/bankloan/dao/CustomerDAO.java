package com.bankloan.dao;

import com.bankloan.database.DatabaseConnection;
import com.bankloan.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public long create(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers(full_name,email,phone,address) VALUES(?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Customer was saved but no generated ID was returned.");
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM customers ORDER BY customer_id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) customers.add(new Customer(rs.getLong("customer_id"),
                    rs.getString("full_name"), rs.getString("email"),
                    rs.getString("phone"), rs.getString("address")));
        }
        return customers;
    }
}
