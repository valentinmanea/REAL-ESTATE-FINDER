package com.realestatefinder.Real.Estate.Finder.config;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class DriverManager {
    private final Logger logger = LoggerFactory.getLogger(DriverManager.class);

    private static Map<String, WebDriver> WEB_DRIVERS = new HashMap<>();

    public String add() {
        WebDriver webDriver = DriverFactory.build();
        String tag = UUID.randomUUID().toString();
        WEB_DRIVERS.put(tag, webDriver);
        return tag;
    }

    public void close(String tag) {
        WebDriver webDriver = WEB_DRIVERS.get(tag);
        if (webDriver != null) {
            webDriver.close();
            WEB_DRIVERS.remove(tag);
            logger.info("Web driver removed for tag: {}", tag);

        } else {
            logger.error("Tried to remove a web driver for not found tag: {}", tag);
        }

    }

    public WebDriver get(String tag) {
        return WEB_DRIVERS.get(tag);
    }
}
