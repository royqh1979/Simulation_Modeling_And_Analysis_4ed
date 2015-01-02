package net.roy.sim.chapter02.e04;

import net.roy.sim.distribution.ExponentialVariable;
import net.roy.sim.tools.ContinuousTimeSeriesSummary;
import net.roy.sim.tools.ContinuousVariableSummary;
import net.roy.sim.tools.DiscreteTimeSeriesSummary;
import net.roy.sim.tools.EventDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntBinaryOperator;

/**
 * a multiteller bank with jockeying
 * chapter 2.6
 * Created by Roy on 2015/1/1.
 */
public class Simulator {
    /* parameters */
    private double doorCloseTime;
    private double meanInterarrivalTime;
    private double meanServiceTime;
    /* parameters change at each simulation */
    private int numOfTellers;

    private ExponentialVariable interarrivalTime;
    private ExponentialVariable serviceTime;
    private TellerQueueComparator tellerQueueComparator=new TellerQueueComparator();
    /* system states */

    EventDispatcher<EventType,Customer> eventDispatcher=new EventDispatcher<>();
    List<Teller> tellers=new ArrayList<>();

    /* statistics */
    ContinuousVariableSummary customerDelays=new ContinuousVariableSummary("Customer Delays");
    DiscreteTimeSeriesSummary customersInQueue=new DiscreteTimeSeriesSummary("Customers in queue",0);

    public Simulator(double doorCloseTime, double meanInterarrivalTime, double meanServiceTime) {
        this.doorCloseTime = doorCloseTime;
        this.meanInterarrivalTime = meanInterarrivalTime;
        this.meanServiceTime = meanServiceTime;
        this.interarrivalTime=new ExponentialVariable(meanInterarrivalTime);
        this.serviceTime=new ExponentialVariable(meanServiceTime);
    }

    public void simulate(int numOfTellers) {
        this.numOfTellers=numOfTellers;
        init();
        while(true) {
            eventDispatcher.timing();
            updateStatistics();
            switch (eventDispatcher.getCurrentEventType()) {
                case Arrival:
                    arrival();
                    break;
                case Departure:
                    departure();
                    break;
            }
            if (isSimFinished()){
                break;
            }
        }
    }

    private void updateStatistics() {
        customersInQueue.addValue(
                tellers.stream().mapToInt(t->t.customerQueue.size()).sum(),
                eventDispatcher.getTime());
    }

    private void departure() {
        Customer customer=eventDispatcher.getCurrentEventData();
        if (customer==null)
            throw new RuntimeException("Customer is null!");
        Teller teller=customer.getTeller();
        customer.unservice();
        jockeyTellers(teller);
        if (!teller.customerQueue.isEmpty()) {
            Customer newCustomer=teller.customerQueue.poll();
            startService(teller,newCustomer);
        }
    }

    private void jockeyTellers(Teller teller) {
        Teller longsetQueueTeller=tellers.stream().max(tellerQueueComparator).get();
        if (longsetQueueTeller.customerQueue.size()>teller.customerQueue.size()+1) {
            teller.customerQueue.addLast(longsetQueueTeller.customerQueue.pollLast());
        }
    }

    private void arrival() {
        Teller teller=chooseTeller();
        Customer customer=new Customer(eventDispatcher.getTime(),
                serviceTime.nextValue());

        if (teller.isBusy()) {
            teller.customerQueue.addLast(customer);
        } else {
            startService(teller, customer);
        }
        if (eventDispatcher.getTime()<doorCloseTime) {
            eventDispatcher.schedule(EventType.Arrival,interarrivalTime.nextValue());
        }
    }

    private void startService(Teller teller, Customer customer) {
        customerDelays.addValue(eventDispatcher.getTime()-customer.getArriveTime());
        customer.serviceBy(teller);
        eventDispatcher.schedule(EventType.Departure,customer.getServiceTime(),
                customer);
    }

    private Teller chooseTeller() {
        //find idle teller
        Optional<Teller> teller=tellers.stream().filter(t->!t.isBusy()).findAny();
        if (teller.isPresent()){
            return teller.get();
        }
        //return the teller has the shortest queue
        return tellers.stream().min(tellerQueueComparator).get();
    }


    private boolean isSimFinished() {
        //Door is closed
        if (eventDispatcher.getTime()<doorCloseTime)
            return false;
        //no teller is busy
        if(tellers.stream().anyMatch(t->t.isBusy())){
            return false;
        }
        //all teller's queue is empty
        return tellers.stream().allMatch(t->t.customerQueue.isEmpty());
    }

    private void init() {
        eventDispatcher.reset();
        tellers.clear();
        customersInQueue.reset();
        customerDelays.reset();

        for (int i=1;i<=numOfTellers;i++) {
            Teller teller=new Teller(i);
            tellers.add(teller);
        }

        eventDispatcher.schedule(EventType.Arrival, interarrivalTime.nextValue());
    }

    public static  void main(String[] args) {
        Simulator simulator=new Simulator(8*60,1,4.5);
        reportHeader(simulator);
        for (int i=4;i<=10;i++) {
            simulator.simulate(i);
            reportSimulate(simulator);
        }
    }

    private static void reportHeader(Simulator simulator) {
        System.out.println("Multiteller bank with separate queues & jockeying");
        System.out.println(String.format("Number of tellers%16d to %3d",4,10));
        System.out.println(String.format("Mean interarrival time%11.3f minutes",simulator.meanInterarrivalTime));
        System.out.println(String.format("Mean service time%16.3f minutes",simulator.meanServiceTime));
        System.out.println(String.format("bank closes after%16.3f hours",simulator.doorCloseTime/60));

    }

    private static void reportSimulate(Simulator simulator){
        System.out.println(String.format("With%2d tellers, average number in queue = %10.3f",
                simulator.numOfTellers,simulator.customersInQueue.getMean()));
        System.out.println(String.format("Delays in queue:    Average      Count        Max       Min"));
        System.out.println(String.format("                %12.3f%12.3f%12.3f%12.3f",
                simulator.customerDelays.getMean(),
                simulator.customerDelays.getCount(),
                simulator.customerDelays.getMax(),
                simulator.customerDelays.getMin()));

    }
}
