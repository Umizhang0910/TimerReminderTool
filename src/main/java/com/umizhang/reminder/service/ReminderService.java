package com.umizhang.reminder.service;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.umizhang.core.ThreadPoolScheduler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public final class ReminderService {

    private final ScheduledExecutorService executorService = ApplicationManager.getApplication().getService(ThreadPoolScheduler.class).getScheduler();

    private ScheduledFuture<?> reminderTask;

    public void scheduleReminder(long delayMinutes, String message) {
        cancelExistingReminder(); // Cancel existing reminder
        reminderTask = executorService.schedule(
                () -> showNotification(message),
                delayMinutes,
                TimeUnit.MINUTES
        );
    }

    // Cancel reminder
    public void cancelExistingReminder() {
        if (reminderTask != null) {
            reminderTask.cancel(true);
            reminderTask = null;
        }
    }

    // Display notification
    private void showNotification(String message) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Notification notification = new Notification(
                    "ReminderGroup", // 需在plugin.xml注册
                    "Timer Reminder",
                    message,
                    NotificationType.INFORMATION
            );
            Notifications.Bus.notify(notification);
        });
    }
}
