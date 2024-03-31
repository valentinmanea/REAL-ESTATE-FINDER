package com.realestatefinder.Real.Estate.Finder.steps;

import com.realestatefinder.Real.Estate.Finder.db.RealEstateItemEntity;
import com.realestatefinder.Real.Estate.Finder.threading.ConsentTaskManager;
import com.realestatefinder.Real.Estate.Finder.config.SeleniumConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class RomimoStep {

    Logger logger = LoggerFactory.getLogger(RomimoStep.class);

    private final WebDriver driver;
    private WebDriverWait wait;


    public RomimoStep(SeleniumConfig seleniumConfig, ConsentTaskManager consentTaskManager) {
        this.driver = seleniumConfig.getDriver();
        this.wait = new WebDriverWait(driver, Duration.of(20, ChronoUnit.SECONDS));

        consentTaskManager.schedule(this::acceptConsentIfDisplayed, LocalDateTime.now());
    }

    public List<RealEstateItemEntity> getRealEstateItems() {
        openPage();
        acceptConsent();

        insertLocation();
        wait(1);

        clickSearchButton();
        wait(1);

        filter();
        wait(1);

        clickFilterButton();
        wait(1);

        return driver.findElements(By.tagName("data-articleid"))
                .stream()
                .map(this::mapToRealEstateItems)
                .toList();
    }

    private RealEstateItemEntity mapToRealEstateItems(WebElement webElement) {
        String description = webElement.getText();
        String url = webElement.findElement(By.tagName("a")).getAttribute("href");
        return RealEstateItemEntity.builder()
                .url(url)
                .description(description)
                .build();
    }

    private void wait(int seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }

    private void filter() {
        displayFilteringSection();

        countyFilter();
        wait(2);

        ownerFilter();
        wait(2);
    }

    private void displayFilteringSection() {
        WebElement filterButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[8]/div/div/div[1]/div/a")));
        filterButton.click();
    }

    private void clickFilterButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='btnb btn-primary showAds']"))).click();
    }

    private void ownerFilter() {
        WebElement elemetn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"mobileSearchModal\"]/div/div/div[3]/div[2]/div[3]/div/div/div[9]/span")));
        Actions actions = new Actions(driver);
        actions.moveToElement(elemetn).click().perform();
        logger.info("Filtered by owner");
    }

    private void countyFilter() {
        WebElement countyButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@class='btn slide-menu-toggle radius']")));
        countyButton.click();

        logger.info("Filter button pressed");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("mobileSearchModal")));
        driver.findElements(By.tagName("li"))
                .stream()
                .filter(element -> element.getText().contains("Bucuresti"))
                .findAny().
                get()
                .click();
        logger.info("{} county selected", "Bucuresti");
    }

//    private void subCountyFilter() {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(
//                By.id("mobileSearchModal")));
//        driver.findElements(By.tagName("li"))
//                .stream()
//                .filter(element -> element.getText().contains("Bucuresti"))
//                .findAny().
//                get()
//                .click();
//    }

    private void clickSearchButton() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("btn-search")));
        element.click();
        logger.info("Search button pressed.");
    }

    private void insertLocation() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("Location")));
        element.sendKeys("Sector 6, Bucuresti");
        logger.info("Location inserted");
    }

    public void openPage() {
        driver.get("https://www.romimo.ro/");
        logger.info("Web page opened.");
    }

    public void acceptConsent() {
        WebElement consentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='cl-consent__btn cl-consent-node-a']")));
        try {
            logger.info("Consent accepted in normal flow");
            consentElement.click();
        } catch (Exception e) {
            logger.info("Consent not accepted in normal flow, exception occured");
        }
    }

    private boolean acceptConsentIfDisplayed(LocalDateTime startDate) {
        try {
            WebElement consentElement = driver.findElement(By.xpath("//a[@class='cl-consent__btn cl-consent-node-a']"));
            if (consentElement.isDisplayed()) {
                consentElement.click();
                return true;
            }
            if (Math.abs(Duration.between(LocalDateTime.now(), startDate).toMillis()) > 5000) {
                logger.info("thread name {}", Thread.currentThread());
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Exception if displayed");
            return false;
        }
    }
}
