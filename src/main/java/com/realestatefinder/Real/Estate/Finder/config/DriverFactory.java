package com.realestatefinder.Real.Estate.Finder.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.concurrent.TimeUnit;
public class DriverFactory {

    public static WebDriver build(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        driver.manage().window().maximize();

        return driver;
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
}