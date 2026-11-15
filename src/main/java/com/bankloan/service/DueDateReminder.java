package com.bankloan.service;

import com.bankloan.dao.LoanDAO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

public class DueDateReminder implements AutoCloseable {
    private final ScheduledExecutorService scheduler=Executors.newSingleThreadScheduledExecutor(r->{
        Thread t=new Thread(r,"loan-due-date-reminder"); t.setDaemon(true); return t;
    });
    private final LoanDAO loanDAO=new LoanDAO();

    public void start() {
        scheduler.scheduleAtFixedRate(()->{
            try {
                List<String[]> due=loanDAO.findDueLoans(7);
                if(!due.isEmpty()) {
                    System.out.println("[Loan reminders "+LocalDateTime.now()+"] Upcoming/overdue instalments:");
                    for(String[] row:due) System.out.printf("Loan #%s | %s | due %s%n",row[0],row[1],row[2]);
                }
            } catch(Exception ex) {
                System.err.println("Unable to check loan due dates: "+ex.getMessage());
            }
        },0,1,TimeUnit.DAYS);
    }
    @Override public void close(){scheduler.shutdownNow();}
}