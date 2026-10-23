package CRM_Vtiger_BITM_BaseClass;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_WebDriverUtility;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ScreenshotUtility;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_LoginHomePage;

public class CRM_Vtiger_BITM_BaseClass {

    public WebDriver driver;
    public CRM_Vtiger_BITM_WebDriverUtility wu = new CRM_Vtiger_BITM_WebDriverUtility();

    protected String url;
    protected String username;
    protected String password;

    private static final String CONFIG_FILE = "CRM_Vtiger_BITM_CommonData.properties";

    @BeforeSuite(alwaysRun = true)
    public void dbConnection() {
        Reporter.log("Before Suite: Setup started", true);
    }

    @BeforeTest(alwaysRun = true)
    public void beforeTest() {
        Reporter.log("Before Test: Test execution started", true);
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IOException("Configuration file not found in classpath (src/test/resources): " + CONFIG_FILE);
            }
            properties.load(input);
        }
        return properties;
    }

    private String getRequiredProperty(Properties properties, String key) throws IOException {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IOException("Missing or empty property in " + CONFIG_FILE + ": " + key);
        }
        return value.trim();
    }

    @Parameters("browser")
    @BeforeClass(alwaysRun = true)
    public void navigateToApp(@Optional("chrome") String browser) throws IOException {
        Reporter.log("Before Class: Launching browser", true);

        try {
            Properties properties = loadProperties();
            url = getRequiredProperty(properties, "url");
            username = getRequiredProperty(properties, "username");
            password = getRequiredProperty(properties, "password");

            if (browser.equalsIgnoreCase("chrome")) {
                ChromeOptions settings = new ChromeOptions();
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);

                settings.setExperimentalOption("prefs", prefs);
                driver = new ChromeDriver(settings);
            } else if (browser.equalsIgnoreCase("edge")) {
                EdgeOptions settings = new EdgeOptions();
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);

                settings.setExperimentalOption("prefs", prefs);
                driver = new EdgeDriver(settings);
            } else {
                driver = wu.launchBrowser(browser);
            }

            if (driver == null) {
                throw new IllegalStateException("WebDriver could not be initialized.");
            }

            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));

            driver.get(url);
            Reporter.log("Application opened: " + url, true);

            CRM_Vtiger_BITM_LoginHomePage loginPage = new CRM_Vtiger_BITM_LoginHomePage(driver);

            if (!loginPage.isLoginPageDisplayed()) {
                throw new IllegalStateException("VTiger login form was not displayed. Current URL: " + driver.getCurrentUrl());
            }
            Reporter.log("Login form displayed successfully.", true);

            boolean isLoginValidationClass = getClass().getSimpleName().contains("Login");
            if (isLoginValidationClass) {
                Reporter.log("Login validation class detected. Automatic login skipped.", true);
                return;
            }

            loginPage.loginToApplication(username, password);
            Reporter.log("Login completed successfully.", true);

        } catch (IOException | RuntimeException e) {
            Reporter.log("Browser setup or login failed: " + e.getMessage(), true);
            if (driver != null) {
                try { driver.quit(); } catch (RuntimeException cleanupError) {
                    Reporter.log("Browser cleanup failed: " + cleanupError.getMessage(), true);
                } finally { driver = null; }
            }
            throw e;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void captureScreenshotOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String className = result.getTestClass().getRealClass().getSimpleName();
            String methodName = result.getMethod().getMethodName();
            String screenshotName = className + "_" + methodName;

            if (driver == null) {
                Reporter.log("Screenshot skipped because WebDriver is null: " + screenshotName, true);
                return;
            }

            try {
                String screenshotPath = CRM_Vtiger_BITM_ScreenshotUtility.captureScreenshot(driver, screenshotName);
                Reporter.log("Failure screenshot saved: " + screenshotPath, true);
            } catch (Exception e) {
                Reporter.log("Screenshot capture failed for " + screenshotName + ": " + e.getMessage(), true);
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        Reporter.log("After Class: Closing browser", true);
        if (driver != null) {
            try {
                driver.quit();
                Reporter.log("Browser closed successfully.", true);
            } catch (RuntimeException e) {
                Reporter.log("Error while closing browser: " + e.getMessage(), true);
            } finally {
                driver = null;
            }
        }
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        Reporter.log("After Test: Test execution completed", true);
    }

    @AfterSuite(alwaysRun = true)
    public void closeConnection() {
        Reporter.log("After Suite: Cleanup completed", true);
    }
}