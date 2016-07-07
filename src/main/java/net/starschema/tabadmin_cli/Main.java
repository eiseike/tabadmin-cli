package net.starschema.tabadmin_cli;


import org.apache.commons.cli.*;
import org.apache.log4j.Logger;


public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        //fast&dirty cli implementation
        CommandLineParser parser = new GnuParser();
        Options options = new Options();


        options.addOption( "h", "help", false, "This help." );
        options.addOption( "rv", "restart-vizql", false, "Restart VizQL workers." );

        options.addOption(OptionBuilder.withLongOpt("jmx-polling-time")
                .withDescription("JMX data polling time")
                .hasArg()
                .withArgName("SECONDS")
                .create());

        options.addOption( OptionBuilder.withLongOpt( "force-restart-timeout" )
                .withDescription( "Force restart timeout" )
                .hasArg()
                .withArgName("SECONDS")
                .create() );

        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );


            int clicontrol_force_shutdown = 0;
            if( line.hasOption( "force-restart-timeout" ) ) {
                try {
                    clicontrol_force_shutdown = Integer.parseInt( line.getOptionValue( "force-restart-timeout" ) );
                }
                catch( Exception e ) {
                    throw new Exception("force-restart-timeout must be a number!");
                }
                if (clicontrol_force_shutdown<1) {
                    throw new Exception("force-restart-timeout must be a positive number!");
                }

                CliControl.FORCE_SHUTDOWN = clicontrol_force_shutdown;
            }

            int jmxclienthelper_jmx_polling_time = 0;
            if( line.hasOption( "jmx-polling-time" ) ) {
                try {
                    jmxclienthelper_jmx_polling_time = Integer.parseInt( line.getOptionValue( "jmx-polling-time" ) );
                }
                catch( Exception e ) {
                    throw new Exception("jmx-polling-time must be a number!");
                }
                if (jmxclienthelper_jmx_polling_time<1) {
                    throw new Exception("jmx-polling-time be a positive number!");
                }
//                else if (jmxclienthelper_jmx_polling_time<15) {
//                    throw new Exception("jmx-polling-time be a positive number!");
//                }

                if (clicontrol_force_shutdown<jmxclienthelper_jmx_polling_time) {
                    throw new Exception("force-restart-timeout must be at least jmx-polling-time");
                }
                CliControl.JMX_POLLING_TIME = jmxclienthelper_jmx_polling_time;

            }

            if( line.hasOption( "restart-vizql" ) ) {
//                System.out.println("CliControl.restartVizqlWorkers();");
//                System.out.println("JmxClientHelper.JMX_POLLING_TIME = "+JmxClientHelper.JMX_POLLING_TIME );
//                System.out.println("CliControl.FORCE_SHUTDOWN = "+CliControl.FORCE_SHUTDOWN );
                CliControl.restartVizqlWorkers();
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "tabadmin-cli", options );
            }

        } catch (Exception e) {
            //System.out.println("Fatal: "+e.getMessage());
            logger.fatal(e.getMessage());
        }
    }
}
