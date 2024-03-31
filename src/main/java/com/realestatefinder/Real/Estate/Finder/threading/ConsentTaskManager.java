package com.realestatefinder.Real.Estate.Finder.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

@Component
public class ConsentTaskManager {
    Logger logger = LoggerFactory.getLogger(ConsentTaskManager.class);

    private final Timer timer = new Timer();

    private TimerTask task;

    public void schedule(Function<LocalDateTime, Boolean> toExecuted, LocalDateTime startDate) {
        task = new TimerTask() {
            public void run() {
                Boolean applied = toExecuted.apply(startDate);
                logger.info("running... {} applied {}", LocalDateTime.now(), applied);
                if (Boolean.TRUE.equals(applied)) {
                    boolean cancelled = task.cancel();
                    if (cancelled) {
                        logger.info("Stop running the task to accept consent.");
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 500);
    }
}
