package net.starschema.tabadmin_cli;

import java.util.regex.Pattern;

/**
 * Created by balazsa on 2016.07.11..
 */
public abstract class WorkerAbstract implements Worker {

    public abstract String getWindowsProcessName();

    public int getProcessId() throws Exception {
        String regex = "^\"([^\"])*"+getWindowsProcessName()+"\".*\\s+([0-9]+)\\s*$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

        int pid = WindowsTaskHelper.searchForPidInWmic(getWindowsProcessName(), pattern);
        if (pid==-1) {
            throw new Exception("Cannot find PID of the worker");
        }
        return pid;
    }
}
