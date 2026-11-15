# Bank Loan Management System

A Java 17 desktop application built with Swing, JDBC and MySQL for a Semester 3 OOP mini-project.

## Features
- Register customers and submit home, car or education loan applications.
- Approve or reject pending applications.
- Calculate monthly EMI using the reducing-balance formula (including zero-interest loans).
- Record repayments only against approved loans.
- Estimate the remaining principal using an amortisation calculation.
- View loan applications and due/overdue instalments.
- Run a background reminder worker that checks due dates daily.
- Generate an approved loan's EMI value through a MySQL stored procedure called with JDBC `CallableStatement`.

## Technology
- Java 17, Maven, Swing
- MySQL 8+, JDBC / MySQL Connector/J
- JUnit 5 for unit tests

## Setup
1. Install JDK 17+, Maven 3.8+ and MySQL 8+.
2. For a fresh database, execute `database/schema.sql` in MySQL Workbench or the MySQL client.
3. If you already created the earlier version of this database, execute `database/migration_v2.sql` once to add the due-date column.
4. Configure environment variables:
   - `BANK_DB_URL` — defaults to `jdbc:mysql://localhost:3306/bank_loan_db`
   - `BANK_DB_USER` — defaults to `root`
   - `BANK_DB_PASSWORD` — defaults to an empty password
5. From the project root, build and run:
   ```bash
   mvn clean test
   mvn package
   mvn exec:java
   ```

The Swing window opens after launch. Database operations require the MySQL server and schema to be available.

## Workflow
1. Register a customer and note the generated customer ID.
2. Submit a loan application using that customer ID.
3. Open **Loan Management**, refresh, then approve or reject the pending loan by ID.
4. Use **EMI Calculator** or the approved loan's stored-procedure EMI.
5. Record instalment payments in **Repayment**.
6. Review the **Outstanding Report** and **Due-date Reminders** tabs.

## Project structure
- `model` — customer, loan hierarchy and repayment model
- `dao` — JDBC persistence and stored-procedure access
- `service` — business validation, EMI and reminder worker
- `database` — connection configuration and SQL schema
- `exception` — custom invalid-loan exception
- `ui` — Swing interface

## OOP concepts demonstrated
Encapsulation, constructors, inheritance, method overriding, interface implementation, custom exceptions, multithreading, `Math.pow`, JDBC, `CallableStatement`, Swing layouts and `ActionListener`.

## Notes / limitations
- The outstanding-principal value is an estimate based on recorded payments allocated to scheduled amortisation instalments. This academic mini-project does not model late fees, prepayment penalties or daily interest accrual.
- The reminder worker prints upcoming/overdue instalments to the application console; it does not send email/SMS or desktop notifications.
- Due dates advance by one month when a recorded payment is at least one calculated EMI.
