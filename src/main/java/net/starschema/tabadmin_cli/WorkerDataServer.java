package net.starschema.tabadmin_cli;

import java.util.List;

class WorkerDataServer extends WorkerAbstract {

    //TODO: a config file would be nice.
    private static final String BALANCERMEMBER_NAME = "dataserver-cluster";
    private static final String WINDOWS_PROCESS_NAME = "dataserver.exe";
    private static final String M_BEAN_OBJECT_NAME = "tableau.health.jmx:name=dataserver";

    private String memberName;
    private String route;
    private String nonce;
    private int jmxPort;

    WorkerDataServer(String memberName, String route, String nonce, int jmxPort) {
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

    public String getWindowsProcessName() { return WINDOWS_PROCESS_NAME; }

    static List<Worker> getworkersFromHtml(String body) throws Exception {
        return HttpClientHelper.getworkersFromHtml(body, BALANCERMEMBER_NAME, M_BEAN_OBJECT_NAME);
    }

    public String toString() {
        return "vizqlserver " + this.route + " " + this.memberName;
    }

}
