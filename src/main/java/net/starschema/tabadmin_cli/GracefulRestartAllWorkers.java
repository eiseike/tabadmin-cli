package net.starschema.tabadmin_cli;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by balazsa on 2016.06.06..
 */
public class GracefulRestartAllWorkers {

    public GracefulRestartAllWorkers() {

        List<VizqlserverWorker> workers = new ArrayList<VizqlserverWorker>();

        System.out.println("Hi!" + workers.size() +
            HttpClientHelper.getPage("http://stage-151.starschema.biz/balancer-manager")
        );
    }
}
