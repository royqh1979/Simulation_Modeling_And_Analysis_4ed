package net.roy.sim.chapter02.e04;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Roy on 2015/1/2.
 */
public class Teller {
    private int id;
    Deque<Customer> customerQueue=new LinkedList<>();
    Customer currentCustomer=null;

    public Teller(int id) {
        this.id = id;
    }

    public void startService(Customer customer) {
        if (currentCustomer!=null)
            throw new RuntimeException("Teller in service!");
        this.currentCustomer=customer;
    }

    public void finishService() {
        currentCustomer=null;
    }

    public int getId() {
        return id;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public boolean isBusy() {
        return currentCustomer!=null;
    }
}
