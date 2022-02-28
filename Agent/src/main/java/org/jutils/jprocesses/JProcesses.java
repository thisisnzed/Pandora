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
package org.jutils.jprocesses;

import org.jutils.jprocesses.info.ProcessesFactory;
import org.jutils.jprocesses.info.ProcessesService;
import org.jutils.jprocesses.model.JProcessesResponse;
import org.jutils.jprocesses.model.ProcessInfo;
import org.jutils.jprocesses.model.WindowsPriority;

import java.util.List;

/**
 * Class that gives access to Processes details.<p>
 * 
 * JProcesses allows to interact with system processes. It is possible to 
 * list all alive processes, kill them or change the priority.
 *
 * @author Javier Garcia Alonso
 */
public class JProcesses {

    //This mode retrieves less information but faster
    private boolean fastMode = false;

    private JProcesses() {
    }

    /**
     * Gets the JProcesses instance.<br>
     * This method creates a new instance each time. 
     *  
     * @return JProcesses instance
     */
    public static JProcesses get() {
        return new JProcesses();
    }

    /**
     * Enables the fast mode. <br>In this mode JProcesses does not try to retrieve 
     * time consuming data as the use percentage or the owner of the process in Windows implementation.<br>
     * <b>This flag has no effect in Linux implementation as it is fast enough</b>.
     * 
     * @return JProcesses instance
     */
    public JProcesses fastMode() {
        this.fastMode = true;
        return this;
    }

    /**
     * Enables/disables the fast mode. <br>In this mode JProcesses does not try to retrieve 
     * time consuming data as the use percentage or the owner of the process in Windows implementation.<br>
     * <b>This flag has no effect in Linux implementation as it is fast enough</b>.
     * 
     * @param enabled boolean true or false
     * @return JProcesses instance
     */
    public JProcesses fastMode(boolean enabled) {
        this.fastMode = enabled;
        return this;
    }

    /**
     * Return the list of processes running in the system.<br>
     * For each process some information is retrieved:
     * <ul>
     * <li>PID</li>
     * <li>Name</li>
     * <li>Used memory</li>
     * <li>Date/time</li>
     * <li>Priority</li>
     * </ul>
     * [...]<p>
     * 
     * For further details see {@link ProcessInfo} 
     * 
     * @return List of processes
     */
    public List<ProcessInfo> listProcesses() {
        return getService().getList(fastMode);
    }

    /**
     * Return the list of processes that match with the provided name.<br>
     * For each process some information is retrieved:
     * <ul>
     * <li>PID</li>
     * <li>Name</li>
     * <li>Used memory</li>
     * <li>Date/time</li>
     * <li>Priority</li>
     * </ul>
     * [...]<p>
     * 
     * For further details see {@link ProcessInfo} 
     * 
     * @param name The name of the searched process
     * @return List of found processes
     */
    public List<ProcessInfo> listProcesses(String name) {
        return getService().getList(name, fastMode);
    }

    /**
     * Convenience static method that returns the list of processes 
     * that match with the provided name.<br>
     * For each process some information is retrieved:
     * <ul>
     * <li>PID</li>
     * <li>Name</li>
     * <li>Used memory</li>
     * <li>Date/time</li>
     * <li>Priority</li>
     * </ul>
     * [...]<p>
     * 
     * For further details see {@link ProcessInfo} 
     * 
     * @return List of found processes
     */
    public static List<ProcessInfo> getProcessList() {
        return getService().getList();
    }

    /**
     * Convenience static method that returns the list of processes 
     * that match with the provided name.<br>
     * For each process some information is retrieved:
     * <ul>
     * <li>PID</li>
     * <li>Name</li>
     * <li>Used memory</li>
     * <li>Date/time</li>
     * <li>Priority</li>
     * </ul>
     * [...]<p>
     * 
     * For further details see {@link ProcessInfo} 
     * 
     * @param name The name of the searched process
     * @return List of found processes
     */
    public static List<ProcessInfo> getProcessList(String name) {
        return getService().getList(name);
    }

    /**
     * Static method that returns the information of a process<br> 
     * Some information is retrieved:
     * <ul>
     * <li>PID</li>
     * <li>Name</li>
     * <li>Used memory</li>
     * <li>Date/time</li>
     * <li>Priority</li>
     * </ul>
     * [...]<p>
     * 
     * For further details see {@link ProcessInfo} 
     * 
     * @param pid The PID of the searched process
     * @return {@link JProcesses} object with the information of the process
     */
    public static ProcessInfo getProcess(int pid) {
        return getService().getProcess(pid);
    }

    /**
     * Static method that kills a process abruptly (forced mode)<br> 
     * 
     * For further details see {@link ProcessInfo} 
     * 
     * @param pid The PID of the process to kill
     * @return {@link JProcessesResponse} response object that contains information about the result of the operation
     */
    public static JProcessesResponse killProcess(int pid) {
        return getService().killProcess(pid);
    }

    /**
     * Static method that kills a process, letting it the necessary time to finish<br> 
     * If the process does not finish with this method, try with the stronger killProcess()<p>
     * 
     * For further details see {@link ProcessInfo} 
     * 
     * @param pid The PID of the process to kill
     * @return {@link JProcessesResponse} response object that contains information about the result of the operation
     */
    public static JProcessesResponse killProcessGracefully(int pid) {
        return getService().killProcessGracefully(pid);
    }

    /**
     * Static method that changes the priority of a process<br> 
     * If the process does not finish with this method, try with the stronger killProcess()<p>
     * 
     * For further details see {@link ProcessInfo} 
     * 
     * @param pid the PID of the process tochange priority
     * @param newPriority integer with the new version. For windows you can use the helper constants at {@link WindowsPriority}
     * @return {@link JProcessesResponse} response object that contains information about the result of the operation
     */
    public static JProcessesResponse changePriority(int pid, int newPriority) {
        return getService().changePriority(pid, newPriority);
    }

    private static ProcessesService getService() {
        return ProcessesFactory.getService();
    }
}
