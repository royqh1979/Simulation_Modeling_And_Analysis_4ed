package net.roy.sim.chapter02;

import net.roy.sim.chapter01.e03.EventType;
import net.roy.sim.tools.ActionDispatcher;

/**
 * Created by Roy on 2014/12/30.
 */
public class Simulator {
    public void test() {
        ActionDispatcher<EventType> actionDispatcher=new ActionDispatcher<>();
        actionDispatcher.registerAction(EventType.Demand,
                this::test1);
    }
    public void test1() {}
}
