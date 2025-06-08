package com.umizhang.reminder.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ReminderConfigDialog extends DialogWrapper {
    private JTextField minutesField;
    private JComboBox<String> reminderTypeCombo;
    private JTextField intervalField;
    private JTextField messageField;

    public ReminderConfigDialog() {
        super(true);
        setTitle("Set Timed Reminder");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Reminder time input
        panel.add(new JLabel("Reminder Time (minutes):"));
        minutesField = new JTextField();
        panel.add(minutesField);

        // Reminder type input
        panel.add(new JLabel("Reminder Type:"));
        reminderTypeCombo = new ComboBox<>(new String[]{"One-time", "Recurring Reminder"});
        reminderTypeCombo.addActionListener(e -> {
            boolean isRepeating = "Recurring Reminder".equals(reminderTypeCombo.getSelectedItem());
            intervalField.setVisible(isRepeating);
//            intervalField.getParent().getComponent(4).setVisible(isRepeating);
        });
        panel.add(reminderTypeCombo);

        // Interval time (default hidden)
        panel.add(new JLabel("Interval Time (minutes):"));
        intervalField = new JTextField();
        intervalField.setVisible(false);
        panel.add(intervalField);

        // Reminder Content
        panel.add(new JLabel("Reminder Content:"));
        messageField = new JTextField();
        panel.add(messageField);

        return panel;
    }

    public ReminderConfig getConfig() {
        return new ReminderConfig(
                Long.parseLong(minutesField.getText()),
                messageField.getText(),
                "Recurring Reminder".equals(reminderTypeCombo.getSelectedItem()),
                intervalField.isVisible() ? Long.parseLong(intervalField.getText()) : 0
        );
    }

    // Configuration Data Class
    public static class ReminderConfig {
        public final long initialDelay;
        public final String message;
        public final boolean isRepeating;
        public final long interval;

        public ReminderConfig(long initialDelay, String message, boolean isRepeating, long interval) {
            this.initialDelay = initialDelay;
            this.message = message;
            this.isRepeating = isRepeating;
            this.interval = interval;
        }
    }
}
