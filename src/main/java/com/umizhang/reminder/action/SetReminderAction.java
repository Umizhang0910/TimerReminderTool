package com.umizhang.reminder.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.umizhang.reminder.service.ReminderService;
import org.jetbrains.annotations.NotNull;

public class SetReminderAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // 1. Get user input
        String minutesStr = Messages.showInputDialog(
                "Please enter the reminder time (minutes):",
                "Set Reminder",
                Messages.getQuestionIcon()
        );

        if (minutesStr == null || minutesStr.isEmpty()) return;

        // 2. Schedule Reminder
        try {
            long minutes = Long.parseLong(minutesStr);
            String message = Messages.showInputDialog(
                    "Please enter the reminder content:",
                    "Reminder Content",
                    Messages.getQuestionIcon()
            );

            if (message != null && !message.isEmpty()) {
                ReminderService service = ApplicationManager.getApplication().getService(ReminderService.class);
                service.scheduleReminder(minutes, message);
                Messages.showInfoMessage("Reminder set successfully!", "Success");
            }
        } catch (NumberFormatException ex) {
            Messages.showErrorDialog("Please enter a valid number!", "Error");
        }
    }
}
