package com.hamdiwanis.claude;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Shared utility methods for Claude Code plugin
 */
public final class ClaudeCodeUtils {
    private static final String NOTIF_GROUP_ID = "claude.code.notifications";

    private ClaudeCodeUtils() {
        // Utility class - no instantiation
    }

    /**
     * Shell-quote a string for safe execution
     */
    public static String shQuote(String s) {
        if (s == null) return "''";
        return "'" + s.replace("'", "'\\''") + "'";
    }

    /**
     * Expand ~ to user home directory
     */
    public static String expandHome(String p) {
        if (p == null) return null;
        if (p.equals("~")) return System.getProperty("user.home");
        if (p.startsWith("~" + File.separator)) {
            return p.replaceFirst("~", System.getProperty("user.home"));
        }
        return p;
    }

    /**
     * Execute command safely in terminal widget
     */
    public static void exec(Project project, ShellTerminalWidget widget, String cmd) {
        try {
            widget.executeCommand(cmd);
        } catch (IOException e) {
            notify(project, "启动失败：" + e.getMessage(), NotificationType.ERROR);
        }
    }

    /**
     * Show notification to user
     */
    public static void notify(Project project, String msg, NotificationType type) {
        NotificationGroup group = NotificationGroupManager.getInstance().getNotificationGroup(NOTIF_GROUP_ID);
        Notification notification = (group != null)
                ? group.createNotification(msg, type)
                : new Notification(NOTIF_GROUP_ID, "Claude Code", msg, type);
        Notifications.Bus.notify(notification, project);
    }
}