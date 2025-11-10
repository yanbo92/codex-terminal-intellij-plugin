package com.yanbo92.codex;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalView;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CodexCodeToolWindowFactory implements ToolWindowFactory {
    public static final String TOOL_WINDOW_ID = "Codex Code";
    public static final com.intellij.openapi.util.Key<ShellTerminalWidget> WIDGET_KEY =
            com.intellij.openapi.util.Key.create("CODEX_CODE_WIDGET");
    private static final com.intellij.openapi.util.Key<Boolean> AUTORUN_DONE_KEY =
            com.intellij.openapi.util.Key.create("CODEX_CODE_AUTORUN_DONE");

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel(new BorderLayout());
        String workDir = project.getBasePath() != null ? project.getBasePath() : System.getProperty("user.home");

        // 仍用 TerminalView 创建 widget（最兼容），但随后马上隐藏底部 Terminal 工具窗
        ShellTerminalWidget widget = TerminalView.getInstance(project)
                .createLocalShellWidget(workDir, TOOL_WINDOW_ID);

        panel.add(widget.getComponent(), BorderLayout.CENTER);

        Content content = ContentFactory.getInstance().createContent(panel, "", false);
        content.putUserData(WIDGET_KEY, widget);
        toolWindow.getContentManager().addContent(content);

        // 关键：把底部 Terminal 工具窗收起，避免用户看到两个终端
        ToolWindow term = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        if (term != null && term.isVisible()) {
            term.hide(null);
        }

        if (Boolean.TRUE.equals(content.getUserData(AUTORUN_DONE_KEY))) return;
        content.putUserData(AUTORUN_DONE_KEY, true);

        ApplicationManager.getApplication().invokeLater(() -> autorun(project, widget, workDir));
    }

    private void autorun(Project project, ShellTerminalWidget widget, String workDir) {
        CodexCodeUtils.exec(project, widget, "codex");
    }
}

