package com.microfinance.los.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ApplicantDashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators based on Angular Material form elements from previous implementation
    private By ageInput = By.xpath("//input[@formcontrolname='age']");
    private By incomeInput = By.xpath("//input[@formcontrolname='income']");
    private By loanAmountInput = By.xpath("//input[@formcontrolname='loanAmount']");
    private By submitButton = By.xpath("//button[@type='submit']");
    private By successAlert = By.cssSelector(".success-alert");

    public ApplicantDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateTo() {
        driver.get("http://localhost:4200/applicant");
    }

    public void fillApplication(String age, String income, String loanAmount) {
        driver.findElement(ageInput).sendKeys(age);
        driver.findElement(incomeInput).sendKeys(income);
        driver.findElement(loanAmountInput).sendKeys(loanAmount);
    }

    public void submitApplication() {
        driver.findElement(submitButton).click();
    }

    public String getSuccessMessage() {
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(successAlert));
        return alert.getText();
    }
}
