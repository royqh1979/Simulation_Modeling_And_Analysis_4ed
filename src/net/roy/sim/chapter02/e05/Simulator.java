package net.roy.sim.chapter02.e05;

import net.roy.sim.distribution.ExponentialVariable;
import net.roy.sim.tools.ContinuousVariableSummary;
import net.roy.sim.tools.DiscreteTimeSeriesSummary;
import net.roy.sim.tools.EventDispatcher;

import java.util.HashMap;
import java.util.Map;

import static net.roy.sim.chapter02.e05.EventType.EndSimulation;
import static net.roy.sim.chapter02.e05.EventType.StationDeparture;
import static net.roy.sim.chapter02.e05.JobType.Job1;
import static net.roy.sim.chapter02.e05.JobType.Job2;
import static net.roy.sim.chapter02.e05.JobType.Job3;
import static net.roy.sim.chapter02.e05.Workstation.*;

/**
 * A Job-Shop Model
 * Chapter 2.7
 * Created by Roy on 2015/1/6.
 */
public class Simulator {
    /* parameters */
    private double meanInterarrivalTime;
    private JobGenerator jobGenerator;
    private double simulationLength;

    private ExponentialVariable interarrivalTime;

    /* system states */
    private EventDispatcher<EventType,Job> eventDispatcher=new EventDispatcher<>();

    /* statistics */
    private Map<Workstation, ContinuousVariableSummary> delayInWorkstations;
    private Map<JobType,ContinuousVariableSummary> jobDelays;
    private Map<Workstation, DiscreteTimeSeriesSummary> workstationQueueLen;

    public Simulator(double meanInterarrivalTime, JobGenerator jobGenerator, double simulationLength) {
        this.meanInterarrivalTime = meanInterarrivalTime;
        this.jobGenerator = jobGenerator;
        this.simulationLength = simulationLength;
        interarrivalTime=new ExponentialVariable(meanInterarrivalTime);
        eventDispatcher=new EventDispatcher<>();
        delayInWorkstations=new HashMap<>();
        delayInWorkstations.put(WS1,new ContinuousVariableSummary("Delay in WS1"));
        delayInWorkstations.put(WS2,new ContinuousVariableSummary("Delay in WS2"));
        delayInWorkstations.put(WS3,new ContinuousVariableSummary("Delay in WS3"));
        delayInWorkstations.put(WS4,new ContinuousVariableSummary("Delay in WS4"));
        delayInWorkstations.put(WS5,new ContinuousVariableSummary("Delay in WS5"));
        jobDelays=new HashMap<>();
        jobDelays.put(Job1,new ContinuousVariableSummary("Job1 Delays"));
        jobDelays.put(Job2,new ContinuousVariableSummary("Job2 Delays"));
        jobDelays.put(Job3,new ContinuousVariableSummary("Job3 Delays"));
        workstationQueueLen=new HashMap<>();
        workstationQueueLen.put(WS1,new DiscreteTimeSeriesSummary("WS1 Queue Len",0));
        workstationQueueLen.put(WS1,new DiscreteTimeSeriesSummary("WS1 Queue Len",0));
        workstationQueueLen.put(WS1,new DiscreteTimeSeriesSummary("WS1 Queue Len",0));
    }

    public void init() {
        eventDispatcher.reset();
        eventDispatcher.schedule(EndSimulation,simulationLength);
        for (Workstation ws :delayInWorkstations.keySet()) {
            delayInWorkstations.get(ws).reset();
            workstationQueueLen.get(ws).reset();
            ws.reset();
        }
        for (JobType jobType:jobDelays.keySet()) {
            jobDelays.get(jobType).reset();
        }
    }

    public void simulate() {
        init();
        statistics();
        while(true) {
            eventDispatcher.timing();
            switch (eventDispatcher.getCurrentEventType()) {
                case JobArrival:
                    jobArrival();
                    break;
                case StationDeparture:
                    stationDeparture();
                    break;
            }
            if (eventDispatcher.getCurrentEventType()==EventType.EndSimulation){
                break;
            }
        }
    }

    private void statistics() {
        for(Workstation ws: workstationQueueLen.keySet()) {
            workstationQueueLen.get(ws).addValue(ws.getJobQueue().size(),eventDispatcher.getTime());
        }
    }

    private void stationDeparture() {
        Job finishedJob=eventDispatcher.getCurrentEventData();
        Workstation currentWorkstation=finishedJob.getCurrentWorkstation();
        currentWorkstation.setCurrentJob(null);
        if (!finishedJob.getJobType().isEndWorkstation(currentWorkstation)) {
            Workstation nextWorkstation=finishedJob.getJobType().getNextWorkstation(currentWorkstation);
            arriveWorkstation(finishedJob,nextWorkstation);
        }
        if (!currentWorkstation.getJobQueue().isEmpty()) {
            Job nextJob=currentWorkstation.getJobQueue().poll();
            processJob(nextJob,currentWorkstation);
        }
    }

    private void jobArrival() {
        Job newJob=new Job(jobGenerator.nextJobType(),eventDispatcher.getTime());
        Workstation ws=newJob.getJobType().getStartWorkstation();
        arriveWorkstation(newJob, ws);
    }

    private void arriveWorkstation(Job job, Workstation ws) {
        job.arriveStation(ws,eventDispatcher.getTime());
        if (ws.isBusy()) {
            ws.getJobQueue().add(job);
        } else {
            processJob(job,ws);
        }
    }

    private void processJob(Job job, Workstation ws) {
        double delay = eventDispatcher.getTime() - job.getArriveTime();
        delayInWorkstations.get(ws).addValue(delay);
        jobDelays.get(job.getJobType()).addValue(delay);
        ws.setCurrentJob(job);
        eventDispatcher.schedule(StationDeparture,job.getJobType().getServiceTime(ws).nextValue());
    }


}
