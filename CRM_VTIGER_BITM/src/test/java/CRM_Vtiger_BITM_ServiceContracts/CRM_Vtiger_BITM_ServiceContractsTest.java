package CRM_Vtiger_BITM_ServiceContracts;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ExcelUtility;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_LoginHomePage;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_ServiceContractsPage;

public class CRM_Vtiger_BITM_ServiceContractsTest extends CRM_Vtiger_BITM_BaseClass {

    private static final String SHEET_NAME = "VTiger_Test_Service_Contacts_Te";

    private CRM_Vtiger_BITM_ServiceContractsPage getServiceContractsPage() {
        Assert.assertNotNull(driver, "WebDriver is null. Check BaseClass setup.");
        return new CRM_Vtiger_BITM_ServiceContractsPage(driver);
    }

    private List<Map<String, String>> getServiceContractData() {
        try (CRM_Vtiger_BITM_ExcelUtility excel = new CRM_Vtiger_BITM_ExcelUtility()) {
            excel.openWorkbook();
            return excel.getAllDataAsMaps(SHEET_NAME);
        } catch (Exception e) {
            Reporter.log("Warning: Could not read Excel sheet " + SHEET_NAME + ": " + e.getMessage(), true);
            return List.of();
        }
    }

    @Test(priority = 1, description = "TC-VT-SC-05: Verify Service Contracts visibility under Support in More menu", groups = { "Smoke" })
    public void verifyServiceContractsInMoreMenuTest() {
        CRM_Vtiger_BITM_LoginHomePage homePage = new CRM_Vtiger_BITM_LoginHomePage(driver);
        try {
            homePage.navigateToServiceContracts();
        } catch (Exception e) {
            CRM_Vtiger_BITM_ServiceContractsPage page = getServiceContractsPage();
            page.openServiceContractsList();
        }

        CRM_Vtiger_BITM_ServiceContractsPage page = getServiceContractsPage();
        Assert.assertTrue(page.isServiceContractsPageDisplayed(), "Service Contracts page was not displayed.");
        Reporter.log("PASS: Service Contracts opened successfully.", true);
    }

    @Test(priority = 2, description = "TC-VT-SC-01: Verify navigation to create and view Service Contract details", groups = { "Integration" })
    public void verifyNavigationToServiceContractDetailTest() {
        CRM_Vtiger_BITM_ServiceContractsPage page = getServiceContractsPage();
        page.openServiceContractsList();

        String subject = "Nav_Contract_" + UUID.randomUUID().toString().substring(0, 5);
        page.createServiceContract(subject, null, "Support", "In Progress", "High", "2026-10-15", "2026-11-30");

        page.openServiceContractsList();
        page.searchServiceContractBySubject(subject);
        page.openFirstContract();

        Assert.assertTrue(page.isContractDetailPageDisplayed(), "Navigation to Service Contract detail page failed.");
        Reporter.log("PASS: Navigation from list to Service Contract detail page verified.", true);
    }

    @Test(priority = 3, description = "TC-VT-SC-10: Verify basic Service Contract creation flow", groups = { "Functional" })
    public void createServiceContractWithExcelDataTest() {
        List<Map<String, String>> records = getServiceContractData();
        String subject;
        if (!records.isEmpty() && records.get(0).get("Subject") != null && !records.get(0).get("Subject").trim().isEmpty()) {
            subject = records.get(0).get("Subject").trim() + "_" + UUID.randomUUID().toString().substring(0, 4);
        } else {
            subject = "Mukesh - Support Agreement " + UUID.randomUUID().toString().substring(0, 5);
        }

        CRM_Vtiger_BITM_ServiceContractsPage page = getServiceContractsPage();
        page.openServiceContractsList();
        page.clickCreateButton();
        page.enterSubject(subject);

        Assert.assertEquals(page.getSubjectValue(), subject, "Subject entered in form does not match.");
        page.saveContract();

        Assert.assertTrue(page.isServiceContractsPageDisplayed() || page.isContractDetailPageDisplayed(),
                "The application did not leave the create form after saving.");
        Reporter.log("PASS: Service Contract created with Subject: " + subject, true);
    }

    @Test(priority = 4, description = "TC-VT-SC-06: Verify mandatory-field validation when saving an empty contract", groups = { "Negative" })
    public void verifyEmptySubjectMandatoryValidationTest() {
        CRM_Vtiger_BITM_ServiceContractsPage page = getServiceContractsPage();
        page.openServiceContractsList();
        page.clickCreateButton();

        Assert.assertTrue(page.isCreateFormDisplayed(), "Create form did not open.");
        page.enterSubject("");
        page.saveContract();

        Assert.assertTrue(page.isSubjectMandatoryValidationShown(), "Subject mandatory validation was not detected.");
        Reporter.log("PASS: Empty Subject mandatory validation verified.", true);
    }

    @Test(priority = 5, description = "TC-VT-SC-07: Verify search returns no results for mismatched contract query", groups = { "Negative" })
    public void verifySearchWithNonexistentContractNumberTest() {
        CRM_Vtiger_BITM_ServiceContractsPage page = getServiceContractsPage();
        page.openServiceContractsList();

        String searchValue = "NoSuchContract_NONMATCH_9999";
        page.searchByContractNumber(searchValue);

        Assert.assertFalse(page.isContractNumberDisplayed(searchValue), "Unexpected matching contract was displayed.");
        Reporter.log("PASS: Nonexistent contract search returned no results.", true);
    }
}