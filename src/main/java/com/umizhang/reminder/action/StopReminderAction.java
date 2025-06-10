package com.umizhang.reminder.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.umizhang.reminder.service.ReminderService;
import org.jetbrains.annotations.NotNull;

public class StopReminderAction extends AnAction {
    public StopReminderAction() {
        super("Stop Reminder",
                "Stop the currently active reminder",
                AllIcons.Actions.Suspend);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ReminderService service = ApplicationManager.getApplication().getService(ReminderService.class);
        service.stopRepeatingReminder();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        ReminderService service = ApplicationManager.getApplication().getService(ReminderService.class);
        e.getPresentation().setEnabledAndVisible(
                service != null && service.isReminderActive()
        );
    }
}
