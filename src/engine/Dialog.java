package engine;

import javax.swing.*;
import java.awt.*;

public class Dialog {
    private static String returnValue;
    private static int finished = 0;

    public static String InputDialog(String title, String defaultValue) {
        JFrame frame = new JFrame(title);
        frame.pack();

        frame.setSize(300, 103);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container pane = frame.getContentPane();
        pane.setLayout(null);

        returnValue = defaultValue;
        JTextField field = new JTextField(defaultValue);
        field.setBounds(5, 5, 275, 25);
        pane.add(field);

        JButton ok = new JButton("OK");
        ok.setBounds(24, 34, 80, 24);

        ok.addActionListener(e -> {
            returnValue = field.getText();
            finished = 1;
        });
        pane.add(ok);

        JButton cancel = new JButton("CANCEL");
        cancel.setBounds(198, 34, 80, 24);

        cancel.addActionListener(e -> {
            returnValue = null;
            finished = 1;
        });
        pane.add(cancel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        finished = 0;

        while (finished != 1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!frame.isDisplayable()) break;
        }

        frame.dispose();
        return returnValue;
    }

    public static void MessageDialog(String title, String message) {
        JFrame frame = new JFrame(title);
        frame.pack();
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea textArea = new JTextArea(message);
        JScrollPane scroll = new JScrollPane(textArea);
        textArea.setEditable(false);

        frame.add(scroll);
        frame.setVisible(true);
    }
}