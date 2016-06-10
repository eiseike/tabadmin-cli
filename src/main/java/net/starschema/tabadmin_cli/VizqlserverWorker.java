package net.starschema.tabadmin_cli;

import java.util.List;

class VizqlserverWorker extends AbstractWorker {

    //TODO: a config file would be nice.
    private static final String BALANCERMEMBER_NAME = "vizqlserver-cluster";
    private static final String M_BEAN_OBJECT_NAME = "tableau.health.jmx:name=vizqlservice";

    private String memberName;
    private String route;
    private String nonce;
    private int jmxPort;

    VizqlserverWorker(String memberName, String route, String nonce, int jmxPort) {
        this.memberName = memberName;
        this.route = route;
        this.nonce = nonce;
        this.jmxPort = jmxPort;
    }

    public String getMBeanObjectName() { return M_BEAN_OBJECT_NAME; }

    public String getBalancerMemberName() {
        return BALANCERMEMBER_NAME;
    }

    public String getName() {
        return memberName;
    }

    public String getNonce() {
        return nonce;
    }

    public String getRoute() {
        return route;
    }

    public int getJmxPort() {
        return jmxPort;
    }

    static List<VizqlserverWorker> getworkersFromHtml(String body) throws Exception {
        return HttpClientHelper.getworkersFromHtml(body, BALANCERMEMBER_NAME);
    }

    public String toString() {
        return "vizqlserver " + this.route + " " + this.memberName;
    }

}
