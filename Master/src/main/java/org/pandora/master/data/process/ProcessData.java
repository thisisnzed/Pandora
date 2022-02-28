package org.pandora.master.data.process;

import lombok.Getter;
import lombok.Setter;

public class ProcessData {

    @Getter
    @Setter
    private String pid, name, command;

    public ProcessData(String pid, String name, String command) {
        this.pid = pid;
        this.name = name;
        this.command = command;
    }
}