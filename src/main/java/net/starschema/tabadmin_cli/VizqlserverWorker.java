package net.starschema.tabadmin_cli;

import org.apache.http.impl.client.HttpRequestFutureTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by balazsa on 2016.06.06..
 */
public class VizqlserverWorker implements Worker {

    String memberName;
    String nonce;
    String jmxPort;

    public static List<VizqlserverWorker> getworkersFromHtml(String body) throws Exception {
        return HttpClientHelper.getworkersFromHtml(body, "vizqlserver-cluster");
    }

    public VizqlserverWorker() {


        memberName = "???";
        nonce = "???";
        jmxPort = "???";

    }

}
