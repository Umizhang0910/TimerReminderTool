package com.umizhang.reminder.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.umizhang.core.ReminderSettings;
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

        // Load saved settings
        ReminderSettings.State settings = ReminderSettings.getInstance().getState();
        assert settings != null;
        minutesField.setText(String.valueOf(settings.initialDelay));
        messageField.setText(settings.defaultMessage);
        reminderTypeCombo.setSelectedItem(settings.isRepeating ? "Recurring Reminder" : "One-time");
        if (settings.isRepeating) {
            intervalField.setText(String.valueOf(settings.intervalMinutes));
            intervalField.setVisible(true);
        }
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

    @Override
    protected void doOKAction() {
        saveSettings();
        super.doOKAction();
    }

    private void saveSettings() {
        ReminderSettings.State settings = ReminderSettings.getInstance().getState();
        assert settings != null;
        settings.initialDelay = Long.parseLong(minutesField.getText());
        settings.defaultMessage = messageField.getText();
        settings.isRepeating = "Recurring Reminder".equals(reminderTypeCombo.getSelectedItem());

        if (settings.isRepeating) {
            settings.intervalMinutes = Long.parseLong(intervalField.getText());
        }
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
