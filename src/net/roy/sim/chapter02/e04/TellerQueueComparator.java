package net.roy.sim.chapter02.e04;

import java.util.Comparator;

/**
 * Created by Roy on 2015/1/2.
 */
public class TellerQueueComparator implements Comparator<Teller> {

    @Override
    public int compare(Teller o1, Teller o2) {
        return Integer.compare(o1.customerQueue.size(),o2.customerQueue.size());
    }
}
