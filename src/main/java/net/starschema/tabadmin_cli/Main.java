package net.starschema.tabadmin_cli;


import org.apache.log4j.Logger;

import java.io.IOException;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);


    public static void main(String[] args) {

        try {
            CliControl.restartWorkers();
        } catch (IOException e) {
            System.out.println("Cannot connect to the balancer-manager.");
            logger.fatal("Cannot connect to the balancer-manager.");
        } catch (Exception e) {
            System.out.println("Fatal: "+e.toString());
        }
    }
}
