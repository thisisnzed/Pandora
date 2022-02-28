package org.pandora.master.data.agent;

import lombok.Getter;
import lombok.Setter;
import org.pandora.master.data.browser.BrowserData;
import org.pandora.master.data.files.FileData;
import org.pandora.master.data.process.ProcessData;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AgentData {

    @Getter
    @Setter
    private String address, id, mac, os, user, computer, port, mode, lang, version;
    @Getter
    @Setter
    private ConcurrentLinkedQueue<ProcessData> processes;
    @Getter
    @Setter
    private ConcurrentLinkedQueue<BrowserData> browser;
    @Getter
    @Setter
    private ConcurrentLinkedQueue<FileData> files;

    public AgentData(String address, String port, String id, String mode, String mac, String os, String user, String computer, String lang, String version) {
        this.address = address;
        this.port = port;
        this.id = id;
        this.mode = mode;
        this.mac = mac;
        this.os = os;
        this.user = user;
        this.computer = computer;
        this.version = version;
        this.lang = lang.toUpperCase();
        this.processes = new ConcurrentLinkedQueue<>();
        this.browser = new ConcurrentLinkedQueue<>();
        this.files = new ConcurrentLinkedQueue<>();
    }
}