package net.roy.sim.chapter02.e05;

import net.roy.sim.distribution.ExponentialVariable;

import java.util.*;

/**
 * Created by Roy on 2015/1/6.
 */
public class Workstation {
    private String id;
    private Queue<Job> jobQueue;
    private Machine[] machines;
    public final static Workstation WS1=new Workstation("WS1",3);
    public final static Workstation WS2=new Workstation("WS2",2);
    public final static Workstation WS3=new Workstation("WS3",4);
    public final static Workstation WS4=new Workstation("WS4",3);
    public final static Workstation WS5=new Workstation("WS5",1);


    private Workstation(String id,int numOfMachines) {
        this.id = id;
        jobQueue=new LinkedList<>();
        machines=new Machine[numOfMachines];
        for (int i=0;i<numOfMachines;i++) {
            machines[i]=new Machine();
        }
    }

    public String getId() {
        return id;
    }

    public void reset() {
        Arrays.stream(machines).forEach(machine->{
            machine.currentJob=null;
        });
        jobQueue.clear();
    }

    public Queue<Job> getJobQueue() {
        return jobQueue;
    }

    public Optional<Machine> getIdleMachine() {
        return Arrays.stream(machines).filter(machine->machine.currentJob==null).findAny();
    }

    public long getBusyMachineCount() {
        return Arrays.stream(machines).filter(machine->machine.currentJob!=null).count();
    }

    public double getNumOfMachines() {
        return machines.length;
    }

    public class Machine{
        public Job currentJob=null;
    }

    @Override
    public String toString() {
        return "Workstation{" +
                "id='" + id + '\'' +
                ", num of machines=" + machines.length +
                '}';
    }
}
