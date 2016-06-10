package net.starschema.tabadmin_cli;

import java.util.regex.Pattern;

abstract class AbstractWorker implements Worker {

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
