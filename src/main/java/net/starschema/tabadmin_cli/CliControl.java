package net.starschema.tabadmin_cli;

import org.apache.log4j.Logger;

import javax.management.MalformedObjectNameException;
import java.util.ArrayList;
import java.util.List;

public class CliControl {

    final static Logger logger = Logger.getLogger(CliControl.class);

    public static final String BALANCER_MANAGER_URL = "http://localhost/balancer-manager";
    public static final String TASK_KILLER          = "taskkill /F /PID";

    private CliControl() {
    }

    public static void sleepSTDOUTFor(int secs) throws InterruptedException {
        if (secs < 2) {
            secs = 2;
        }
        for (int run = 0; run < 2; run++) {
            for (int i = 0; i < (int) secs / 2; i++) {
                System.out.print(run == 0 ? "." : ".");
                Thread.sleep(100);
            }

        }
        System.out.println();
    }

    public static String restartWorkers() throws Exception {

        List<VizqlserverWorker> workers = new ArrayList<VizqlserverWorker>();

        System.out.println("Locating vizqlserver-cluster workers from balancer-manager");

        String body = HttpClientHelper.getPage(BALANCER_MANAGER_URL);
        workers = VizqlserverWorker.getworkersFromHtml(body);
        for (VizqlserverWorker w : workers) {
            System.out.println(w.toString());
        }
        for (VizqlserverWorker w : workers) {


            System.out.println("Graceful restart worker " + w.getRoute());
            System.out.println("Switch worker to Draining mode");

            WorkerController.Drain(w,true);

            System.out.print("Wait for it!");
            CliControl.sleepSTDOUTFor(60);

            System.out.println("Connecting to JMX endpoint jmx://localhost:" + w.getJmxPort());

            JmxClientHelper kliens = null;
            try {
                kliens = new JmxClientHelper();
                kliens.connectService("service:jmx:rmi:///jndi/rmi://:" + w.getJmxPort() + "/jmxrmi");

                int currentSessions = Integer.parseInt(kliens.getActiveSessions());
                boolean done = false;
                while (!done) {
                    if (0 == currentSessions) {
                        System.out.println("No active sessions.");
                        System.out.println("Switch worker to Disabled mode");
                        WorkerController.Disable(w,true);

                        System.out.print("Wait for it!");
                        CliControl.sleepSTDOUTFor(60);

                        int pid = w.getProcessId();
                        System.out.println("Sending stop signal to process "+pid+". Sleeping 60 secs");
                        WorkerController.Kill(w);
                        CliControl.sleepSTDOUTFor(60);


                        System.out.println("Switch worker to Non-disabled mode");
                        WorkerController.Drain(w,false);
                        WorkerController.Disable(w,false);

                        done = true;
                    } else {
                        System.out.print("Number of active sessions " + kliens.getActiveSessions() + ". Sleeping 60secs ");
                        CliControl.sleepSTDOUTFor(60);
                    }
                }

                System.out.println("Graceful restart complete");

            } catch (MalformedObjectNameException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (kliens != null) {
                    kliens.close();
                }
            }
        }
        return null;
    }
}
