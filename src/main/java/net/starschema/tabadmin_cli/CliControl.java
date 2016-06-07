package net.starschema.tabadmin_cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by balazsa on 2016.06.06..
 */
public class CliControl {

    private static final String balancerManagerUrl = "http://localhost/balancer-manager";
    
    private CliControl() {
    }

    public static String RestartWorkers() throws Exception {


//        vizqlserver 0:0 http://localhost:9100
//        vizqlserver 0:1 http://localhost:9101
//        Graceful restart worker 0:0
//        Switch worker to Draining mode
//        Connecting to JMX endpoint jmx://localhost:9400
//        Number of active sessions 3. Sleeping 60secs
//        Number of active sessions 1. Sleeping 60secs
//        No active sessions.
//                Switch worker to Disabled mode
//        Sending stop signal to process 1844. Sleeping 60 secs
//        Switch worker to Non-disabled mode
//        Graceful restart worker 0:1
//        Switch worker to Draining mode
//        Connecting to JMX endpoint jmx://localhost:9401
//        Number of active sessions 1. Sleeping 60secs
//        No active sessions.
//                Switch worker to Disabled mode
//        Sending stop signal to process 9176. Sleeping 60 secs
//        Switch worker to Non-disabled mode
//        Graceful restart complete


        List<VizqlserverWorker> workers = new ArrayList<VizqlserverWorker>();

        System.out.println("Locating vizqlserver-cluster workers from balancer-manager");

        String body = HttpClientHelper.getPage(balancerManagerUrl);
        workers=VizqlserverWorker.getworkersFromHtml(body);

        return null;
    }
}
