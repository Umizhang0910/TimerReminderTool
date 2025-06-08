package com.umizhang.core;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public final class ThreadPoolScheduler implements Disposable {
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    @Override
    public void dispose() {
        // Close the thread pool when the plugin is unloaded
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
