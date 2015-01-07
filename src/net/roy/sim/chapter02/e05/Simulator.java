package net.roy.sim.chapter02.e05;

import net.roy.sim.distribution.ExponentialVariable;
import net.roy.sim.tools.ContinuousVariableSummary;
import net.roy.sim.tools.DiscreteTimeSeriesSummary;
import net.roy.sim.tools.EventDispatcher;

import java.util.*;

import static net.roy.sim.chapter02.e05.EventType.EndSimulation;
import static net.roy.sim.chapter02.e05.EventType.JobArrival;
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
    private Map<Workstation,DiscreteTimeSeriesSummary> workstationUsage;

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
        workstationQueueLen.put(WS2,new DiscreteTimeSeriesSummary("WS2 Queue Len",0));
        workstationQueueLen.put(WS3,new DiscreteTimeSeriesSummary("WS3 Queue Len",0));
        workstationQueueLen.put(WS4,new DiscreteTimeSeriesSummary("WS4 Queue Len",0));
        workstationQueueLen.put(WS5,new DiscreteTimeSeriesSummary("WS5 Queue Len",0));
        workstationUsage=new HashMap<>();
        workstationUsage.put(WS1,new DiscreteTimeSeriesSummary("WS1 Usage",0));
        workstationUsage.put(WS2,new DiscreteTimeSeriesSummary("WS2 Usage",0));
        workstationUsage.put(WS3,new DiscreteTimeSeriesSummary("WS3 Usage",0));
        workstationUsage.put(WS4,new DiscreteTimeSeriesSummary("WS4 Usage",0));
        workstationUsage.put(WS5,new DiscreteTimeSeriesSummary("WS5 Usage",0));

    }

    public void init() {
        eventDispatcher.reset();
        eventDispatcher.schedule(EndSimulation,simulationLength);
        for (Workstation ws :delayInWorkstations.keySet()) {
            delayInWorkstations.get(ws).reset();
            workstationQueueLen.get(ws).reset();
            workstationUsage.get(ws).reset();
            ws.reset();
        }
        jobDelays.forEach((jobType,jobDelay)->{
            jobDelay.reset();
        });
        eventDispatcher.schedule(JobArrival,interarrivalTime.nextValue());
    }

    public void simulate() {
        init();

        while(true) {
            eventDispatcher.timing();
            updateStatistics();
            //System.out.println(eventDispatcher.getTime()+" "+eventDispatcher.getCurrentEventType());
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

    private void updateStatistics() {
        workstationQueueLen.forEach(
                (ws,queueLen)->{
                    queueLen.addValue(ws.getJobQueue().size(),eventDispatcher.getTime());
                });
        workstationUsage.forEach(
                (ws,usage)->{
                    usage.addValue(ws.getBusyMachineCount(),eventDispatcher.getTime());
                }
        );
    }

    private void stationDeparture() {
        Job finishedJob=eventDispatcher.getCurrentEventData();
        Workstation currentWorkstation=finishedJob.getCurrentWorkstation();
        Workstation.Machine currentMachine=finishedJob.getCurrentMachine();
        currentMachine.currentJob=null;
        if (!finishedJob.getJobType().isEndWorkstation(currentWorkstation)) {
            Workstation nextWorkstation=finishedJob.getJobType().getNextWorkstation(currentWorkstation);
            arriveWorkstation(finishedJob,nextWorkstation);
        }
        if (!currentWorkstation.getJobQueue().isEmpty()) {
            Job nextJob=currentWorkstation.getJobQueue().poll();
            processJob(nextJob,currentWorkstation,currentMachine);
        }
    }

    private void jobArrival() {
        Job newJob=new Job(jobGenerator.nextJobType(),eventDispatcher.getTime());
        Workstation ws=newJob.getJobType().getStartWorkstation();
        arriveWorkstation(newJob, ws);
        eventDispatcher.schedule(JobArrival,interarrivalTime.nextValue());
    }

    private void arriveWorkstation(Job job, Workstation ws) {
        job.arriveStation(ws,eventDispatcher.getTime());
        Optional<Workstation.Machine> idleMachine=ws.getIdleMachine();
        if (idleMachine.isPresent()) {
            processJob(job,ws,idleMachine.get());
        } else {
            ws.getJobQueue().add(job);
        }
    }

    private void processJob(Job job, Workstation ws, Workstation.Machine machine) {
        double delay = eventDispatcher.getTime() - job.getStationArriveTime();
        delayInWorkstations.get(ws).addValue(delay);
        job.addDelay(delay);
        if (job.getJobType().isEndWorkstation(ws)) {
            jobDelays.get(job.getJobType()).addValue(job.getTotalDelay());
        }

        job.process(machine);
        machine.currentJob=job;
        eventDispatcher.schedule(StationDeparture,job.getJobType().getServiceTime(ws).nextValue(),
                job);
    }

    public static void main(String[] args) {
        JobGenerator jobGenerator=new JobGenerator();
        jobGenerator.addJob(0.3,Job1);
        jobGenerator.addJob(0.5,Job2);
        jobGenerator.addJob(0.2,Job3);

        Simulator simulator=new Simulator(0.25,jobGenerator,365*8);
        reportHeader(simulator);
        simulator.simulate();
        report(simulator);
    }

    private static void report(Simulator simulator) {
        System.out.println("Job types      Average total delay in queue");
        simulator.jobDelays.forEach((jobtype,jobdelay)->{
            System.out.println(String.format("  %s    %11.3f",jobtype.getName(),jobdelay.getMean()));
        });
        System.out.println(String.format("Overall average job total delay = %8.3f",
                simulator.jobDelays.values().stream().mapToDouble(jobDelay->jobDelay.getMean()*jobDelay.getCount()).sum()
                / simulator.jobDelays.values().stream().mapToDouble(jobDelay -> jobDelay.getCount()).sum()
                ));
        System.out.println("  Work      Average number     Average      Average delay");
        System.out.println(" station       in  queue     utilization      in queue");
        Workstation[] workstations=new Workstation[]{WS1,WS2,WS3,WS4,WS5};
        for (Workstation ws:workstations) {
            System.out.println(String.format("  %s   %14.3f   %14.3f  %14.3f",
                    ws.getId(), simulator.workstationQueueLen.get(ws).getTimeAverage(),
                    simulator.workstationUsage.get(ws).getTimeAverage()/ws.getNumOfMachines(),simulator.delayInWorkstations.get(ws).getMean()));
        }

    }

    private static void reportHeader(Simulator simulator) {
        System.out.println("Job-shop model");
        System.out.println("Station info:");
        System.out.println(WS1);
        System.out.println(WS2);
        System.out.println(WS3);
        System.out.println(WS4);
        System.out.println(WS5);
        System.out.println("Distribution function for job types "+simulator.jobGenerator);
        System.out.println(String.format("Mean interarrival time of jobs %11.3f",simulator.meanInterarrivalTime) );
        System.out.println(String.format("Length of the simulation %13.1f eight-hour days",simulator.simulationLength/8));
        System.out.println("Job type info:");
        System.out.println(Job1);
        System.out.println(Job2);
        System.out.println(Job3);

    }


}
