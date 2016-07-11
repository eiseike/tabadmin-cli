package net.starschema.tabadmin_cli;

import java.util.HashMap;

class WorkerController {

    static void disable(BalancerManagerManagedWorker w) throws Exception {
        HttpClientHelper.modifyWorker(
                CliControl.BALANCER_MANAGER_URL,
                w,
                new HashMap<String, Integer>() {{
                    put("w_status_D", 1);
                }}

        );
    }

    static void drain(BalancerManagerManagedWorker w) throws Exception {
        HttpClientHelper.modifyWorker(
                CliControl.BALANCER_MANAGER_URL,
                w,
                new HashMap<String, Integer>() {{
                    put("w_status_N", 1);
                }}
        );
    }

    static void reset(BalancerManagerManagedWorker w) throws Exception {
        HttpClientHelper.modifyWorker(
                CliControl.BALANCER_MANAGER_URL,
                w,
                new HashMap<String, Integer>() {{
                    put("w_status_N", 0);
                    put("w_status_D", 0);
                }}
        );
    }

    static void kill(Worker w) throws Exception {
        WindowsTaskHelper.killProcessByPid(w.getProcessId());
    }
}