package net.starschema.tabadmin_cli;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by balazsa on 2016.07.12..
 */
public class FileHelper {

    static final String REDIS_CONFIG_FILENAME ="redis.conf";
    static final String WORKGROUP_YAML_FILENAME ="workgroup.yml";

    static boolean checkIfDir(String path) {
        File f = new File(path);
        return (f.isDirectory());
    }

    static String filePregMatch(String filepath, String needle) throws Exception {

        Pattern p = Pattern.compile(needle);
        Matcher m = null;

        FileReader fileReader = new FileReader(filepath);
        try (
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                m = p.matcher(line);
                if (m.matches()) {
                    return m.group(1);
                }
            }
        }
        throw new Exception ("Could not find " + needle + " in " + filepath);
    }
}
