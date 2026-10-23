package CRM_Vtiger_BITM_ObjRepo;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CRM_Vtiger_BITM_FAQPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // List & Search Locators
    private final By createFaqIcon = By.xpath("//img[@title='Create FAQ...' or @alt='Create FAQ...']");
    private final By searchText = By.name("search_text");
    private final By searchField = By.id("bas_searchfield");
    private final By searchNow = By.xpath("//input[@name='submit' and contains(@value,'Search Now')]"
            + " | //input[contains(@value,'Search Now')]" + " | //button[contains(normalize-space(.),'Search Now')]");
    private final By faqQuestionLinks = By.xpath("//a[contains(@href,'module=Faq') and contains(@href,'action=DetailView')]");
    private final By noRecordsFoundText = By.xpath("//span[contains(normalize-space(),'No FAQ Found')]");

    // Create & Edit Form Locators
    private final By questionEdt = By.name("question");
    private final By productSelectIcon = By.xpath("//img[contains(@onclick,'Products&action=Popup')] | //input[@name='product_name']/following-sibling::img[@title='Select']");
    private final By statusDropdown = By.name("faqstatus");
    private final By categoryDropdown = By.name("faqcategories");
    private final By answerEdt = By.xpath("//textarea[@name='faq_answer'] | //td[@class='dvtCellInfo']/textarea");
    private final By saveBtn = By.xpath("//input[@title='Save [Alt+S]']");
    private final By editBtn = By.xpath("//input[@title='Edit [Alt+E]']");
    private final By deleteBtn = By.xpath("//input[@title='Delete [Alt+D]']");

    // Product Popup Locators
    private final By popupSearchText = By.name("search_text");
    private final By popupSearchNowBtn = By.name("search");

    public CRM_Vtiger_BITM_FAQPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null.");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void openFAQList(String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("FAQ base URL cannot be empty.");
        }
        String cleanUrl = baseUrl.trim();
        if (cleanUrl.contains("?")) {
            cleanUrl = cleanUrl.substring(0, cleanUrl.indexOf("?"));
        }
        driver.get(cleanUrl + "?module=Faq&action=index");
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchText));
    }

    public boolean isFAQPageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='FAQ' or normalize-space()='FAQs']"))).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clickCreateFAQ() {
        wait.until(ExpectedConditions.elementToBeClickable(createFaqIcon)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(questionEdt));
    }

    public boolean isCreateFAQFormDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(questionEdt)).isDisplayed()
                    && driver.findElement(statusDropdown).isDisplayed()
                    && driver.findElement(saveBtn).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void selectProductFromLookup(String productName) {
        String parentWindow = driver.getWindowHandle();
        wait.until(ExpectedConditions.elementToBeClickable(productSelectIcon)).click();

        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(parentWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(popupSearchText)).sendKeys(productName);
            driver.findElement(popupSearchNowBtn).click();
            By productLink = By.xpath("//a[normalize-space()='" + productName + "']");
            wait.until(ExpectedConditions.elementToBeClickable(productLink)).click();
        } finally {
            driver.switchTo().window(parentWindow);
        }
    }

    public void createFAQ(String question, String status, String category, String answer, String productName) {
        clickCreateFAQ();
        WebElement qBox = wait.until(ExpectedConditions.visibilityOfElementLocated(questionEdt));
        qBox.clear();
        qBox.sendKeys(question);

        if (status != null && !status.isEmpty()) {
            new Select(driver.findElement(statusDropdown)).selectByVisibleText(status);
        }
        if (category != null && !category.isEmpty()) {
            new Select(driver.findElement(categoryDropdown)).selectByVisibleText(category);
        }
        if (productName != null && !productName.isEmpty()) {
            selectProductFromLookup(productName);
        }
        if (answer != null && !answer.isEmpty()) {
            WebElement ans = driver.findElement(answerEdt);
            ans.clear();
            ans.sendKeys(answer);
        }
        clickSave();
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
    }

    public String clickSaveWithEmptyFieldsAndGetAlert() {
        clickSave();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        alert.accept();
        return alertText;
    }

    public void clickEdit() {
        wait.until(ExpectedConditions.elementToBeClickable(editBtn)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(saveBtn));
    }

    public void updateAnswer(String newAnswer) {
        WebElement ans = wait.until(ExpectedConditions.visibilityOfElementLocated(answerEdt));
        ans.clear();
        ans.sendKeys(newAnswer);
        clickSave();
    }

    public void deleteCurrentFAQ() {
        wait.until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchText));
    }

    public void searchFAQ(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("FAQ search value cannot be empty.");
        }
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("FAQ search field cannot be empty.");
        }

        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchText));
        searchBox.clear();
        searchBox.sendKeys(value.trim());

        WebElement searchDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));
        Select dropdown = new Select(searchDropdown);

        boolean optionFound = false;
        for (WebElement option : dropdown.getOptions()) {
            if (fieldName.equalsIgnoreCase(option.getText().trim())) {
                dropdown.selectByVisibleText(option.getText().trim());
                optionFound = true;
                break;
            }
        }
        if (!optionFound) {
            throw new NoSuchElementException(fieldName + " option is not available in the FAQ search dropdown.");
        }

        wait.until(ExpectedConditions.elementToBeClickable(searchNow)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchText));
    }

    public void searchFAQByQuestion(String question) {
        searchFAQ(question, "Question");
    }

    public boolean isQuestionListed(String expectedValue) {
        if (expectedValue == null || expectedValue.trim().isEmpty()) {
            return false;
        }
        String expected = expectedValue.trim();
        try {
            WebDriverWait questionWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            return questionWait.until(d -> {
                List<WebElement> links = d.findElements(faqQuestionLinks);
                for (WebElement link : links) {
                    try {
                        if (!link.isDisplayed()) continue;
                        String href = link.getAttribute("href");
                        if (href == null || !href.contains("module=Faq") || !href.contains("action=DetailView")) continue;
                        if (matchesText(link.getText(), expected)) {
                            return true;
                        }
                    } catch (StaleElementReferenceException ignored) {}
                }
                return false;
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isNoRecordsFoundDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(noRecordsFoundText)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private boolean matchesText(String displayedText, String expected) {
        if (displayedText == null || expected == null) return false;
        String displayed = displayedText.trim();
        if (displayed.equalsIgnoreCase(expected)) return true;
        if (displayed.endsWith("...")) {
            String prefix = displayed.substring(0, displayed.length() - 3).trim();
            return !prefix.isEmpty() && expected.regionMatches(true, 0, prefix, 0, prefix.length());
        }
        if (displayed.endsWith("\u2026")) {
            String prefix = displayed.substring(0, displayed.length() - 1).trim();
            return !prefix.isEmpty() && expected.regionMatches(true, 0, prefix, 0, prefix.length());
        }
        return false;
    }

    public void openFAQQuestion(String expectedValue) {
        if (expectedValue == null || expectedValue.trim().isEmpty()) {
            throw new IllegalArgumentException("FAQ value cannot be empty.");
        }
        String expected = expectedValue.trim();
        WebDriverWait questionWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement matchingLink = questionWait.until(d -> {
                    List<WebElement> links = d.findElements(faqQuestionLinks);
                    for (WebElement link : links) {
                        try {
                            if (!link.isDisplayed()) continue;
                            String href = link.getAttribute("href");
                            String displayedText = link.getText();
                            if (href != null && href.contains("module=Faq") && href.contains("action=DetailView")
                                    && matchesText(displayedText, expected) && link.isEnabled()) {
                                return link;
                            }
                        } catch (StaleElementReferenceException ignored) {}
                    }
                    return null;
                });
                if (matchingLink == null || !matchingLink.isDisplayed()) continue;
                matchingLink.click();
                questionWait.until(d -> d.getCurrentUrl().contains("action=DetailView"));
                return;
            } catch (StaleElementReferenceException | TimeoutException e) {
                if (attempt == 3) {
                    throw new RuntimeException("Could not open FAQ detail page for: " + expected, e);
                }
            }
        }
    }

    public boolean isQuestionDisplayedOnDetailPage(String expectedValue) {
        if (expectedValue == null || expectedValue.trim().isEmpty()) return false;
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
                String pageSource = d.getPageSource();
                return pageSource.contains(expectedValue) || pageSource.contains(expectedValue.replace("&", "&amp;"));
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getDisplayedProductName() {
        try {
            WebElement productElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//td[normalize-space()='Product Name']/following-sibling::td"))
            );
            return productElement.getText().trim();
        } catch (TimeoutException e) {
            throw new AssertionError("Product Name field not found on FAQ detail page.");
        }
    }

    public String getDisplayedFieldValue(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be empty.");
        }
        By fieldLocator = By.xpath("//td[normalize-space()='" + fieldName.trim() + "']/following-sibling::td[1]");
        try {
            WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(fieldLocator));
            return field.getText().trim();
        } catch (TimeoutException e) {
            throw new AssertionError("Field not found on FAQ detail page: " + fieldName, e);
        }
    }

    public String getDisplayedStatus() {
        return getDisplayedFieldValue("Status");
    }

    public String getDisplayedCategory() {
        return getDisplayedFieldValue("Category");
    }

    public String getDisplayedAnswer() {
        return getDisplayedFieldValue("Answer");
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}