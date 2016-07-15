/*
The MIT License (MIT)
Copyright (c) 2016, Starschema Ltd

Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be included in all copies
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.starschema.tabadmin_cli;

import java.util.List;

class CliControl {

    static final String BALANCER_MANAGER_URL = "http://localhost/balancer-manager";
    static final String TASK_KILLER = "taskkill /F /PID";
    static int FORCE_SHUTDOWN = 240;
    static int JMX_POLLING_TIME = 60;
    static boolean FORCE_RESTARTS = false;
    static int WAIT_AFTER = 30;
    static int WAIT_AFTER_ERROR = 60;
    static String TABSVC_CONFIG_DIR = "c:\\ProgramData\\Tableau\\Tableau Server\\data\\tabsvc\\config";

    private CliControl() {
    }

    static void sleep(int secs) throws Exception {
        Thread.sleep(1000 * secs);
    }

    private static void restartBalancerManagerManagedWorkers(List<BalancerManagerManagedWorker> workers) throws Exception {
        for (Worker w : workers) {
            Main.loggerStdOut.info(w.toString());
        }
        for (BalancerManagerManagedWorker w : workers) {
            try (HelperJmxClient jmxClient = new HelperJmxClient()) {

                if (w.getJmxPort() != -1 ) {
                    Main.loggerStdOut.info("Gracefully restarting worker " + w.getRoute());
                } else {
                    Main.loggerStdOut.info("Restarting worker " + w.getRoute());
                }
                Main.loggerStdOut.info("Switching worker to Draining mode");

                ControllerWorker.drain(w);

                //jmxable worker
                if (w.getJmxPort() != -1 ) {

                    Main.loggerStdOut.info("Connecting to JMX endpoint jmx://localhost:" + w.getJmxPort());

                    try {
                        jmxClient.connectService("service:jmx:rmi:///jndi/rmi://:" + w.getJmxPort() + "/jmxrmi");
                    } catch (Exception e) {
                        Main.loggerStdOut.info(":(");
                        throw e;
                    }

                    int activeSessions;
                    int elapsedSeconds = 0;
                    boolean done = false;
                    while (!done) {
                        activeSessions = Integer.parseInt(jmxClient.getActiveSessions(w.getMBeanObjectName()));
                        if (elapsedSeconds>=FORCE_SHUTDOWN || 0 >= activeSessions) {

                            if (elapsedSeconds>=FORCE_SHUTDOWN) {
                                Main.loggerStdOut.info("Force restart.");
                            } else {
                                if (0>activeSessions) {
                                    Main.loggerStdOut.info("Inconclusive data from MBean : ActiveSessions = " + activeSessions + ". Force restart.");
                                } else {
                                    Main.loggerStdOut.info("No active sessions.");
                                }
                            }

                            int pid = w.getProcessId(false).get(0);
                            Main.loggerStdOut.info("Sending stop signal to process " + pid);
                            ControllerWorker.kill(w);
                            CliControl.sleep(CliControl.WAIT_AFTER);

                            Main.loggerStdOut.info("Switch worker to Non-disabled mode");
                            ControllerWorker.reset(w);

                            done = true;
                        } else {
                            Main.loggerStdOut.info("Number of active sessions " + activeSessions + ". Sleeping "+JMX_POLLING_TIME+" secs ");
                            CliControl.sleep(JMX_POLLING_TIME);
                            elapsedSeconds+=JMX_POLLING_TIME;
                        }
                    }
                    Main.loggerStdOut.info("Graceful restart complete");

                    //non-jmxable worker
                } else {
                    int pid = w.getProcessId(false).get(0);
                    Main.loggerStdOut.info("Sending stop signal to process " + pid);
                    ControllerWorker.kill(w);
                    CliControl.sleep(CliControl.WAIT_AFTER);

                    Main.loggerStdOut.info("Switch worker to Non-disabled mode");
                    ControllerWorker.reset(w);
                    Main.loggerStdOut.info("Restart complete");

                }

            }
        }
    }

    static void restartVizqlWorkers() throws Exception {

        List<BalancerManagerManagedWorker> workers;
        String body;

        Main.loggerStdOut.info("Locating vizqlserver-cluster workers from balancer-manager");

        body = HttpClientHelper.getPage(BALANCER_MANAGER_URL);
        workers = WorkerVizql.getworkersFromHtml(body);
        restartBalancerManagerManagedWorkers(workers);
    }

    static void restartDataServerWorkers() throws Exception {

        List<BalancerManagerManagedWorker> workers;
        String body;

        Main.loggerStdOut.info("Locating dataserever-cluster workers from balancer-manager");

        body = HttpClientHelper.getPage(BALANCER_MANAGER_URL);
        workers = WorkerDataServer.getworkersFromHtml(body);
        restartBalancerManagerManagedWorkers(workers);

    }

    static void restartVizportalWorkers() throws Exception {

        List<BalancerManagerManagedWorker> workers;
        String body;

        Main.loggerStdOut.info("Locating local-vizportal workers from balancer-manager");

        body = HttpClientHelper.getPage(BALANCER_MANAGER_URL);
        workers = WorkerVizportal.getworkersFromHtml(body);
        restartBalancerManagerManagedWorkers(workers);
    }


    static void restartGateway() throws Exception {

        Main.loggerStdOut.info("Restarting Gateway");
        ControllerWorker.kill(new WorkerGateway());
        CliControl.sleep(CliControl.WAIT_AFTER);

    }

    static void restartRepository() throws Exception{
        Main.loggerStdOut.info("Restarting Repository");
        ControllerWorker.RestartPostgreServer(WorkerRepositoryServer.getAppPath(), WorkerRepositoryServer.getDataDir());
        CliControl.sleep(CliControl.WAIT_AFTER);
    }

    static void restartBackgrounderWorkers() throws Exception{
        Main.loggerStdOut.info("Restarting Backgrounder(s)");
        ControllerWorker.killAll(new WorkerBackgrounder());
        CliControl.sleep(CliControl.WAIT_AFTER);
    }

    static void restartCacheServerWorkers() throws Exception {
        Main.loggerStdOut.info("Restarting Cache Server(s)");
        String pw = WorkerCacheServer.getCacheServerAuthPassword();
        List<Integer> ports = WorkerCacheServer.getCacheServerports();
        Main.loggerStdOut.info("There " +(ports.size()>1?"are":"is") +" " + ports.size()  + " port" + (ports.size()>1?"s":"") );
        for (int port : ports) {
            Main.loggerStdOut.info("Restarting Cache server at port " + port);
            ControllerWorker.restartCacheServer(pw, port);
            CliControl.sleep(CliControl.WAIT_AFTER);
        }
    }
}