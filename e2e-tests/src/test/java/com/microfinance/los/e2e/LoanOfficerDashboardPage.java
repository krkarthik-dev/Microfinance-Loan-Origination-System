package com.microfinance.los.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoanOfficerDashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By tableRows = By.cssSelector("table tbody tr");
    private By approveButtons = By.xpath("//button[contains(text(), 'Approve')]");

    public LoanOfficerDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateTo() {
        driver.get("http://localhost:4200/officer");
        // Wait for table to load
        wait.until(ExpectedConditions.presenceOfElementLocated(tableRows));
    }

    public int getNumberOfPendingApplications() {
        List<WebElement> rows = driver.findElements(tableRows);
        return rows.size();
    }

    public void approveFirstApplication() {
        WebElement firstApproveBtn = wait.until(ExpectedConditions.elementToBeClickable(approveButtons));
        firstApproveBtn.click();
    }
}
