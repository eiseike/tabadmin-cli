package net.starschema.tabadmin_cli;

import org.apache.http.client.fluent.Request;

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
        //végigmegyünk a testen

        //kikeressük ezt a sort: balancer://vizqlserver-cluster => nonce=7436df2a-dbbd-db43-87c0-ffb12e83ed31

        String regex;
        Pattern p;
        String nonce="";

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
            throw new Exception("Cannot found the vizqlserver ("+regex+")load balancer in balancer-manager");
        }



        regex = "<td><a href=\"/balancer-manager\\?b=" + clusterName + "&w=([^&]+)&nonce=" + nonce+".*";




        Pattern p2 = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        List<VizqlserverWorker> workers = new ArrayList<VizqlserverWorker>();
        for (String s: bodySlpit) {
            Matcher m2 = p2.matcher(s);
            if (m2.matches()) {
                String worker = m2.group(1);

                //már csak a jmx port kell


            }
        }
        return workers;

    }
}
