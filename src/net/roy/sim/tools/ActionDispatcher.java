package net.roy.sim.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Roy on 2014/12/30.
 */
public class ActionDispatcher<EventType> {
    public Map<EventType,Action> actions=new HashMap<>();

    public void registerAction(EventType eventType, Action action) {
        actions.put(eventType,action);
    }

    public void act(EventType eventType) {
        Action action=actions.get(eventType);
        if (action!=null) {
            action.act();
        } else {
            throw new RuntimeException("No action of type "+eventType);
        }
    }

}
