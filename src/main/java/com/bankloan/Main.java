package com.bankloan;
import com.bankloan.service.DueDateReminder;
import com.bankloan.ui.MainFrame;
import javax.swing.SwingUtilities;
public class Main {
 public static void main(String[] args) {
  DueDateReminder reminder = new DueDateReminder();
  reminder.start();
  Runtime.getRuntime().addShutdownHook(new Thread(reminder::close));
  SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
 }
}