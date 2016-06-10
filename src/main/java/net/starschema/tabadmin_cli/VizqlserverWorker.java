package net.starschema.tabadmin_cli;

import java.util.List;

public class VizqlserverWorker extends AbstractWorker {

    static final String BALANCERMEMBER_NAME = "vizqlserver-cluster";

    String memberName;
    String route;
    String nonce;
    int jmxPort;

    public VizqlserverWorker(String memberName, String route, String nonce, int jmxPort) {
        this.memberName = memberName;
        this.route = route;
        this.nonce = nonce;
        this.jmxPort = jmxPort;
    }

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

    public String toString() {
        return "vizqlserver " + this.route + " " + this.memberName;
    }

}
