package org.pandora.master.data.files;

import org.pandora.master.Master;

public class FileManager {

    private final Master master;

    public FileManager(final Master master) {
        this.master = master;
    }

    public void delete(final String agentId, final String location) {
        this.master.agentData.stream().filter(agentData -> agentData.getId().equals(agentId)).forEach(agentData -> agentData.getFiles().forEach(fileData -> {
            if (fileData.getLocation().equals(location))
                agentData.getFiles().remove(fileData);
        }));
    }

    public void deleteAll(final String agentId) {
        this.master.agentData.stream().filter(agentData -> agentData.getId().equals(agentId)).forEach(agentData -> agentData.getFiles().forEach(fileData -> this.delete(agentId, fileData.getLocation())));
    }
}