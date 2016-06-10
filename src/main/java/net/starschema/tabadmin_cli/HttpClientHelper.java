package net.starschema.tabadmin_cli;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HttpClientHelper {

    //get targetURL's HTML
    static String getPage(String targetURL) throws IOException {
        return Request.Get(targetURL).execute().returnContent().toString();
    }

    static void modifyWorker(String targetURL, Worker w, HashMap<String, Integer> switches) throws Exception {

        CloseableHttpClient client =null;
        try {

            client = HttpClients.createDefault();
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
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    static List<VizqlserverWorker> getworkersFromHtml(String body, String clusterName) throws Exception {

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
            throw new Exception("Cannot found the vizqlserver load balancer in balancer-manager");
        }

        List<VizqlserverWorker> workers = new ArrayList<>();

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
                    throw new Exception("Cannot found the vizqlserver workers' port");
                }

                //calculate JMX port
                jmxPort = Integer.parseInt(m.group(1))+300;

                //check if port exists
                try (JmxClientHelper jmxClient = new JmxClientHelper()) {
                    String jMXServiceURL ="service:jmx:rmi:///jndi/rmi://:"+jmxPort+"/jmxrmi";
                    String objectName = "tableau.health.jmx:name=vizqlservice";
                    jmxClient.connectService(jMXServiceURL);
                    if (!jmxClient.checkBeanExists(objectName)) {
                        throw new Exception("Cannot found the required MBean " + jMXServiceURL + ":" + objectName);
                    }

                }
                workers.add(new VizqlserverWorker(memberName, route, nonce, jmxPort));
            }
        }
        return workers;

    }
}
