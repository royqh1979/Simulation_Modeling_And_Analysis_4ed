package net.roy.sim.chapter02.e05;

import net.roy.sim.distribution.ExponentialVariable;

/**
 * Created by Roy on 2015/1/6.
 */
public class WorkstationInfo {
    private Workstation workstation;
    private double meanServiceTime;
    private ExponentialVariable serviceTime;

    public WorkstationInfo(Workstation workstation, double meanServiceTime) {
        this.workstation = workstation;
        this.meanServiceTime = meanServiceTime;
        serviceTime=new ExponentialVariable(meanServiceTime);
    }

    public Workstation getWorkstation() {
        return workstation;
    }

    public ExponentialVariable getServiceTime() {
        return serviceTime;
    }
}
