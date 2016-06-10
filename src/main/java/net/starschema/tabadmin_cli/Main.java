package net.starschema.tabadmin_cli;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);


    public static void main(String[] args) {

        try {
            CliControl.RestartWorkers();
        } catch (IOException e) {
            System.out.println("Cannot connect to the balancer-manager.");
            logger.fatal("Cannot connect to the balancer-manager.");
        } catch (Exception e) {
            System.out.println("Fatal: "+e.toString());
        }
    }
}
