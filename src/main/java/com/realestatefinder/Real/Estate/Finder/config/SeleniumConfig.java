package com.realestatefinder.Real.Estate.Finder.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;
@Component
public class SeleniumConfig {

    private final WebDriver driver;

    public SeleniumConfig() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        driver.manage().window().maximize();
    }

    static {
        System.setProperty("webdriver.chrome.driver", findFile("chromedriver"));
    }

    private static String findFile(String fileName) {
        var path = "src/main/resources/";
        if (new File(path + fileName).exists())
            return path + fileName;
        return "";
    }

    public WebDriver getDriver() {
        return driver;
    }
}