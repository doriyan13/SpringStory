package com.dori.SpringStory.events;

import com.dori.SpringStory.logger.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class EventManager {
    // Scheduler for the events -
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService worker= Executors.newVirtualThreadPerTaskExecutor();
    private static final HashSet<ScheduledFuture<?>> events = new HashSet<>();
    // Logger -
    private static final Logger logger = new Logger(EventManager.class);

//    public static <V> void addEvent(Callable<V> task, long delay) {
//        ScheduledFuture<V> event = scheduler.schedule(() ->worker.submit(task), delay, TimeUnit.SECONDS);
//        events.add(event);
//    }

    public static void addEvent(Runnable task, long delay) {
        ScheduledFuture<?> event = scheduler.schedule(() -> worker.execute(task), delay, TimeUnit.SECONDS);
        events.add(event);
    }

    public static Set<ScheduledFuture<?>> getAllActiveEvents() {
        return events;
    }

    @Scheduled(fixedRateString = "${app.event_manager.cleanup_interval_in_milliseconds}")
    public static void clearOldEvents() {
        logger.serverNotice("|> Start cleanup of all the old events... |>");
        if (!events.isEmpty()) {
            events.removeIf(Future::isDone);
        }
        logger.serverNotice("~ Finished cleanup! there still " + events.size() + " events ~");
    }
}
