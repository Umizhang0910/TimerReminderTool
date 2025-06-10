package com.umizhang.reminder.service;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.umizhang.core.ThreadPoolScheduler;
import com.umizhang.reminder.ui.ReminderConfigDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public final class ReminderService {
    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);
    private final ScheduledExecutorService executorService = ApplicationManager.getApplication().getService(ThreadPoolScheduler.class).getScheduler();
    public boolean isRepeating;
    private ScheduledFuture<?> reminderTask;
    private long intervalMinutes;
    private ReminderState state = ReminderState.STOPPED;
    private long remainingTime;
    private String currentMessage;


    private ScheduledFuture<?> timerUpdateTask;


    public void scheduleReminder(ReminderConfigDialog.ReminderConfig config) {
        // Cancel existing reminder
        cancelExistingReminder();

        intervalMinutes = config.interval;
        currentMessage = config.message;
        isRepeating = config.isRepeating;

        // Create reminder task
        Runnable runnableTask = () -> showNotification(config.message);

        if (config.isRepeating) {
            // Schedule repeating reminder
            reminderTask = executorService.scheduleAtFixedRate(runnableTask,
                    config.initialDelay, config.interval, TimeUnit.MINUTES);
        } else {
            // Schedule one-time reminder
            reminderTask = executorService.schedule(runnableTask,
                    config.initialDelay, TimeUnit.MINUTES);
        }
        state = ReminderState.ACTIVE;
    }

    // Cancel reminder
    public void cancelExistingReminder() {
        if (reminderTask != null) {
            reminderTask.cancel(true);
            reminderTask = null;
        }
        state = ReminderState.STOPPED;
    }

    // Added: Immediately stop the current recurring reminder
    public void stopRepeatingReminder() {
        cancelExistingReminder();
        state = ReminderState.STOPPED;
        showNotification("Repeating reminder has been stopped");
    }

    // Pause existing reminder
    public void pauseReminder() {
        if (reminderTask != null && !reminderTask.isDone()) {
            // Get the remaining time before the next reminder
            remainingTime = Math.max(0, reminderTask.getDelay(TimeUnit.MINUTES));

            reminderTask.cancel(false);
            reminderTask = null;

            state = ReminderState.PAUSED;
            showNotification("Reminder has been paused. Remaining time: " + remainingTime + " minutes");
        }
    }

    public void resumeReminder() {
        if (state == ReminderState.PAUSED) {
            // Reschedule the reminder with the remaining time
            ReminderConfigDialog.ReminderConfig resumeConfig = new ReminderConfigDialog.ReminderConfig(
                    remainingTime, currentMessage, isRepeating, intervalMinutes
            );
            scheduleReminder(resumeConfig);

            state = ReminderState.ACTIVE;
            showNotification("Reminder has been resumed. Next reminder in: " + remainingTime + " minutes");
        }
    }

    // Display notification
    private void showNotification(String message) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Notification notification = new Notification("ReminderGroup", // Must be registered in plugin.xml
                    "Timer reminder", message, NotificationType.INFORMATION);

            if (isRepeating && state == ReminderState.ACTIVE) {
                notification.addAction(NotificationAction.createSimple("Pause reminder",
                        this::pauseReminder));
            } else if (isRepeating && state == ReminderState.PAUSED) {
                notification.addAction(NotificationAction.createSimple("Resume reminder",
                        this::resumeReminder));
            }

            /*// Add stop action for recurring reminders
            if (reminderTask != null && !reminderTask.isDone() && intervalMinutes > 0) {
                notification.addAction(NotificationAction.createSimple("Stop reminder",
                        () -> ApplicationManager.getApplication().getService(ReminderService.class)
                                .stopRepeatingReminder()));
            }*/
            if (isRepeating && state != ReminderState.STOPPED) {
                notification.addAction(NotificationAction.createSimple("Stop reminder",
                        this::stopRepeatingReminder));
            }
            Notifications.Bus.notify(notification);
        });
    }

    // Check if reminder is active
    public boolean isReminderActive() {
        // return reminderTask != null && !reminderTask.isDone();
        return state == ReminderState.ACTIVE;
    }

    public boolean isReminderPaused() {
        return state == ReminderState.PAUSED;
    }

    public String getStatusText() {
        if (state == ReminderState.ACTIVE) {
            // 计算剩余时间并格式化
            long seconds = getRemainingSeconds();
            long mins = seconds / 60;
            long secs = seconds % 60;
            return String.format("⏱ %02d:%02d", mins, secs);
        } else if (state == ReminderState.PAUSED) {
            return "⏸ Reminder Paused";
        }
        return "⏰ No reminder";
    }

    // 调试用状态覆盖
    public void debugSetState(ReminderState state) {
        this.state = state;
        System.out.println("调试：服务状态设置为 " + state);
    }

    private long getRemainingSeconds() {
        if (reminderTask != null && !reminderTask.isDone()) {
            try {
                return reminderTask.getDelay(TimeUnit.SECONDS);
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    private enum ReminderState {ACTIVE, PAUSED, STOPPED}
}
