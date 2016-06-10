package net.starschema.tabadmin_cli;


import org.apache.log4j.Logger;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            CliControl.restartWorkers();
        } catch (Exception e) {
            System.out.println("Fatal: "+e.getMessage());
            logger.fatal(e.toString(), e);
        }
    }
}
