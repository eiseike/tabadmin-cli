package net.starschema.tabadmin_cli;

/**
 * Iterface represents any kid of mod_balancer worker in Tableau Server gateway configuration.
 */
interface Worker {
    /**
     *
     * @return              the PID of the worker's gateway process.
     * @throws Exception    if not found any
     */
    int getProcessId() throws Exception;
}
