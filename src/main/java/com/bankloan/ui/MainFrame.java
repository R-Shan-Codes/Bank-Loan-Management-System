package com.bankloan.ui;

import com.bankloan.service.EmiCalculator;
import com.bankloan.service.LoanService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final LoanService service=new LoanService();
    private final DefaultTableModel loanModel=readOnlyModel("Loan ID","Customer","Type","Principal (₹)","Rate (%)","Months","Status");
    private final DefaultTableModel dueModel=readOnlyModel("Loan ID","Customer","Due date");

    public MainFrame() {
        super("Bank Loan Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120,720); setMinimumSize(new Dimension(900,600)); setLocationRelativeTo(null);
        setLayout(new BorderLayout(12,12));
        JLabel heading=new JLabel("BANK LOAN MANAGEMENT SYSTEM",SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif",Font.BOLD,23));
        heading.setBorder(BorderFactory.createEmptyBorder(18,8,12,8));
        add(heading,BorderLayout.NORTH);
        JTabbedPane tabs=new JTabbedPane();
        tabs.addTab("Customer",customerPanel());
        tabs.addTab("Loan Application",loanPanel());
        tabs.addTab("EMI Calculator",emiPanel());
        tabs.addTab("Repayment",repaymentPanel());
        tabs.addTab("Loan Management",managementPanel());
        tabs.addTab("Outstanding Report",reportPanel());
        tabs.addTab("Due-date Reminders",duePanel());
        add(tabs,BorderLayout.CENTER);
        JLabel footer=new JLabel("  Java 17  |  Swing  |  JDBC  |  MySQL",SwingConstants.LEFT);
        footer.setBorder(BorderFactory.createEmptyBorder(4,10,10,10)); add(footer,BorderLayout.SOUTH);
    }

    private static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns,0) { @Override public boolean isCellEditable(int r,int c){return false;} };
    }
    private JPanel formPanel() {
        JPanel p=new JPanel(new GridLayout(0,2,12,12));
        p.setBorder(BorderFactory.createEmptyBorder(24,32,24,32)); return p;
    }
    private JPanel customerPanel() {
        JPanel p=formPanel();
        JTextField name=new JTextField(),email=new JTextField(),phone=new JTextField(),address=new JTextField();
        addField(p,"Full name",name); addField(p,"Email",email); addField(p,"Phone",phone); addField(p,"Address",address);
        JButton save=new JButton("Register customer"); p.add(new JLabel()); p.add(save);
        save.addActionListener(e->runAction(()->{
            long id=service.registerCustomer(name.getText(),email.getText(),phone.getText(),address.getText());
            info("Customer registered successfully. Customer ID: "+id);
            name.setText("");email.setText("");phone.setText("");address.setText("");
        }));
        return p;
    }
    private JPanel loanPanel() {
        JPanel p=formPanel();
        JTextField customerId=new JTextField(),amount=new JTextField(),rate=new JTextField("9.5"),months=new JTextField("60");
        JComboBox<String> type=new JComboBox<>(new String[]{"HOME","CAR","EDUCATION"});
        addField(p,"Customer ID",customerId);addField(p,"Loan type",type);addField(p,"Principal amount (₹)",amount);
        addField(p,"Annual interest rate (%)",rate);addField(p,"Tenure (months)",months);
        JButton apply=new JButton("Submit application");p.add(new JLabel());p.add(apply);
        apply.addActionListener(e->runAction(()->{
            long id=service.applyLoan((String)type.getSelectedItem(),Long.parseLong(customerId.getText().trim()),
                    Double.parseDouble(amount.getText().trim()),Double.parseDouble(rate.getText().trim()),Integer.parseInt(months.getText().trim()));
            info("Application submitted. Loan ID: "+id+" (Pending approval).");
        }));
        return p;
    }
    private JPanel emiPanel() {
        JPanel p=formPanel();
        JTextField principal=new JTextField(),rate=new JTextField("9.5"),months=new JTextField("60");
        JLabel result=new JLabel("Enter loan details to calculate EMI.");
        addField(p,"Principal (₹)",principal);addField(p,"Annual rate (%)",rate);addField(p,"Tenure (months)",months);
        JButton calculate=new JButton("Calculate EMI");p.add(calculate);p.add(result);
        calculate.addActionListener(e->runAction(()->{
            double emi=EmiCalculator.calculate(Double.parseDouble(principal.getText()),Double.parseDouble(rate.getText()),Integer.parseInt(months.getText()));
            result.setText(String.format("Monthly EMI: ₹%.2f",emi));
        }));
        return p;
    }
    private JPanel repaymentPanel() {
        JPanel p=formPanel();
        JTextField loanId=new JTextField(),amount=new JTextField(),note=new JTextField("Monthly instalment");
        addField(p,"Loan ID",loanId);addField(p,"Payment amount (₹)",amount);addField(p,"Note",note);
        JButton pay=new JButton("Record repayment");p.add(new JLabel());p.add(pay);
        pay.addActionListener(e->runAction(()->{
            long id=Long.parseLong(loanId.getText().trim());
            service.recordRepayment(id,Double.parseDouble(amount.getText().trim()),note.getText());
            double balance=service.repayments().getOutstandingPrincipal(id);
            info(String.format("Payment recorded. Estimated outstanding principal: ₹%.2f",balance));
        }));
        return p;
    }
    private JPanel managementPanel() {
        JPanel p=new JPanel(new BorderLayout(10,10));p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField id=new JTextField(9);JButton approve=new JButton("Approve"),reject=new JButton("Reject"),refresh=new JButton("Refresh");
        actions.add(new JLabel("Loan ID:"));actions.add(id);actions.add(approve);actions.add(reject);actions.add(refresh);
        approve.addActionListener(e->changeStatus(id,"APPROVED"));reject.addActionListener(e->changeStatus(id,"REJECTED"));
        refresh.addActionListener(e->refreshLoans());
        JTable table=new JTable(loanModel);table.setRowHeight(25);p.add(actions,BorderLayout.NORTH);p.add(new JScrollPane(table),BorderLayout.CENTER);
        return p;
    }
    private void changeStatus(JTextField id,String status) {
        runAction(()->{service.changeLoanStatus(Long.parseLong(id.getText().trim()),status);info("Loan status updated to "+status+".");refreshLoans();});
    }
    private JPanel reportPanel() {
        JPanel p=new JPanel(new BorderLayout(8,8));p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JButton refresh=new JButton("Refresh outstanding report");
        refresh.addActionListener(e->refreshLoans());
        JTable table=new JTable(loanModel);table.setRowHeight(25);p.add(refresh,BorderLayout.NORTH);p.add(new JScrollPane(table),BorderLayout.CENTER);
        return p;
    }
    private JPanel duePanel() {
        JPanel p=new JPanel(new BorderLayout(8,8));p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JButton refresh=new JButton("Check due / overdue loans");
        refresh.addActionListener(e->runAction(()->{
            dueModel.setRowCount(0);
            List<String[]> rows=service.loans().findDueLoans(7);
            for(String[] row:rows) dueModel.addRow(row);
            if(rows.isEmpty()) info("No approved loan instalments are due within the next 7 days.");
        }));
        JTable table=new JTable(dueModel);table.setRowHeight(25);p.add(refresh,BorderLayout.NORTH);p.add(new JScrollPane(table),BorderLayout.CENTER);
        return p;
    }
    private void refreshLoans() {
        runAction(()->{loanModel.setRowCount(0);for(String[] row:service.loans().findAllSummary())loanModel.addRow(row);});
    }
    private static void addField(JPanel p,String label,JComponent field){p.add(new JLabel(label));p.add(field);}
    private void info(String message){JOptionPane.showMessageDialog(this,message,"Bank Loan Management",JOptionPane.INFORMATION_MESSAGE);}
    @FunctionalInterface private interface UiAction {void run() throws Exception;}
    private void runAction(UiAction action) {
        try{action.run();}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Operation failed",JOptionPane.ERROR_MESSAGE);}
    }
}
