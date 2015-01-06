package net.roy.sim.chapter02.e05;

import net.roy.sim.distribution.ExponentialVariable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Roy on 2015/1/6.
 */
public class Workstation {
    private String id;
    private Queue<Job> jobQueue;
    private Job currentJob;
    public final static Workstation WS1=new Workstation("WS1");
    public final static Workstation WS2=new Workstation("WS2");
    public final static Workstation WS3=new Workstation("WS3");
    public final static Workstation WS4=new Workstation("WS4");
    public final static Workstation WS5=new Workstation("WS5");


    private Workstation(String id) {
        this.id = id;
        jobQueue=new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public void reset() {
        currentJob=null;
        jobQueue.clear();
    }

    public Queue<Job> getJobQueue() {
        return jobQueue;
    }

    public Job getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Job job) {
        currentJob=job;
    }

    public boolean isBusy() {
        return currentJob!=null;
    }
}
