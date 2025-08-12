import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AnimatedClock {
    private static LocalDateTime alarmTime = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Digital Clock with Alarm");
        frame.setSize(500, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Custom panel for animated background
        JPanel panel = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private float hue = 0;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                hue += 0.001f;
                if (hue > 1) hue = 0;
                Graphics2D g2d = (Graphics2D) g;
                Color c1 = Color.getHSBColor(hue, 0.5f, 1f);
                Color c2 = Color.getHSBColor((hue + 0.5f) % 1f, 0.5f, 1f);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BorderLayout());

        JLabel timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        timeLabel.setForeground(Color.BLACK);

        JLabel dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        dateLabel.setForeground(Color.BLACK);

        JButton alarmButton = new JButton("Set Alarm");
        alarmButton.setFont(new Font("Arial", Font.BOLD, 16));
        alarmButton.addActionListener(e -> {
            String timeInput = JOptionPane.showInputDialog(frame, "Enter alarm time (HH:mm AM/PM):", "Set Alarm", JOptionPane.PLAIN_MESSAGE);
            try {
                DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("hh:mm a");
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime setTime = LocalDateTime.of(now.toLocalDate(), java.time.LocalTime.parse(timeInput.toUpperCase(), inputFormat));
                if (setTime.isBefore(now)) {
                    setTime = setTime.plusDays(1);
                }
                alarmTime = setTime;
                JOptionPane.showMessageDialog(frame, "Alarm set for: " + setTime.format(DateTimeFormatter.ofPattern("hh:mm a")));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid time format! Please use HH:mm AM/PM format.");
            }
        });

        panel.add(timeLabel, BorderLayout.CENTER);
        panel.add(dateLabel, BorderLayout.NORTH);
        panel.add(alarmButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        // Clock update thread
        Thread clockThread = new Thread(() -> {
            while (true) {
                LocalDateTime now = LocalDateTime.now();
                String formattedTime = now.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
                String formattedDate = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));

                SwingUtilities.invokeLater(() -> {
                    timeLabel.setText(formattedTime);
                    dateLabel.setText(formattedDate);
                    panel.repaint();
                });

                if (alarmTime != null && now.getHour() == alarmTime.getHour() &&
                        now.getMinute() == alarmTime.getMinute() && now.getSecond() == 0) {
                    SwingUtilities.invokeLater(() -> {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(frame, "⏰ Alarm Time");
                        alarmTime = null;
                    });
                }

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
