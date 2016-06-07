package net.starschema.tabadmin_cli;

/**
 * Created by balazsa on 2016.06.06..
 */
public class VizqlserverWorker {


    String balancerName;
    String memberName;
    String nonce;
    String jmxPort;

    boolean Disable(boolean enable) {
        return true;
    }
    boolean Drain(boolean enable) {
        return true;
    }

    public VizqlserverWorker() {
        this.balancerName="???";
        this.memberName="???";
        this.nonce="???";
        this.jmxPort="???";

        if (Disable(true)) {
            System.out.println(":)");
        } else {
            System.out.println(":)");
        }
    }
}
