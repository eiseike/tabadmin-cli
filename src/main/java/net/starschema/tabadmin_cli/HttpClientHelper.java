package net.starschema.tabadmin_cli;

import org.apache.http.client.fluent.Request;

import javax.management.MalformedObjectNameException;
import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by balazsa on 2016.06.06..
 */
public class HttpClientHelper {

    //get targetURL's HTML
    public static String getPage(String targetURL) throws IOException {
        return Request.Get(targetURL).execute().returnContent().toString();
    }

    static List<VizqlserverWorker> getworkersFromHtml(String body, String clusterName) throws Exception {

        String regex;
        Pattern p;
        String nonce="";

        //search for the cluster's nonce string
        regex = "<h3>.*&nonce=([^\"]+)\">balancer://" + clusterName + "</a>.*";
        p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        String[] bodySlpit = body.split("\n");
        for (String s : bodySlpit) {
            Matcher m = p.matcher(s);
            if (m.matches()) {
                nonce = m.group(1);

                break;
            }
        }

        if (nonce=="") {
            throw new Exception("Cannot found the vizqlserver load balancer in balancer-manager");
        }

        List<VizqlserverWorker> workers = new ArrayList<VizqlserverWorker>();

        //Search for the workers' name
        for (String s: bodySlpit) {
            regex = "<td><a href=\"/balancer-manager\\?b=" + clusterName + "&w=([^&]+)&nonce=" + nonce+"[^<]*</a></td><td>([^<]+).*";
            p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m = p.matcher(s);
            if (m.matches()) {
                String memberName = m.group(1);
                String route = m.group(2);

                regex = ".*:([0-9]+)";
                p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
                m = p.matcher(memberName);

                if (!m.matches()) {
                    throw new Exception("Cannot found the vizqlserver workers' port");
                }

                //calculate JMX port
                int jmxPort = Integer.parseInt(m.group(1))+300;

                //check if port exists
                try {
                    JmxClientHelper kliens = new JmxClientHelper();
                    kliens.ConnectService("service:jmx:rmi:///jndi/rmi://:"+jmxPort+"/jmxrmi");
                    if (!kliens.CheckBeanExists("tableau.health.jmx:name=vizqlservice")) {
                        throw new Exception("Cannot found the required MBean");
                    }
                    kliens.Close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MalformedObjectNameException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                workers.add(new VizqlserverWorker(memberName, route, nonce, jmxPort));
            }
        }
        return workers;

    }
}
