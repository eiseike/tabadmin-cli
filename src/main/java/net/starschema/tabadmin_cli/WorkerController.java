package net.starschema.tabadmin_cli;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    static void RestartCacheServer( String pw, int port) throws Exception {

        try ( Socket clientSocket = new Socket("localhost", port))
        {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            String xxx = "AUTH " + pw + '\n' + "SHUTDOWN SAVE" + '\n';
            outToServer.writeBytes(xxx);
        } catch (IOException e) {
            throw new Exception ( "Socket error: " + e.getMessage());
        }

    }

}