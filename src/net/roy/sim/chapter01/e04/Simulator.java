package net.roy.sim.chapter01.e04;

import net.roy.sim.distribution.ExponentialVariable;
import net.roy.sim.distribution.IContiuousRandomVariable;
import net.roy.sim.distribution.IDiscreteRandomVariable;
import net.roy.sim.distribution.UniformVariable;
import net.roy.sim.tools.EventDispatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Simulation of an inventory system
 * Simulation modeling and analysis, 4ed, Chapter 1.5.3
 * using simulation tools
 * Created by Roy on 2014/12/27.
 */
public class Simulator {
    /* parameters of system */
    private int initialInventoryLevel;
    private IContiuousRandomVariable demandTime;
    private IDiscreteRandomVariable demandQuantity;
    private IContiuousRandomVariable deliveryLag;
    private int numOfMonths;
    private double setupCost;
    private double incrementalCost;
    private double holdingCost;
    private double shortageCost;

    /* System states */
    private int inventoryLevel;
    private EventDispatcher<EventType,Object> eventDispatcher=new EventDispatcher<>();
    private int orderAmount;

    /* statistics */
    private double integralShortage;
    private double integralHolding;
    private double totalOrderingCost;

    public Simulator(int initialInventoryLevel, IContiuousRandomVariable demandTime,
                     IDiscreteRandomVariable demandQuantity, IContiuousRandomVariable deliveryLag,
                     int numOfMonths, double setupCost, double incrementalCost, double holdingCost, double shortageCost) {
        this.initialInventoryLevel = initialInventoryLevel;
        this.demandTime = demandTime;
        this.demandQuantity = demandQuantity;
        this.deliveryLag = deliveryLag;
        this.numOfMonths = numOfMonths;
        this.setupCost = setupCost;
        this.incrementalCost = incrementalCost;
        this.holdingCost = holdingCost;
        this.shortageCost = shortageCost;
    }

    public static void main(String[] args) {
        DemandSizeVariable demandSizeVariable=new DemandSizeVariable();
        demandSizeVariable.addSize(1,0.167);
        demandSizeVariable.addSize(2,0.5);
        demandSizeVariable.addSize(3,0.833);
        demandSizeVariable.addSize(4,1);

        Simulator simulator=new Simulator(
                60,
                new ExponentialVariable(0.1),
                demandSizeVariable,
                new UniformVariable(0.5,1),
                120,
                32,3,1,5
        );
        reportHeader(simulator);
        reportSimulate(simulator,20,40);
        reportSimulate(simulator,20,60);
        reportSimulate(simulator,20,80);
        reportSimulate(simulator,20,100);
        reportSimulate(simulator,40,60);
        reportSimulate(simulator,40,80);
        reportSimulate(simulator,40,100);
        reportSimulate(simulator,60,80);
        reportSimulate(simulator,60,100);
    }
    private void simulate(int small, int big) {
        intialize();
        while (true) {
            eventDispatcher.timing();
            //System.out.println(eventDispatcher.getTime()+" "+eventDispatcher.getCurrentEventType());
            updateStatistics();
            switch (eventDispatcher.getCurrentEventType()) {
                case OrderArrival:
                    orderArrival();
                    break;
                case Demand:
                    demand();
                    break;
                case Evaluate:
                    evaluate(small,big);
                    break;
                case EndSimulation:
                    endSimulation();
                    break;
            }
            if (eventDispatcher.getCurrentEventType()== EventType.EndSimulation)
                break;
        }
    }

    private void endSimulation() {
        //TODO: clean up? any undone statistics?
    }

    private void evaluate(int small, int big) {
        if (inventoryLevel<small) {
            orderAmount=big-inventoryLevel;
            totalOrderingCost+=setupCost+orderAmount*incrementalCost;
            eventDispatcher.schedule(EventType.OrderArrival,
                    eventDispatcher.getTime() + deliveryLag.nextValue());
        }
        eventDispatcher.schedule(EventType.Evaluate,
                eventDispatcher.getTime() + 1.0);
    }

    private void demand() {
        int demandSize=demandQuantity.nextValue();
        inventoryLevel -= demandSize;
        eventDispatcher.schedule(EventType.Demand,
                eventDispatcher.getTime() + demandTime.nextValue());
    }

    private void orderArrival() {
        inventoryLevel+=orderAmount;
    }

    private void updateStatistics() {
        double timeSinceLastEvent =
                eventDispatcher.getTime()-eventDispatcher.getLastEventTime();

        if (inventoryLevel<0) {
            integralShortage += timeSinceLastEvent * (-inventoryLevel);
        } else if (inventoryLevel>0) {
            integralHolding += timeSinceLastEvent * inventoryLevel;
        }
    }



    private void intialize() {
        inventoryLevel=initialInventoryLevel;
        eventDispatcher.reset();

        totalOrderingCost=0;
        integralHolding=0;
        integralShortage=0;

        eventDispatcher.schedule(EventType.Demand,
                eventDispatcher.getTime() + demandTime.nextValue());
        eventDispatcher.schedule(EventType.Evaluate,
                eventDispatcher.getTime() + 0);
        eventDispatcher.schedule(EventType.EndSimulation,
                numOfMonths);
    }

    private static void reportSimulate(Simulator simulator, int small, int big) {
        double sumOrderingCost=0;
        double sumHoldingCost=0;
        double sumShortageCost=0;
        int times=10000;
        for (int i=0;i<times;i++) {
            simulator.simulate(small, big);
            sumOrderingCost+=simulator.totalOrderingCost / simulator.numOfMonths;
            sumHoldingCost+=simulator.integralHolding * simulator.holdingCost / simulator.numOfMonths;
            sumShortageCost+= simulator.integralShortage * simulator.shortageCost / simulator.numOfMonths;
        }
        double avgOrderingCost=sumOrderingCost/times;
        double avgHoldingCost=sumHoldingCost/times;
        double avgShorageCost=sumShortageCost/times;
        System.out.println(String.format("(%3d,%3d)%15.2f%15.2f%15.2f%15.2f",
                small,big,avgOrderingCost+avgHoldingCost+avgShorageCost,
                avgOrderingCost,avgHoldingCost,avgShorageCost));
    }

    private static void reportHeader(Simulator simulator) {
        System.out.println("Single-product inventory system");
        System.out.println(String.format("Initial inventory level %24d items",simulator.initialInventoryLevel));
        System.out.println("Demand Size Distributes:");
        ((DemandSizeVariable)simulator.demandQuantity).output();
        System.out.println(String.format("Mean interdemand time %26.2f", ((ExponentialVariable) simulator.demandTime).getMean()));
        System.out.println(String.format("Delivery lag range%29.2f to%10.2f months",
                ((UniformVariable)simulator.deliveryLag).getMin(),((UniformVariable)simulator.deliveryLag).getMax()));
        System.out.println(String.format("Length of the simulation%23d months",simulator.numOfMonths));
        System.out.println(String.format("K =%6.1f  i =%6.1f  h =%6.1f  pi =%6.1f",
                simulator.setupCost,simulator.incrementalCost,simulator.holdingCost,simulator.shortageCost));
        System.out.print("                 Average        Average");
        System.out.println("        Average        Average");
        System.out.print("  Policy       total cost    ordering cost");
        System.out.println("  holding cost   shortage cost");

    }

}
