package com.microfinance.los.e2e;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LoanFlowE2ETest extends BaseTest {

    @Test
    public void testCompleteLoanOriginationFlow() throws InterruptedException {
        // Step 1: Applicant applies for a loan
        ApplicantDashboardPage applicantPage = new ApplicantDashboardPage(driver);
        applicantPage.navigateTo();
        
        applicantPage.fillApplication("30", "60000", "15000");
        applicantPage.submitApplication();
        
        String successMsg = applicantPage.getSuccessMessage();
        Assert.assertTrue(successMsg.contains("Submitted Successfully"), "Application submission failed.");
        
        // Wait for async processing (Kafka -> ML -> DB)
        // In a real environment with mock backend, we might wait or use Awaitility
        Thread.sleep(2000); 

        // Step 2: Officer reviews and approves the loan
        LoanOfficerDashboardPage officerPage = new LoanOfficerDashboardPage(driver);
        officerPage.navigateTo();
        
        int pendingApps = officerPage.getNumberOfPendingApplications();
        Assert.assertTrue(pendingApps > 0, "No pending applications found in Officer dashboard.");
        
        officerPage.approveFirstApplication();
        
        // Let UI update
        Thread.sleep(1000);
    }
}
