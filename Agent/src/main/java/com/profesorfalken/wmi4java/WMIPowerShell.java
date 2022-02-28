// =============================================== //
// Recompile disabled. Please run Recaf with a JDK //
// =============================================== //

// Decompiled with: FernFlower
// Class Version: 5
package com.profesorfalken.wmi4java;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class WMIPowerShell implements WMIStub {
    private static final String NAMESPACE_PARAM = "-Namespace ";
    private static final String COMPUTERNAME_PARAM = "-ComputerName ";
    private static final String GETWMIOBJECT_COMMAND = "Get-WMIObject ";

    private static String executeCommand(String command) throws WMIException {
        String commandResponse = null;
        PowerShell powerShell = null;

        try {
            powerShell = PowerShell.openSession();
            Map<String, String> config = new HashMap();
            config.put("maxWait", "20000");
            PowerShellResponse psResponse = powerShell.configuration(config).executeCommand(command);
            if (psResponse.isError()) {
                throw new WMIException("WMI operation finished in error: " + psResponse.getCommandOutput());
            }

            commandResponse = psResponse.getCommandOutput().trim();
            powerShell.close();
        } catch (PowerShellNotAvailableException var8) {
            throw new WMIException(var8.getMessage(), var8);
        } finally {
            if (powerShell != null) {
                powerShell.close();
            }

        }

        return commandResponse;
    }

    public String listClasses(String namespace, String computerName) throws WMIException {
        String namespaceString = "";
        if (!"*".equals(namespace)) {
            namespaceString = namespaceString + "-Namespace " + namespace;
        }

        return executeCommand("Get-WMIObject " + namespaceString + " -List | Sort Name");
    }

    public String listProperties(String wmiClass, String namespace, String computerName) throws WMIException {
        String command = this.initCommand(wmiClass, namespace, computerName);
        command = command + " | ";
        command = command + "Select-Object * -excludeproperty \"_*\" | ";
        command = command + "Get-Member | select name | format-table -hidetableheader";
        return executeCommand(command);
    }

    public String listObject(String wmiClass, String namespace, String computerName) throws WMIException {
        String command = this.initCommand(wmiClass, namespace, computerName);
        command = command + " | ";
        command = command + "Select-Object * -excludeproperty \"_*\" | ";
        command = command + "Format-List *";
        return executeCommand(command);
    }

    public String queryObject(String wmiClass, List<String> wmiProperties, List<String> conditions, String namespace, String computerName) throws WMIException {
        String command = this.initCommand(wmiClass, namespace, computerName);
        List usedWMIProperties;
        if (wmiProperties != null && !wmiProperties.isEmpty()) {
            usedWMIProperties = wmiProperties;
        } else {
            usedWMIProperties = Collections.singletonList("*");
        }

        command = command + " | ";
        command = command + "Select-Object " + WMI4JavaUtil.join(", ", usedWMIProperties) + " -excludeproperty \"_*\" | ";
        String condition;
        if (conditions != null && !conditions.isEmpty()) {
            for(Iterator var8 = conditions.iterator(); var8.hasNext(); command = command + "Where-Object -FilterScript {" + condition + "} | ") {
                condition = (String)var8.next();
            }
        }

        command = command + "Format-List *";
        return executeCommand(command);
    }

    private String initCommand(String wmiClass, String namespace, String computerName) {
        String command = "Get-WMIObject " + wmiClass + " ";
        if (!"*".equals(namespace)) {
            command = command + "-Namespace " + namespace + " ";
        }

        if (!computerName.isEmpty()) {
            command = command + "-ComputerName " + computerName + " ";
        }

        return command;
    }
}
 