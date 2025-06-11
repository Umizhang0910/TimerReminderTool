package com.umizhang.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Persistent state component
 */
@Service
@State(name = "ReminderSettings", storages = @Storage("reminderSettings.xml"))
public final class ReminderSettings implements PersistentStateComponent<ReminderSettings.State> {

    private final State state = new State();

    public static ReminderSettings getInstance() {
        return ApplicationManager.getApplication().getService(ReminderSettings.class);
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }

    public static class State {
        public long initialDelay = 0; // Default to no initial delay
        public long intervalMinutes = 60; // Default to 1 hour
        public boolean isRepeating = true; // Default to repeating reminder
        public String defaultMessage = "";
    }
}
