package com.bankloan.model;

public class Customer {
    private long customerId;
    private String fullName;
    private String email;
    private String phone;
    private String address;

    public Customer(String fullName, String email, String phone, String address) {
        this(0, fullName, email, phone, address);
    }

    public Customer(long customerId, String fullName, String email, String phone, String address) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
}
