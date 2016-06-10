package net.starschema.tabadmin_cli;

/**
 * Created by balazsa on 2016.06.07..
 */
public interface Worker {
    public String getBalancerMemberName();
    public String getName();
    public String getNonce();
    public String getRoute();
    public int getJmxPort();
    public int getProcessId() throws Exception;
}
