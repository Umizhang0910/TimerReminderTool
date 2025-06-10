package com.umizhang.reminder.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.umizhang.reminder.action.PauseReminderAction;
import com.umizhang.reminder.action.ResumeReminderAction;
import com.umizhang.reminder.action.StopReminderAction;
import com.umizhang.reminder.service.ReminderService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReminderActionGroup extends DefaultActionGroup {

    private final Project project;

    public ReminderActionGroup(@NotNull Project project) {
        super("Reminder Actions", true); // 设为弹出菜单
        this.project = project;
    }

    @NotNull
    @Override
    public AnAction[] getChildren(AnActionEvent e) {
        // 从项目获取服务
        ReminderService service = ApplicationManager.getApplication().getService(ReminderService.class);

        // 根据当前状态返回不同的操作项
        if (service == null) {
            /*return new AnAction[]{
                    new SetReminderAction()
            };*/
            return new AnAction[]{
            };
        }

        List<AnAction> actions = new ArrayList<>();

        // 添加通用操作
        // actions.add(new SetReminderAction());

        if (service.isReminderActive()) {
            if (service.isReminderPaused()) {
                actions.add(new ResumeReminderAction());
            } else {
                actions.add(new PauseReminderAction());
            }
            actions.add(new StopReminderAction());
        }

        actions.add(new Separator());

        return actions.toArray(new AnAction[0]);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(true);
        e.getPresentation().setVisible(true);
    }
}
