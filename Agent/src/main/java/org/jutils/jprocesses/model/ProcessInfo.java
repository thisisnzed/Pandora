/*
 * Copyright 2016 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jutils.jprocesses.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model that encapsulates process information
 *
 * @author Javier Garcia Alonso
 */
public class ProcessInfo {

    private String pid;
    private String time;
    private String name;
    private String user;
    private String virtualMemory;
    private String physicalMemory;
    private String cpuUsage;
    private String startTime;
    private String priority;
    private String command;
    
    //Used to store system specific data
    private Map<String, String> extraData = new HashMap<String, String>();

    public ProcessInfo() {
    }

    public ProcessInfo(String pid, String time, String name, String user, String virtualMemory, String physicalMemory, String cpuUsage, String startTime, String priority, String command) {
        this.pid = pid;
        this.time = time;
        this.name = name;
        this.user = user;
        this.virtualMemory = virtualMemory;
        this.physicalMemory = physicalMemory;
        this.cpuUsage = cpuUsage;
        this.startTime = startTime;
        this.priority = priority;
        this.command = command;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVirtualMemory() {
        return virtualMemory;
    }

    public void setVirtualMemory(String virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public String getPhysicalMemory() {
        return physicalMemory;
    }

    public void setPhysicalMemory(String physicalMemory) {
        this.physicalMemory = physicalMemory;
    }

    public String getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(String cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Map<String, String> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, String> extraData) {
        this.extraData = extraData;
    }
    
    public void addExtraData(String key, String value) {
        this.extraData.put(key, value);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessInfo that = (ProcessInfo) o;

        if (pid != null ? !pid.equals(that.pid) : that.pid != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
//        if (user != null ? !user.equals(that.user) : that.user != null) return false;
//        if (virtualMemory != null ? !virtualMemory.equals(that.virtualMemory) : that.virtualMemory != null)
//            return false;
//        if (physicalMemory != null ? !physicalMemory.equals(that.physicalMemory) : that.physicalMemory != null)
//            return false;
//        if (cpuUsage != null ? !cpuUsage.equals(that.cpuUsage) : that.cpuUsage != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (priority != null ? !priority.equals(that.priority) : that.priority != null) return false;
        return command != null ? command.equals(that.command) : that.command == null;

    }

    @Override
    public int hashCode() {
        int result = pid != null ? pid.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
//        result = 31 * result + (user != null ? user.hashCode() : 0)   //TODO: return this to equals and hashcode after getProcessesOwner refactoring
//        result = 31 * result + (virtualMemory != null ? virtualMemory.hashCode() : 0);
//        result = 31 * result + (physicalMemory != null ? physicalMemory.hashCode() : 0);
//        result = 31 * result + (cpuUsage != null ? cpuUsage.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        result = 31 * result + (command != null ? command.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PID:" + pid + "	CPU:" + cpuUsage + "	MEM:" + physicalMemory
                + "	PRIORITY:" + priority + "	CMD:" + command;
    }
}
