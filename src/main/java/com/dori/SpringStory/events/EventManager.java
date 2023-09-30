package com.dori.SpringStory.events;

import com.dori.SpringStory.logger.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

@Component
public class EventManager {
    // Scheduler for the events -
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(50);
    private static final HashSet<ScheduledFuture<?>> events = new HashSet<>();
    // Logger -
    private static final Logger logger = new Logger(EventManager.class);

    public static <V> void addEvent(Callable<V> task, long delay) {
        ScheduledFuture<V> event = scheduler.schedule(task, delay, TimeUnit.SECONDS);
        events.add(event);
    }

    public static void addEvent(Runnable task, long delay) {
        ScheduledFuture<?> event = scheduler.schedule(task, delay, TimeUnit.SECONDS);
        events.add(event);
    }

    public static Set<ScheduledFuture<?>> getAllActiveEvents() {
        return events;
    }

    @Scheduled(fixedRateString = "${app.event_manager.cleanup_interval_in_milliseconds}")
    public static void clearOldEvents() {
        logger.serverNotice("|> Start cleanup of all the old events... |>");
        if (events.size() > 0) {
            events.removeIf(Future::isDone);
        }
        logger.serverNotice("~ Finished cleanup! there still " + events.size() + " events ~");
    }
}
