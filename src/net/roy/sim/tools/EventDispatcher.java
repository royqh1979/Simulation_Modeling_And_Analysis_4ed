package net.roy.sim.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A class for dispatch events
 * Created by Roy on 2014/12/27.
 */
public class EventDispatcher<EventType,DataType> {
    private PriorityQueue<Event>  nextEventTimes=new PriorityQueue<>();
    private double time;
    private double lastEventTime;
    private Event currentEvent;
    public EventDispatcher() {
        reset();
    }
    public void reset() {
        time=0;
        lastEventTime=0;
        nextEventTimes.clear();
    }
    public void schedule(EventType eventType, double scheduleTime) {
         schedule(eventType,scheduleTime,null);
    }
    public void schedule(EventType eventType, double scheduleTime, DataType data) {
        if (scheduleTime<time) {
            throw new RuntimeException(String.format("Schedule Time %8.3f ahead of current clock %8.3f",
                    scheduleTime,time));
        }
        nextEventTimes.add(new Event(eventType,scheduleTime,data));
    }
    public void timing() {
        currentEvent=nextEventTimes.poll();
        if (currentEvent==null) {
            throw new RuntimeException("No event in queue!");
        }
        lastEventTime=time;
        time=currentEvent.time;
    }
    public double getTime() {
        return time;
    }
    public EventType getCurrentEventType() {
        return currentEvent.eventType;
    }
    public DataType getCurrentEventData() {
        return currentEvent.data;
    }
    public double getLastEventTime() {
        return lastEventTime;
    }
    private class Event implements   Comparable<Event>{
        EventType eventType;
        double time;
        DataType data;

        public Event(EventType eventType, double time, DataType data) {
            this.eventType = eventType;
            this.time = time;
            this.data = data;
        }

        @Override
        public int compareTo(Event o) {
            return Double.compare(this.time,o.time);
        }
    }
}
