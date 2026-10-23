package CRM_Vtiger_BITM_ObjRepo;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

public class CRM_Vtiger_BITM_ProjectsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final String BASE_URL = "http://49.249.29.4:8888/index.php";

    private final By projectsHeading = By.xpath("//*[normalize-space()='Projects']");
    private final By createButton = By.xpath("//img[@title='Create Project...' or @alt='Create Project...']"
            + " | //a[contains(@href,'module=Project') and contains(@href,'action=EditView')]");

    private final By projectNameField = By.name("projectname");
    private final By projectStatusDropdown = By.name("projectstatus");
    private final By projectTypeDropdown = By.name("projecttype");
    private final By projectPriorityDropdown = By.name("projectpriority");
    private final By startDateField = By.name("startdate");
    private final By targetEndDateField = By.name("targetenddate");
    private final By actualEndDateField = By.name("actualenddate");

    private final By searchField = By.name("search_text");
    private final By searchDropdown = By.id("bas_searchfield");
    private final By searchNowButton = By.xpath("//input[@name='submit' and contains(@value,'Search Now')]"
            + " | //input[contains(@value,'Search Now')]");
    private final By emptyStateMessage = By.xpath("//*[contains(normalize-space(.),'No Project Found')]");
    private final By tableRows = By.xpath("//table[contains(@class,'listViewEntriesTable')]//tr[td] | //table//tr[td[contains(@class,'listTableRow')]] | //table//tr[td and not(th)]");

    private final By saveButton = By.xpath("//input[@name='button' and (contains(@title,'Save') or contains(@value,'Save'))]"
            + " | //input[@type='submit' and contains(@value,'Save')]");
    private final By cancelButton = By.xpath("//input[@name='button' and (contains(@title,'Cancel') or contains(@value,'Cancel'))]");
    private final By headerText = By.className("dvHeaderText");

    public CRM_Vtiger_BITM_ProjectsPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null.");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void openProjectsList() {
        driver.get(BASE_URL + "?module=Project&action=index");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(searchField),
                ExpectedConditions.presenceOfElementLocated(projectsHeading),
                ExpectedConditions.presenceOfElementLocated(emptyStateMessage)));
        Reporter.log("Projects list opened: " + driver.getCurrentUrl(), true);
    }

    public boolean isProjectsPageDisplayed() {
        try {
            return driver.getCurrentUrl().contains("module=Project")
                    && !driver.getCurrentUrl().contains("action=EditView");
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField));
        Reporter.log("Create Project form opened.", true);
    }

    public boolean isCreateFormDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void enterProjectName(String name) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField));
        field.clear();
        if (name != null && !name.trim().isEmpty()) {
            field.sendKeys(name.trim());
        }
        Reporter.log("Project Name entered: " + (name == null ? "" : name), true);
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
        return selectRandomOption(projectTypeDropdown);
    }

    public String selectRandomStatus() {
        return selectRandomOption(projectStatusDropdown);
    }

    public String selectRandomPriority() {
        return selectRandomOption(projectPriorityDropdown);
    }

    public void enterStartDate(String startDate) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateField));
        field.clear();
        if (startDate != null && !startDate.isEmpty()) {
            field.sendKeys(startDate);
        }
    }

    public void enterTargetEndDate(String endDate) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(targetEndDateField));
        field.clear();
        if (endDate != null && !endDate.isEmpty()) {
            field.sendKeys(endDate);
        }
    }

    public void enterActualEndDate(String endDate) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(actualEndDateField));
        field.clear();
        if (endDate != null && !endDate.isEmpty()) {
            field.sendKeys(endDate);
        }
    }

    public void clickSave() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        Reporter.log("Save button clicked.", true);

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

    public void createProject(String projectName, String startDate, String targetEndDate) {
        clickCreateButton();
        enterProjectName(projectName);
        selectRandomStatus();
        selectRandomType();
        selectRandomPriority();
        if (startDate != null && !startDate.isEmpty()) {
            enterStartDate(startDate);
        }
        if (targetEndDate != null && !targetEndDate.isEmpty()) {
            enterTargetEndDate(targetEndDate);
            enterActualEndDate(targetEndDate);
        }
        clickSave();
    }

    public void searchProjectByName(String projectName) {
        if (!driver.getCurrentUrl().contains("module=Project") || driver.getCurrentUrl().contains("action=EditView")) {
            openProjectsList();
        }
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));
        searchBox.clear();
        searchBox.sendKeys(projectName);

        Select dropdown = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(searchDropdown)));
        for (WebElement opt : dropdown.getOptions()) {
            if ("projectname".equalsIgnoreCase(opt.getAttribute("value")) || opt.getText().toLowerCase().contains("name")) {
                dropdown.selectByVisibleText(opt.getText());
                break;
            }
        }

        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(searchNowButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);
        submitBtn.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(tableRows));
    }

    public boolean isProjectDisplayed(String projectName) {
        By projectLink = By.xpath("//a[contains(@href,'module=Project') and contains(normalize-space(), " + xpathLiteral(projectName) + ")]");
        try {
            return wait.until(d -> {
                for (WebElement link : d.findElements(projectLink)) {
                    try {
                        if (link.isDisplayed()) {
                            return true;
                        }
                    } catch (StaleElementReferenceException ignored) {
                    }
                }
                return false;
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void openProjectByName(String projectName) {
        By projectLink = By.xpath("//a[contains(@href,'module=Project') and (contains(normalize-space(), "
                + xpathLiteral(projectName) + ") or contains(@title, " + xpathLiteral(projectName) + "))]");

        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(projectLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", link);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(link)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(headerText),
                ExpectedConditions.urlContains("action=DetailView")));
    }

    public boolean isProjectDetailPageDisplayed() {
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
            return currentUrl.contains("module=Project")
                    && (currentUrl.contains("action=DetailView") || currentUrl.contains("record="));
        } catch (Exception e) {
            return false;
        }
    }

    public String getHeaderText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(headerText)).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public boolean isMandatoryValidationShown() {
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
            WebElement nameField = driver.findElement(projectNameField);
            Object valid = ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", nameField);
            if (Boolean.FALSE.equals(valid)) {
                return true;
            }
        } catch (Exception ignored) {
        }

        return isCreateFormDisplayed();
    }

    private String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        String[] parts = value.split("'", -1);
        StringBuilder result = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                result.append(", \"'\", ");
            }
            result.append("'").append(parts[i]).append("'");
        }
        return result.append(")").toString();
    }
}