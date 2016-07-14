/*
The MIT License (MIT)
Copyright (c) 2016, Starschema Ltd

Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be included in all copies
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.starschema.tabadmin_cli;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

public class Main {

    final static Logger loggerStdOut = Logger.getLogger(Main.class);
    final static Logger loggerFile = Logger.getLogger("fileLogger");


    public static void main(String[] args) {

        //fast&dirty cli implementation
        CommandLineParser parser = new GnuParser();
        Options options = new Options();


        options.addOption( "h", "help", false, "This help." );

        //TODO:version
        options.addOption( "v", "version", false, "Print version information." );
        options.addOption( "r", "restart", false, "Restart all processes one-by-one.");
        options.addOption( "rv", "restart-vizql", false, "Restart VizQL workers." );
        options.addOption( "rc", "restart-cache", false, "Restart Cache Server." );

        //TODO:Backgrounder
        options.addOption( "rb", "restart-backgrounder", false, "Restart Backgrounder workers." );

        //TODO:Vizportal
        options.addOption( "rp", "restart-vizportal", false, "Restart Vizportal workers." );
        options.addOption( "rd", "restart-dataserver", false, "Restart Data Server workers." );

        //TODO:postgres
        options.addOption( "pg", "reload-postgres", false, "Send reload signal to repository." );
        options.addOption( "ra", "reload-apache", false, "Reload gateway rules." );

        //TODO:force
        options.addOption( "f", "force", false, "Disable JMX, send signals immediately (non-graceful)." );

        options.addOption(OptionBuilder.withLongOpt("jmx-polling-time")
                .withDescription("JMX data polling time")
                .hasArg()
                .withArgName("SECONDS")
                .create());

        options.addOption(OptionBuilder.withLongOpt("wait")
                .withDescription("Waiting time between jobs")
                .hasArg()
                .withArgName("SECONDS")
                .create());

        options.addOption(OptionBuilder.withLongOpt("wait-errors")
                .withDescription("Waiting time after errors/retries")
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

            int jmxclienthelper_jmx_polling_time;
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

            if( line.hasOption( "wait" ) ) {
                int clicontrol_wait;
                try {
                    clicontrol_wait = Integer.parseInt( line.getOptionValue( "wait" ) );
                }
                catch( Exception e ) {
                    throw new Exception("wait must be a number!");
                }
                if (clicontrol_wait<1) {
                    throw new Exception("wait must be a positive number!");
                }
                CliControl.WAIT_AFTER = clicontrol_wait;
            }


            if( line.hasOption( "wait-errors" ) ) {
                int clicontrol_wait_errors = 0;
                try {
                    clicontrol_wait_errors = Integer.parseInt( line.getOptionValue( "wait-errors" ) );
                }
                catch( Exception e ) {
                    throw new Exception("wait-errors must be a number!");
                }
                if (clicontrol_wait_errors<1) {
                    throw new Exception("wait-errors must be a positive number!");
                }

                CliControl.WAIT_AFTER_ERROR = clicontrol_wait_errors;
            }

            boolean need_help = true;

            if( line.hasOption("restart") || line.hasOption( "reload-postgres" ) ) {
                need_help=false;
                CliControl.restartRepository();
            }

            if( line.hasOption("restart") || line.hasOption( "restart-cache" ) ) {
                need_help=false;
                CliControl.restartCacheServerWorkers();
            }

            if( line.hasOption("restart") || line.hasOption( "restart-vizql" ) ) {
                need_help=false;
                CliControl.restartVizqlWorkers();
            }

            if( line.hasOption("restart") || line.hasOption( "restart-dataserver" ) ) {
                need_help=false;
                CliControl.restartDataServerWorkers();
            }

            if( line.hasOption("restart") || line.hasOption( "restart-backgrounder" ) ) {
                need_help=false;
                CliControl.restartBackgrounderWorkers();
            }

            if( line.hasOption("restart") || line.hasOption( "reload-apache" ) ) {
                need_help=false;
                CliControl.restartGateway();
            }

            if (need_help || line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "tabadmin-cli", options );
            }

        } catch (Exception e) {
            //e.printStackTrace();
            loggerStdOut.info(e.getMessage());


            loggerFile.fatal(e);
        }
    }
}
