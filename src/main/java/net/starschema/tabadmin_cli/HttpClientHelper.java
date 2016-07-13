package net.starschema.tabadmin_cli;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HttpClientHelper {

    //get targetURL's HTML
    static String getPage(String targetURL) throws Exception {
        Request x = Request.Get(targetURL);
        Response y = x.execute();
        Content z = y.returnContent();
        return z.toString();
    }

    static void modifyWorker(String targetURL, BalancerManagerManagedWorker w, HashMap<String, Integer> switches) throws Exception {

        try (
            CloseableHttpClient client = HttpClients.createDefault();
        ) {

            HttpPost httpPost = new HttpPost(targetURL);

            List<NameValuePair> params = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : switches.entrySet())
            {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));

            }
            params.add(new BasicNameValuePair("b", w.getBalancerMemberName() ));
            params.add(new BasicNameValuePair("w", w.getName()));
            params.add(new BasicNameValuePair("nonce", w.getNonce()));
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse response = client.execute(httpPost);
            int responseCode=response.getStatusLine().getStatusCode();
            if ( 200 != responseCode){
                throw new Exception("Balancer-manager returned a http response of "+responseCode);
            }
        }
    }

    static List<BalancerManagerManagedWorker> getworkersFromHtml(String body, String clusterName, String jmxObjectName) throws Exception {

        String regex;
        Pattern p;
        String nonce="";
        String[] bodySlpit;
        Matcher m;
        int jmxPort;

        //search for the cluster's nonce string
        regex = "<h3>.*&nonce=([^\"]+)\">balancer://" + clusterName + "</a>.*";
        p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        bodySlpit = body.split("\n");
        for (String s : bodySlpit) {
            m = p.matcher(s);
            if (m.matches()) {
                nonce = m.group(1);
                break;
            }
        }

        if (nonce.equals("")) {
            throw new Exception("Cannot found the worker load balancer in balancer-manager");
        }

        List<BalancerManagerManagedWorker> workers = new ArrayList<>();

        //Search for the workers' name
        for (String s: bodySlpit) {
            regex = "<td><a href=\"/balancer-manager\\?b=" + clusterName + "&w=([^&]+)&nonce=" + nonce+"[^<]*</a></td><td>([^<]+).*";
            p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
            m = p.matcher(s);
            if (m.matches()) {
                String memberName = m.group(1);
                String route = m.group(2);

                regex = ".*:([0-9]+)";
                p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
                m = p.matcher(memberName);

                if (!m.matches()) {
                    throw new Exception("Cannot found the workers' port");
                }

                //calculate JMX port
                jmxPort = Integer.parseInt(m.group(1))+300;

                //check if port exists
                try (JmxClientHelper jmxClient = new JmxClientHelper()) {

                    boolean done = false;
                    int count = 0;
                    String error = "";
                    while (count++ <3) {
                        String jMXServiceURL ="service:jmx:rmi:///jndi/rmi://:"+jmxPort+"/jmxrmi";
                        jmxClient.connectService(jMXServiceURL);
                        if (!jmxClient.checkBeanExists(jmxObjectName)) {
                            error = "Cannot found the required MBean " + jMXServiceURL + ":" + jmxObjectName;
                            Main.logger.info( error +"\nRetrying after "+ CliControl.WAIT_AFTER_ERROR +" seconds...");
                            CliControl.sleep(CliControl.WAIT_AFTER_ERROR);
                        } else {
                            error="";
                            break;
                        }
                    }
                    if (!Objects.equals(error, "")) {
                        throw new Exception(error);
                    }

                }

                //TODO:fast and ugly:
                if (Objects.equals(clusterName, "vizqlserver-cluster")) {
                    workers.add(new WorkerVizql(memberName, route, nonce, jmxPort));
                } else if (Objects.equals(clusterName, "dataserver-cluster")) {
                    workers.add(new WorkerDataServer(memberName, route, nonce, jmxPort));
                }

            }
        }
        return workers;
    }
}
