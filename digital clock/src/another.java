import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class another extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel timeLabel;
    private JLabel dateLabel;
    private float hue = 0f;

    public void AnimatedClock() {
        setLayout(new BorderLayout());

        // Time Label
        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 60));
        timeLabel.setForeground(Color.WHITE);

        // Date Label
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 28));
        dateLabel.setForeground(Color.WHITE);

        add(timeLabel, BorderLayout.CENTER);
        add(dateLabel, BorderLayout.SOUTH);

        // Update Time Every Second
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();

        // Animate Background
        Timer bgTimer = new Timer(50, e -> {
            hue += 0.002f;
            if (hue > 1) hue = 0;
            repaint();
        });
        bgTimer.start();
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        String dateStr = now.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
        timeLabel.setText(timeStr);
        dateLabel.setText(dateStr);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Create animated gradient background
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        Color color1 = Color.getHSBColor(hue, 0.6f, 0.9f);
        Color color2 = Color.getHSBColor((hue + 0.5f) % 1f, 0.6f, 0.9f);

        GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Animated Digital Clock");
        Component clockPanel = new AnimatedClock();
        frame.add(clockPanel);
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
