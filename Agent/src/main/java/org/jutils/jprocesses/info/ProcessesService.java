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

import org.jutils.jprocesses.model.JProcessesResponse;
import org.jutils.jprocesses.model.ProcessInfo;

import java.util.List;

/**
 * Interface for service retrieving processes information
 * 
 * @author Javier Garcia Alonso
 */
public interface ProcessesService {
    List<ProcessInfo> getList();
    List<ProcessInfo> getList(boolean fastMode);
    List<ProcessInfo> getList(String name);
    List<ProcessInfo> getList(String name, boolean fastMode);
    ProcessInfo getProcess(int pid);
    ProcessInfo getProcess(int pid, boolean fastMode);
    JProcessesResponse killProcess(int pid);
    JProcessesResponse killProcessGracefully(int pid);
    JProcessesResponse changePriority(int pid, int priority);
}
