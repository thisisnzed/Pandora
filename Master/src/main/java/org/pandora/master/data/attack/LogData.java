package org.pandora.master.data.attack;

import lombok.Getter;
import lombok.Setter;

public class LogData {

    @Getter
    @Setter
    private int id, port, time, threads;
    @Getter
    @Setter
    private String address, method;
    @Getter
    @Setter
    private boolean active;

    public LogData(int id, String address, int port, int time, int threads, String method, boolean active) {
        this.id = id;
        this.address = address;
        this.port = port;
        this.time = time;
        this.threads = threads;
        this.method = method;
        this.active = active;
    }
}