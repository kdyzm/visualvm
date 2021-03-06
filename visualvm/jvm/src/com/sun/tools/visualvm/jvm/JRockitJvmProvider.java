/*
 * Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.tools.visualvm.jvm;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.application.jvm.Jvm;
import com.sun.tools.visualvm.tools.jmx.JmxModel;
import com.sun.tools.visualvm.tools.jmx.JmxModelFactory;
import com.sun.tools.visualvm.tools.jmx.JvmMXBeans;
import com.sun.tools.visualvm.tools.jmx.JvmMXBeansFactory;
import com.sun.tools.visualvm.tools.jvmstat.JvmstatModel;
import com.sun.tools.visualvm.tools.jvmstat.JvmstatModelFactory;
import java.lang.management.RuntimeMXBean;

/**
 *
 * @author Tomas Hurka
 */
public class JRockitJvmProvider extends JvmProvider {
    private static final String JROCKIT_VM_NAME = "BEA JRockit(R)"; // NOI18N
    private static final String VM_NAME = "java.property.java.vm.name"; // NOI18N
    
    @Override
    public Jvm createModelFor(Application app) {
        Jvm jvm = null;
        JvmstatModel jvmstat = JvmstatModelFactory.getJvmstatFor(app);

        if (jvmstat != null) {
            String vmName = jvmstat.findByName(VM_NAME);
            if (JROCKIT_VM_NAME.equals(vmName)) {
                jvm = new JRockitJVMImpl(app, jvmstat);
            }
        } else {
            JmxModel jmxModel = JmxModelFactory.getJmxModelFor(app);
            if (jmxModel != null && jmxModel.getConnectionState() == JmxModel.ConnectionState.CONNECTED) {
                JvmMXBeans mxbeans = JvmMXBeansFactory.getJvmMXBeans(jmxModel);
                if (mxbeans != null) {
                    RuntimeMXBean runtime = mxbeans.getRuntimeMXBean();
                    if (runtime != null && JROCKIT_VM_NAME.equals(runtime.getVmName())) {
                        jvm = new JRockitJVMImpl(app);
                    }
                }
            }
        }
        return jvm;
    }
}
