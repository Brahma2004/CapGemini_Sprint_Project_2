package CRM_Vtiger_BITM_GenericUtility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class CRM_Vtiger_BITM_ScreenshotUtility {

    private static final String SCREENSHOT_DIRECTORY = "Screenshot";

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }

        try {
            Path directory = Paths.get(SCREENSHOT_DIRECTORY);
            Files.createDirectories(directory);

            String sanitizedName = (screenshotName == null || screenshotName.trim().isEmpty())
                    ? "Screenshot"
                    : screenshotName.replaceAll("[^a-zA-Z0-9._-]", "_");

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String fileName = sanitizedName + "_" + timestamp + ".png";
            Path destination = directory.resolve(fileName);

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Screenshot saved: " + destination.toAbsolutePath());
            return destination.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new RuntimeException("Unable to save screenshot for: " + screenshotName, e);
        }
    }
}