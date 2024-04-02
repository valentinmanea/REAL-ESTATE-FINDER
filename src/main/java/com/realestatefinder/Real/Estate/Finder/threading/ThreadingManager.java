package com.realestatefinder.Real.Estate.Finder.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@Component
public class ThreadingManager {
    Logger logger = LoggerFactory.getLogger(ThreadingManager.class);

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


    public static void invokeWithTimeout(long timeoutInMillis, Runnable method) throws TimeoutException {
        Thread thread = new Thread(method);

        thread.start();

        try {
            thread.join(timeoutInMillis); // Wait for the thread to complete or timeout
            if (thread.isAlive()) { // If the thread is still alive after join, it means it's still running
                thread.interrupt(); // Interrupt the thread
                throw new TimeoutException("Method execution timed out");
            }
        } catch (InterruptedException e) {
            // Handle interrupted exception
            e.printStackTrace();
        }
    }
}
