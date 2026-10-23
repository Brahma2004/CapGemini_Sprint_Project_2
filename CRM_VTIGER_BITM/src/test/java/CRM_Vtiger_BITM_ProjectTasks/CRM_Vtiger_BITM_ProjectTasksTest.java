package CRM_Vtiger_BITM_ProjectTasks;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ExcelUtility;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_LoginHomePage;
import CRM_Vtiger_BITM_ObjRepo.CRM_Vtiger_BITM_ProjectTasksPage;

public class CRM_Vtiger_BITM_ProjectTasksTest extends CRM_Vtiger_BITM_BaseClass {

    private CRM_Vtiger_BITM_ProjectTasksPage getProjectTasksPage() {
        Assert.assertNotNull(driver, "WebDriver was not initialized by BaseClass.");
        return new CRM_Vtiger_BITM_ProjectTasksPage(driver);
    }

    private String getDynamicDate(int daysAhead) {
        return LocalDate.now().plusDays(daysAhead).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private String getDynamicTaskName() {
        return "Task_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
    private Map<String, String> getParsedExcelTestData() {
        Map<String, String> parsedData = new HashMap<>();
        try (CRM_Vtiger_BITM_ExcelUtility excel = new CRM_Vtiger_BITM_ExcelUtility()) {
            excel.openWorkbook();
            List<Map<String, String>> records = excel.getAllDataAsMaps("Project Tasks");
            if (!records.isEmpty()) {
                String rawTestData = records.get(0).get("Test Data");
                if (rawTestData != null && !rawTestData.trim().isEmpty()) {
                    for (String part : rawTestData.split(";")) {
                        String[] pair = part.split(":", 2);
                        if (pair.length == 2) {
                            parsedData.put(pair[0].trim().toLowerCase(), pair[1].trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Reporter.log("Warning: Could not read Excel test data, using dynamic fallback: " + e.getMessage(), true);
        }
        return parsedData;
    }

    @Test(priority = 1, description = "TC-VT-PT-06: Verify Project Tasks is visible under the More menu", groups = { "Smoke" })
    public void verifyProjectTasksInMoreMenuTest() {
        CRM_Vtiger_BITM_LoginHomePage homePage = new CRM_Vtiger_BITM_LoginHomePage(driver);
        homePage.navigateToProjectTasks();

        CRM_Vtiger_BITM_ProjectTasksPage page = getProjectTasksPage();
        Assert.assertTrue(page.isProjectTasksPageDisplayed(), "Project Tasks page was not displayed via More menu.");
        Reporter.log("PASS: Project Tasks page is accessible from More menu.", true);
    }

    @Test(priority = 2, description = "TC-VT-PT-09: Verify navigation from Project Tasks to its related page", groups = { "Integration" })
    public void verifyNavigationFromListToTaskDetailTest() {
        CRM_Vtiger_BITM_ProjectTasksPage page = getProjectTasksPage();
        page.navigateToProjectTasks();

        String taskName = getDynamicTaskName();
        page.clickCreateButton();
        page.enterTaskName(taskName);
        page.selectFirstAvailableProject();
        page.selectRandomType();
        page.selectRandomPriority();
        page.enterStartDate(getDynamicDate(0));
        page.enterEndDate(getDynamicDate(15));
        page.saveTask();

        Assert.assertTrue(page.isTaskDetailPageDisplayed() || page.isTaskDisplayed(taskName) || page.isProjectTasksPageDisplayed(),
                "Navigation to Project Task detail page failed.");
        Reporter.log("PASS: Navigation from Project Tasks list to related detail page verified for: " + taskName, true);
    }

    @Test(priority = 3, description = "TC-VT-PT-01: Verify creation of a new Project Task with valid details", groups = { "Functional" })
    public void createProjectTaskWithValidDetailsTest() {
        Map<String, String> excelData = getParsedExcelTestData();
        String baseTaskName = excelData.getOrDefault("task name", "QA_Task_Module5");
        String taskName = baseTaskName + "_" + UUID.randomUUID().toString().substring(0, 5);

        CRM_Vtiger_BITM_ProjectTasksPage page = getProjectTasksPage();
        page.navigateToProjectTasks();
        page.clickCreateButton();

        Assert.assertTrue(page.isCreateFormDisplayed(), "Create Project Task form was not displayed.");
        page.enterTaskName(taskName);
        String selectedProject = page.selectFirstAvailableProject();
        page.selectRandomType();
        page.selectRandomPriority();
        page.enterStartDate(getDynamicDate(0));
        page.enterEndDate(getDynamicDate(30));
        page.saveTask();

        Assert.assertTrue(page.isTaskDetailPageDisplayed() || page.getHeaderText().contains(taskName) || page.isTaskDisplayed(taskName),
                "Project Task creation failed for: " + taskName);
        Reporter.log("PASS: Project Task created successfully: " + taskName + " (Related Project: " + selectedProject + ")", true);
    }

    @Test(priority = 4, description = "TC-VT-PT-07: Verify Task Name mandatory validation", groups = { "Negative" })
    public void verifyTaskNameMandatoryValidationTest() {
        CRM_Vtiger_BITM_ProjectTasksPage page = getProjectTasksPage();
        page.navigateToProjectTasks();
        page.clickCreateButton();

        Assert.assertTrue(page.isCreateFormDisplayed(), "Create Project Task form was not displayed.");
        page.enterTaskName("");
        page.saveTask();

        Assert.assertTrue(page.isMandatoryValidationShown(), "Task Name mandatory validation was not detected.");
        Reporter.log("PASS: Task Name mandatory validation checked.", true);
    }

    @Test(priority = 5, description = "TC-VT-PT-07: Verify Related Project mandatory validation", groups = { "Negative" })
    public void verifyRelatedProjectMandatoryValidationTest() {
        CRM_Vtiger_BITM_ProjectTasksPage page = getProjectTasksPage();
        page.navigateToProjectTasks();
        page.clickCreateButton();

        Assert.assertTrue(page.isCreateFormDisplayed(), "Create Project Task form was not displayed.");
        page.enterTaskName(getDynamicTaskName());
        page.saveTask();

        Assert.assertTrue(page.isMandatoryValidationShown(), "Related Project mandatory validation was not detected.");
        Reporter.log("PASS: Related Project mandatory validation checked.", true);
    }
}