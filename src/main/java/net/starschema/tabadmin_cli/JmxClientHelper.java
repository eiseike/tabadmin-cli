package net.starschema.tabadmin_cli;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

public class JmxClientHelper {

    JMXConnector jmxc;
    JMXServiceURL url;

    public boolean CheckBeanExists(String objectName) throws Exception {
        if (jmxc == null || url == null) {
            throw new Exception("Cannot check Mbean without connection");
        }
        MBeanServerConnection mbsc = GetBeans();
        try {
            mbsc.invoke(new ObjectName(objectName), "getPerformanceMetrics", new Object[]{}, new String[]{});
        } catch (InstanceNotFoundException e) {
            return false;
        }
        return true;
    }

    public JmxClientHelper() throws MalformedObjectNameException {
        this.jmxc = null;
        this.url = null;
    }

    public void Close() throws IOException {
        this.jmxc.close();
    }

    public void ConnectService(String JMXServiceURL) {
        try {
            url = new JMXServiceURL(JMXServiceURL);
            jmxc = JMXConnectorFactory.connect(url, null);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public MBeanServerConnection GetBeans() {
        MBeanServerConnection mbsc = null;
        try {
            mbsc = jmxc.getMBeanServerConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return mbsc;
    }

    //                String domains[] = mbsc.getDomains();
//                Arrays.sort(domains);
//                for (String domain : domains) {
//                    System.out.println("\tDomain = " + domain);
//                }
//
//                System.out.println("\nMBeanServer default domain = " + mbsc.getDefaultDomain());
//
//                System.out.println("\nMBean count = " +  mbsc.getMBeanCount());
//                System.out.println("\nQuery MBeanServer MBeans:");
//                Set<ObjectName> names =
//                        new TreeSet<ObjectName>(mbsc.queryNames(null, null));
//                for (ObjectName name : names) {
//                    System.out.println("\tObjectName = " + name);
//                }
//                //tableau.health.jmx:name=vizqlservice




//    CompositeData szar = (CompositeData)mbsc.invoke(new ObjectName("tableau.health.jmx:name=vizqlservice"), "getPerformanceMetrics", new Object[]{}, new String[]{});
//    System.out.println(szar.get("ActiveSessions").toString());

}