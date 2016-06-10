package net.starschema.tabadmin_cli;

import org.apache.http.impl.client.HttpRequestFutureTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by balazsa on 2016.06.06..
 */
public class VizqlserverWorker extends AbstractWorker {

    static final String BALANCERMEMBER_NAME = "vizqlserver-cluster";

    String memberName;
    String route;
    String nonce;
    int jmxPort;

    public String getBalancerMemberName() {
        return BALANCERMEMBER_NAME;
    }

    public String getName() {
        return memberName;
    }

    public String getNonce() {
        return nonce;
    };

    public String getRoute() {
        return route;
    }

    public int getJmxPort() {
        return jmxPort;
    }

    public static List<VizqlserverWorker> getworkersFromHtml(String body) throws Exception {
        return HttpClientHelper.getworkersFromHtml(body, BALANCERMEMBER_NAME);
    }

    public VizqlserverWorker(String memberName, String route, String nonce, int jmxPort) {
        this.memberName = memberName;
        this.route = route;
        this.nonce = nonce;
        this.jmxPort = jmxPort;
    }

    public String toString() {
        return "vizqlserver " + this.route + " " + this.memberName;
    }

}
