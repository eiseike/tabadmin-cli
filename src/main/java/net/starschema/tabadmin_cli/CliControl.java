package net.starschema.tabadmin_cli;

import java.util.List;

class CliControl {

    static final String BALANCER_MANAGER_URL = "http://localhost/balancer-manager";
    static final String TASK_KILLER = "taskkill /F /PID";
    static int FORCE_SHUTDOWN = 240;
    static int JMX_POLLING_TIME = 60;
    private CliControl() {
    }

    private static void sleep(int secs) throws Exception {
        Thread.sleep(200 * secs);
    }

    private static void restartBalancerManagerManagedWorkers(List<BalancerManagerManagedWorker> workers) throws Exception {
        for (Worker w : workers) {
            Main.logger.info(w.toString());
        }
        for (BalancerManagerManagedWorker w : workers) {

            try (JmxClientHelper jmxClient = new JmxClientHelper()) {

                Main.logger.info("Graceful restart worker " + w.getRoute());
                Main.logger.info("Switch worker to Draining mode");

                WorkerController.drain(w);

                Main.logger.info("Connecting to JMX endpoint jmx://localhost:" + w.getJmxPort());



                jmxClient.connectService("service:jmx:rmi:///jndi/rmi://:" + w.getJmxPort() + "/jmxrmi");

                int activeSessions;
                int elapsedSeconds = 0;
                boolean done = false;
                while (!done) {
                    activeSessions = Integer.parseInt(jmxClient.getActiveSessions(w.getMBeanObjectName()));
                    if (elapsedSeconds>=FORCE_SHUTDOWN || 0 >= activeSessions) {

                        if (elapsedSeconds>=FORCE_SHUTDOWN) {
                            Main.logger.info("Force restart.");
                        } else {
                            if (0>activeSessions) {
                                Main.logger.info("Inconclusive data from MBean : ActiveSessions = " + activeSessions + ". Force restart.");
                                //throw new Exception("Inconclusive data from MBean : ActiveSessions = " + activeSessions);
                            } else {
                                Main.logger.info("No active sessions.");
                            }
                        }

//                        Main.logger.info("Switch worker to Disabled mode");
//                        WorkerController.disable(w);

                        int pid = w.getProcessId();
                        Main.logger.info("Sending stop signal to process " + pid + ". Sleeping 60 secs");
                        WorkerController.kill(w);
                        CliControl.sleep(60);

                        Main.logger.info("Switch worker to Non-disabled mode");
                        WorkerController.reset(w);


                        done = true;

                    } else {
                        Main.logger.info("Number of active sessions " + activeSessions + ". Sleeping "+JMX_POLLING_TIME+" secs ");
                        CliControl.sleep(JMX_POLLING_TIME);
                        elapsedSeconds+=JMX_POLLING_TIME;
                    }
                }

                Main.logger.info("Graceful restart complete");

            }
        }
    }

    static String restartVizqlWorkers() throws Exception {

        List<BalancerManagerManagedWorker> workers;
        String body;

        Main.logger.info("Locating vizqlserver-cluster workers from balancer-manager");

        body = HttpClientHelper.getPage(BALANCER_MANAGER_URL);
        workers = WorkerVizql.getworkersFromHtml(body);
        restartBalancerManagerManagedWorkers(workers);
        return null;
    }

    static String restartDataServerWorkers() throws Exception {

        List<BalancerManagerManagedWorker> workers;
        String body;

        Main.logger.info("Locating dataserever-cluster workers from balancer-manager");

        body = HttpClientHelper.getPage(BALANCER_MANAGER_URL);
        workers = WorkerDataServer.getworkersFromHtml(body);
        restartBalancerManagerManagedWorkers(workers);
        return null;
    }

    static String restartGatewayWorker() throws Exception {

        List<BalancerManagerManagedWorker> workers;
        String body;

        Main.logger.info("Restarting gateway");
        WorkerController.kill(new WorkerGateway());

        return null;
    }


}