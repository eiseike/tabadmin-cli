package net.starschema.tabadmin_cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class WindowsTaskHelper {

    public WindowsTaskHelper() throws Exception {
    }

    static void killProcessByPid(int toKill) throws Exception {

        String cmd = CliControl.TASK_KILLER + " " + toKill;
        Runtime.getRuntime().exec(cmd);

    }

    static int searchForPidInWmic( Pattern pattern) throws Exception {
        String line;
        Process p = Runtime.getRuntime().exec
                (System.getenv("windir") +"\\system32\\wbem\\wmic.exe "+
                        " PROCESS get Processid, Commandline");
        BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = input.readLine()) != null) {
            Matcher m = pattern.matcher(line);
            if (m.matches()){
                input.close();
                return Integer.parseInt(m.group(2));
            }
        }
        input.close();
        return -1;
    }
}
