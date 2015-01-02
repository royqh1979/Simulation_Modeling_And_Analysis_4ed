package net.roy.sim.chapter02.e03;

/**
 * Created by Roy on 2015/1/2.
 */
public class Job{
    double arriveTime;
    double serviceTime;
    double remainingServiceTime;

    public Job(double arriveTime, double serviceTime) {
        this.arriveTime = arriveTime;
        this.serviceTime = serviceTime;
        this.remainingServiceTime = serviceTime;
    }
}
