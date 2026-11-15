# Bank Loan Management System

A Java 17 + Swing + MySQL mini-project for managing customers, loan applications, EMI calculations and repayments.

## Features
- Customer registration and listing
- Home, car and education loan applications
- Loan approval/rejection workflow
- EMI calculation using the standard reducing-balance formula
- Repayment recording and outstanding balance tracking
- Stored procedure for generating an EMI schedule
- Background due-date reminder worker
- JDBC persistence with DAO/service separation

## Requirements
- JDK 17+
- Maven 3.8+
- MySQL 8+

## Setup
1. Create the database by running `database/schema.sql` in MySQL.
2. Update database credentials using environment variables:
   - `BANK_DB_URL` (default: `jdbc:mysql://localhost:3306/bank_loan_db`)
   - `BANK_DB_USER` (default: `root`)
   - `BANK_DB_PASSWORD` (default: empty)
3. Build: `mvn clean package`
4. Run: `mvn exec:java`

The application opens in demo-friendly desktop mode. Database-backed operations require MySQL to be running and configured.

## Project structure
- `model`: domain objects and loan inheritance hierarchy
- `dao`: JDBC data access
- `service`: business rules and EMI logic
- `database`: connection management
- `exception`: custom validation exception
- `ui`: Swing interface
- `database/schema.sql`: tables and stored procedure

## Academic concepts demonstrated
Encapsulation, constructors, inheritance, method overriding, interfaces, custom exceptions, threads, Math calculations, JDBC, CallableStatement, Swing layouts and ActionListener.
