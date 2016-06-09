package net.starschema.tabadmin_cli;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;

public class JmxClientHelper {

    JMXConnector jmxc;
    JMXServiceURL url;

    public JmxClientHelper() throws MalformedObjectNameException {
        this.jmxc = null;
        this.url = null;
    }

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


    public String getActiveSessions() throws Exception {
        return getPerformanceMetrics("tableau.health.jmx:name=vizqlservice","ActiveSessions");
    }

    //return something for the Tableu jmx server's getPerformanceMetrics.
    private String getPerformanceMetrics(String objectName, String variableName) {
        MBeanServerConnection mbsc = GetBeans();
        CompositeData invoked = null;
        try {
            invoked = (CompositeData)mbsc.invoke(new ObjectName(objectName), "getPerformanceMetrics", new Object[]{}, new String[]{});
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        return(invoked.get(variableName).toString());
    }

    public void Close()  {
        try {
            this.jmxc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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