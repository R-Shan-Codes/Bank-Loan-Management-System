package com.bankloan.ui;

import com.bankloan.dao.CustomerDAO;
import com.bankloan.dao.LoanDAO;
import com.bankloan.service.EmiCalculator;
import com.bankloan.service.LoanService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class MainFrame extends JFrame {
    private final LoanService service = new LoanService();

    public MainFrame() {
        super("Bank Loan Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("BANK LOAN MANAGEMENT SYSTEM", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        heading.setBorder(BorderFactory.createEmptyBorder(18, 8, 18, 8));
        add(heading, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Register Customer", customerPanel());
        tabs.addTab("Apply Loan", loanPanel());
        tabs.addTab("EMI Calculator", emiPanel());
        tabs.addTab("Repayment", repaymentPanel());
        tabs.addTab("Loan Report", reportPanel());
        add(tabs, BorderLayout.CENTER);
        add(new JLabel("  Java 17  |  Swing  |  JDBC  |  MySQL"), BorderLayout.SOUTH);
    }

    private JPanel formPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        return panel;
    }

    private JPanel customerPanel() {
        JPanel p = formPanel();
        JTextField name = new JTextField(), email = new JTextField(), phone = new JTextField(), address = new JTextField();
        p.add(new JLabel("Full name")); p.add(name);
        p.add(new JLabel("Email")); p.add(email);
        p.add(new JLabel("Phone")); p.add(phone);
        p.add(new JLabel("Address")); p.add(address);
        JButton save = new JButton("Register customer"); p.add(new JLabel()); p.add(save);
        save.addActionListener(e -> runAction(() -> {
            long id = service.registerCustomer(name.getText(), email.getText(), phone.getText(), address.getText());
            JOptionPane.showMessageDialog(this, "Customer registered. ID: " + id);
            name.setText(""); email.setText(""); phone.setText(""); address.setText("");
        }));
        return p;
    }

    private JPanel loanPanel() {
        JPanel p = formPanel();
        JTextField customerId = new JTextField(), amount = new JTextField(), rate = new JTextField("9.5"), months = new JTextField("60");
        JComboBox<String> type = new JComboBox<>(new String[]{"HOME", "CAR", "EDUCATION"});
        p.add(new JLabel("Customer ID")); p.add(customerId);
        p.add(new JLabel("Loan type")); p.add(type);
        p.add(new JLabel("Principal amount (₹)")); p.add(amount);
        p.add(new JLabel("Annual interest rate (%)")); p.add(rate);
        p.add(new JLabel("Tenure (months)")); p.add(months);
        JButton apply = new JButton("Submit application"); p.add(new JLabel()); p.add(apply);
        apply.addActionListener(e -> runAction(() -> {
            long id = service.applyLoan((String) type.getSelectedItem(), Long.parseLong(customerId.getText().trim()),
                    Double.parseDouble(amount.getText().trim()), Double.parseDouble(rate.getText().trim()),
                    Integer.parseInt(months.getText().trim()));
            JOptionPane.showMessageDialog(this, "Application submitted. Loan ID: " + id + " (Pending approval)");
        }));
        return p;
    }

    private JPanel emiPanel() {
        JPanel p = formPanel();
        JTextField principal = new JTextField(), rate = new JTextField("9.5"), months = new JTextField("60");
        JLabel result = new JLabel("Enter loan details to calculate EMI.");
        JButton calculate = new JButton("Calculate EMI");
        p.add(new JLabel("Principal (₹)")); p.add(principal);
        p.add(new JLabel("Annual rate (%)")); p.add(rate);
        p.add(new JLabel("Tenure (months)")); p.add(months);
        p.add(calculate); p.add(result);
        calculate.addActionListener(e -> runAction(() -> {
            double emi = EmiCalculator.calculate(Double.parseDouble(principal.getText()),
                    Double.parseDouble(rate.getText()), Integer.parseInt(months.getText()));
            result.setText(String.format("Monthly EMI: ₹%.2f", emi));
        }));
        return p;
    }

    private JPanel repaymentPanel() {
        JPanel p = formPanel();
        JTextField loanId = new JTextField(), amount = new JTextField(), note = new JTextField("Monthly instalment");
        p.add(new JLabel("Loan ID")); p.add(loanId);
        p.add(new JLabel("Payment amount (₹)")); p.add(amount);
        p.add(new JLabel("Note")); p.add(note);
        JButton pay = new JButton("Record repayment"); p.add(new JLabel()); p.add(pay);
        pay.addActionListener(e -> runAction(() -> {
            service.recordRepayment(Long.parseLong(loanId.getText()), Double.parseDouble(amount.getText()), note.getText());
            double outstanding = service.repayments().getOutstandingPrincipal(Long.parseLong(loanId.getText()));
            JOptionPane.showMessageDialog(this, String.format("Payment recorded. Outstanding principal: ₹%.2f", outstanding));
        }));
        return p;
    }

    private JPanel reportPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        String[] columns = {"Loan ID", "Customer", "Type", "Principal", "Rate", "Months", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        JButton refresh = new JButton("Refresh report");
        refresh.addActionListener(e -> runAction(() -> {
            model.setRowCount(0);
            for (String[] row : service.loans().findAllSummary()) model.addRow(row);
        }));
        p.add(refresh, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    @FunctionalInterface private interface SqlAction { void run() throws Exception; }
    private void runAction(SqlAction action) {
        try { action.run(); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
