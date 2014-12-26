package net.roy.sim.chapter01_1;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Demo Program for Book "Simulation Modeling and Analysis, 4ed"
 * Chapter 1.4.4
 * A M/M/1 Service simulator
 * Created by Roy on 2014/12/25.
 */
public class Simulator {
    /* parameters of simulator */
    private double meanInterarrival;
    private double meanService;
    private double numCustomerNeeded;
    /* states of the system */
    private LinkedList<CustomerInfo> customerQueue;
    private Map<EventType,Double> nextEventTimes;
    private ServerStatus serverStatus;
    private double simTime;
    private double timeOfLastEvent;
    private int customerServed;

    /* statistics of the system*/
    private double totalDelay;
    private double integralNumInQueue;
    private double integralServerStatus;
    private RandomDataGenerator randomDataGenerator;

    public Simulator(double meanInterarrival, double meanService, int numCustomerNeeded) {
        this.meanInterarrival = meanInterarrival;
        this.meanService = meanService;
        this.numCustomerNeeded = numCustomerNeeded;
        /* initialize random data generator */
        randomDataGenerator=new RandomDataGenerator();
        /* initialize the simulation clock */
        simTime = 0;
        /* initialize the state variables */
        serverStatus=ServerStatus.Idle;
        customerServed=0;
        customerQueue=new LinkedList<>();
        totalDelay=0;
        integralNumInQueue=0;
        integralServerStatus=0;
        nextEventTimes=new HashMap<>();

        nextEventTimes.put(EventType.CustomerArrival,
                simTime +randomDataGenerator.nextExponential(this.meanInterarrival));
        nextEventTimes.put(EventType.CustomerDepart,
                Double.MAX_VALUE);
    }

    public double getTotalDelay() {
        return totalDelay;
    }

    public double getIntegralNumInQueue() {
        return integralNumInQueue;
    }

    public double getIntegralServerStatus() {
        return integralServerStatus;
    }

    public static void main(String[] args) {
        Simulator simulator = new Simulator(1, 0.5, 100000);
        simulator.simulate();
        System.out.println("Customer served:" + simulator.getCustomerServed());
        System.out.println("Mean delay:" + simulator.getTotalDelay() / simulator.getSimTime());
        System.out.println("Average number in queue :" + simulator.getIntegralNumInQueue() / simulator.getSimTime());
        System.out.println("Server utility: " + simulator.getTotalDelay() / simulator.getSimTime());


    }

    public void simulate(){
        while (customerServed<numCustomerNeeded) {
            EventType nextEvent=timing();
            updateStatistics();
            switch (nextEvent) {
                case CustomerArrival:
                    arrive();
                    break;
                case CustomerDepart:
                    depart();
                    break;
            }

        }
    }

    private void updateStatistics() {
        double timeSinceLastEvent;
        timeSinceLastEvent= simTime -timeOfLastEvent;
        timeOfLastEvent= simTime;

        integralNumInQueue += timeSinceLastEvent * customerQueue.size();
        if (serverStatus==ServerStatus.Busy) {
            integralServerStatus += timeSinceLastEvent;
        }
    }

    private EventType timing() {
        double minNextEventTime=Double.MAX_VALUE;
        EventType nextEventType=EventType.Unknown;
        for (EventType eventType: nextEventTimes.keySet()) {
            double t=nextEventTimes.get(eventType);
            if (t<minNextEventTime) {
                nextEventType=eventType;
                minNextEventTime=t;
            }
        }
        if (nextEventType==EventType.Unknown) {
            throw new RuntimeException(String.format("Event list empty at time %f", simTime));
        }
        simTime=minNextEventTime;
        return nextEventType;
    }

    private void depart() {
        customerServed++;
        if (customerQueue.isEmpty()) {
            nextEventTimes.put(EventType.CustomerDepart, Double.MAX_VALUE);
            serverStatus = ServerStatus.Idle;
        } else {
            CustomerInfo customerInfo=customerQueue.removeFirst();
            Double delay= simTime - customerInfo.getArrivalTime();
            totalDelay+=delay;
            nextEventTimes.put(EventType.CustomerDepart,
                    simTime +
                            randomDataGenerator.nextExponential(meanService));
        }

    }

    private void arrive() {

        nextEventTimes.put(EventType.CustomerArrival,
                simTime +
                        randomDataGenerator.nextExponential(meanInterarrival));

        if (serverStatus == ServerStatus.Idle) {
            serverStatus = ServerStatus.Busy;
            nextEventTimes.put(EventType.CustomerDepart,
                    simTime +randomDataGenerator.nextExponential(meanService));
        }    else {
            CustomerInfo customerInfo=new CustomerInfo(simTime);
            customerQueue.addLast(customerInfo);
        }
        
    }

    public double getCustomerServed() {
        return customerServed;
    }

    public double getSimTime() {
        return simTime;
    }
}
