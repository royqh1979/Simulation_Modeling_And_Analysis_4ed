package net.roy.sim.chapter02.e03;

import net.roy.sim.distribution.ExponentialVariable;
import net.roy.sim.tools.*;

import java.util.LinkedList;
import java.util.Queue;

/**
 * simulation of Time-Shared Computer Model
 * Chapter 2.5
 * Created by Roy on 2014/12/30.
 */
public class Simulator {
    /* parameters */
    private double meanThinkTime;
    private double meanServiceTime;
    private double quantum;
    private double swapTime;
    private int jobsRequired;
    private ExponentialVariable thinkTime;
    private ExponentialVariable serviceTime;

    /* System states */
    private Queue<Job> cpuJobs=new LinkedList<>();
    private Job currentJob;
    private int jobFinished;
    private EventDispatcher<EventType,Object> eventDispatcher=new EventDispatcher<>();

    /* statistics */
    private ContinuousVariableSummary responseTimeSummary=new ContinuousVariableSummary("Response Time");
    private DiscreteTimeSeriesSummary numInQueueSummary=new DiscreteTimeSeriesSummary("CPU Queue",0);
    private ContinuousTimeSeriesSummary utility =new ContinuousTimeSeriesSummary("Utility",0);

    public Simulator(double meanThinkTime, double meanServiceTime, double quantum, double swapTime, int jobsRequired) {
        this.meanThinkTime = meanThinkTime;
        this.meanServiceTime = meanServiceTime;
        this.quantum = quantum;
        this.swapTime = swapTime;
        this.jobsRequired = jobsRequired;

        thinkTime=new ExponentialVariable(meanThinkTime);
        serviceTime=new ExponentialVariable(meanServiceTime);
    }

    public void simulate(int numOfTerminals){
        init();
        for (int i=0;i<numOfTerminals;i++) {
            eventDispatcher.scheduleAbsoluteTime(EventType.JobArrival,thinkTime.nextValue());
        }
        while (true) {
            eventDispatcher.timing();
            updateStatistics();
            switch (eventDispatcher.getCurrentEventType()) {
                case JobArrival:
                    jobArrival();
                    break;
                case CPURunEnd:
                    endCpuRun();
                    break;
            }
            if (eventDispatcher.getCurrentEventType()==EventType.SimFinish) {
                break;
            }
        }
    }

    private void updateStatistics() {
        numInQueueSummary.addValue(cpuJobs.size(), eventDispatcher.getTime());
        utility.addValue(currentJob == null ? 0 : 1, eventDispatcher.getTime());
    }

    private void endCpuRun() {
        //System.out.println("EndCPURun1");
        if (currentJob.remainingServiceTime>0) {
            cpuJobs.add(currentJob);
        }   else {
            responseTimeSummary.addValue(eventDispatcher.getTime()-currentJob.arriveTime);
            jobFinished++;
            if (jobFinished>=jobsRequired) {
                eventDispatcher.schedule(EventType.SimFinish,0);
                return ;
            } else {
                eventDispatcher.schedule(EventType.JobArrival, thinkTime.nextValue());
            }
        }
        startCpuRun();
    }

    private void jobArrival() {
        //System.out.println("Job Arrival2");
        Job job=new Job(eventDispatcher.getTime(),serviceTime.nextValue());
        if (currentJob==null) {
            cpuJobs.add(job);
            startCpuRun();
        } else {
            cpuJobs.add(job);
        }
    }

    private void startCpuRun() {
        //System.out.println("StartCPURun3");
        currentJob=cpuJobs.poll();
        if (currentJob==null)
            return ;
        double runTime=0;
        if (currentJob.remainingServiceTime<quantum) {
            runTime+=currentJob.remainingServiceTime;
        } else {
            runTime+=quantum;
        }
        currentJob.remainingServiceTime-=quantum;
        eventDispatcher.schedule(EventType.CPURunEnd,runTime+swapTime);
    }

    private void init() {
        cpuJobs.clear();
        eventDispatcher.reset();
        currentJob=null;
        jobFinished=0;

        responseTimeSummary.reset();
        numInQueueSummary.reset();
        utility.reset();

    }

    public static void main(String[] args) {
        Simulator simulator=new Simulator(25,0.8,0.1,0.015,10000);

        reportHeader(simulator);
        for (int i=10;i<=100;i+=10) {
            simulator.simulate(i);
            report(simulator,i);
        }
    }

    private static void report(Simulator simulator, int numOfTerms) {
        System.out.println(String.format("%5d%16.3f%16.3f%16.3f", numOfTerms,
                simulator.responseTimeSummary.getMean(),
                simulator.numInQueueSummary.getTimeAverage(),
                simulator.utility.getTimeAverage()));
    }

    private static void reportHeader(Simulator simulator) {
        System.out.println(String.format("Mean think time  %11.3f seconds",simulator.thinkTime.getMean()));
        System.out.println(String.format("Mean service time%11.3f seconds",simulator.serviceTime.getMean()));
        System.out.println(String.format("Quantum          %11.3f seconds",simulator.quantum));
        System.out.println(String.format("Swap time        %11.3f seconds", simulator.swapTime));
        System.out.println(String.format("Number of jobs processed%13d",simulator.jobsRequired));
        System.out.println("Number of      Average         Average       Utilization");
        System.out.println("terminals   response time  number in queue     of CPU");

    }
}
