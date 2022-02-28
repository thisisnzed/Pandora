// =============================================== //
// Recompile disabled. Please run Recaf with a JDK //
// =============================================== //

// Decompiled with: FernFlower
// Class Version: 5
package com.profesorfalken.wmi4java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WMI4Java {
    private static final String NEWLINE_REGEX = "\\r?\\n";
    private static final String SPACE_REGEX = "\\s+";
    private static final String GENERIC_ERROR_MSG = "Error calling WMI4Java";
    private String namespace = "*";
    private String computerName = ".";
    private boolean forceVBEngine = false;
    List<String> properties = null;
    List<String> filters = null;

    private WMI4Java() {
    }

    private WMIStub getWMIStub() {
        return (WMIStub)(this.forceVBEngine ? new WMIVBScript() : new WMIPowerShell());
    }

    public static WMI4Java get() {
        return new WMI4Java();
    }

    public WMI4Java namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public WMI4Java computerName(String computerName) {
        this.computerName = computerName;
        return this;
    }

    public WMI4Java PowerShellEngine() {
        this.forceVBEngine = false;
        return this;
    }

    public WMI4Java VBSEngine() {
        this.forceVBEngine = true;
        return this;
    }

    public WMI4Java properties(List<String> properties) {
        this.properties = properties;
        return this;
    }

    public WMI4Java filters(List<String> filters) {
        this.filters = filters;
        return this;
    }

    public List<String> listClasses() throws WMIException {
        ArrayList wmiClasses = new ArrayList();

        try {
            String rawData = this.getWMIStub().listClasses(this.namespace, this.computerName);
            String[] dataStringLines = rawData.split("\\r?\\n");
            String[] var4 = dataStringLines;
            int var5 = dataStringLines.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String line = var4[var6];
                if (!line.isEmpty() && !line.startsWith("_")) {
                    String[] infos = line.split("\\s+");
                    wmiClasses.addAll(Arrays.asList(infos));
                }
            }

            Set<String> hs = new HashSet();
            hs.addAll(wmiClasses);
            wmiClasses.clear();
            wmiClasses.addAll(hs);
            return wmiClasses;
        } catch (Exception var9) {
            Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, "Error calling WMI4Java", var9);
            throw new WMIException(var9);
        }
    }

    public List<String> listProperties(String wmiClass) throws WMIException {
        ArrayList foundPropertiesList = new ArrayList();

        try {
            String rawData = this.getWMIStub().listProperties(wmiClass, this.namespace, this.computerName);
            String[] dataStringLines = rawData.split("\\r?\\n");
            String[] var5 = dataStringLines;
            int var6 = dataStringLines.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String line = var5[var7];
                if (!line.isEmpty()) {
                    foundPropertiesList.add(line.trim());
                }
            }

            List<String> notAllowed = Arrays.asList("Equals", "GetHashCode", "GetType", "ToString");
            foundPropertiesList.removeAll(notAllowed);
            return foundPropertiesList;
        } catch (Exception var9) {
            Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, "Error calling WMI4Java", var9);
            throw new WMIException(var9);
        }
    }

    public Map<String, String> getWMIObject(WMIClass wmiClass) {
        return this.getWMIObject(wmiClass.getName());
    }

    public Map<String, String> getWMIObject(String wmiClass) throws WMIException {
        HashMap foundWMIClassProperties = new HashMap();

        try {
            String rawData;
            if (this.properties == null && this.filters == null) {
                rawData = this.getWMIStub().listObject(wmiClass, this.namespace, this.computerName);
            } else {
                rawData = this.getWMIStub().queryObject(wmiClass, this.properties, this.filters, this.namespace, this.computerName);
            }

            String[] dataStringLines = rawData.split("\\r?\\n");
            String[] var5 = dataStringLines;
            int var6 = dataStringLines.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String line = var5[var7];
                if (!line.isEmpty()) {
                    String[] entry = line.split(":");
                    if (entry != null && entry.length == 2) {
                        foundWMIClassProperties.put(entry[0].trim(), entry[1].trim());
                    }
                }
            }

            return foundWMIClassProperties;
        } catch (WMIException var10) {
            Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, "Error calling WMI4Java", var10);
            throw new WMIException(var10);
        }
    }

    public List<Map<String, String>> getWMIObjectList(WMIClass wmiClass) {
        return this.getWMIObjectList(wmiClass.getName());
    }

    public List<Map<String, String>> getWMIObjectList(String wmiClass) throws WMIException {
        ArrayList foundWMIClassProperties = new ArrayList();

        try {
            String rawData;
            if (this.properties == null && this.filters == null) {
                rawData = this.getWMIStub().listObject(wmiClass, this.namespace, this.computerName);
            } else {
                rawData = this.getWMIStub().queryObject(wmiClass, this.properties, this.filters, this.namespace, this.computerName);
            }

            String[] dataStringObjects = rawData.split("\\r?\\n\\r?\\n");
            String[] var5 = dataStringObjects;
            int var6 = dataStringObjects.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String dataStringObject = var5[var7];
                String[] dataStringLines = dataStringObject.split("\\r?\\n");
                Map<String, String> objectProperties = new HashMap();
                String[] var11 = dataStringLines;
                int var12 = dataStringLines.length;

                for(int var13 = 0; var13 < var12; ++var13) {
                    String line = var11[var13];
                    if (!line.isEmpty()) {
                        String[] entry = line.split(":");
                        if (entry.length == 2) {
                            objectProperties.put(entry[0].trim(), entry[1].trim());
                        }
                    }
                }

                foundWMIClassProperties.add(objectProperties);
            }

            return foundWMIClassProperties;
        } catch (WMIException var16) {
            Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, "Error calling WMI4Java", var16);
            throw new WMIException(var16);
        }
    }

    public String getRawWMIObjectOutput(WMIClass wmiClass) {
        return this.getRawWMIObjectOutput(wmiClass.getName());
    }

    public String getRawWMIObjectOutput(String wmiClass) throws WMIException {
        try {
            String rawData;
            if (this.properties == null && this.filters == null) {
                rawData = this.getWMIStub().listObject(wmiClass, this.namespace, this.computerName);
            } else {
                rawData = this.getWMIStub().queryObject(wmiClass, this.properties, this.filters, this.namespace, this.computerName);
            }

            return rawData;
        } catch (WMIException var4) {
            Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, "Error calling WMI4Java", var4);
            throw new WMIException(var4);
        }
    }
}
