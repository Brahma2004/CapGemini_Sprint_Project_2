package CRM_Vtiger_BITM_ObjRepo;

import java.time.Duration;
import java.util.Set;

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

public class CRM_Vtiger_BITM_ServiceContractsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final String BASE_URL = "http://49.249.29.4:8888/index.php";

    // List page locators
    private final By serviceContractsHeading = By.xpath("//*[normalize-space()='Service Contracts']");
    private final By searchField = By.name("search_text");
    private final By searchDropdown = By.id("bas_searchfield");
    private final By searchNowButton = By.xpath("//input[@name='submit' and contains(@value,'Search Now')]"
            + " | //input[contains(@value,'Search Now')]");
    private final By emptyStateMessage = By.xpath("//*[contains(normalize-space(.),'No Service Contract Found')]");
    private final By contractNumberLinks = By.xpath("//a[contains(@href,'module=ServiceContracts') and (contains(@href,'action=DetailView') or contains(@href,'record='))]");
    private final By contractTableHeaders = By.xpath("//th[normalize-space()='Contract No'] | //td[normalize-space()='Contract No']");
    private final By tableRows = By.xpath("//table[contains(@class,'listViewEntriesTable')]//tr[td] | //table//tr[td[contains(@class,'listTableRow')]] | //table//tr[td and not(th)]");
    private final By createButton = By.xpath("//img[@title='Create Service Contract...' or @alt='Create Service Contract...']"
            + " | //a[contains(@href,'module=ServiceContracts') and contains(@href,'action=EditView')]");

    // Form field locators
    private final By subjectField = By.name("subject");
    private final By assignedToDropdown = By.name("assigned_user_id");
    private final By contractStatusDropdown = By.name("contract_status");
    private final By contractTypeDropdown = By.name("contract_type");
    private final By contractPriorityDropdown = By.name("contract_priority");
    private final By startDateField = By.name("start_date");
    private final By dueDateField = By.name("due_date");
    private final By descriptionField = By.name("description");

    // Related Contact lookup
    private final By contactLookupButton = By.xpath("//input[@name='sc_related_to']/following-sibling::img[@title='Select']"
            + " | //input[@id='single_accountid']/following-sibling::img[@title='Select']"
            + " | //img[contains(@onclick,'sc_related_to') or contains(@onclick,'Contacts&action=Popup')]");
    private final By popupSearchText = By.name("search_text");
    private final By popupSearchButton = By.name("search");

    // Form Action Buttons
    private final By saveButton = By.xpath("//input[@name='button' and (contains(@title,'Save') or contains(@value,'Save'))]"
            + " | //input[@type='submit' and contains(@value,'Save')]");
    private final By cancelButton = By.xpath("//input[@name='button' and (contains(@title,'Cancel') or contains(@value,'Cancel'))]");
    private final By editButton = By.xpath("//input[@name='button' and (contains(@title,'Edit') or contains(@value,'Edit'))]");
    private final By deleteButton = By.xpath("//input[@name='button' and (contains(@title,'Delete') or contains(@value,'Delete'))]");
    private final By headerText = By.className("dvHeaderText");

    public CRM_Vtiger_BITM_ServiceContractsPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null.");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void openServiceContractsList() {
        driver.get(BASE_URL + "?module=ServiceContracts&action=index");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(searchField),
                ExpectedConditions.presenceOfElementLocated(serviceContractsHeading),
                ExpectedConditions.presenceOfElementLocated(emptyStateMessage)));
        Reporter.log("Service Contracts list opened.", true);
    }

    public boolean isServiceContractsPageDisplayed() {
        try {
            return driver.getCurrentUrl().contains("module=ServiceContracts")
                    && !driver.getCurrentUrl().contains("action=EditView")
                    && (driver.findElements(searchField).stream().anyMatch(WebElement::isDisplayed)
                        || driver.findElements(serviceContractsHeading).stream().anyMatch(WebElement::isDisplayed)
                        || isEmptyStateDisplayed() || isContractTableDisplayed());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isContractTableDisplayed() {
        return driver.findElements(contractTableHeaders).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isEmptyStateDisplayed() {
        for (WebElement message : driver.findElements(emptyStateMessage)) {
            try {
                if (message.isDisplayed()) return true;
            } catch (StaleElementReferenceException ignored) {}
        }
        return false;
    }

    public int getContractCount() {
        int count = 0;
        for (WebElement contract : driver.findElements(contractNumberLinks)) {
            try {
                if (contract.isDisplayed()) count++;
            } catch (StaleElementReferenceException ignored) {}
        }
        return count;
    }

    public void clickCreateButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(createButton));
        button.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(subjectField));
        Reporter.log("Create Service Contract form opened.", true);
    }

    public boolean isCreateFormDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(subjectField)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void enterSubject(String subject) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(subjectField));
        field.clear();
        if (subject != null && !subject.trim().isEmpty()) {
            field.sendKeys(subject.trim());
        }
    }

    public String getSubjectValue() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(subjectField)).getAttribute("value");
    }

    public void selectStatus(String status) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(contractStatusDropdown));
        new Select(element).selectByVisibleText(status);
    }

    public void selectType(String type) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(contractTypeDropdown));
        new Select(element).selectByVisibleText(type);
    }

    public void selectPriority(String priority) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(contractPriorityDropdown));
        new Select(element).selectByVisibleText(priority);
    }

    public void enterStartDate(String startDate) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateField));
        element.clear();
        if (startDate != null && !startDate.isEmpty()) element.sendKeys(startDate);
    }

    public void enterDueDate(String dueDate) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(dueDateField));
        element.clear();
        if (dueDate != null && !dueDate.isEmpty()) element.sendKeys(dueDate);
    }

    public void enterDescription(String description) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionField));
        element.clear();
        if (description != null && !description.isEmpty()) element.sendKeys(description);
    }

    public void selectContact(String contactName) {
        if (contactName == null || contactName.trim().isEmpty()) return;
        String parentWindow = driver.getWindowHandle();
        Set<String> oldHandles = driver.getWindowHandles();

        wait.until(ExpectedConditions.elementToBeClickable(contactLookupButton)).click();
        wait.until(ExpectedConditions.numberOfWindowsToBe(oldHandles.size() + 1));

        Set<String> newHandles = driver.getWindowHandles();
        newHandles.removeAll(oldHandles);
        driver.switchTo().window(newHandles.iterator().next());

        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(popupSearchText));
        searchBox.clear();
        searchBox.sendKeys(contactName);
        driver.findElement(popupSearchButton).click();

        By link = By.xpath("//a[normalize-space(.)=" + xpathLiteral(contactName) + "]");
        wait.until(ExpectedConditions.elementToBeClickable(link)).click();

        driver.switchTo().window(parentWindow);
    }

    public void createServiceContract(String subject, String contact, String type, String status,
                                      String priority, String startDate, String dueDate) {
        clickCreateButton();
        enterSubject(subject);
        if (contact != null && !contact.isEmpty()) selectContact(contact);
        if (type != null && !type.isEmpty()) selectType(type);
        if (status != null && !status.isEmpty()) selectStatus(status);
        if (priority != null && !priority.isEmpty()) selectPriority(priority);
        if (startDate != null && !startDate.isEmpty()) enterStartDate(startDate);
        if (dueDate != null && !dueDate.isEmpty()) enterDueDate(dueDate);
        saveContract();
    }

    public void saveContract() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        btn.click();
        Reporter.log("Save button clicked.", true);
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("action=DetailView"),
                ExpectedConditions.urlContains("record="),
                ExpectedConditions.invisibilityOf(btn)
        ));
    }

    public void clickEdit() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(editButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        btn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(saveButton));
    }

    public void clickDelete() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(deleteButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        btn.click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        wait.until(d -> d.getCurrentUrl().contains("action=index"));
    }

    public String acceptAlertIfPresent() {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());
            String message = alert.getText();
            alert.accept();
            return message;
        } catch (TimeoutException e) {
            return null;
        }
    }

    public boolean isSubjectMandatoryValidationShown() {
        String alertMessage = acceptAlertIfPresent();
        if (alertMessage != null && !alertMessage.trim().isEmpty()) return true;
        try {
            WebElement field = driver.findElement(subjectField);
            boolean formStillOpen = field.isDisplayed();
            boolean subjectEmpty = field.getAttribute("value").trim().isEmpty();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            boolean htmlInvalid = Boolean.TRUE.equals(js.executeScript("return !arguments[0].checkValidity();", field));
            return htmlInvalid || (formStillOpen && subjectEmpty);
        } catch (Exception e) {
            return false;
        }
    }

    public void cancelForm() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
    }

    public boolean isBackOnServiceContractsList() {
        try {
            wait.until(d -> d.getCurrentUrl().contains("module=ServiceContracts")
                    && !d.getCurrentUrl().contains("action=EditView"));
            return isServiceContractsPageDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void performSearch(String fieldName, String value) {
        if (!driver.getCurrentUrl().contains("module=ServiceContracts")
                || driver.getCurrentUrl().contains("action=EditView")) {
            openServiceContractsList();
        }
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));
        searchBox.clear();
        searchBox.sendKeys(value);

        Select dropdown = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(searchDropdown)));
        for (WebElement option : dropdown.getOptions()) {
            if (option.getText().trim().equalsIgnoreCase(fieldName)) {
                dropdown.selectByVisibleText(option.getText());
                break;
            }
        }

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(searchNowButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", button);
        button.click();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
                !d.findElements(tableRows).isEmpty() || isEmptyStateDisplayed()
            );
        } catch (TimeoutException ignored) {
        }
    }

    public void searchByContractNumber(String contractNumber) {
        performSearch("Contract No", contractNumber);
    }

    public void searchServiceContractBySubject(String subject) {
        performSearch("Subject", subject);
    }

    public boolean isContractNumberDisplayed(String contractNumber) {
        for (WebElement row : driver.findElements(tableRows)) {
            try {
                if (row.isDisplayed() && row.getText().contains(contractNumber)) return true;
            } catch (StaleElementReferenceException ignored) {}
        }
        return false;
    }

    public boolean isServiceContractSubjectDisplayed(String subject) {
        for (WebElement row : driver.findElements(tableRows)) {
            try {
                if (row.isDisplayed()) {
                    String rowText = row.getText().trim();
                    if (rowText.toLowerCase().contains(subject.toLowerCase())) {
                        return true;
                    }
                }
            } catch (StaleElementReferenceException ignored) {}
        }
        return false;
    }

    public void openContractBySubject(String subject) {
        By subjectLink = By.xpath("//a[contains(@href,'module=ServiceContracts') and (contains(normalize-space(), "
                + xpathLiteral(subject) + ") or contains(@title, " + xpathLiteral(subject) + "))]");

        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(subjectLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", link);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(link)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(headerText),
                ExpectedConditions.urlContains("action=DetailView")
        ));
    }

    public void openFirstContract() {
        WebElement contract = wait.until(ExpectedConditions.presenceOfElementLocated(contractNumberLinks));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", contract);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(contract)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", contract);
        }

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("action=DetailView"),
                ExpectedConditions.urlContains("record=")));
    }

    public boolean isContractDetailPageDisplayed() {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl.contains("module=ServiceContracts")
                && (currentUrl.contains("action=DetailView") || currentUrl.contains("record="));
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