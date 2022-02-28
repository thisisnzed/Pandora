// =============================================== //
// Recompile disabled. Please run Recaf with a JDK //
// =============================================== //

// Decompiled with: FernFlower
// Class Version: 5
package com.profesorfalken.wmi4java;

import java.util.List;

interface WMIStub {
    String listClasses(String var1, String var2) throws WMIException;

    String listObject(String var1, String var2, String var3) throws WMIException;

    String queryObject(String var1, List<String> var2, List<String> var3, String var4, String var5) throws WMIException;

    String listProperties(String var1, String var2, String var3) throws WMIException;
}
 