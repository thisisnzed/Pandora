// =============================================== //
// Recompile disabled. Please run Recaf with a JDK //
// =============================================== //

// Decompiled with: FernFlower
// Class Version: 5
package com.profesorfalken.wmi4java;

public enum WMIClass {

    WIN32_PROCESS("Win32_Process"),
    WIN32_PERFFORMATTEDDATA_PERFPROC_PROCESS("Win32_PerfFormattedData_PerfProc_Process");

    private final String wmiClassName;

    private WMIClass(String wmiClassName) {
        this.wmiClassName = wmiClassName;
    }

    public String getName() {
        return this.wmiClassName;
    }
}
