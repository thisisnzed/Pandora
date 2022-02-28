package org.pandora.agent.control.impl;

import org.pandora.agent.Client;
import org.pandora.agent.utils.NumberUtils;
import org.jutils.jprocesses.JProcesses;

import java.util.stream.Collectors;

public class ProcessController {

    private final Client client;

    public ProcessController(final Client client) {
        this.client = client;
    }

    public void list(final String requestId) {
        final String result = JProcesses.getProcessList().stream().map(processInfo -> processInfo.getPid().replace("#", "(hashtag)").replace(":", "(doubleDot)") + ":" + processInfo.getName().replace("#", "(hashtag)").replace(":", "(doubleDot)") + ":" + (processInfo.getCommand().equals("") ? "Not found" : processInfo.getCommand().replace("#", "(hashtag)").replace(":", "(doubleDot)")) + "#").collect(Collectors.joining());
        this.client.getSocketUtils().write("processManager:list:" + requestId + ":" + result);
    }

    public void close(final String pid) {
        if (!NumberUtils.isInteger(pid)) return;
        JProcesses.killProcess(Integer.parseInt(pid));
    }
}