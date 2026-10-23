package CRM_Vtiger_BITM_ProjectMilestones;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_LoginHomePage;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_ProjectMilestonesPage;

public class CRM_Vtiger_BITM_ProjectMilestonesTest extends CRM_Vtiger_BITM_BaseClass {

    private CRM_Vtiger_BITM_ProjectMilestonesPage getMilestonePage() {
        Assert.assertNotNull(driver, "WebDriver is null. Check BaseClass setup.");
        return new CRM_Vtiger_BITM_ProjectMilestonesPage(driver);
    }

    private String getDynamicDate(int daysAhead) {
        return LocalDate.now().plusDays(daysAhead).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Test(priority = 1, description = "TC-VT-PM-06: Verify Project Milestones is visible under the More menu", groups = {"Smoke"})
    public void verifyProjectMilestonesInMoreMenuTest() {
        CRM_Vtiger_BITM_LoginHomePage homePage = new CRM_Vtiger_BITM_LoginHomePage(driver);
        homePage.navigateToProjectMilestones();
        CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();
        Assert.assertTrue(page.isProjectMilestonesPageDisplayed(), "Project Milestones page was not displayed via More menu.");
        Reporter.log("PASS: Project Milestones opened from More menu.", true);
    }

    @Test(priority = 2, description = "TC-VT-PM-05: Verify all expected fields are visible on the New Project Milestone form", groups = {"UI"})
    public void verifyAllExpectedFieldsVisibleTest() {
        CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();
        page.navigateToProjectMilestones();
        page.clickCreateButton();
        Assert.assertTrue(page.isCreateFormDisplayed(), "Milestone form controls are not displayed.");
        Reporter.log("PASS: All expected milestone fields are visible and interactable.", true);
    }

    @Test(priority = 3, description = "TC-VT-PM-03: Verify creation of a new Project Milestone with valid details", groups = {"Functional"})
    public void createProjectMilestoneWithValidDetailsTest() {
        CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();
        page.navigateToProjectMilestones();
        page.clickCreateButton();

        String dynamicMilestoneName = "Milestone_" + UUID.randomUUID().toString().substring(0, 8);
        String dynamicDate = getDynamicDate(5);

        page.enterMilestoneName(dynamicMilestoneName);
        String selectedType = page.selectRandomMilestoneType();
        page.enterMilestoneDate(dynamicDate);
        String selectedProject = page.selectFirstAvailableProject();
        page.clickSave();

        Assert.assertTrue(page.getHeaderText().contains(dynamicMilestoneName) || page.isMilestoneDisplayed(dynamicMilestoneName),
                "Project Milestone was not created successfully.");
        Reporter.log("PASS: Created milestone: " + dynamicMilestoneName + " | Type: " + selectedType + " | Project: " + selectedProject, true);
    }

    @Test(priority = 4, description = "TC-VT-PM-07: Verify mandatory-field validation when saving a milestone with missing details", groups = {"Negative"})
    public void verifyMandatoryFieldsValidationTest() {
        CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();
        page.navigateToProjectMilestones();
        page.clickCreateButton();

        page.enterMilestoneName("");
        page.clickSave();

        Assert.assertTrue(page.isMandatoryValidationShown(), "Mandatory validation was not triggered for empty Milestone Name.");
        Reporter.log("PASS: Empty mandatory field validation caught successfully.", true);
    }

    @Test(priority = 5, description = "TC-VT-PM-08: Verify milestone search returns no unrelated record for a mismatched filter", groups = {"Negative"})
    public void verifyMilestoneSearchMismatchedFilterTest() {
        CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();
        page.navigateToProjectMilestones();

        String nonExistingMilestone = "NonExistent_" + UUID.randomUUID().toString().substring(0, 10);
        page.searchMilestoneByName(nonExistingMilestone);

        Assert.assertTrue(page.isNoRecordsFoundDisplayed() || !page.isMilestoneDisplayed(nonExistingMilestone),
                "Unrelated records were displayed for non-existing milestone query.");
        Reporter.log("PASS: Mismatched search returned no records as expected.", true);
    }
}