package net.starschema.tabadmin_cli;

/**
 * Iterface represents any kid of mod_balancer worker in Tableau Server gateway configuration.
 */
public interface Worker {

    /**
     * 
     * @return the name of the balancer cluster
     */
    public String getBalancerMemberName();

    /**
     *
     * @return the name of the balancer worker
     */
    public String getName();

    /**
     *
     * @return the nonce of the balancer in balancer-manager
     */
    public String getNonce();

    /**
     *
     * @return the route of the balancer worker
     */
    public String getRoute();

    /**
     *
     * @return the associated JMX port of the worker
     */
    public int getJmxPort();

    /**
     *
     * @return              the PID of the worker's gateway process.
     * @throws Exception    if not found any
     */
    public int getProcessId() throws Exception;
}
