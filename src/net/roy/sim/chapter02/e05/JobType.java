package net.roy.sim.chapter02.e05;

import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import net.roy.sim.distribution.ExponentialVariable;

import java.util.HashMap;
import java.util.Map;

import static net.roy.sim.chapter02.e05.Workstation.*;


/**
 * Created by Roy on 2015/1/6.
 */
public class JobType {
    private Workstation startWorkstation;
    private Map<Workstation,Workstation> nextWorkstationTable;
    private Workstation endWorkstation;
    private Map<Workstation,ExponentialVariable> workstationServiceTimes;
    private String name;
    private Workstation preWorkstation=null;
    public final static JobType Job1;
    public final static JobType Job2;
    public final static JobType Job3;
    static {
        Job1=new JobType("Job1");
        Job1.setStartWorkstation(WS3,0.50);
        Job1.addWorkstation(WS1,0.60);
        Job1.addWorkstation(WS2,0.85);
        Job1.setEndWorkstation(WS5, 0.50);

        Job2=new JobType("Job2");
        Job2.setStartWorkstation(WS4,1.10);
        Job2.addWorkstation(WS1,0.80);
        Job2.setEndWorkstation(WS3, 0.75);

        Job3=new JobType("Job3");
        Job3.setStartWorkstation(WS2,1.20);
        Job3.addWorkstation(WS5,0.25);
        Job3.addWorkstation(WS1,0.70);
        Job3.addWorkstation(WS4,0.90);
        Job3.setEndWorkstation(WS3,1.00);

    }


    public JobType(String name) {
        this.name=name;
        nextWorkstationTable=new HashMap<>();
        workstationServiceTimes=new HashMap<>();
    }

    public void setStartWorkstation(Workstation workstation, double meanServiceTime) {
        startWorkstation=workstation;
        ExponentialVariable st=new ExponentialVariable(meanServiceTime);
        workstationServiceTimes.put(startWorkstation,st);
        preWorkstation=startWorkstation;
    }

    public void setEndWorkstation(Workstation workstation,double meanServiceTime) {
        endWorkstation=workstation;
        ExponentialVariable st=new ExponentialVariable(meanServiceTime);
        nextWorkstationTable.put(preWorkstation, workstation);
        workstationServiceTimes.put(endWorkstation,st);
        preWorkstation=null;
    }

    public void addWorkstation(Workstation workstation, double meanServiceTime) {
        ExponentialVariable st=new ExponentialVariable(meanServiceTime);
        nextWorkstationTable.put(preWorkstation,workstation);
        workstationServiceTimes.put(workstation,st);
        preWorkstation=workstation;
    }

    public ExponentialVariable getServiceTime(Workstation workstation) {
        return workstationServiceTimes.get(workstation);
    }

    public Workstation getNextWorkstation(Workstation currentWorkstation) {
        return nextWorkstationTable.get(currentWorkstation);
    }

    public Workstation getStartWorkstation() {
        return startWorkstation;
    }

    public Workstation getEndWorkstation() {
        return endWorkstation;
    }

    public String getName() {
        return name;
    }

    public boolean isEndWorkstation(Workstation workstation) {
        return endWorkstation==workstation;
    }
}
