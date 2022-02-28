/*
 * Copyright 2016 Javier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jutils.jprocesses.info;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class of windows implementation to create and execute VBS scripts
 *
 * @author Javier Garcia Alonso
 */
class VBScriptHelper {

    private static final String CRLF = "\r\n";

    //Hide constructor
    private VBScriptHelper() {
    }

    private static String executeScript(String scriptCode) {
        String scriptResponse = "";
        File tmpFile = null;
        FileWriter writer = null;
        BufferedReader processOutput = null;
        BufferedReader errorOutput = null;

        try {
            tmpFile = File.createTempFile("wmi4java" + new Date().getTime(), ".vbs");
            writer = new FileWriter(tmpFile);
            writer.write(scriptCode);
            writer.flush();
            writer.close();

            Process process = Runtime.getRuntime().exec(
                    new String[]{"cmd.exe", "/C", "cscript.exe", "/NoLogo", tmpFile.getAbsolutePath()});
            processOutput
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = processOutput.readLine()) != null) {
                if (!line.isEmpty()) {
                    scriptResponse += line + CRLF;
                }
            }

            if (scriptResponse.isEmpty()) {
                errorOutput
                        = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String errorResponse = "";
                while ((line = errorOutput.readLine()) != null) {
                    if (!line.isEmpty()) {
                        errorResponse += line + CRLF;
                    }
                }
                if (!errorResponse.isEmpty()) {
                    Logger.getLogger(VBScriptHelper.class.getName()).log(Level.SEVERE, "WMI operation finished in error: ");
                }
                errorOutput.close();
            }

        } catch (Exception ex) {
            Logger.getLogger(VBScriptHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (processOutput != null) {
                    processOutput.close();
                }
                if (errorOutput != null) {
                    errorOutput.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (tmpFile != null) {
                    tmpFile.delete();
                }
            } catch (IOException ioe) {
                Logger.getLogger(VBScriptHelper.class.getName()).log(Level.SEVERE, null, ioe);
            }
        }
        return scriptResponse.trim();
    }

    /**
     * Retrieve the owner of the processes.
     *
     * @return list of strings composed by PID and owner, separated by :
     */
    public static String getProcessesOwner() {

        try {
            StringBuilder scriptCode = new StringBuilder(200);

            scriptCode.append("Set objWMIService=GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\")
                    .append(".").append("/").append("root/cimv2").append("\")").append(CRLF);

            scriptCode.append("Set colProcessList = objWMIService.ExecQuery(\"Select * from Win32_Process\")").append(CRLF);

            scriptCode.append("For Each objProcess in colProcessList").append(CRLF);
            scriptCode.append("colProperties = objProcess.GetOwner(strNameOfUser,strUserDomain)").append(CRLF);
            scriptCode.append("Wscript.Echo objProcess.ProcessId & \":\" & strNameOfUser").append(CRLF);
            scriptCode.append("Next").append(CRLF);

            return executeScript(scriptCode.toString());
        } catch (Exception ex) {
            Logger.getLogger(VBScriptHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Changes the priority of a process
     *
     * @param pid the PID
     * @param newPriority the priority to set @see Windo
     * @return the output of the script
     */
    public static String changePriority(int pid, int newPriority) {

        try {
            StringBuilder scriptCode = new StringBuilder(200);

            scriptCode.append("Set objWMIService=GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\")
                    .append(".").append("/").append("root/cimv2").append("\")").append(CRLF);

            scriptCode.append("Set colProcesses = objWMIService.ExecQuery(\"Select * from Win32_Process Where ProcessId = ")
                    .append(pid).append("\")").append(CRLF);

            scriptCode.append("For Each objProcess in colProcesses").append(CRLF);
            scriptCode.append("objProcess.SetPriority(").append(newPriority).append(")").append(CRLF);
            scriptCode.append("Next").append(CRLF);

            return executeScript(scriptCode.toString());
        } catch (Exception ex) {
            Logger.getLogger(VBScriptHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
