package org.pandora.master.data.attack;

import org.pandora.master.Master;

import java.util.Optional;

public class LogManager {

    private final Master master;

    public LogManager(final Master master) {
        this.master = master;
    }

    public Optional<LogData> getLog(final int id) {
        return this.master.logData.stream().filter(data -> id == data.getId()).findFirst();
    }

    public LogData getLogData(final int id) {
        return this.master.logData.stream().filter(data -> data.getId() == id).findFirst().orElse(null);
    }

    public void delete(final int id) {
        this.getLog(id).ifPresent(this.master.logData::remove);
    }
}