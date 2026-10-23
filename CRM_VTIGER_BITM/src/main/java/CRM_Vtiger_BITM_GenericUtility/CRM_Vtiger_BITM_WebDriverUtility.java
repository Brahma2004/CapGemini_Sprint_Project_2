package CRM_Vtiger_BITM_GenericUtility;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CRM_Vtiger_BITM_WebDriverUtility {

    private WebDriver driver;

    public WebDriver launchBrowser(String browser) {
        if (browser == null || browser.trim().isEmpty()) {
            browser = "chrome";
        }

        String selectedBrowser = browser.trim().toLowerCase();

        try {
            switch (selectedBrowser) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();

                    Map<String, Object> chromePrefs = new HashMap<>();
                    chromePrefs.put("credentials_enable_service", false);
                    chromePrefs.put("profile.password_manager_enabled", false);
                    chromePrefs.put("profile.password_manager_leak_detection", false);

                    chromeOptions.setExperimentalOption("prefs", chromePrefs);
                    chromeOptions.addArguments("--disable-save-password-bubble");
                    chromeOptions.addArguments("--disable-features=PasswordLeakDetection");
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--no-first-run");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--remote-allow-origins=*");

                    driver = new ChromeDriver(chromeOptions);
                    break;

                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();

                    Map<String, Object> edgePrefs = new HashMap<>();
                    edgePrefs.put("credentials_enable_service", false);
                    edgePrefs.put("profile.password_manager_enabled", false);
                    edgePrefs.put("profile.password_manager_leak_detection", false);

                    edgeOptions.setExperimentalOption("prefs", edgePrefs);
                    edgeOptions.addArguments("--disable-save-password-bubble");
                    edgeOptions.addArguments("--disable-notifications");
                    edgeOptions.addArguments("--no-first-run");
                    edgeOptions.addArguments("--disable-gpu");
                    edgeOptions.addArguments("--no-sandbox");
                    edgeOptions.addArguments("--disable-dev-shm-usage");
                    edgeOptions.addArguments("--remote-allow-origins=*");

                    driver = new EdgeDriver(edgeOptions);
                    break;

                default:
                    throw new IllegalArgumentException(
                        "Unsupported browser: " + browser + ". Supported browsers: chrome, edge.");
            }

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));
            driver.manage().window().maximize();

            System.out.println("Browser launched successfully: " + selectedBrowser);
            return driver;

        } catch (RuntimeException e) {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (RuntimeException ignored) {
                }
                driver = null;
            }
            throw new RuntimeException("Failed to launch browser: " + selectedBrowser
                    + ". Check browser installation, driver compatibility, and system resources.", e);
        }
    }

    public void waitForPageToLoad(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    public void waitForElementToBeVisible(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementToBeClickable(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void mouseHover(WebDriver driver, WebElement element) {
        Actions act = new Actions(driver);
        act.moveToElement(element).perform();
    }

    public void selectByVisibleText(WebElement element, String text) {
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    public void selectByValue(WebElement element, String value) {
        Select select = new Select(element);
        select.selectByValue(value);
    }

    public void selectByIndex(WebElement element, int index) {
        Select select = new Select(element);
        select.selectByIndex(index);
    }

    public void switchToWindowByTitle(WebDriver driver, String partialTitle) {
        Set<String> allWindowIds = driver.getWindowHandles();
        for (String windowId : allWindowIds) {
            driver.switchTo().window(windowId);
            if (driver.getTitle().contains(partialTitle)) {
                break;
            }
        }
    }

    public void switchToWindowByUrl(WebDriver driver, String partialUrl) {
        Set<String> allWindowIds = driver.getWindowHandles();
        for (String windowId : allWindowIds) {
            driver.switchTo().window(windowId);
            if (driver.getCurrentUrl().contains(partialUrl)) {
                break;
            }
        }
    }

    public void acceptAlert(WebDriver driver) {
        Alert alert = driver.switchTo().alert();
        alert.accept();
    }

    public void dismissAlert(WebDriver driver) {
        Alert alert = driver.switchTo().alert();
        alert.dismiss();
    }

    public String getAlertText(WebDriver driver) {
        Alert alert = driver.switchTo().alert();
        return alert.getText();
    }

    public void maximizeWindow() {
        if (driver != null) driver.manage().window().maximize();
    }

    public void minimizeWindow() {
        if (driver != null) driver.manage().window().minimize();
    }

    public void navigateToUrl(String url) {
        if (driver != null) driver.get(url);
    }

    public String getCurrentUrl() {
        return driver != null ? driver.getCurrentUrl() : null;
    }

    public String getPageTitle() {
        return driver != null ? driver.getTitle() : null;
    }

    public void refreshPage() {
        if (driver != null) driver.navigate().refresh();
    }

    public void closeCurrentBrowser() {
        if (driver != null) driver.close();
    }

    public void quitBrowser() {
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
            }
        }
    }
}