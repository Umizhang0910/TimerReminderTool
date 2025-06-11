package com.umizhang.reminder.widget;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import org.jetbrains.annotations.NotNull;

public class TimerWidgetFactory implements StatusBarWidgetFactory {

    @Override
    public @NotNull String getId() {
        return "ReminderTimerWidget";
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Reminder Timer";
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true; // 始终可用
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new TimerReminderStatusWidget(project);
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        if (widget instanceof TimerReminderStatusWidget) {
            ((TimerReminderStatusWidget) widget).dispose();
        }
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
