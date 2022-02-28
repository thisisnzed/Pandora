package org.pandora.master.data.browser;

import org.pandora.master.Master;

public class BrowserManager {

    private final Master master;

    public BrowserManager(final Master master) {
        this.master = master;
    }

    public void delete(final String agentId, final String id) {
        this.master.agentData.stream().filter(agentData -> agentData.getId().equals(agentId)).forEach(agentData -> agentData.getBrowser().forEach(data -> {
            if (data.getId().equals(id))
                agentData.getBrowser().remove(data);
        }));
    }
    
    public void deleteAll(final String agentId) {
        this.master.agentData.stream().filter(agentData -> agentData.getId().equals(agentId)).forEach(agentData -> agentData.getBrowser().forEach(data -> this.delete(agentId, data.getId())));
    }
}