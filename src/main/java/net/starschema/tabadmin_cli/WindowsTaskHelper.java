package net.starschema.tabadmin_cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
/**
 * Created by balazsa on 2016.06.10..
 */
public class WindowsTaskHelper {

    public WindowsTaskHelper() throws IOException {
    }

    public static void killProcessByPid(int tokill) throws Exception {

        String cmd = "taskkill /F /PID " + tokill;
        System.out.println(cmd);
        //Runtime.getRuntime().exec(cmd);

    }

    public static int searchForPidInWmic( Pattern pattern) throws IOException {
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
