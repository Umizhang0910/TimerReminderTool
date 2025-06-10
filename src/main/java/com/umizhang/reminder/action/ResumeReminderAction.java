package com.umizhang.reminder.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.umizhang.reminder.service.ReminderService;
import org.jetbrains.annotations.NotNull;

public class ResumeReminderAction extends AnAction {

    public ResumeReminderAction() {
        super("Resume Reminder",
                "Resume the currently pause reminder",
                AllIcons.Actions.Resume);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ReminderService service = ApplicationManager.getApplication().getService(ReminderService.class);
        service.resumeReminder();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        ReminderService service = ApplicationManager.getApplication().getService(ReminderService.class);
        e.getPresentation().setEnabledAndVisible(
                service != null && service.isReminderActive()
        );
    }
}
