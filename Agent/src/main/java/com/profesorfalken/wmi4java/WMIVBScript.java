// =============================================== //
// Recompile disabled. Please run Recaf with a JDK //
// =============================================== //

// Decompiled with: FernFlower
// Class Version: 5
package com.profesorfalken.wmi4java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class WMIVBScript implements WMIStub {
    private static final String ROOT_CIMV2 = "root/cimv2";
    private static final String IMPERSONATION_VARIABLE = "Set objWMIService=GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\";
    private static final String CRLF = "\r\n";

    private static String executeScript(String scriptCode) throws WMIException {
        String scriptResponse = "";
        File tmpFile = null;
        FileWriter writer = null;
        BufferedReader errorOutput = null;

        try {
            tmpFile = File.createTempFile("wmi4java" + (new Date()).getTime(), ".vbs");
            writer = new FileWriter(tmpFile);
            writer.write(scriptCode);
            writer.flush();
            writer.close();
            Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/C", "cscript.exe", "/NoLogo", tmpFile.getAbsolutePath()});
            BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while((line = processOutput.readLine()) != null) {
                if (!line.isEmpty()) {
                    scriptResponse = scriptResponse + line + "\r\n";
                }
            }

            if (scriptResponse.isEmpty()) {
                errorOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String errorResponse = "";

                while((line = errorOutput.readLine()) != null) {
                    if (!line.isEmpty()) {
                        errorResponse = errorResponse + line + "\r\n";
                    }
                }

                if (!errorResponse.isEmpty()) {
                    throw new WMIException("WMI operation finished in error: " + errorResponse);
                }
            }
        } catch (Exception var16) {
            throw new WMIException(var16.getMessage(), var16);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }

                if (tmpFile != null) {
                    tmpFile.delete();
                }

                if (errorOutput != null) {
                    errorOutput.close();
                }
            } catch (IOException var15) {
                Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, "Exception closing in finally", var15);
            }

        }

        return scriptResponse.trim();
    }

    public String listClasses(String namespace, String computerName) throws WMIException {
        try {
            StringBuilder scriptCode = new StringBuilder(200);
            String namespaceCommand = "root/cimv2";
            if (!"*".equals(namespace)) {
                namespaceCommand = namespace;
            }

            scriptCode.append("Set objWMIService=GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\").append(computerName).append("/").append(namespaceCommand).append("\")").append("\r\n");
            scriptCode.append("Set colClasses = objWMIService.SubclassesOf()").append("\r\n");
            scriptCode.append("For Each objClass in colClasses").append("\r\n");
            scriptCode.append("For Each objClassQualifier In objClass.Qualifiers_").append("\r\n");
            scriptCode.append("WScript.Echo objClass.Path_.Class").append("\r\n");
            scriptCode.append("Next").append("\r\n");
            scriptCode.append("Next").append("\r\n");
            return executeScript(scriptCode.toString());
        } catch (Exception var5) {
            throw new WMIException(var5.getMessage(), var5);
        }
    }

    public String listProperties(String wmiClass, String namespace, String computerName) throws WMIException {
        try {
            StringBuilder scriptCode = new StringBuilder(200);
            String namespaceCommand = "root/cimv2";
            if (!"*".equals(namespace)) {
                namespaceCommand = namespace;
            }

            scriptCode.append("Set objWMIService=GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\").append(computerName).append("/").append(namespaceCommand).append(":").append(wmiClass).append("\")").append("\r\n");
            scriptCode.append("For Each objClassProperty In objWMIService.Properties_").append("\r\n");
            scriptCode.append("WScript.Echo objClassProperty.Name").append("\r\n");
            scriptCode.append("Next").append("\r\n");
            return executeScript(scriptCode.toString());
        } catch (Exception var6) {
            throw new WMIException(var6.getMessage(), var6);
        }
    }

    public String listObject(String wmiClass, String namespace, String computerName) throws WMIException {
        return this.queryObject(wmiClass, (List)null, (List)null, namespace, computerName);
    }

    public String queryObject(String wmiClass, List<String> wmiProperties, List<String> conditions, String namespace, String computerName) throws WMIException {
        List usedWMIProperties;
        if (wmiProperties != null && !wmiProperties.isEmpty()) {
            usedWMIProperties = wmiProperties;
        } else {
            usedWMIProperties = WMI4Java.get().VBSEngine().computerName(computerName).namespace(namespace).listProperties(wmiClass);
        }

        try {
            StringBuilder scriptCode = new StringBuilder(200);
            String namespaceCommand = "root/cimv2";
            if (!"*".equals(namespace)) {
                namespaceCommand = namespace;
            }

            scriptCode.append("Set objWMIService=GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\").append(computerName).append("/").append(namespaceCommand).append("\")").append("\r\n");
            scriptCode.append("Set colClasses = objWMIService.SubclassesOf()").append("\r\n");
            scriptCode.append("Set wmiQueryData = objWMIService.ExecQuery(\"Select ").append("*").append(" from ").append(wmiClass);
            if (conditions != null && !conditions.isEmpty()) {
                scriptCode.append(" where ").append(WMI4JavaUtil.join(" AND ", conditions));
            }

            scriptCode.append("\")").append("\r\n");
            scriptCode.append("For Each element In wmiQueryData").append("\r\n");
            Iterator var9 = usedWMIProperties.iterator();

            while(var9.hasNext()) {
                String wmiProperty = (String)var9.next();
                if (!wmiProperty.equals("ConfigOptions")) {
                    scriptCode.append("Wscript.Echo \"").append(wmiProperty).append(": \" & ").append("element.").append(wmiProperty).append("\r\n");
                } else {
                    scriptCode.append("Wscript.Echo \"").append(wmiProperty).append(": \" & ").append("Join(element.").append(wmiProperty).append(", \"|\")").append("\r\n");
                }
            }

            scriptCode.append("Next").append("\r\n");
            return executeScript(scriptCode.toString());
        } catch (Exception var11) {
            throw new WMIException(var11.getMessage(), var11);
        }
    }
}
