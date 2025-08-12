import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.event.*;

public class extra {
    private JFrame frame;
    private JLabel timeLabel, dateLabel;
    private DefaultListModel<String> alarmListModel;
    private java.util.List<Alarm> alarms = new ArrayList<>();

    public extra() {
        frame = new JFrame();
        frame.setUndecorated(true); // no title bar
        frame.setLayout(new BorderLayout());
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        // Clock Time Label
        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        frame.add(timeLabel, BorderLayout.NORTH);

        // Date Label
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        frame.add(dateLabel, BorderLayout.CENTER);

        // Alarm List
        alarmListModel = new DefaultListModel<>();
        JList<String> alarmList = new JList<>(alarmListModel);
        frame.add(new JScrollPane(alarmList), BorderLayout.EAST);

        // Add Alarm Button
        JButton addAlarmBtn = new JButton("Add Alarm");
        addAlarmBtn.addActionListener(e -> addAlarm());
        frame.add(addAlarmBtn, BorderLayout.SOUTH);

        // Update Clock Every Second
        Timer clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();

        // Check Alarm Every Second
        Timer alarmTimer = new Timer(1000, e -> checkAlarms());
        alarmTimer.start();

        frame.setVisible(true);
    }

    private void updateClock() {
        Date now = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy");
        timeLabel.setText(timeFormat.format(now));
        dateLabel.setText(dateFormat.format(now));
    }

    private void addAlarm() {
        String time = JOptionPane.showInputDialog(frame, "Enter alarm time (hh:mm a):");
        if (time == null || time.isEmpty()) return;
        String reason = JOptionPane.showInputDialog(frame, "Enter reason for alarm:");
        if (reason == null) reason = "";

        alarms.add(new Alarm(time.toUpperCase(), reason));
        alarmListModel.addElement(time.toUpperCase() + " - " + reason);
    }

    private void checkAlarms() {
        String currentTime = new SimpleDateFormat("hh:mm a").format(new Date()).toUpperCase();
        for (Alarm alarm : alarms) {
            if (alarm.time.equals(currentTime) && !alarm.triggered) {
                alarm.triggered = true;
                JOptionPane.showMessageDialog(frame,
                        "Alarm! Time: " + alarm.time + "\nReason: " + alarm.reason);
            }
        }
    }

    class Alarm {
        String time;
        String reason;
        boolean triggered = false;

        Alarm(String time, String reason) {
            this.time = time;
            this.reason = reason;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DigitalClockAlarm::new);
    }
}
