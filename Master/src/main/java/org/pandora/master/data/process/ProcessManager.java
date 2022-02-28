package org.pandora.master.data.process;

import org.pandora.master.Master;

public class ProcessManager {

    private final Master master;

    public ProcessManager(final Master master) {
        this.master = master;
    }

    public void delete(final String agentId, final String pid) {
        this.master.agentData.stream().filter(agentData -> agentData.getId().equals(agentId)).forEach(agentData -> agentData.getProcesses().forEach(processData -> {
            if (processData.getPid().equals(pid))
                agentData.getProcesses().remove(processData);
        }));
    }
    
    public void deleteAll(final String agentId) {
        this.master.agentData.stream().filter(agentData -> agentData.getId().equals(agentId)).forEach(agentData -> agentData.getProcesses().forEach(processData -> this.delete(agentId, processData.getPid())));
    }
}