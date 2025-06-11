package com.umizhang.reminder.widget;

import com.intellij.ide.DataManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Alarm;
import com.intellij.util.Consumer;
import com.umizhang.reminder.service.ReminderService;
import com.umizhang.reminder.ui.ReminderActionGroup;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Status bar widget
 */
public final class TimerReminderStatusWidget
        implements StatusBarWidget, StatusBarWidget.TextPresentation, Disposable {

    private static final Logger log = Logger.getInstance(TimerReminderStatusWidget.class);
    private final Project project;
    private String text = "No reminder";
    private Alarm alarm;
    private StatusBar statusBar;
    private boolean disposed = false;

    public TimerReminderStatusWidget(Project project) {
        this.project = project;
        log.info("Create widget instance: {}" + project.getName());
    }

    @NotNull
    @Override
    public String ID() {
        return "ReminderTimerWidget";
    }

    @Override
    public @NotNull WidgetPresentation getPresentation() {
        return this;
    }

    @Override
    public float getAlignment() {
        return Component.RIGHT_ALIGNMENT;
    }

    @NotNull
    @Override
    public String getText() {
        return text;
    }

    @Override
    public @NotNull String getTooltipText() {
        return "Manage reminders";
    }

    @Override
    public @NotNull Consumer<MouseEvent> getClickConsumer() {
        return e -> showPopupMenu(e.getComponent());
    }

    private void showPopupMenu(Component component) {
        if (disposed || project.isDisposed() || component == null) {
            log.info(String.format("Unable to display menu：disposed=%s, projectDisposed=%s, componentNull=%s",
                    disposed, project.isDisposed(), component == null));
            return;
        }

        try {
            ReminderActionGroup actionGroup = new ReminderActionGroup(project);

            DataContext dataContext = DataManager.getInstance().getDataContext(component);

            // Create a pop-up menu (without using ActionPlaces)
            ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(
                    "Reminder Actions",
                    actionGroup,
                    dataContext,
                    JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                    true
            );

            // Displayed below the component
            popup.showUnderneathOf(component);

            log.info("Popup menu displayed");
        } catch (Exception e) {
            log.info(String.format("Failed to display popup menu: %s", e.getMessage()));
            Notifications.Bus.notify(
                    new Notification("ReminderGroup", "Popup error",
                            "Failed to show menu: " + e.getMessage(),
                            NotificationType.ERROR)
            );
        }
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        if (disposed) return;

        log.info("Install widget: " + project.getName());
        this.statusBar = statusBar;

        // 1. 初始化定时器
        alarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);

        // 2. 执行首次更新
        ApplicationManager.getApplication().invokeLater(() -> {
            updateFromService();
            statusBar.updateWidget(ID());
            log.info("Widget initial update completed");
        });

        // 3. 启动定时更新循环
        scheduleUpdate();
    }

    private void scheduleUpdate() {
        if (disposed || alarm == null || alarm.isDisposed()) return;

        alarm.cancelAllRequests();
        alarm.addRequest(() -> {
            if (!disposed && !project.isDisposed()) {
                try {
                    updateFromService();
                    if (statusBar != null) {
                        statusBar.updateWidget(ID());
                        log.info("Widget periodic update: {}" + text);
                    }
                } finally {
                    scheduleUpdate();
                }
            }
        }, 1000);
    }

    private void updateFromService() {
        if (project.isDisposed()) return;

        ReminderService service = ApplicationManager.getApplication().getService(ReminderService.class);
        if (service != null) {
            text = service.getStatusText();
        } else {
            text = "No reminder";
        }
    }

    @Override
    public void dispose() {
        log.info("Widget disposal: " + project.getName());

        disposed = true;

        if (alarm != null && !alarm.isDisposed()) {
            alarm.cancelAllRequests();
            Disposer.dispose(alarm);
        }

        statusBar = null;
    }

    public void debugForceUpdate() {
        log.info("Force widget update");
        if (!disposed && statusBar != null) {
            updateFromService();
            statusBar.updateWidget(ID());
        }
    }

    public boolean isDisposed() {
        return disposed;
    }
}