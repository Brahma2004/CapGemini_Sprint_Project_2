package CRM_Vtiger_BITM_ObjRepo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

public class CRM_Vtiger_BITM_ProjectMilestonesPage {

	private final WebDriver driver;
	private final WebDriverWait wait;

	private final String baseUrl = "http://49.249.29.4:8888/index.php";
	private final String listUrl = baseUrl + "?module=ProjectMilestone&action=index";
	private final String createUrl = baseUrl + "?module=ProjectMilestone&action=EditView";

	private final By createButton = By.xpath("//img[@title='Create Project Milestone...' or @alt='Create Project Milestone...']"
			+ " | //a[contains(@href,'ProjectMilestone') and contains(@href,'EditView')]"
			+ " | //a[contains(@title,'Create') and contains(@href,'ProjectMilestone')]");

	private final By milestoneNameField = By.name("projectmilestonename");
	private final By milestoneDateField = By.name("projectmilestonedate");
	private final By milestoneTypeDropdown = By.name("projectmilestonetype");
	private final By projectField = By.id("projectid_display");

	private final By projectLookupButton = By.xpath("//img[@title='Select' and contains(@onclick,'projectid')]"
			+ " | //input[@name='projectid']/following-sibling::img[@title='Select']"
			+ " | //input[@name='projectid']/following-sibling::input[@type='image']");

	private final By saveButton = By.xpath("//input[@name='button' and contains(@title,'Save')]"
			+ " | //input[@name='button' and contains(@value,'Save')]");

	private final By cancelButton = By.xpath("//input[@name='button' and contains(@title,'Cancel')]"
			+ " | //input[@name='button' and contains(@value,'Cancel')]");

	private final By listSearchButton = By.xpath("//img[contains(@title,'Search in Project Milestones')]"
			+ " | //img[contains(@title,'Search')]");

	private final By listSearchText = By.name("search_text");
	private final By listSearchField = By.id("bas_searchfield");
	private final By listSearchNowButton = By.xpath("//input[@name='submit' and contains(@value,'Search Now')]"
			+ " | //input[contains(@value,'Search Now')]");

	private final By listViewContents = By.id("ListViewContents");
	private final By noRecordsFoundText = By.xpath("//span[contains(normalize-space(),'No Project Milestone Found')]");
	private final By headerText = By.className("dvHeaderText");

	private final By popupSearchText = By.name("search_text");
	private final By popupSearchField = By.name("search_field");
	private final By popupSearchButton = By.name("search");

	private final By inlineValidationMessages = By.xpath("//*[contains(@class,'errorMessage')"
			+ " or contains(@class,'validation-error') or contains(@class,'fieldError')"
			+ " or contains(@class,'error')][normalize-space(.)!='']");

	public CRM_Vtiger_BITM_ProjectMilestonesPage(WebDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("WebDriver cannot be null.");
		}
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	public void navigateToProjectMilestones() {
		acceptAlertIfPresent();
		try {
			driver.get(listUrl);
		} catch (TimeoutException e) {
			try {
				((JavascriptExecutor) driver).executeScript("window.stop();");
			} catch (Exception ignored) {
			}
		}

		WebDriverWait pageWait = new WebDriverWait(driver, Duration.ofSeconds(20));
		pageWait.until(d -> {
			try {
				String currentUrl = d.getCurrentUrl();
				if (currentUrl == null || !currentUrl.contains("ProjectMilestone")) {
					return false;
				}
				return isDisplayed(createButton) || !d.findElements(listViewContents).isEmpty()
						|| d.getPageSource().contains("Project Milestones");
			} catch (org.openqa.selenium.UnhandledAlertException e) {
				acceptAlertIfPresent();
				return false;
			}
		});
	}

	public boolean isProjectMilestonesPageDisplayed() {
		try {
			String currentUrl = driver.getCurrentUrl();
			if (currentUrl == null || !currentUrl.contains("ProjectMilestone")) {
				return false;
			}
			return isDisplayed(createButton) || !driver.findElements(listViewContents).isEmpty()
					|| driver.getPageSource().contains("Project Milestones");
		} catch (Exception e) {
			return false;
		}
	}

	public void clickCreateButton() {
		boolean clicked = false;
		try {
			for (WebElement element : driver.findElements(createButton)) {
				if (element.isDisplayed() && element.isEnabled()) {
					element.click();
					clicked = true;
					break;
				}
			}
		} catch (Exception ignored) {
		}

		if (!clicked) {
			driver.get(createUrl);
		}
		wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField));
	}

	public boolean isCreateFormDisplayed() {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField)).isDisplayed()
					&& isDisplayed(saveButton) && isDisplayed(cancelButton);
		} catch (TimeoutException e) {
			return false;
		}
	}

	public void enterMilestoneName(String name) {
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField));
		element.clear();
		if (name != null && !name.isEmpty()) {
			element.sendKeys(name);
		}
	}

	public String getMilestoneName() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField)).getAttribute("value");
	}

	public void enterMilestoneDate(String date) {
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneDateField));
		element.clear();
		if (date != null && !date.isEmpty()) {
			element.sendKeys(date);
		}
	}

	public String selectRandomMilestoneType() {
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneTypeDropdown));
		Select select = new Select(element);

		List<WebElement> validOptions = select.getOptions().stream()
				.filter(opt -> !opt.getText().trim().isEmpty() && !opt.getText().toLowerCase().contains("none"))
				.toList();

		if (validOptions.isEmpty()) {
			throw new IllegalStateException("No valid options available in Milestone Type dropdown.");
		}

		int randomIndex = new Random().nextInt(validOptions.size());
		WebElement chosenOption = validOptions.get(randomIndex);
		String chosenText = chosenOption.getText();

		select.selectByVisibleText(chosenText);
		return chosenText;
	}

	public void selectMilestoneType(String type) {
		if (type == null || type.trim().isEmpty()) {
			return;
		}
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneTypeDropdown));
		Select select = new Select(element);

		WebElement matchedOption = select.getOptions().stream()
				.filter(option -> option.getText().trim().equalsIgnoreCase(type.trim()))
				.findFirst()
				.orElse(null);

		if (matchedOption != null) {
			select.selectByVisibleText(matchedOption.getText());
		} else {
			selectRandomMilestoneType();
		}
	}

	public String getRelatedProject() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(projectField)).getAttribute("value");
	}

	public boolean openRelatedToLookup() {
		String parentWindow = driver.getWindowHandle();
		Set<String> oldHandles = driver.getWindowHandles();

		try {
			WebElement lookupButton = wait.until(ExpectedConditions.elementToBeClickable(projectLookupButton));
			lookupButton.click();

			try {
				wait.until(ExpectedConditions.numberOfWindowsToBe(oldHandles.size() + 1));
				Set<String> newHandles = driver.getWindowHandles();
				newHandles.removeAll(oldHandles);

				if (!newHandles.isEmpty()) {
					driver.switchTo().window(newHandles.iterator().next());
					wait.until(d -> "complete".equals(
							((JavascriptExecutor) d).executeScript("return document.readyState")));
					return waitForLookupControls();
				}
			} catch (TimeoutException ignored) {
			}

			if (driver.getWindowHandles().contains(parentWindow)) {
				driver.switchTo().window(parentWindow);
			}
			return waitForLookupControls();

		} catch (Exception e) {
			return false;
		} finally {
			closePopupAndReturn(parentWindow);
		}
	}

	private boolean waitForLookupControls() {
		try {
			return new WebDriverWait(driver, Duration.ofSeconds(5)).until(
					d -> !d.findElements(popupSearchText).isEmpty() || !d.findElements(popupSearchField).isEmpty());
		} catch (TimeoutException e) {
			return false;
		}
	}

	public void selectRelatedProject(String projectName) {
		if (projectName == null || projectName.trim().isEmpty()) {
			throw new IllegalArgumentException("Related Project name cannot be empty.");
		}

		String parentWindow = driver.getWindowHandle();
		Set<String> oldHandles = driver.getWindowHandles();

		try {
			wait.until(ExpectedConditions.elementToBeClickable(projectLookupButton)).click();
			wait.until(ExpectedConditions.numberOfWindowsToBe(oldHandles.size() + 1));

			Set<String> newHandles = driver.getWindowHandles();
			newHandles.removeAll(oldHandles);

			if (newHandles.isEmpty()) {
				throw new RuntimeException("Project lookup popup did not open.");
			}

			driver.switchTo().window(newHandles.iterator().next());

			WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(popupSearchText));
			searchBox.clear();
			searchBox.sendKeys(projectName);

			if (!driver.findElements(popupSearchField).isEmpty()) {
				WebElement dropdown = driver.findElement(popupSearchField);
				Select select = new Select(dropdown);
				for (WebElement option : select.getOptions()) {
					if ("projectname".equalsIgnoreCase(option.getAttribute("value"))) {
						select.selectByValue("projectname");
						break;
					}
				}
			}

			if (!driver.findElements(popupSearchButton).isEmpty()) {
				wait.until(ExpectedConditions.elementToBeClickable(popupSearchButton)).click();
			}

			By projectLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(projectName) + "]");
			wait.until(ExpectedConditions.elementToBeClickable(projectLink)).click();

			wait.until(ExpectedConditions.numberOfWindowsToBe(oldHandles.size()));
			driver.switchTo().window(parentWindow);
			wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField));

		} catch (Exception e) {
			throw new RuntimeException("Unable to select project: " + projectName, e);
		} finally {
			closePopupAndReturn(parentWindow);
		}
	}

	public String selectFirstAvailableProject() {
		String parentWindow = driver.getWindowHandle();
		Set<String> oldHandles = driver.getWindowHandles();

		try {
			WebElement lookupButton = wait.until(ExpectedConditions.elementToBeClickable(projectLookupButton));
			lookupButton.click();

			wait.until(ExpectedConditions.numberOfWindowsToBe(oldHandles.size() + 1));
			Set<String> newHandles = driver.getWindowHandles();
			newHandles.removeAll(oldHandles);

			if (newHandles.isEmpty()) {
				throw new RuntimeException("Project lookup popup did not open.");
			}

			driver.switchTo().window(newHandles.iterator().next());
			wait.until(ExpectedConditions.presenceOfElementLocated(popupSearchText));

			By firstRecordLink = By.xpath("//table[contains(@class,'small')]//tr[position()>1]//a[contains(@href,'javascript:window.close()') or contains(@href,'set_return')]");
			WebElement link = wait.until(ExpectedConditions.elementToBeClickable(firstRecordLink));
			String selectedProject = link.getText().trim();
			link.click();

			wait.until(ExpectedConditions.numberOfWindowsToBe(oldHandles.size()));
			driver.switchTo().window(parentWindow);
			wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField));
			return selectedProject;
		} catch (Exception e) {
			closePopupAndReturn(parentWindow);
			throw new RuntimeException("Unable to pick first available project from lookup", e);
		}
	}

	public void createMilestone(String milestoneName, String projectName, String milestoneType, String milestoneDate) {
		clickCreateButton();
		enterMilestoneName(milestoneName);
		if (projectName != null && !projectName.isEmpty()) {
			selectRelatedProject(projectName);
		} else {
			selectFirstAvailableProject();
		}
		if (milestoneType != null && !milestoneType.isEmpty()) {
			selectMilestoneType(milestoneType);
		}
		if (milestoneDate != null && !milestoneDate.isEmpty()) {
			enterMilestoneDate(milestoneDate);
		}
		clickSave();
	}

	public void clickSave() {
		WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
		try {
			btn.click();
		} catch (Exception e) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
		}

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

	public void clickCancel() {
		WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(cancelButton));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
		try {
			btn.click();
		} catch (Exception e) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
		}
	}

	public void searchMilestoneByName(String milestoneName) {
		if (milestoneName == null || milestoneName.trim().isEmpty()) {
			throw new IllegalArgumentException("Milestone name cannot be empty.");
		}

		if (!isDisplayed(listSearchText)) {
			wait.until(ExpectedConditions.elementToBeClickable(listSearchButton)).click();
		}

		WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(listSearchText));
		searchBox.clear();
		searchBox.sendKeys(milestoneName);

		WebElement searchDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(listSearchField));
		Select select = new Select(searchDropdown);
		select.selectByValue("projectmilestonename");

		WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(listSearchNowButton));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);
		submitBtn.click();
		wait.until(ExpectedConditions.presenceOfElementLocated(listViewContents));
	}

	public boolean isMilestoneDisplayed(String milestoneName) {
		By milestoneLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(milestoneName) + "]");
		try {
			return wait.until(d -> {
				for (WebElement link : d.findElements(milestoneLink)) {
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

	public void openMilestone(String milestoneName) {
		By milestoneLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(milestoneName) + "]");
		WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(milestoneLink));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", link);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(link)).click();
		} catch (Exception e) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
		}
		wait.until(ExpectedConditions.visibilityOfElementLocated(headerText));
	}

	public boolean isNoRecordsFoundDisplayed() {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(noRecordsFoundText)).isDisplayed();
		} catch (TimeoutException e) {
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

	public String getDisplayedFieldValue(String fieldName) {
		By fieldLocator = By.xpath("//td[normalize-space()='" + fieldName.trim() + "']/following-sibling::td[1]");
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(fieldLocator)).getText().trim();
		} catch (TimeoutException e) {
			return "";
		}
	}

	public boolean isMandatoryValidationShown() {
		try {
			Alert alert = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());
			String message = alert.getText();
			alert.accept();
			if (message != null) {
				String normalized = message.toLowerCase();
				if (normalized.contains("cannot be empty") || normalized.contains("cannot be none")
						|| normalized.contains("required") || normalized.contains("mandatory")
						|| normalized.contains("enter")) {
					return true;
				}
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

		try {
			WebElement nameField = driver.findElement(milestoneNameField);
			Object valid = ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", nameField);
			if (Boolean.FALSE.equals(valid)) {
				return true;
			}
		} catch (Exception ignored) {
		}

		try {
			if (isCreateFormDisplayed() && getMilestoneName().trim().isEmpty()) {
				return true;
			}
		} catch (Exception ignored) {
		}

		return false;
	}

	private void acceptAlertIfPresent() {
		try {
			Alert alert = new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.alertIsPresent());
			alert.accept();
		} catch (TimeoutException | org.openqa.selenium.NoAlertPresentException ignored) {
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

	private boolean isDisplayed(By locator) {
		try {
			for (WebElement element : driver.findElements(locator)) {
				if (element.isDisplayed()) {
					return true;
				}
			}
		} catch (Exception ignored) {
		}
		return false;
	}
}