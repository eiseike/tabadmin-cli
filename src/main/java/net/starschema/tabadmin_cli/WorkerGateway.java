package net.starschema.tabadmin_cli;

import java.util.List;
import java.util.regex.Pattern;

class WorkerGateway extends WorkerAbstract {

    private static final String WINDOWS_PROCESS_NAME = "httpd.exe";

    WorkerGateway() {
    }


    // "C:/Program Files/Tableau/Tableau Server/10.0/apache/bin/httpd.exe" -E "C:/ProgramData/Tableau/Tableau Server/data/tabsvc/logs/httpd/startup.log" -f "C:/ProgramData/Tableau/Tableau Server/data/tabsvc/config/httpd.conf"

    //"C:\Program Files\Tableau\Tableau Server\10.0\apache\bin\httpd.exe" -d "C:/Program Files/Tableau/Tableau Server/10.0/apache" -E "C:/ProgramData/Tableau/Tableau Server/data/tabsvc/logs/httpd/startup.log" -f "C:/ProgramData/Tableau/Tableau Server/data/tabsvc/config/httpd.conf"

    //TODO:identify tableau's apache
    public int getProcessId() throws Exception {
        String regex = "^\"([^\"])*"+getWindowsProcessName()+"\" -E.*\\s+([0-9]+)\\s*$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

        int pid = WindowsTaskHelper.searchForPidInWmic(getWindowsProcessName(), pattern);
        if (pid==-1) {
            throw new Exception("Cannot find PID of the worker");
        }
        return pid;
    }

    public String getWindowsProcessName() { return WINDOWS_PROCESS_NAME; }

    public String toString() {
        return "httpd";
    }

}
