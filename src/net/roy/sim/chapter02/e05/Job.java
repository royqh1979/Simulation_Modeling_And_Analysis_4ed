package net.roy.sim.chapter02.e05;

/**
 * Created by Roy on 2015/1/6.
 */
public class Job {
    private JobType jobType;
    private double stationArriveTime;
    private double arriveTime;
    private Workstation currentWorkstation;
    private Workstation.Machine currentMachine;
    private double totalDelay=0;

    public Job(JobType jobType, double arriveTime) {
        this.jobType = jobType;
        this.arriveTime = arriveTime;
    }

    public void arriveStation(Workstation workstation,double time) {
        this.currentMachine=null;
        this.currentWorkstation=workstation;
        this.stationArriveTime=time;
    }

    public void addDelay(double delay){
        totalDelay+=delay;
    }

    public double getTotalDelay() {
        return totalDelay;
    }

    public void process(Workstation.Machine machine){
        this.currentMachine =machine;
    }

    public Workstation.Machine getCurrentMachine() {
        return currentMachine;
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
