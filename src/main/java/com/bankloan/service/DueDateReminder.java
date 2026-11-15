package com.bankloan.service;

import java.time.LocalDate;
import java.util.concurrent.*;

public class DueDateReminder implements AutoCloseable {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "loan-due-date-reminder");
        t.setDaemon(true);
        return t;
    });

    public void start() {
        scheduler.scheduleAtFixedRate(() ->
                System.out.println("[Reminder worker] " + LocalDate.now()
                        + " - Check upcoming loan instalment due dates."),
                0, 1, TimeUnit.DAYS);
    }

    @Override public void close() { scheduler.shutdownNow(); }
}
