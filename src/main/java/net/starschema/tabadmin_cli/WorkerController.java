package net.starschema.tabadmin_cli;

import java.util.HashMap;

public class WorkerController {

    static boolean Disable(Worker w, boolean set_to) {

        HttpClientHelper.modifyWorker(
                CliControl.BALANCER_MANAGER_URL,
                w,
                (set_to
                        ?
                        new HashMap<String, Integer>() {{
                            put("w_status_D", 1);
                        }}
                        :
                        new HashMap<String, Integer>() {{
                            put("w_status_D", 0);
                        }}
                )
        );
        return true;
    }

    static boolean Drain(Worker w, boolean set_to) {
        HttpClientHelper.modifyWorker(
                CliControl.BALANCER_MANAGER_URL,
                w,
                (set_to
                        ?
                        new HashMap<String, Integer>() {{
                            put("w_status_N", 1);
                        }}
                        :
                        new HashMap<String, Integer>() {{
                            put("w_status_N", 0);
                        }}
                )
        );
        return true;
    }

    static void Kill(Worker w) throws Exception {
        WindowsTaskHelper.killProcessByPid(w.getProcessId());
    }

}
