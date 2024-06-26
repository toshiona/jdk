/*
 * Copyright (c) 2003, 2024, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Provides the management interfaces for monitoring and management of the
 * Java virtual machine and other components in the Java runtime.
 * It allows both local and remote
 * monitoring and management of the running Java virtual machine.
 *
 * <h2><a id="MXBean">Platform MXBean</a></h2>
 * <p>
 * A platform MXBean is a <i>managed bean</i> that
 * conforms to the {@linkplain javax.management JMX}
 * Instrumentation Specification and only uses a set of basic data types.
 * Each platform MXBean is a {@link java.lang.management.PlatformManagedObject}
 * with a unique
 * {@linkplain java.lang.management.PlatformManagedObject#getObjectName name}.
 * <h2>ManagementFactory</h2>
 *
 * <p>The {@link java.lang.management.ManagementFactory} class is the management
 * factory class for the Java platform.  This class provides a set of
 * static factory methods to obtain the MXBeans for the Java platform
 * to allow an application to access the MXBeans directly.
 *
 * <p>A <em>platform MBeanServer</em> can be accessed with the
 * {@link java.lang.management.ManagementFactory#getPlatformMBeanServer
 * getPlatformMBeanServer} method.  On the first call to this method,
 * it creates the platform MBeanServer and registers all platform MXBeans
 * including {@linkplain java.lang.management.PlatformManagedObject
 * platform MXBeans}.
 * Each platform MXBean is registered with a unique name defined in
 * the specification of the management interface.
 * This is a single MBeanServer that can be shared by different managed
 * components running within the same Java virtual machine.
 *
 * <h2>Interoperability</h2>
 *
 * <p>A management application and a platform MBeanServer of a running
 * virtual machine can interoperate
 * without requiring classes used by the platform MXBean interfaces.
 * The data types being transmitted between the JMX connector
 * server and the connector client are JMX
 * {@linkplain javax.management.openmbean.OpenType open types} and
 * this allows interoperation across versions.
 * A data type used by the MXBean interfaces are mapped to an
 * open type when being accessed via MBeanServer interface.
 * See the <a href="{@docRoot}/java.management/javax/management/MXBean.html#MXBean-spec">
 * MXBean</a> specification for details.
 *
 * <h2><a id="examples">Ways to Access MXBeans</a></h2>
 *
 * <p>An application can monitor the instrumentation of the
 * Java virtual machine and the runtime in the following ways:
 * <p>
 * <b>1. Direct access to an MXBean interface</b>
 * <ul>
 * <li>Get an MXBean instance locally in the running Java virtual machine:
 * <pre>
 *    RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
 *
 *    // Get the standard attribute "VmVendor"
 *    String vendor = mxbean.getVmVendor();
 * </pre>
 * <p>Or by calling the
 *         {@link java.lang.management.ManagementFactory#getPlatformMXBean(Class)
 *                getPlatformMXBean} or
 *         {@link java.lang.management.ManagementFactory#getPlatformMXBeans(Class)
 *                getPlatformMXBeans} method:
 * <pre>
 *    RuntimeMXBean mxbean = ManagementFactory.getPlatformMXBean(RuntimeMXBean.class);
 *
 *    // Get the standard attribute "VmVendor"
 *    String vendor = mxbean.getVmVendor();
 * </pre>
 * </li>
 * <li>Construct an MXBean proxy instance that forwards the
 *     method calls to a given MBeanServer:
 * <pre>
 *    MBeanServerConnection mbs;
 *
 *    // Connect to a running JVM (or itself) and get MBeanServerConnection
 *    // that has the JVM MBeans registered in it
 *    ...
 *
 *    // Get a MBean proxy for RuntimeMXBean interface
 *    RuntimeMXBean proxy =
 *        {@link java.lang.management.ManagementFactory#getPlatformMXBean(MBeanServerConnection, Class)
 *        ManagementFactory.getPlatformMXBean}(mbs,
 *                                            RuntimeMXBean.class);
 *    // Get standard attribute "VmVendor"
 *    String vendor = proxy.getVmVendor();
 * </pre>
 * <p>A proxy is typically used to access an MXBean
 *    in a remote Java virtual machine.
 *    An alternative way to create an MXBean proxy is:
 * <pre>
 *    RuntimeMXBean proxy =
 *        {@link java.lang.management.ManagementFactory#newPlatformMXBeanProxy
 *               ManagementFactory.newPlatformMXBeanProxy}(mbs,
 *                                                 ManagementFactory.RUNTIME_MXBEAN_NAME,
 *                                                 RuntimeMXBean.class);
 * </pre>
 * </li>
 * </ul>
 * <p>
 * <b>2. Indirect access to an MXBean interface via MBeanServer</b>
 * <ul>
 * <li>Go through the
 *     {@link java.lang.management.ManagementFactory#getPlatformMBeanServer
 *     platform MBeanServer} to access MXBeans locally or
 *     a specific {@code MBeanServerConnection} to access
 *     MXBeans remotely.
 *     The attributes and operations of an MXBean use only
 *     <em>JMX open types</em> which include basic data types,
 *     {@link javax.management.openmbean.CompositeData CompositeData},
 *     and {@link javax.management.openmbean.TabularData TabularData}
 *     defined in {@link javax.management.openmbean.OpenType OpenType}.
 * <pre>
 *    MBeanServerConnection mbs;
 *
 *    // Connect to a running JVM (or itself) and get MBeanServerConnection
 *    // that has the JVM MXBeans registered in it
 *    ...
 *
 *    try {
 *        // Assuming the RuntimeMXBean has been registered in mbs
 *        ObjectName oname = new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME);
 *
 *        // Get standard attribute "VmVendor"
 *        String vendor = (String) mbs.getAttribute(oname, "VmVendor");
 *    } catch (....) {
 *        // Catch the exceptions thrown by ObjectName constructor
 *        // and MBeanServer.getAttribute method
 *        ...
 *    }
 * </pre>
 * </li>
 * </ul>
 *
 *
 * <h2><a id="extension">Platform Extension</a></h2>
 *
 * <p>A Java virtual machine implementation may add its platform extension to
 * the management interface by defining platform-dependent
 * interfaces that extend the standard management interfaces to include
 * platform-specific metrics and management operations.
 * The static factory methods in the <code>ManagementFactory</code> class will
 * return the MXBeans with the platform extension.
 *
 * <p>
 * It is recommended to name the platform-specific attributes with
 * a vendor-specific prefix such as the vendor's name to
 * avoid collisions of the attribute name between the future extension
 * to the standard management interface and the platform extension.
 * If the future extension to the standard management interface defines
 * a new attribute for a management interface and the attribute name
 * is happened to be same as some vendor-specific attribute's name,
 * the applications accessing that vendor-specific attribute would have
 * to be modified to cope with versioning and compatibility issues.
 *
 * <p>Below is an example showing how to access an attribute
 * from the platform extension:
 *
 * <p>
 * 1) Direct access to the Oracle-specific MXBean interface
 * <blockquote>
 * <pre>
 *    List&lt;com.sun.management.GarbageCollectorMXBean&gt; mxbeans =
 *        ManagementFactory.getPlatformMXBeans(com.sun.management.GarbageCollectorMXBean.class);
 *
 *    for (com.sun.management.GarbageCollectorMXBean gc : mxbeans) {
 *        // Get the standard attribute "CollectionCount"
 *        String count = mxbean.getCollectionCount();
 *
 *        // Get the platform-specific attribute "LastGcInfo"
 *        GcInfo gcinfo = gc.getLastGcInfo();
 *        ...
 *    }
 * </pre>
 * </blockquote>
 *
 * <p>
 * 2) Access the Oracle-specific MXBean interface via <code>MBeanServer</code>
 *    through proxy
 *
 * <blockquote><pre>
 *    MBeanServerConnection mbs;
 *
 *    // Connect to a running JVM (or itself) and get MBeanServerConnection
 *    // that has the JVM MXBeans registered in it
 *    ...
 *
 *    List&lt;com.sun.management.GarbageCollectorMXBean&gt; mxbeans =
 *        ManagementFactory.getPlatformMXBeans(mbs, com.sun.management.GarbageCollectorMXBean.class);
 *
 *    for (com.sun.management.GarbageCollectorMXBean gc : mxbeans) {
 *        // Get the standard attribute "CollectionCount"
 *        String count = mxbean.getCollectionCount();
 *
 *        // Get the platform-specific attribute "LastGcInfo"
 *        GcInfo gcinfo = gc.getLastGcInfo();
 *        ...
 *    }
 * </pre></blockquote>
 *
 * <p> Unless otherwise noted, passing a <code>null</code> argument to a constructor
 * or method in any class or interface in this package will cause a {@link
 * java.lang.NullPointerException NullPointerException} to be thrown.
 *
 * <p> The java.lang.management API is thread-safe.
 *
 * @see javax.management JMX Specification
 *
 * @author Mandy Chung
 * @since 1.5
 */
package java.lang.management;
