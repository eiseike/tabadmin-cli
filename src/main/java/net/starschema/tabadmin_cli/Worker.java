package net.starschema.tabadmin_cli;

/**
 * Iterface represents any kid of mod_balancer worker in Tableau Server gateway configuration.
 */
interface Worker {

    /**
     *
     * @return the name of the balancer cluster
     */
    String getBalancerMemberName();

    /**
     *
     * @return the name of the balancer worker
     */
    String getName();

    /**
     *
     * @return the nonce of the balancer in balancer-manager
     */
    String getNonce();

    /**
     *
     * @return the route of the balancer worker
     */
    String getRoute();

    /**
     *
     * @return the associated JMX port of the worker
     */
    int getJmxPort();

    String getMBeanObjectName();

    /**
     *
     * @return              the PID of the worker's gateway process.
     * @throws Exception    if not found any
     */
    int getProcessId() throws Exception;
}
