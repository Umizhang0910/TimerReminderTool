package com.umizhang;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class SampleAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // show message
        Messages.showInfoMessage("Hello from Gradle-based Plugin!", "Plugin Demo");
    }
}