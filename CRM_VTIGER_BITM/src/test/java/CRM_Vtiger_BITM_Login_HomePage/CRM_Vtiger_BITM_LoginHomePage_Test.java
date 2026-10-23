package CRM_Vtiger_BITM_Login_HomePage;

import java.time.Duration;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ScreenshotUtility;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_LoginHomePage;

public class CRM_Vtiger_BITM_LoginHomePage_Test extends CRM_Vtiger_BITM_BaseClass {

    private static final String INVALID_USERNAME = "invalid_username";

    private CRM_Vtiger_BITM_LoginHomePage loginHomePage;

    private CRM_Vtiger_BITM_LoginHomePage getLoginHomePage() {
        Assert.assertNotNull(driver, "WebDriver is not initialized.");
        if (loginHomePage == null) {
            loginHomePage = new CRM_Vtiger_BITM_LoginHomePage(driver);
        }
        return loginHomePage;
    }

    private void openLoginPage() {
        CRM_Vtiger_BITM_LoginHomePage page = getLoginHomePage();
        
        // If already logged in, log out first to reach the login screen
        if (driver.getCurrentUrl().contains("module=Home") || page.isHomePageDisplayed()) {
            page.clickLogout();
        }

        driver.get(url);
        Assert.assertTrue(page.isLoginPageDisplayed(), "Login page is not displayed. Current URL: " + driver.getCurrentUrl());
        Reporter.log("Login page opened successfully.", true);
    }

    // 1. UI: Login Page Controls Visibility
    @Test(priority = 1, description = "TC-VT-LOGIN-02: Verify login page elements visibility", groups = { "UI", "Smoke" })
    public void verifyLoginPageElementsVisibilityTest() {
        openLoginPage();
        CRM_Vtiger_BITM_LoginHomePage page = getLoginHomePage();

        Assert.assertTrue(page.isUsernameFieldDisplayed(), "Username field is not visible.");
        Assert.assertTrue(page.isPasswordFieldDisplayed(), "Password field is not visible.");
        Assert.assertTrue(page.isLoginButtonDisplayed(), "Login button is not visible.");
        Reporter.log("PASS: Login page controls and labels are visible and usable.", true);
    }

    // 2. FUNCTIONAL: Valid Login
    @Test(priority = 2, description = "TC-VT-LOGIN-01: Verify login with valid credentials", groups = { "Smoke", "Functional" })
    public void verifyLoginWithValidCredentialsTest() {
        openLoginPage();
        CRM_Vtiger_BITM_LoginHomePage page = getLoginHomePage();
        page.loginToApplication(username, password);

        Assert.assertTrue(page.isHomePageDisplayed(), "Home page was not displayed after valid login.");
        Reporter.log("PASS: User authenticated and VTiger Home page is displayed.", true);
    }

    // 3. UI: Home Page Elements & Navigation Visibility
    @Test(priority = 3, description = "TC-VT-LOGIN-03: Verify home page elements visibility", groups = { "UI", "Functional" })
    public void verifyHomePageElementsVisibilityTest() {
        CRM_Vtiger_BITM_LoginHomePage page = getLoginHomePage();
        if (!page.isHomePageDisplayed()) {
            openLoginPage();
            page.loginToApplication(username, password);
        }

        Assert.assertTrue(page.isHomePageHeadingDisplayed(), "Home page heading is missing.");
        Assert.assertTrue(page.isProductsModuleDisplayed(), "Products module link is not visible in navigation.");
        Assert.assertTrue(page.isContactsModuleDisplayed(), "Contacts module link is not visible in navigation.");
        Reporter.log("PASS: Home page navigation, Products, and Contacts modules are visible.", true);
    }

    // 4. NEGATIVE: Invalid Username Validation
    @Test(priority = 4, description = "TC-VT-LOGIN-04: Verify login with invalid username", groups = { "Negative" })
    public void verifyLoginWithInvalidUsernameTest() {
        openLoginPage();
        CRM_Vtiger_BITM_LoginHomePage page = getLoginHomePage();
        page.enterUsername(INVALID_USERNAME);
        page.enterPassword(password);

        CRM_Vtiger_BITM_ScreenshotUtility.captureScreenshot(driver, "verifyLoginWithInvalidUsernameTest_Input");
        page.clickLoginButton();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean rejected;
        try {
            wait.until(d -> page.isLoginPageDisplayed() || page.isErrorMessageDisplayed());
            rejected = page.isLoginPageDisplayed() && !page.isHomePageDisplayed();
        } catch (TimeoutException e) {
            rejected = !driver.getCurrentUrl().contains("module=Home");
        }

        Assert.assertTrue(rejected, "Invalid username was not rejected. Current URL: " + driver.getCurrentUrl());
        Reporter.log("PASS: Invalid username was rejected.", true);
    }

    // 5. REGRESSION: Navigation from Login to Home and Logout
    @Test(priority = 5, description = "TC-VT-LOGIN-08: Verify navigation from Login page to Home page and verify logout options", groups = { "Regression" })
    public void verifyLogoutOptionVisibilityAndLogoutTest() {
        CRM_Vtiger_BITM_LoginHomePage page = getLoginHomePage();
        if (!page.isHomePageDisplayed()) {
            openLoginPage();
            page.loginToApplication(username, password);
        }

        Assert.assertTrue(page.isLogoutDisplayed(), "Sign Out option is not displayed in the administrator menu.");
        Reporter.log("PASS: Sign Out option is verified.", true);

        page.clickLogout();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> page.isLoginPageDisplayed());
        Assert.assertTrue(page.isLoginPageDisplayed(), "User was not returned to Login page after logout.");
        Reporter.log("PASS: Logout succeeded and user returned to login page.", true);
    }
}