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
package org.jutils.jprocesses.info;

import com.profesorfalken.wmi4java.WMI4Java;
import org.jutils.jprocesses.model.JProcessesResponse;
import org.jutils.jprocesses.model.ProcessInfo;
import org.jutils.jprocesses.util.ProcessesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for Windows
 *
 * @author Javier Garcia Alonso
 */
class WindowsProcessesService extends AbstractProcessesService {
//TODO: This Windows implementation works but it is not optimized by lack of time.
//For example, the filter by name or the search by pid is done retrieving 
//all the processes searching in the returning list.
//Moreover, the information is dispersed and I had to get it from different sources (WMI classes, VBS scripts...)

    private final Map<String, String> userData = new HashMap<String, String>();
    private final Map<String, String> cpuData = new HashMap<String, String>();

    private static final String LINE_BREAK_REGEX = "\\r?\\n";

    private static final Map<String, String> keyMap;

    private Map<String, String> processMap;

    private static final String NAME_PROPNAME = "Name";
    private static final String PROCESSID_PROPNAME = "ProcessId";
    private static final String USERMODETIME_PROPNAME = "UserModeTime";
    private static final String PRIORITY_PROPNAME = "Priority";
    private static final String VIRTUALSIZE_PROPNAME = "VirtualSize";
    private static final String WORKINGSETSIZE_PROPNAME = "WorkingSetSize";
    private static final String COMMANDLINE_PROPNAME = "CommandLine";
    private static final String CREATIONDATE_PROPNAME = "CreationDate";
    private static final String CAPTION_PROPNAME = "Caption";

    private final WMI4Java wmi4Java;

    static {
        Map<String, String> tmpMap = new HashMap<String, String>();
        tmpMap.put(NAME_PROPNAME, "proc_name");
        tmpMap.put(PROCESSID_PROPNAME, "pid");
        tmpMap.put(USERMODETIME_PROPNAME, "proc_time");
        tmpMap.put(PRIORITY_PROPNAME, "priority");
        tmpMap.put(VIRTUALSIZE_PROPNAME, "virtual_memory");
        tmpMap.put(WORKINGSETSIZE_PROPNAME, "physical_memory");
        tmpMap.put(COMMANDLINE_PROPNAME, "command");
        tmpMap.put(CREATIONDATE_PROPNAME, "start_time");

        keyMap = Collections.unmodifiableMap(tmpMap);
    }

    public WindowsProcessesService() {
        this(null);
    }

    WindowsProcessesService(WMI4Java wmi4Java) {
        this.wmi4Java = wmi4Java;
    }

    public WMI4Java getWmi4Java() {
        if (wmi4Java == null) {
            return WMI4Java.get();
        }
        return wmi4Java;
    }

    @Override
    protected List<Map<String, String>> parseList(String rawData) {
        List<Map<String, String>> processesDataList = new ArrayList<Map<String, String>>();

        String[] dataStringLines = rawData.split(LINE_BREAK_REGEX);

        for (final String dataLine : dataStringLines) {
            if (dataLine.trim().length() > 0) {
                processLine(dataLine, processesDataList);
            }
        }

        return processesDataList;
    }

    private void processLine(String dataLine, List<Map<String, String>> processesDataList) {
        if (dataLine.startsWith(CAPTION_PROPNAME)) {
            processMap = new HashMap<String, String>();
            processesDataList.add(processMap);
        }

        if (processMap != null) {
            String[] dataStringInfo = dataLine.split(":", 2);
            processMap.put(normalizeKey(dataStringInfo[0].trim()),
                    normalizeValue(dataStringInfo[0].trim(), dataStringInfo[1].trim()));

            if (PROCESSID_PROPNAME.equals(dataStringInfo[0].trim())) {
                processMap.put("user", userData.get(dataStringInfo[1].trim()));
                processMap.put("cpu_usage", cpuData.get(dataStringInfo[1].trim()));
            }

            if (CREATIONDATE_PROPNAME.equals(dataStringInfo[0].trim())) {
                processMap.put("start_datetime",
                        ProcessesUtils.parseWindowsDateTimeToFullDate(dataStringInfo[1].trim()));
            }
        }
    }

    @Override
    protected String getProcessesData(String name) {
        if (!fastMode) {
            fillExtraProcessData();
        }

        if (name != null) {
            return getWmi4Java().VBSEngine()
                    .properties(Arrays.asList(CAPTION_PROPNAME, PROCESSID_PROPNAME, NAME_PROPNAME,
                                    USERMODETIME_PROPNAME, COMMANDLINE_PROPNAME,
                                    WORKINGSETSIZE_PROPNAME, CREATIONDATE_PROPNAME,
                                    VIRTUALSIZE_PROPNAME, PRIORITY_PROPNAME))
                    .filters(Collections.singletonList("Name like '%" + name + "%'"))
                    .getRawWMIObjectOutput("Win32_Process");
        }

        return getWmi4Java().VBSEngine().getRawWMIObjectOutput("Win32_Process");
    }

    @Override
    protected JProcessesResponse kill(int pid) {
        JProcessesResponse response = new JProcessesResponse();
        if (ProcessesUtils.executeCommandAndGetCode("taskkill", "/PID", String.valueOf(pid), "/F") == 0) {
            response.setSuccess(true);
        }

        return response;
    }

    @Override
    protected JProcessesResponse killGracefully(int pid) {
        JProcessesResponse response = new JProcessesResponse();
        if (ProcessesUtils.executeCommandAndGetCode("taskkill", "/PID", String.valueOf(pid)) == 0) {
            response.setSuccess(true);
        }

        return response;
    }

    private static String normalizeKey(String origKey) {
        return keyMap.get(origKey);
    }

    private static String normalizeValue(String origKey, String origValue) {
        if (USERMODETIME_PROPNAME.equals(origKey)) {
            //100 nano to second - https://msdn.microsoft.com/en-us/library/windows/desktop/aa394372(v=vs.85).aspx
            long seconds = Long.parseLong(origValue) * 100 / 1000000 / 1000;
            return nomalizeTime(seconds);
        }
        if (VIRTUALSIZE_PROPNAME.equals(origKey) || WORKINGSETSIZE_PROPNAME.equals(origKey)) {
            if (!(origValue.isEmpty())) {
                return String.valueOf(Long.parseLong(origValue) / 1024);
            }
        }
        if (CREATIONDATE_PROPNAME.equals(origKey)) {
            return ProcessesUtils.parseWindowsDateTimeToSimpleTime(origValue);
        }

        return origValue;
    }

    private static String nomalizeTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void fillExtraProcessData() {
        String perfData = getWmi4Java().VBSEngine().getRawWMIObjectOutput("Win32_PerfFormattedData_PerfProc_Process");

        String[] dataStringLines = perfData.split(LINE_BREAK_REGEX);
        String pid = null;
        String cpuUsage = null;
        for (final String dataLine : dataStringLines) {

            if (dataLine.trim().length() > 0) {
                if (dataLine.startsWith(CAPTION_PROPNAME)) {
                    if (pid != null && cpuUsage != null) {
                        cpuData.put(pid, cpuUsage);
                        pid = null;
                        cpuUsage = null;
                    }
                    continue;
                }

                if (pid == null) {
                    pid = checkAndGetDataInLine("IDProcess", dataLine);
                }
                if (cpuUsage == null) {
                    cpuUsage = checkAndGetDataInLine("PercentProcessorTime", dataLine);
                }
            }
        }

        String processesData = VBScriptHelper.getProcessesOwner();

        if (processesData != null) {
            dataStringLines = processesData.split(LINE_BREAK_REGEX);
            for (final String dataLine : dataStringLines) {
                String[] dataStringInfo = dataLine.split(":", 2);
                if (dataStringInfo.length == 2) {
                    userData.put(dataStringInfo[0].trim(), dataStringInfo[1].trim());
                }
            }
        }
    }

    private static String checkAndGetDataInLine(String dataName, String dataLine) {
        if (dataLine.startsWith(dataName)) {
            String[] dataStringInfo = dataLine.split(":");
            if (dataStringInfo.length == 2) {
                return dataStringInfo[1].trim();
            }
        }
        return null;
    }

    public JProcessesResponse changePriority(int pid, int priority) {
        JProcessesResponse response = new JProcessesResponse();
        String message = VBScriptHelper.changePriority(pid, priority);
        if (message == null || message.length() == 0) {
            response.setSuccess(true);
        } else {
            response.setMessage(message);
        }
        return response;
    }

    public ProcessInfo getProcess(int pid) {
        return getProcess(pid, false);
    }

    public ProcessInfo getProcess(int pid, boolean fastMode) {
        this.fastMode = fastMode;
        List<Map<String, String>> allProcesses = parseList(getProcessesData(null));

        for (final Map<String, String> process : allProcesses) {
            if (String.valueOf(pid).equals(process.get("pid"))) {
                ProcessInfo info = new ProcessInfo();
                info.setPid(process.get("pid"));
                info.setName(process.get("proc_name"));
                info.setTime(process.get("proc_time"));
                info.setCommand(process.get("command"));
                info.setCpuUsage(process.get("cpu_usage"));
                info.setPhysicalMemory(process.get("physical_memory"));
                info.setStartTime(process.get("start_time"));
                info.setUser(process.get("user"));
                info.setVirtualMemory(process.get("virtual_memory"));
                info.setPriority(process.get("priority"));

                return info;
            }
        }
        return null;
    }
}
