package net.starschema.tabadmin_cli;

import org.apache.log4j.Logger;

import javax.management.MalformedObjectNameException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by balazsa on 2016.06.06..
 */
public class CliControl {

    final static Logger logger = Logger.getLogger(CliControl.class);

    private static final String BALANCER_MANAGER_URL = "http://localhost/balancer-manager";

    private CliControl() {
    }

    public static void SleepSTDOUTFor(int secs) throws InterruptedException {
        if (secs<2) {
            secs=2;
        }
        for (int run=0;run<2;run++){
            for (int i=0;i<(int)secs/2;i++) {
                System.out.print(run==0?".":".");
                Thread.sleep(100);
            }

        }
        System.out.println();
    }

    public static String RestartWorkers() throws Exception {


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

        String body = HttpClientHelper.getPage(BALANCER_MANAGER_URL);
        workers=VizqlserverWorker.getworkersFromHtml(body);
        for (VizqlserverWorker w:workers) {
           System.out.println(w.toString());
        }
        for (VizqlserverWorker w:workers) {
            System.out.println("Graceful restart worker " + w.route);
            System.out.println("Switch worker to Draining mode");
            //TODO: :)
            System.out.println("Connecting to JMX endpoint jmx://localhost:"+w.jmxPort);

            JmxClientHelper kliens=null;
            try {
                kliens = new JmxClientHelper();
                kliens.ConnectService("service:jmx:rmi:///jndi/rmi://:" + w.jmxPort + "/jmxrmi");

                int currentSessions = Integer.parseInt(kliens.getActiveSessions());
                boolean done = false;
                while (!done) {
                    if (0 == currentSessions) {
                        System.out.println("No active sessions.");

                        System.out.println("Switch worker to Disabled mode");
                HttpClientHelper.ModifyWorker(
                        BALANCER_MANAGER_URL,
                        w,
                        new HashMap<String, Integer>(){{put("w_status_D", 1);}}
                );



                        //TODO: :)
                        System.out.println("Sending stop signal to process 1844. Sleeping 60 secs");
                        //TODO :|

                        done = true;
                    } else {
                        System.out.print("Number of active sessions "+kliens.getActiveSessions()+". Sleeping 60secs ");
                        CliControl.SleepSTDOUTFor(60);
                    }
                }

            } catch (MalformedObjectNameException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally{

                if (kliens!=null) {
                    kliens.Close();
                }
            }
        }
        return null;
    }
}
