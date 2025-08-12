import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class time {

    public static void main(String[] args) {
        // উইন্ডো তৈরি
        JFrame frame = new JFrame("Digital Clock");
        JLabel timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        frame.add(timeLabel);
        frame.setSize(400, 120);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // UPDATE 1: শুধু একবার সময় দেখাবে (Static Time)
        LocalTime initialTime = LocalTime.now();
        String staticTime = initialTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        timeLabel.setText("Static Time: " + staticTime);

        // ৫ সেকেন্ড পরে UPDATE 2 চালু হবে
        Timer startRealTime = new Timer(5000, e -> {
            // UPDATE 2: Real-Time Clock Update
            Thread clockThread = new Thread(() -> {
                while (true) {
                    LocalTime now = LocalTime.now();
                    String realTime = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    timeLabel.setText("Live Time: " + realTime);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            clockThread.start();
        });

        startRealTime.setRepeats(false); // একবারই চলবে
        startRealTime.start(); // টাইমার চালু করো
    }
}

