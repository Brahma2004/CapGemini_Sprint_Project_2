package CRM_Vtiger_BITM_ObjRepo;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

public class CRM_Vtiger_BITM_ProjectTasksPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final String BASE_URL = "http://49.249.29.4:8888/index.php";

    private final By taskNameLinks = By.xpath("//table//tr[td]//a[contains(@href,'ProjectTask') and contains(@href,'action=DetailView')]");
    private final By createButton = By.xpath("//img[@title='Create Project Task...' or @alt='Create Project Task...']"
            + " | //a[contains(@href,'module=ProjectTask') and contains(@href,'action=EditView')]");

    private final By taskNameField = By.name("projecttaskname");
    private final By priorityDropdown = By.name("projecttaskpriority");
    private final By taskTypeDropdown = By.name("projecttasktype");
    private final By taskCodeField = By.name("projecttaskcode");
    private final By assignedToDropdown = By.name("assigned_user_id");
    private final By progressDropdown = By.name("projecttaskprogress");
    private final By workedHoursField = By.name("projecttaskhours");
    private final By startDateField = By.name("startdate");
    private final By endDateField = By.name("enddate");
    private final By descriptionField = By.name("description");

    private final By projectValueField = By.id("projectid_display");
    private final By projectLookupButton = By.xpath("//input[@name='projectid']/following-sibling::img[@title='Select']"
            + " | //img[contains(@onclick,'projectid')]");

    private final By popupSearchText = By.name("search_text");
    private final By popupSearchButton = By.name("search");

    private final By saveButton = By.xpath("//input[@name='button' and (contains(@title,'Save') or contains(@value,'Save'))]"
            + " | //input[@type='submit' and contains(@value,'Save')]");
    private final By cancelButton = By.xpath("//input[@name='button' and (contains(@title,'Cancel') or contains(@value,'Cancel'))]");
    private final By editButton = By.xpath("//input[@name='button' and (contains(@title,'Edit') or contains(@value,'Edit'))]");
    private final By deleteButton = By.xpath("//input[@name='button' and (contains(@title,'Delete') or contains(@value,'Delete'))]");

    private final By searchTextField = By.name("search_text");
    private final By searchFieldDropdown = By.id("bas_searchfield");
    private final By searchNowButton = By.xpath("//input[@name='submit' and contains(@value,'Search Now')]"
            + " | //input[contains(@value,'Search Now')]");
    private final By searchIcon = By.xpath("//img[contains(@title,'Search')]");
    private final By listViewContents = By.id("ListViewContents");
    private final By noRecordsFoundText = By.xpath("//span[contains(normalize-space(),'No Project Task Found')]");
    private final By headerText = By.className("dvHeaderText");

    private final By inlineValidationMessages = By.xpath("//*[contains(@class,'errorMessage')"
            + " or contains(@class,'validation-error')"
            + " or contains(@class,'fieldError')][normalize-space(.)!='']");

    public CRM_Vtiger_BITM_ProjectTasksPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null.");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateToProjectTasks() {
        driver.get(BASE_URL + "?module=ProjectTask&action=index");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(createButton),
                ExpectedConditions.presenceOfElementLocated(listViewContents)));
        Reporter.log("Project Tasks page opened.", true);
    }

    public boolean isProjectTasksPageDisplayed() {
        try {
            String currentUrl = driver.getCurrentUrl();
            boolean correctModule = currentUrl.contains("module=ProjectTask");
            boolean listElementPresent = isDisplayed(createButton) || isDisplayed(listViewContents);
            return correctModule && listElementPresent;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickCreateButton() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(createButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(taskNameField));
        Reporter.log("Create Project Task form opened.", true);
    }

    public boolean isCreateFormDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(taskNameField)).isDisplayed()
                    && isDisplayed(saveButton)
                    && isDisplayed(cancelButton);
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void enterTaskName(String taskName) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(taskNameField));
        field.clear();
        if (taskName != null && !taskName.trim().isEmpty()) {
            field.sendKeys(taskName.trim());
        }
        Reporter.log("Task Name entered: " + (taskName == null ? "" : taskName), true);
    }

    public String getTaskNameValue() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(taskNameField)).getAttribute("value");
    }

    public String selectRandomOption(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        Select select = new Select(element);

        List<WebElement> validOptions = select.getOptions().stream()
                .filter(opt -> !opt.getText().trim().isEmpty() && !opt.getText().toLowerCase().contains("none"))
                .toList();

        if (validOptions.isEmpty()) {
            return "";
        }

        int randomIndex = new Random().nextInt(validOptions.size());
        WebElement chosen = validOptions.get(randomIndex);
        String text = chosen.getText();
        select.selectByVisibleText(text);
        return text;
    }

    public String selectRandomType() {
        return selectRandomOption(taskTypeDropdown);
    }

    public String selectRandomPriority() {
        return selectRandomOption(priorityDropdown);
    }

    public String selectRandomStatus() {
        return selectRandomOption(progressDropdown);
    }

    public void enterTaskCode(String code) { enterText(taskCodeField, code); }
    public void enterWorkedHours(String hours) { enterText(workedHoursField, hours); }
    public void enterStartDate(String date) { enterText(startDateField, date); }
    public void enterEndDate(String date) { enterText(endDateField, date); }
    public void enterDescription(String description) { enterText(descriptionField, description); }

    private void enterText(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        if (value != null && !value.trim().isEmpty()) {
            element.sendKeys(value.trim());
        }
    }

    public String selectFirstAvailableProject() {
        String parentWindow = driver.getWindowHandle();
        Set<String> oldWindows = driver.getWindowHandles();

        WebElement lookupBtn = wait.until(ExpectedConditions.presenceOfElementLocated(projectLookupButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", lookupBtn);
        try {
            lookupBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", lookupBtn);
        }

        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(oldWindows.size() + 1));
            Set<String> newWindows = driver.getWindowHandles();
            newWindows.removeAll(oldWindows);

            if (newWindows.isEmpty()) {
                throw new RuntimeException("Project lookup popup did not open.");
            }

            driver.switchTo().window(newWindows.iterator().next());
            wait.until(ExpectedConditions.presenceOfElementLocated(popupSearchText));

            By firstRecordLink = By.xpath("//table[contains(@class,'small')]//tr[position()>1]//a[contains(@href,'window.close') or contains(@href,'set_return')]");
            WebElement link = wait.until(ExpectedConditions.elementToBeClickable(firstRecordLink));
            String selectedProject = link.getText().trim();
            link.click();

            wait.until(ExpectedConditions.numberOfWindowsToBe(oldWindows.size()));
            driver.switchTo().window(parentWindow);
            wait.until(ExpectedConditions.visibilityOfElementLocated(taskNameField));
            Reporter.log("Related Project selected dynamically: " + selectedProject, true);
            return selectedProject;
        } catch (Exception e) {
            closePopupAndReturn(parentWindow);
            throw new RuntimeException("Unable to pick first available project from lookup", e);
        }
    }

    public void selectRelatedProject(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        String parentWindow = driver.getWindowHandle();
        Set<String> oldWindows = driver.getWindowHandles();

        wait.until(ExpectedConditions.elementToBeClickable(projectLookupButton)).click();
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(oldWindows.size() + 1));
            Set<String> newWindows = driver.getWindowHandles();
            newWindows.removeAll(oldWindows);
            driver.switchTo().window(newWindows.iterator().next());

            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(popupSearchText));
            searchBox.clear();
            searchBox.sendKeys(projectName);
            driver.findElement(popupSearchButton).click();

            By projectLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(projectName) + "]");
            wait.until(ExpectedConditions.elementToBeClickable(projectLink)).click();

            wait.until(ExpectedConditions.numberOfWindowsToBe(oldWindows.size()));
            driver.switchTo().window(parentWindow);
            wait.until(ExpectedConditions.visibilityOfElementLocated(taskNameField));
            Reporter.log("Related Project selected: " + projectName, true);
        } catch (Exception e) {
            closePopupAndReturn(parentWindow);
            throw new RuntimeException("Failed selecting related project: " + projectName, e);
        }
    }

    private void closePopupAndReturn(String parentWindow) {
        try {
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(parentWindow)) {
                    driver.switchTo().window(handle);
                    driver.close();
                }
            }
            if (driver.getWindowHandles().contains(parentWindow)) {
                driver.switchTo().window(parentWindow);
            }
        } catch (Exception ignored) {
        }
    }

    public String getProjectValue() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(projectValueField)).getAttribute("value");
    }

    public void clickSave() {
        saveTask();
    }

    public void saveTask() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        Reporter.log("Project Task Save button clicked.", true);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(4)).until(d -> {
                try {
                    Alert alert = d.switchTo().alert();
                    if (alert != null) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
                try {
                    return !d.getCurrentUrl().contains("action=EditView");
                } catch (Exception ignored) {
                    return false;
                }
            });
        } catch (Exception ignored) {
        }
    }

    public boolean isTaskDetailPageDisplayed() {
        try {
            Alert alert = driver.switchTo().alert();
            if (alert != null) {
                alert.accept();
                return false;
            }
        } catch (Exception ignored) {
        }

        try {
            String currentUrl = driver.getCurrentUrl();
            return currentUrl.contains("module=ProjectTask")
                    && (currentUrl.contains("action=DetailView") || currentUrl.contains("record="));
        } catch (Exception e) {
            return false;
        }
    }

    public void cancelForm() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
        Reporter.log("Cancel button clicked.", true);
        wait.until(d -> d.getCurrentUrl().contains("module=ProjectTask")
                && !d.getCurrentUrl().contains("action=EditView"));
    }

    public void clickEdit() {
        wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(saveButton));
    }

    public void clickDelete() {
        wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        wait.until(d -> d.getCurrentUrl().contains("action=index"));
    }

    public void searchTaskByName(String taskName) {
        if (taskName == null || taskName.trim().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be empty.");
        }
        if (!isDisplayed(searchTextField)) {
            if (isDisplayed(searchIcon)) {
                wait.until(ExpectedConditions.elementToBeClickable(searchIcon)).click();
            }
        }
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchTextField));
        searchBox.clear();
        searchBox.sendKeys(taskName);

        if (isDisplayed(searchFieldDropdown)) {
            try {
                Select select = new Select(driver.findElement(searchFieldDropdown));
                select.selectByValue("projecttaskname");
            } catch (Exception ignored) {
            }
        }

        boolean submitted = false;
        for (WebElement button : driver.findElements(searchNowButton)) {
            try {
                if (button.isDisplayed() && button.isEnabled()) {
                    button.click();
                    submitted = true;
                    break;
                }
            } catch (Exception ignored) {
            }
        }
        if (!submitted) {
            searchBox.sendKeys(Keys.ENTER);
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(listViewContents));
    }

    public boolean isTaskDisplayed(String taskName) {
        By taskLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(taskName) + "]");
        try {
            return wait.until(d -> {
                for (WebElement element : d.findElements(taskLink)) {
                    try {
                        if (element.isDisplayed()) return true;
                    } catch (StaleElementReferenceException ignored) {
                    }
                }
                return false;
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void openTask(String taskName) {
        By taskLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(taskName) + "]");
        wait.until(ExpectedConditions.elementToBeClickable(taskLink)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(headerText));
    }

    public String getHeaderText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(headerText)).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public String getDisplayedFieldValue(String fieldName) {
        By fieldLocator = By.xpath("//td[normalize-space()='" + fieldName.trim() + "']/following-sibling::td[1]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(fieldLocator)).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public boolean isNoRecordsFoundDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(noRecordsFoundText)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isBackOnProjectTasksList() {
        try {
            wait.until(d -> d.getCurrentUrl().contains("module=ProjectTask")
                    && !d.getCurrentUrl().contains("action=EditView"));
            return isProjectTasksPageDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isMandatoryValidationShown() {
        return isSubmissionRejected();
    }

    public boolean isSubmissionRejected() {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());
            String message = alert.getText();
            alert.accept();
            if (message != null && !message.trim().isEmpty()) {
                return true;
            }
        } catch (TimeoutException ignored) {
        }

        try {
            for (WebElement error : driver.findElements(inlineValidationMessages)) {
                if (error.isDisplayed() && !error.getText().trim().isEmpty()) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        By[] fields = { taskNameField, projectValueField, startDateField, endDateField };
        for (By locator : fields) {
            try {
                for (WebElement field : driver.findElements(locator)) {
                    if (!field.isDisplayed()) continue;
                    Object valid = ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", field);
                    if (Boolean.FALSE.equals(valid)) {
                        return true;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return isCreateFormDisplayed();
    }

    private boolean isDisplayed(By locator) {
        try {
            for (WebElement element : driver.findElements(locator)) {
                if (element.isDisplayed()) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private String xpathLiteral(String value) {
        if (!value.contains("'")) return "'" + value + "'";
        if (!value.contains("\"")) return "\"" + value + "\"";
        String[] parts = value.split("'", -1);
        StringBuilder result = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) result.append(", \"'\", ");
            result.append("'").append(parts[i]).append("'");
        }
        result.append(")");
        return result.toString();
    }
}