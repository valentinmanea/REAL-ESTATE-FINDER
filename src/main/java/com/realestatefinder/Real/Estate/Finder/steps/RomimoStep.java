package com.realestatefinder.Real.Estate.Finder.steps;

import com.realestatefinder.Real.Estate.Finder.config.DriverManager;
import com.realestatefinder.Real.Estate.Finder.db.RealEstateItemEntity;
import com.realestatefinder.Real.Estate.Finder.threading.ThreadingManager;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.realestatefinder.Real.Estate.Finder.threading.ThreadingManager.invokeWithTimeout;

@Component
public class RomimoStep {

    Logger logger = LoggerFactory.getLogger(RomimoStep.class);

    private final DriverManager driverManager;
    private WebDriverWait wait;
    private String driverTag;


    public RomimoStep(ThreadingManager threadingManager, DriverManager driverManager) {
        this.driverManager = driverManager;

        threadingManager.schedule(this::acceptConsentIfDisplayed, LocalDateTime.now());
    }

    public List<RealEstateItemEntity> getAllRealEstateItems() throws TimeoutException {
        goToRealEstateTable();

        List<RealEstateItemEntity> all = new ArrayList<>();
        int count = 0;

        while(!getLinkForArrow().isEmpty()){
            List<RealEstateItemEntity> elements = getArticles()
                    .stream()
                    .map(this::mapToRealEstateItems)
                    .toList();
            all.addAll(elements);
            logger.info("elements page: {} {}", count, elements);
            count++;
            String attribute = getLinkForArrow();
            getArrowLiElement().click();
            logger.info("count:{}, attribute: {}", count, attribute);
            if(count > 2000){
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

            }
        }
        return all;
    }

    private WebElement getArrowLiElement() {
        return getCurrentDriver().findElement(By.xpath("//*[@id=\"content\"]/div/div/div[2]/ul/ul/div[3]")).findElement(By.tagName("li"));
    }

    private String getLinkForArrow() {
        return getArrowLiElement().findElement(By.tagName("a")).getAttribute("href");
    }

    public List<RealEstateItemEntity> getRealEstateItemsFirstPage() throws TimeoutException {
        goToRealEstateTable();

            return getArticles()
                .stream()
                .map(this::mapToRealEstateItems)
                .toList();
    }

    private void goToRealEstateTable() throws TimeoutException {
        invokeWithTimeout(10_000, this::init);
        invokeWithTimeout(10_000, this::openPage);
        invokeWithTimeout(10_000, this::acceptConsent);
        invokeWithTimeout(10_000, this::insertLocation);
        waitMs(500);
        invokeWithTimeout(15_000, this::clickSearchButton);
        waitMs(500);
        invokeWithTimeout(15_000, this::filter);
        waitMs(500);
        invokeWithTimeout(15_000, this::clickFilterButton);
        waitMs(500);
    }

    private List<WebElement> getArticles() {
        return getCurrentDriver().findElements(By.xpath("//*[@data-articleid]"));
    }

    private RealEstateItemEntity mapToRealEstateItems(WebElement webElement) {
        String description = webElement.getText();
        String url = webElement.findElement(By.tagName("a")).getAttribute("href");
        return RealEstateItemEntity.builder()
                .url(url)
                .description(description)
                .build();
    }

    private void waitMs(int ms) {
        getCurrentDriver().manage().timeouts().implicitlyWait(Duration.ofMillis(ms));
    }

    private void filter() {
        displayFilteringSection();
        waitMs(500);
        countyFilter();
        waitMs(1000);

        ownerFilter();
    }

    private void displayFilteringSection() {
        WebElement filterButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[8]/div/div/div[1]/div/a")));
        Actions actions = new Actions(getCurrentDriver());
        actions.moveToElement(filterButton).click().perform();
    }

    private void clickFilterButton() {
        WebElement filterButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='btnb btn-primary showAds']")));
        logger.info("filter button appeared, will be clicked");
        filterButton.click();
        logger.info("filter button clicked");
    }

    private void ownerFilter() {
        WebElement elemetn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"mobileSearchModal\"]/div/div/div[3]/div[2]/div[3]/div/div/div[9]/span")));
        Actions actions = new Actions(getCurrentDriver());
        actions.moveToElement(elemetn).click().perform();
        logger.info("Filtered by owner");
    }

    private void countyFilter() {
        WebElement countyButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@class='btn slide-menu-toggle radius']")));
        countyButton.click();

        logger.info("County dropdown pressed");
        waitMs(500);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("mobileSearchModal")));
        logger.info("Waited...");
        waitMs(500);

        List<WebElement> liElements = getCurrentDriver().findElements(By.tagName("li"));
        WebElement bucurestiElement = liElements
                .stream()
                .filter(element -> element.getText().contains("Bucuresti"))
                .findAny().
                get();
        bucurestiElement.click();
        logger.info("{} county selected", "Bucuresti");
    }

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
        getCurrentDriver().get("https://www.romimo.ro/");
        logger.info("Web page opened.");
    }

    public void init() {
        driverTag = driverManager.add();
        this.wait = new WebDriverWait(getCurrentDriver(), Duration.of(2, ChronoUnit.SECONDS));
    }

    private WebDriver getCurrentDriver() {
        return driverManager.get(driverTag);
    }

    public void acceptConsent() {
        WebElement consentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='cl-consent__btn cl-consent-node-a']")));
        try {
            consentElement.click();
            logger.info("Consent accepted in normal flow");
        } catch (Exception e) {
            logger.info("Consent not accepted in normal flow, exception occured");
        }
    }

    private boolean acceptConsentIfDisplayed(LocalDateTime startDate) {
        try {
            WebElement consentElement = getCurrentDriver().findElement(By.xpath("//a[@class='cl-consent__btn cl-consent-node-a']"));
            if (consentElement.isDisplayed()) {
                consentElement.click();
                return true;
            }
        } catch (Exception e) {
            logger.error("Exception if displayed");
            if (timeoutBreak(startDate)) {
                return true;
            }
            return false;
        }
        if (timeoutBreak(startDate)) {
            return true;
        }
        return false;

    }

    private boolean timeoutBreak(LocalDateTime startDate) {
        long abs = Math.abs(Duration.between(LocalDateTime.now(), startDate).toMillis());
        logger.info("abs {}", abs);
        if (abs > 2000) {
            logger.info("thread name {}", Thread.currentThread());
            return true;
        }
        return false;
    }

    public void close() {
        driverManager.close(driverTag);
    }
}
