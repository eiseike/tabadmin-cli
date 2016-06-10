package net.starschema.tabadmin_cli;

import java.util.List;

class CliControl {

    static final String BALANCER_MANAGER_URL = "http://localhost/balancer-manager";
    static final String TASK_KILLER = "taskkill /F /PID";

    private CliControl() {
    }

    private static void sleepSTDOUTFor(int secs) throws Exception {
        Thread.sleep(200 * secs);
    }

    static String restartWorkers() throws Exception {

        List<VizqlserverWorker> workers;
        String body;

        Main.logger.info("Locating vizqlserver-cluster workers from balancer-manager");

        body = HttpClientHelper.getPage(BALANCER_MANAGER_URL);
        workers = VizqlserverWorker.getworkersFromHtml(body);
        for (VizqlserverWorker w : workers) {
            Main.logger.info(w.toString());
        }
        for (VizqlserverWorker w : workers) {

            try (JmxClientHelper jmxClient = new JmxClientHelper()) {

                Main.logger.info("Graceful restart worker " + w.getRoute());
                Main.logger.info("Switch worker to Draining mode");

                WorkerController.drain(w);

                Main.logger.info("Connecting to JMX endpoint jmx://localhost:" + w.getJmxPort());



                jmxClient.connectService("service:jmx:rmi:///jndi/rmi://:" + w.getJmxPort() + "/jmxrmi");

                int activeSessions;
                boolean done = false;
                while (!done) {
                    activeSessions = Integer.parseInt(jmxClient.getActiveSessions(w.getMBeanObjectName()));
                    if (0 == activeSessions) {
                        Main.logger.info("No active sessions.");
                        Main.logger.info("Switch worker to Disabled mode");
                        WorkerController.disable(w);

                        int pid = w.getProcessId();
                        Main.logger.info("Sending stop signal to process " + pid + ". Sleeping 60 secs");
                        WorkerController.kill(w);
                        CliControl.sleepSTDOUTFor(60);

                        Main.logger.info("Switch worker to Non-disabled mode");
                        WorkerController.reset(w);


                        done = true;
                    } else
                    if (0>activeSessions) {
                        throw new Exception("Inconclusive data from MBean : ActiveSessions = "+activeSessions);
                    } else {
                        Main.logger.info("Number of active sessions " + activeSessions + ". Sleeping 60secs ");
                        CliControl.sleepSTDOUTFor(60);
                    }
                }

                Main.logger.info("Graceful restart complete");

            }
        }
        return null;
    }
}
