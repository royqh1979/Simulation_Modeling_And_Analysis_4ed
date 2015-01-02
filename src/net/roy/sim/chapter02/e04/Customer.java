package net.roy.sim.chapter02.e04;

/**
 * Created by Roy on 2015/1/2.
 */
public class Customer {
    private double arriveTime;
    private double serviceTime;
    private Teller teller;

    public Customer(double arriveTime, double serviceTime) {
        this.arriveTime = arriveTime;
        this.serviceTime = serviceTime;
        teller=null;
    }

    public void serviceBy(Teller teller) {
        //System.out.println("service "+teller.getId());
        teller.startService(this);
        this.teller=teller;
    }

    public void unservice() {
        //System.out.println("unservice "+teller.getId());
        teller.finishService();
        teller=null;
    }

    public double getArriveTime() {
        return arriveTime;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public Teller getTeller() {
        return teller;
    }
}
