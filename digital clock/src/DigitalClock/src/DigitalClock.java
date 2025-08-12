import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.ArrayList;

public class DigitalClock {
    private static final DateTimeFormatter timeFormatDisplay = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter dateFormatDisplay = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
    private static final DateTimeFormatter alarmDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter alarmTimeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");

    private static JLabel timeLabel;
    private static JLabel dateLabel;
    private static DefaultListModel<String> alarmListModel;
    private static java.util.List<Alarm> alarms = new ArrayList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Digital Clock with Alarms");
        frame.setSize(900, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel gradientPanel = new JPanel() {
            private static final long serialVersionUID = 1L;

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(135, 206, 235);
                Color color2 = new Color(240, 128, 128);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new BorderLayout());

        JPanel timeDatePanel = new JPanel(new GridLayout(2, 1));
        timeDatePanel.setOpaque(false);
        timeDatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Verdana", Font.BOLD, 60));
        timeLabel.setForeground(new Color(0, 0, 139)); // Dark deep blue

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Verdana", Font.PLAIN, 28));
        dateLabel.setForeground(Color.BLACK);

        timeDatePanel.add(timeLabel);
        timeDatePanel.add(dateLabel);

        alarmListModel = new DefaultListModel<>();
        JList<String> alarmList = new JList<>(alarmListModel);
        alarmList.setFont(new Font("Verdana", Font.PLAIN, 14));
        JScrollPane alarmScrollPane = new JScrollPane(alarmList);
        alarmScrollPane.setBorder(BorderFactory.createTitledBorder("Upcoming Alarms"));
        alarmScrollPane.setPreferredSize(new Dimension(600, 80));

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);

        JTextField alarmTimeField = new JTextField(8);
        JTextField alarmDateField = new JTextField(10);
        JTextField alarmReasonField = new JTextField(15);
        JButton addAlarmButton = new JButton("Add Alarm");

        inputPanel.add(new JLabel("Time (hh:mm:ss AM/PM):"));
        inputPanel.add(alarmTimeField);
        inputPanel.add(new JLabel("Date (dd-MM-yyyy):"));
        inputPanel.add(alarmDateField);
        inputPanel.add(new JLabel("Reason:"));
        inputPanel.add(alarmReasonField);
        inputPanel.add(addAlarmButton);

        gradientPanel.add(timeDatePanel, BorderLayout.NORTH);
        gradientPanel.add(alarmScrollPane, BorderLayout.CENTER);
        gradientPanel.add(inputPanel, BorderLayout.SOUTH);
        frame.add(gradientPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Thread clockThread = new Thread(() -> {
            while (true) {
                LocalDateTime now = LocalDateTime.now();

                SwingUtilities.invokeLater(() -> {
                    timeLabel.setText(now.format(timeFormatDisplay));
                    dateLabel.setText(now.format(dateFormatDisplay));
                });

                alarms.removeIf(alarm -> {
                    if (now.withNano(0).equals(alarm.dateTime.withNano(0))) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(frame, "⏰ Alarm! Reason: " + alarm.reason, "Alarm", JOptionPane.INFORMATION_MESSAGE);
                            String alarmStr = alarm.dateTime.format(alarmDateFormat) + " " + alarm.dateTime.format(alarmTimeFormat) + " - " + alarm.reason;
                            alarmListModel.removeElement(alarmStr);
                        });
                        return true;
                    }
                    return false;
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        clockThread.start();

        addAlarmButton.addActionListener(e -> {
            String timeText = alarmTimeField.getText().trim();
            String dateText = alarmDateField.getText().trim();
            String reasonText = alarmReasonField.getText().trim();

            if (reasonText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a reason for the alarm.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LocalTime alarmTime = LocalTime.parse(timeText.toUpperCase(), alarmTimeFormat);
                LocalDate alarmDate = LocalDate.parse(dateText, alarmDateFormat);
                LocalDateTime alarmDateTime = LocalDateTime.of(alarmDate, alarmTime);

                if (alarmDateTime.isBefore(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(frame, "Cannot set an alarm in the past.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                alarms.add(new Alarm(alarmDateTime, reasonText));
                alarmListModel.addElement(alarmDateTime.format(alarmDateFormat) + " " + alarmDateTime.format(alarmTimeFormat) + " - " + reasonText);

                alarmTimeField.setText("");
                alarmDateField.setText("");
                alarmReasonField.setText("");
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Invalid date or time format!\nTime format: hh:mm:ss AM/PM\nDate format: dd-MM-yyyy",
                        "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    static class Alarm {
        LocalDateTime dateTime;
        String reason;

        Alarm(LocalDateTime dateTime, String reason) {
            this.dateTime = dateTime;
            this.reason = reason;
        }
    }
}
