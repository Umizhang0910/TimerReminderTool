package com.umizhang.reminder.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.umizhang.reminder.service.ReminderService;
import com.umizhang.reminder.widget.TimerReminderStatusWidget;
import org.jetbrains.annotations.NotNull;

public class DebugWidgetAction extends AnAction {
    private static final Logger log = Logger.getInstance(DebugWidgetAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            // 强制刷新状态栏
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
            if (statusBar != null) {
                statusBar.updateWidget("ReminderTimerWidget");
                log.info("force refresh status bar: {}" + project.getName());
            }

            // 打印服务状态
            ReminderService service = ApplicationManager.getApplication().getService(ReminderService.class);
            log.info("Output service status: " + service.getStatusText());

            // 查找小部件实例
            TimerReminderStatusWidget widget = (TimerReminderStatusWidget)
                    statusBar.getWidget("ReminderTimerWidget");
            log.info("Find widget instance: " + (widget != null ? "Y" : "N"));
        }
    }
}
