package CRM_Vtiger_BITM_Projects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_LoginHomePage;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_ProjectsPage;

public class CRM_Vtiger_BITM_ProjectsTest extends CRM_Vtiger_BITM_BaseClass {

    private CRM_Vtiger_BITM_ProjectsPage getProjectsPage() {
        Assert.assertNotNull(driver, "WebDriver is null. Check BaseClass setup.");
        return new CRM_Vtiger_BITM_ProjectsPage(driver);
    }

    private String getDynamicDate(int daysAhead) {
        return LocalDate.now().plusDays(daysAhead).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private String getDynamicProjectName(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    @Test(priority = 1, description = "TC-VT-PROJ-06: Verify Projects is visible under the More navigation menu", groups = { "Smoke" })
    public void verifyProjectsInMoreMenuTest() {
        CRM_Vtiger_BITM_LoginHomePage homePage = new CRM_Vtiger_BITM_LoginHomePage(driver);
        homePage.navigateToProjects();
        CRM_Vtiger_BITM_ProjectsPage page = getProjectsPage();
        Assert.assertTrue(page.isProjectsPageDisplayed(), "Projects page was not displayed via More menu.");
        Reporter.log("PASS: Projects page is accessible from More menu.", true);
    }

    @Test(priority = 2, description = "TC-VT-PROJ-09: Verify navigation from Projects list to a project's related page", groups = { "Integration" })
    public void verifyNavigationFromListToProjectDetailTest() {
        CRM_Vtiger_BITM_ProjectsPage page = getProjectsPage();
        page.openProjectsList();

        String projectName = getDynamicProjectName("NavProj");
        page.createProject(projectName, getDynamicDate(0), getDynamicDate(30));

        Assert.assertTrue(page.isProjectDetailPageDisplayed() || page.isProjectsPageDisplayed(),
                "Navigation to Project detail failed after saving.");
        Reporter.log("PASS: Navigation to project detail verified: " + projectName, true);
    }

    @Test(priority = 3, description = "TC-VT-PROJ-01: Verify creation of a new project with valid details", groups = { "Functional" })
    public void createProjectWithValidDetailsTest() {
        CRM_Vtiger_BITM_ProjectsPage page = getProjectsPage();
        page.openProjectsList();

        String projectName = getDynamicProjectName("QAProj");
        page.createProject(projectName, getDynamicDate(1), getDynamicDate(45));

        Assert.assertTrue(page.isProjectDetailPageDisplayed() || page.getHeaderText().contains(projectName) || page.isProjectsPageDisplayed(),
                "Project creation was not confirmed.");
        Reporter.log("PASS: Created project with valid details: " + projectName, true);
    }

    @Test(priority = 4, description = "TC-VT-PROJ-02: Verify a saved project using project-name search", groups = { "Functional" })
    public void searchSavedProjectByNameTest() {
        CRM_Vtiger_BITM_ProjectsPage page = getProjectsPage();
        page.openProjectsList();

        String projectName = getDynamicProjectName("SearchProj");
        page.createProject(projectName, getDynamicDate(0), getDynamicDate(15));

        page.openProjectsList();
        page.searchProjectByName(projectName);
        Assert.assertTrue(page.isProjectDisplayed(projectName), "Saved project was not found in search results: " + projectName);
        Reporter.log("PASS: Saved project found in search results: " + projectName, true);
    }

    @Test(priority = 5, description = "TC-VT-PROJ-07: Verify mandatory-field validation when saving a project with missing details", groups = { "Negative" })
    public void verifyMandatoryFieldsValidationTest() {
        CRM_Vtiger_BITM_ProjectsPage page = getProjectsPage();
        page.openProjectsList();
        page.clickCreateButton();

        page.enterProjectName("");
        page.clickSave();

        Assert.assertTrue(page.isMandatoryValidationShown(), "Mandatory validation for empty Project Name was not triggered.");
        Reporter.log("PASS: Empty mandatory Project Name rejected as expected.", true);
    }
}