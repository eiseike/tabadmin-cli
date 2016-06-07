package net.starschema.tabadmin_cli;


import java.io.IOException;

public class Main {

    public static void main(String[] args) {

            JmxClientHelper.Connect();

//        try {
//            CliControl.RestartWorkers();
//        } catch (IOException e) {
//            System.out.println("Cannot connect to the balancer-manager.");
////            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
