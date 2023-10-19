package com.dori.SpringStory.events;

import com.dori.SpringStory.enums.EventType;
import com.dori.SpringStory.logger.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class EventManager {
    // Scheduler for the events -
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService worker = Executors.newVirtualThreadPerTaskExecutor();
    private static final HashMap<Integer, HashMap<EventType, ScheduledFuture<?>>> events = new HashMap<>();
    // Logger -
    private static final Logger logger = new Logger(EventManager.class);

    public static void addEvent(int eventID, EventType eventType, Runnable task, long delay) {
        events.putIfAbsent(eventID, new HashMap<>());
        if (events.get(eventID).containsKey(eventType)) {
            logger.warning("The event - " + eventID + " | " + eventType + " already in progress thus will be canceled and create a new!");
            events.get(eventID).get(eventType).cancel(false);
        }
        ScheduledFuture<?> event = scheduler.schedule(() -> worker.execute(task), delay, TimeUnit.SECONDS);
        events.get(eventID).put(eventType, event);
    }

    public static  Set<ScheduledFuture<?>> getAllActiveEvents() {
        Set<ScheduledFuture<?>> setOfEvents = new HashSet<>();
        events.values().forEach(eventMap -> setOfEvents.addAll(eventMap.values()));
        return setOfEvents;
    }

    @Scheduled(fixedRateString = "${app.event_manager.cleanup_interval_in_milliseconds}")
    public static void clearOldEvents() {
        logger.serverNotice("|> Start cleanup of all the old events... |>");
        if (!events.isEmpty()) {
            events.values().removeIf(mapOfEvents -> {
                mapOfEvents.values().removeIf(Future::isDone);
                return mapOfEvents.isEmpty();
            });
        }
        logger.serverNotice("~ Finished cleanup! there still " + events.size() + " events ~");
    }
}
