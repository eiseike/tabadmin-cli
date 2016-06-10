package net.starschema.tabadmin_cli;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.Closeable;
import java.io.IOException;

class JmxClientHelper implements Closeable {

    private JMXConnector jmxc;
    private JMXServiceURL url;

    JmxClientHelper()  {
        this.jmxc = null;
        this.url = null;
    }

    boolean checkBeanExists(String objectName) throws Exception {
        if (jmxc == null || url == null) {
            throw new Exception("Cannot check Mbean without connection");
        }
        MBeanServerConnection mbsc = getBeans();
        try {
            mbsc.invoke(new ObjectName(objectName), "getPerformanceMetrics", new Object[]{}, new String[]{});
        } catch (InstanceNotFoundException e) {
            return false;
        }
        return true;
    }

    String getActiveSessions(String objectName) throws Exception {
        return getPerformanceMetrics(objectName,"ActiveSessions");
    }

    //return something for the Tableu jmx server's getPerformanceMetrics.
    private String getPerformanceMetrics(String objectName, String variableName) throws Exception {
        MBeanServerConnection mbsc = getBeans();

        CompositeData invoked = (CompositeData)mbsc.invoke(new ObjectName(objectName), "getPerformanceMetrics", new Object[]{}, new String[]{});

        return(invoked.get(variableName).toString());
    }

    public void close() throws IOException {
            this.jmxc.close();
    }

    void connectService(String JMXServiceURL) throws Exception {
            url = new JMXServiceURL(JMXServiceURL);
            jmxc = JMXConnectorFactory.connect(url, null);
    }

    private MBeanServerConnection getBeans() throws Exception {
        return jmxc.getMBeanServerConnection();
    }
}