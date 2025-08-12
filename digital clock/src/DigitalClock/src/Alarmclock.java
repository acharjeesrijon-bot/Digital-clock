import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.Timer;

public class Alarmclock extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel timeLabel, dateLabel;
    private DefaultListModel<String> alarmListModel;
    private JList<String> alarmList;
    private java.util.List<Alarm> alarms = new ArrayList<>();

    public void DigitalClockAlarm() {
        setTitle("Digital Clock with Multiple Alarms");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background gradient panel
        JPanel mainPanel = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color c1 = new Color(135, 206, 250);
                Color c2 = new Color(70, 130, 180);
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // Time label (12-hour format)
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Verdana", Font.BOLD, 50));
        timeLabel.setForeground(new Color(0, 0, 139)); // Dark Deep Blue
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Date label
        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Verdana", Font.BOLD, 25));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateLabel.setBorder(new LineBorder(Color.BLACK, 4));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        topPanel.add(timeLabel);
        topPanel.add(dateLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Alarm list
        alarmListModel = new DefaultListModel<>();
        alarmList = new JList<>(alarmListModel);
        alarmList.setFont(new Font("Verdana", Font.PLAIN, 14));
        alarmList.setVisibleRowCount(4);
        JScrollPane scrollPane = new JScrollPane(alarmList);
        scrollPane.setPreferredSize(new Dimension(200, 80));

        // Add alarm button
        JButton addAlarmButton = new JButton("Add Alarm");
        addAlarmButton.addActionListener(e -> addAlarm());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(addAlarmButton);
        bottomPanel.add(scrollPane);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Timer for updating clock
        Timer timer = new Timer(1000, e -> updateClock());
        timer.start();
    }

    private void updateClock() {
        Date now = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a"); // 12-hour format with AM/PM
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        timeLabel.setText(timeFormat.format(now));
        dateLabel.setText(dateFormat.format(now));

        checkAlarms(now);
    }

    private void addAlarm() {
        JTextField timeField = new JTextField("hh:mm a");
        JTextField dateField = new JTextField("dd-MM-yyyy");

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Time (hh:mm AM/PM):"));
        panel.add(timeField);
        panel.add(new JLabel("Date (dd-MM-yyyy):"));
        panel.add(dateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Set Alarm", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String timeInput = timeField.getText().trim();
                String dateInput = dateField.getText().trim();

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
                sdf.setLenient(false);
                Date alarmTime = sdf.parse(timeInput + " " + dateInput);

                if (alarmTime.before(new Date())) {
                    JOptionPane.showMessageDialog(this, "Invalid! Alarm time is in the past.");
                    return;
                }

                alarms.add(new Alarm(alarmTime));
                alarmListModel.addElement(sdf.format(alarmTime));
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid time or date format!");
            }
        }
    }

    private void checkAlarms(Date now) {
        Iterator<Alarm> iterator = alarms.iterator();
        while (iterator.hasNext()) {
            Alarm alarm = iterator.next();
            if (Math.abs(now.getTime() - alarm.time.getTime()) < 1000) {
                JOptionPane.showMessageDialog(this, "⏰ Alarm for " + new SimpleDateFormat("hh:mm a dd-MM-yyyy").format(alarm.time));
                iterator.remove();
                alarmListModel.removeElement(new SimpleDateFormat("hh:mm a dd-MM-yyyy").format(alarm.time));
            }
        }
    }

    private static class Alarm {
        Date time;
        Alarm(Date time) {
            this.time = time;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Alarmclock().setVisible(true));
    }
}
