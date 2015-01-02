package net.roy.sim.tools;

import java.util.PriorityQueue;

/**
 * A class for dispatch events
 * Created by Roy on 2014/12/27.
 * @param <EventType>  the type of the event, should be an enum;
 * @param <DataType>  the type of the data associated with each event
 */
public class EventDispatcher<EventType,DataType> {
    private PriorityQueue<Event>  nextEventTimes=new PriorityQueue<>();
    private double time;
    private double lastEventTime;
    private Event currentEvent;
    public EventDispatcher() {
        reset();
    }

    /**
     * reset dispatcher for reuse
     */
    public void reset() {
        time=0;
        lastEventTime=0;
        nextEventTimes.clear();
    }

    /**
     * schedule an event at specified time
     * @param eventType type of the event
     * @param scheduleTime  event time
     */
    public void scheduleAbsoluteTime(EventType eventType, double scheduleTime) {
         scheduleAbsoluteTime(eventType, scheduleTime, null);
    }

    /**
     * schedule an event at specified time
     * @param eventType  type of the event
     * @param scheduleTime  event time
     * @param data  data of the event
     */
    public void scheduleAbsoluteTime(EventType eventType, double scheduleTime, DataType data) {
        if (scheduleTime<time) {
            throw new RuntimeException(String.format("Schedule Time %8.3f ahead of current clock %8.3f",
                    scheduleTime,time));
        }
        nextEventTimes.add(new Event(eventType,scheduleTime,data));
    }

    /**
     *  schedule an event happened later on
     * @param eventType type of the event
     * @param timeOffset  how long from now the event will occure
     */
    public void schedule(EventType eventType, double timeOffset ) {
        scheduleAbsoluteTime(eventType, time+timeOffset);
    }

    /**
     /**
     *  schedule an event happened later on
     * @param eventType type of the event
     * @param timeOffset  how long from now the event will occure
     * @param data  data of the event
     */
    public void schedule(EventType eventType, double timeOffset, DataType data) {
        scheduleAbsoluteTime(eventType, time+timeOffset,data);
    }

    /**
     * advance time to the next event
     */
    public void timing() {
        currentEvent=nextEventTimes.poll();
        if (currentEvent==null) {
            throw new RuntimeException("No event in queue!");
        }
        lastEventTime=time;
        time=currentEvent.time;
    }

    /**
     * get current time
     * @return current time
     */
    public double getTime() {
        return time;
    }

    /**
     * get type of the current event
     * @return  type of the current event
     */
    public EventType getCurrentEventType() {
        return currentEvent.eventType;
    }

    /**
     * get data of the current event
     * @return  data
     */
    public DataType getCurrentEventData() {
        return currentEvent.data;
    }

    /**
     * get time of the last event
     * @return  last event time
     */
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
