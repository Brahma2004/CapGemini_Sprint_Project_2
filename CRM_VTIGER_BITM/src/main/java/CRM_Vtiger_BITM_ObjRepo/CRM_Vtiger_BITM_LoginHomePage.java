package CRM_Vtiger_BITM_ObjRepo;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

public class CRM_Vtiger_BITM_LoginHomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameTextField = By.name("user_name");
    private final By passwordTextField = By.name("user_password");
    private final By loginButton = By.id("submitButton");
    private final By errorMessage = By.xpath("//div[contains(@class,'errorMessage') or contains(text(),'You must specify a valid')]");

    private final By homePageHeading = By.xpath("//td[contains(@class,'moduleName') and contains(normalize-space(),'Home')] "
            + "| //a[contains(@class,'hdrLink') and contains(normalize-space(),'Home')] "
            + "| //*[contains(normalize-space(.),'My Home Page')] "
            + "| //td[contains(normalize-space(),'Home') and @class='heading2']");
    private final By homeMenu = By.xpath("//a[contains(@href,'module=Home')]");
    private final By userNameMarker = By.cssSelector("span.userName");
    private final By productsModule = By.xpath("//a[normalize-space()='Products']");
    private final By contactsModule = By.xpath("//a[normalize-space()='Contacts']");

    private final By moreMenu = By.xpath("//a[contains(text(),'More') or contains(normalize-space(),'More') or contains(@href,'#')][contains(@class,'tab') or @onmouseover]");
    private final By projectsSubMenu = By.xpath("//a[@name='Projects' or contains(@href,'module=Project&action=index')]");
    private final By projectTasksSubMenu = By.xpath("//a[@name='Project Tasks' or contains(@href,'module=ProjectTask')]");
    private final By projectMilestonesSubMenu = By.xpath("//a[@name='Project Milestones' or contains(@href,'module=ProjectMilestone')]");
    private final By faqSubMenu = By.xpath("//a[@name='FAQ' or contains(@href,'module=Faq')]");
    private final By serviceContractsSubMenu = By.xpath("//a[@name='Service Contracts' or contains(@href,'module=ServiceContracts')]");

    private final By adminIcon = By.xpath("//img[@src='themes/softed/images/user.PNG']");
    private final By signOutLink = By.xpath("//a[normalize-space()='Sign Out' or normalize-space()='Logout']");

    public CRM_Vtiger_BITM_LoginHomePage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null.");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void enterUsername(String username) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameTextField));
        field.clear();
        if (username != null && !username.isEmpty()) {
            field.sendKeys(username);
        }
    }

    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordTextField));
        field.clear();
        if (password != null && !password.isEmpty()) {
            field.sendKeys(password);
        }
    }

    public void clickLoginButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        try {
            button.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException | StaleElementReferenceException e) {
            WebElement freshButton = wait.until(ExpectedConditions.presenceOfElementLocated(loginButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", freshButton);
        }
    }

    public void loginToApplication(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        if (!waitForHomePage(Duration.ofSeconds(20))) {
            throw new IllegalStateException("Login could not be confirmed. Current URL: " + driver.getCurrentUrl());
        }
    }

    public void performInvalidLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public boolean isLoginPageDisplayed() {
        WebDriverWait loginWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            return loginWait.until(d -> {
                try {
                    WebElement u = d.findElement(usernameTextField);
                    WebElement p = d.findElement(passwordTextField);
                    WebElement b = d.findElement(loginButton);
                    return u.isDisplayed() && p.isDisplayed() && b.isDisplayed();
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isUsernameFieldDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(usernameTextField)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isPasswordFieldDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(passwordTextField)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isLoginButtonDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessage))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getErrorMessageText() {
        try {
            WebElement err = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return err.getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public boolean isHomePageDisplayed() {
        return waitForHomePage(Duration.ofSeconds(10));
    }

    public boolean isHomeMenuDisplayed() {
        return isElementVisible(homeMenu);
    }

    public boolean isHomePageHeadingDisplayed() {
        return isElementVisible(homePageHeading);
    }

    public boolean isUserNameDisplayed() {
        return isElementVisible(userNameMarker);
    }

    public boolean isProductsModuleDisplayed() {
        return isElementVisible(productsModule);
    }

    public boolean isContactsModuleDisplayed() {
        return isElementVisible(contactsModule);
    }

    public void navigateToProjects() {
        try {
            hoverOver(moreMenu);
            wait.until(ExpectedConditions.elementToBeClickable(projectsSubMenu)).click();
        } catch (Exception e) {
            driver.get(getBaseUrl() + "?module=Project&action=index");
        }
    }

    public void navigateToProjectTasks() {
        try {
            hoverOver(moreMenu);
            wait.until(ExpectedConditions.elementToBeClickable(projectTasksSubMenu)).click();
        } catch (Exception e) {
            driver.get(getBaseUrl() + "?module=ProjectTask&action=index");
        }
    }

    public void navigateToProjectMilestones() {
        try {
            hoverOver(moreMenu);
            wait.until(ExpectedConditions.elementToBeClickable(projectMilestonesSubMenu)).click();
        } catch (Exception e) {
            driver.get(getBaseUrl() + "?module=ProjectMilestone&action=index");
        }
    }

    public void navigateToFAQ() {
        try {
            hoverOver(moreMenu);
            wait.until(ExpectedConditions.elementToBeClickable(faqSubMenu)).click();
        } catch (Exception e) {
            driver.get(getBaseUrl() + "?module=Faq&action=index");
        }
    }

    public void navigateToServiceContracts() {
        try {
            hoverOver(moreMenu);
            wait.until(ExpectedConditions.elementToBeClickable(serviceContractsSubMenu)).click();
        } catch (Exception e) {
            driver.get(getBaseUrl() + "?module=ServiceContracts&action=index");
        }
    }

    public boolean isLogoutDisplayed() {
        WebElement icon = wait.until(ExpectedConditions.presenceOfElementLocated(adminIcon));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", icon);
        try {
            new Actions(driver).moveToElement(icon).perform();
        } catch (Exception ignored) {}

        if (!isElementVisible(signOutLink)) {
            icon.click();
        }
        return wait.until(ExpectedConditions.visibilityOfElementLocated(signOutLink)).isDisplayed();
    }

    public void clickLogout() {
        if (!isElementVisible(signOutLink)) {
            isLogoutDisplayed();
        }
        WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(signOutLink));
        try {
            logout.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logout);
        }
        Reporter.log("Logout clicked successfully.", true);
    }

    public void clickHomeMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(homeMenu)).click();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    private String getBaseUrl() {
        String currentUrl = driver.getCurrentUrl();
        int queryIndex = currentUrl.indexOf("?");
        return queryIndex >= 0 ? currentUrl.substring(0, queryIndex) : currentUrl;
    }

    private boolean waitForHomePage(Duration timeout) {
        WebDriverWait homeWait = new WebDriverWait(driver, timeout);
        try {
            return homeWait.until(d -> {
                String currentUrl = d.getCurrentUrl();
                boolean homeUrl = currentUrl.contains("module=Home") && currentUrl.contains("action=index");
                boolean userVisible = d.findElements(userNameMarker).stream().anyMatch(e -> {
                    try {
                        return e.isDisplayed();
                    } catch (StaleElementReferenceException ex) {
                        return false;
                    }
                });
                return homeUrl || userVisible;
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void hoverOver(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Actions(driver).moveToElement(element).perform();
    }

    private boolean isElementVisible(By locator) {
        try {
            for (WebElement element : driver.findElements(locator)) {
                if (element.isDisplayed()) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}