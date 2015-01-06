package net.roy.sim.chapter02.e05;

/**
 * Created by Roy on 2015/1/6.
 */
public class Job {
    private JobType jobType;
    private double stationArriveTime;
    private double arriveTime;
    private Workstation currentWorkstation;

    public Job(JobType jobType, double arriveTime) {
        this.jobType = jobType;
        this.arriveTime = arriveTime;
    }

    public void arriveStation(Workstation workstation,double time) {
        this.currentWorkstation=workstation;
        this.stationArriveTime=time;
    }

    public Workstation getCurrentWorkstation() {
        return currentWorkstation;
    }

    public JobType getJobType() {
        return jobType;
    }

    public double getStationArriveTime() {
        return stationArriveTime;
    }

    public double getArriveTime() {
        return arriveTime;
    }
}
