package CRM_Vtiger_BITM_FAQ;

import java.util.UUID;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_FAQPage;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_LoginHomePage;

public class CRM_Vtiger_BITM_FAQTest extends CRM_Vtiger_BITM_BaseClass {

    private CRM_Vtiger_BITM_FAQPage getFAQPage() {
        Assert.assertNotNull(driver, "WebDriver is not initialized.");
        return new CRM_Vtiger_BITM_FAQPage(driver);
    }

    private String getBaseUrl() {
        String currentUrl = driver.getCurrentUrl();
        int queryIndex = currentUrl.indexOf("?");
        return queryIndex >= 0 ? currentUrl.substring(0, queryIndex) : currentUrl;
    }

    private void openFAQList(CRM_Vtiger_BITM_FAQPage faqPage) {
        faqPage.openFAQList(getBaseUrl());
        Assert.assertTrue(faqPage.isFAQPageDisplayed(), "FAQ list page was not displayed.");
    }

    @Test(priority = 1, description = "TC-VT-FAQ-08: Verify navigation to FAQ list page", groups = {"Smoke"})
    public void verifyFAQPageTest() {
        CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();
        openFAQList(faqPage);
        Reporter.log("PASS: FAQ list page opened successfully.", true);
    }

    @Test(priority = 2, description = "TC-VT-FAQ-05: Verify FAQ visibility under Support in More menu", groups = {"Smoke"})
    public void verifyFAQInMoreMenuTest() {
        CRM_Vtiger_BITM_LoginHomePage homePage = new CRM_Vtiger_BITM_LoginHomePage(driver);
        homePage.navigateToFAQ();
        CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();
        Assert.assertTrue(faqPage.isFAQPageDisplayed(), "FAQ module failed to open via More navigation menu.");
        Reporter.log("PASS: FAQ navigation via More menu verified.", true);
    }

    @Test(priority = 3, description = "TC-VT-FAQ-09: Verify + icon opens New FAQ details form", groups = {"UI"})
    public void verifyCreateFAQFormDisplaysTest() {
        CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();
        openFAQList(faqPage);
        faqPage.clickCreateFAQ();
        Assert.assertTrue(faqPage.isCreateFAQFormDisplayed(), "New FAQ creation form was not displayed.");
        Reporter.log("PASS: New FAQ form displayed successfully.", true);
    }

    @Test(priority = 4, description = "TC-VT-FAQ-01: Verify creation of a new FAQ", groups = {"Functional"})
    public void createNewFAQTest() {
        CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();
        openFAQList(faqPage);

        String question = "How can a customer update their portal password? " + UUID.randomUUID().toString().substring(0, 5);
        String answer = "Use the Forgot Password option and follow the instructions sent to the registered email";
        String status = "Published";
        String category = "General";

        faqPage.createFAQ(question, status, category, answer, null);

        Assert.assertTrue(faqPage.isQuestionDisplayedOnDetailPage(question), "Saved FAQ question not displayed on detail page.");
        Assert.assertEquals(faqPage.getDisplayedStatus(), status, "Displayed Status does not match expected value.");
        Reporter.log("PASS: FAQ created successfully: " + question, true);
    }

    @Test(priority = 5, description = "TC-VT-FAQ-02: Verify saved FAQ using question/subject search", groups = {"Functional"})
    public void searchFAQByQuestionTest() {
        CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();
        openFAQList(faqPage);

        String question = "Search FAQ Test " + UUID.randomUUID().toString().substring(0, 5);
        String answer = "Use Forgot Password and follow instructions";
        faqPage.createFAQ(question, "Published", "General", answer, null);

        openFAQList(faqPage);
        faqPage.searchFAQByQuestion(question);
        Assert.assertTrue(faqPage.isQuestionListed(question), "Created FAQ was not found using Question search.");
        Reporter.log("PASS: FAQ search by Question verified.", true);
    }

    @Test(priority = 6, description = "TC-VT-FAQ-03: Verify editing and deleting a created FAQ", groups = {"Functional"})
    public void editAndDeleteFAQTest() {
        CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();
        openFAQList(faqPage);

        String question = "FAQ to Edit " + UUID.randomUUID().toString().substring(0, 5);
        String answer = "Initial answer";
        faqPage.createFAQ(question, "Published", "General", answer, null);

        faqPage.clickEdit();
        String updatedAnswer = "Updated during manual testing";
        faqPage.updateAnswer(updatedAnswer);
        Assert.assertEquals(faqPage.getDisplayedAnswer(), updatedAnswer, "Updated answer not displayed on detail page.");

        faqPage.deleteCurrentFAQ();
        openFAQList(faqPage);
        faqPage.searchFAQByQuestion(question);
        Assert.assertFalse(faqPage.isQuestionListed(question), "Deleted FAQ is still listed after deletion.");
        Reporter.log("PASS: FAQ edit and delete verified.", true);
    }

    @Test(priority = 7, description = "TC-VT-FAQ-06: Verify mandatory-field validation when saving an empty FAQ", groups = {"Negative"})
    public void verifyMandatoryFieldsValidationTest() {
        CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();
        openFAQList(faqPage);
        faqPage.clickCreateFAQ();
        String alertMessage = faqPage.clickSaveWithEmptyFieldsAndGetAlert();
        Assert.assertNotNull(alertMessage, "Expected mandatory validation alert was not triggered.");
        Reporter.log("PASS: Mandatory field validation alert: " + alertMessage, true);
    }

    @Test(priority = 8, description = "TC-VT-FAQ-07: Verify FAQ search using a different category", groups = {"Negative"})
    public void verifyFAQSearchMismatchedCategoryTest() {
        CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();
        openFAQList(faqPage);
        faqPage.searchFAQ("NonExistentCategory_XYZ", "Category");
        Assert.assertTrue(faqPage.isNoRecordsFoundDisplayed(), "Unrelated records were displayed for non-existent category.");
        Reporter.log("PASS: Mismatched search returned no records as expected.", true);
    }

    @Test(priority = 9, description = "TC-VT-FAQ-10: Verify basic FAQ creation flow", groups = {"Functional"})
    public void basicFAQCreationSmokeFlowTest() {
        CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();
        openFAQList(faqPage);
        String question = "FAQ smoke test - portal password reset " + UUID.randomUUID().toString().substring(0, 5);
        String answer = "Use Forgot Password and follow the email instructions";
        faqPage.createFAQ(question, "Published", "General", answer, null);
        Assert.assertTrue(faqPage.isQuestionDisplayedOnDetailPage(question), "Smoke test FAQ not created successfully.");
        Reporter.log("PASS: Basic FAQ creation flow verified.", true);
    }

    @Test(priority = 10, description = "TC-VT-FAQ-11: Verify end-to-end FAQ creation, search, and details validation", groups = {"System"})
    public void endToEndFAQTest() {
        CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();
        openFAQList(faqPage);

        String question = "E2E test - customer portal password reset " + UUID.randomUUID().toString().substring(0, 5);
        String answer = "Select Forgot Password and follow the registered-email instructions";
        String status = "Published";
        faqPage.createFAQ(question, status, "General", answer, null);

        openFAQList(faqPage);
        faqPage.searchFAQByQuestion(question);
        Assert.assertTrue(faqPage.isQuestionListed(question), "E2E FAQ not found in list.");

        faqPage.openFAQQuestion(question);
        Assert.assertTrue(faqPage.isQuestionDisplayedOnDetailPage(question), "E2E Question text mismatch on detail page.");
        Assert.assertEquals(faqPage.getDisplayedStatus(), status, "E2E Status mismatch on detail page.");
        Reporter.log("PASS: End-to-end FAQ creation, search, and detail verification completed.", true);
    }
}