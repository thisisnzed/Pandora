/*
 * Copyright 2016 Javier Garcia Alonso.
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
package org.jutils.jprocesses.model;

/**
 * Helper constants class to set different priority levels in Windows
 * 
 * @author Javier Garcia Alonso
 */
public class WindowsPriority {       
    
    public static final int IDLE = 64;
    public static final int BELOW_NORMAL = 16384;
    public static final int NORMAL = 32;
    public static final int ABOVE_NORMAL = 32768;
    public static final int HIGH = 128;
    public static final int REAL_TIME = 258;
    
    //Hide constructor
    private WindowsPriority () {}
}
