package net.starschema.tabadmin_cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by balazsa on 2016.06.10..
 */
public abstract class AbstractWorker implements Worker {

    public abstract String getBalancerMemberName();
    public abstract String getName();
    public abstract String getNonce();
    public abstract String getRoute();
    public abstract int getJmxPort();


    public int getProcessId() throws Exception {
        String regex = "^\"([^\"])*vizqlserver.exe\".*Dcom\\.sun\\.management\\.jmxremote\\.port=9400 .*\\s+([0-9]+)\\s*$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

        int pid = WindowsTaskHelper.searchForPidInWmic(pattern);
        if (pid==-1) {
            throw new Exception("Cannot find PID of the worker");
        }
        return pid;
    }

}
