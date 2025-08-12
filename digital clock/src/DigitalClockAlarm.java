import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class DigitalClockAlarm {
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
    private static final DateTimeFormatter alarmDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // dd-MM-yyyy format

    private static JLabel timeLabel;
    private static JLabel dateLabel;
    private static DefaultListModel<String> alarmListModel;
    private static java.util.List<LocalDateTime> alarms = new ArrayList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Digital Clock with Multiple Alarms");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Gradient Panel
        JPanel gradientPanel = new JPanel() {
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

        // Time & Date Panel
        JPanel timeDatePanel = new JPanel(new GridLayout(2, 1));
        timeDatePanel.setOpaque(false);
        timeDatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Verdana", Font.BOLD, 50));
        timeLabel.setForeground(new Color(0, 0, 139)); // Dark deep blue

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Verdana", Font.PLAIN, 28));
        dateLabel.setForeground(Color.BLACK);

        timeDatePanel.add(timeLabel);
        timeDatePanel.add(dateLabel);

        // Alarm List Panel
        JPanel alarmPanel = new JPanel(new BorderLayout());
        alarmPanel.setOpaque(false);
        alarmPanel.setBorder(BorderFactory.createTitledBorder("Upcoming Alarms"));

        alarmListModel = new DefaultListModel<>();
        JList<String> alarmList = new JList<>(alarmListModel);
        alarmPanel.add(new JScrollPane(alarmList), BorderLayout.CENTER);
        alarmPanel.setPreferredSize(new Dimension(200, 120)); // Smaller height

        // Alarm Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);

        JTextField alarmTimeField = new JTextField(8); // HH:mm:ss
        JTextField alarmDateField = new JTextField(10); // dd-MM-yyyy

        JButton addAlarmButton = new JButton("Add Alarm");
        addAlarmButton.addActionListener(e -> {
            String timeText = alarmTimeField.getText().trim();
            String dateText = alarmDateField.getText().trim();

            try {
                LocalTime alarmTime = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm:ss"));
                LocalDate alarmDate = LocalDate.parse(dateText, alarmDateFormat); // Validate date format

                LocalDateTime alarmDateTime = LocalDateTime.of(alarmDate, alarmTime);
                if (alarmDateTime.isBefore(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(frame, "Cannot set alarm in the past!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                alarms.add(alarmDateTime);
                alarmListModel.addElement(alarmDateTime.format(alarmDateFormat) + " " + alarmDateTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                alarmTimeField.setText("");
                alarmDateField.setText("");
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date or time format!\nTime: HH:mm:ss\nDate: dd-MM-yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputPanel.add(new JLabel("Time (HH:mm:ss):"));
        inputPanel.add(alarmTimeField);
        inputPanel.add(new JLabel("Date (dd-MM-yyyy):"));
        inputPanel.add(alarmDateField);
        inputPanel.add(addAlarmButton);

        // Add panels to frame
        gradientPanel.add(timeDatePanel, BorderLayout.NORTH);
        gradientPanel.add(alarmPanel, BorderLayout.CENTER);
        gradientPanel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(gradientPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Clock Thread
        Thread clockThread = new Thread(() -> {
            while (true) {
                LocalDateTime now = LocalDateTime.now();
                String currentTime = now.format(timeFormat);
                String currentDate = now.format(dateFormat);

                SwingUtilities.invokeLater(() -> {
                    timeLabel.setText(currentTime);
                    dateLabel.setText(currentDate);
                });

                // Check alarms
                alarms.removeIf(alarm -> {
                    if (now.withNano(0).equals(alarm.withNano(0))) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(frame, "⏰ Alarm Time! " + alarm.format(alarmDateFormat) + " " + alarm.format(timeFormat), "Alarm", JOptionPane.INFORMATION_MESSAGE);
                        });
                        return true; // Remove after trigger
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
    }
}
