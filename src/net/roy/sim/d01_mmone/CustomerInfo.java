package net.roy.sim.d01_mmone;

/**
 * Created by Roy on 2014/12/26.
 */
public class CustomerInfo {
    private double arrivalTime;
    public CustomerInfo(double arrivalTime) {
        this.arrivalTime =arrivalTime;
    }
    public double getArrivalTime(){
        return arrivalTime;
    }
}
