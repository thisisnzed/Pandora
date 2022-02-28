package org.pandora.master.data.agent;

import org.pandora.master.Master;

import java.util.Optional;

public class AgentManager {

    private final Master master;

    public AgentManager(final Master master) {
        this.master = master;
    }

    public Optional<AgentData> getAgent(final String id) {
        return this.master.agentData.stream().filter(data -> id.equals(data.getId())).findFirst();
    }

    public AgentData getAgentData(final String id) {
        return this.master.agentData.stream().filter(data -> data.getId().equals(id)).findFirst().orElse(null);
    }

    public AgentData getAgentData(final String address, final String port) {
        return this.master.agentData.stream().filter(data -> data.getAddress().equals(address) && data.getPort().equals(port)).findFirst().orElse(null);
    }

    public void delete(final String id) {
        this.getAgent(id).ifPresent(this.master.agentData::remove);
    }
}