package com.umizhang.reminder.service;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.umizhang.core.ThreadPoolScheduler;
import com.umizhang.reminder.ui.ReminderConfigDialog;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public final class ReminderService {

    private final ScheduledExecutorService executorService = ApplicationManager.getApplication().getService(ThreadPoolScheduler.class).getScheduler();

    private ScheduledFuture<?> reminderTask;
    private Runnable runnableTask;
    private long intervalMinutes;

    public void scheduleReminder(ReminderConfigDialog.ReminderConfig config) {
        // Cancel existing reminder
        cancelExistingReminder();

        intervalMinutes = config.interval;
        // Create reminder task
        runnableTask = () -> showNotification(config.message);

        if (config.isRepeating) {
            // Schedule repeating reminder
            reminderTask = executorService.scheduleAtFixedRate(runnableTask,
                    config.initialDelay, config.interval, TimeUnit.MINUTES);
        } else {
            // Schedule one-time reminder
            reminderTask = executorService.schedule(runnableTask,
                    config.initialDelay, TimeUnit.MINUTES);
        }
    }

    // Cancel reminder
    public void cancelExistingReminder() {
        if (reminderTask != null) {
            reminderTask.cancel(true);
            reminderTask = null;
        }
    }

    // Added: Immediately stop the current recurring reminder
    public void stopRepeatingReminder() {
        cancelExistingReminder();
        showNotification("Repeating reminder has been stopped");
    }

    // Display notification
    private void showNotification(String message) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Notification notification = new Notification("ReminderGroup", // Must be registered in plugin.xml
                    "Timer Reminder", message, NotificationType.INFORMATION);
            // Add stop action for recurring reminders
            if (reminderTask != null && !reminderTask.isDone() && intervalMinutes > 0) {
                notification.addAction(NotificationAction.createSimple("Stop Reminder",
                        () -> ApplicationManager.getApplication().getService(ReminderService.class)
                                .stopRepeatingReminder()));
            }
            Notifications.Bus.notify(notification);
        });
    }

    // Check if reminder is active
    public boolean isReminderActive() {
        return reminderTask != null && !reminderTask.isDone();
    }
}
