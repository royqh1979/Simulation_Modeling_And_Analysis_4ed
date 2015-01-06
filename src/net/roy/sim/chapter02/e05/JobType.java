package net.roy.sim.chapter02.e05;

import java.util.HashMap;
import java.util.Map;

import static net.roy.sim.chapter02.e05.Workstation.*;

/**
 * Created by Roy on 2015/1/6.
 */
public class JobType {
    private WorkstationInfo startWorkstation;
    private Map<WorkstationInfo,WorkstationInfo> nextWorkstationTable;
    private WorkstationInfo endWorkstation;
    private String name;

    public final static JobType Job1=new JobType("Job1",
            new WorkstationInfo(WS3,0.50),
            new WorkstationInfo(WS1,0.60),
            new WorkstationInfo(WS2,0.85),
            new WorkstationInfo(WS5,0.50));
    public final static JobType Job2=new JobType("Job1",
            new WorkstationInfo(WS4,1.10),
            new WorkstationInfo(WS1,0.80),
            new WorkstationInfo(WS3,0.75));
    public final static JobType Job3=new JobType("Job1",
            new WorkstationInfo(WS2,1.20),
            new WorkstationInfo(WS5,0.25),
            new WorkstationInfo(WS1,0.70),
            new WorkstationInfo(WS4,0.90),
            new WorkstationInfo(WS3,1.00));

    private JobType(String name, WorkstationInfo... routing) {
        this.name=name;
        startWorkstation=routing[0];
        endWorkstation=routing[routing.length-1];
        nextWorkstationTable =new HashMap<>();
        for (int i=0;i<routing.length-1;i++) {
            nextWorkstationTable.put(routing[i], routing[i + 1]);
        }
    }

    public WorkstationInfo getNextWorkstation(WorkstationInfo currentWorkstation) {
        return nextWorkstationTable.get(currentWorkstation);
    }

    public WorkstationInfo getStartWorkstation() {
        return startWorkstation;
    }

    public WorkstationInfo getEndWorkstation() {
        return endWorkstation;
    }

    public String getName() {
        return name;
    }
}
