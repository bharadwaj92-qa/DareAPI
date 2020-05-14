package ogs.selenium.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import ogs.selenium.reporting.ExtentManager;
import ogs.selenium.reporting.TestReporter;
import ogs.selenium.web.BaseTest;

public class TestListener implements ITestListener {

	/* //Extent Report Declarations
    private static ExtentReports extent = ExtentManager.createInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();*/

	@Override
	public synchronized void onStart(ITestContext context) {

	}

	@Override
	public synchronized void onFinish(ITestContext context) {
		TestReporter.endSuite();
	}

	@Override
	public synchronized void onTestStart(ITestResult result) {
		TestReporter.startTest(result.getName());
	}

	@Override
	public synchronized void onTestSuccess(ITestResult result) {
		if(TestReporter.eTest.get().getModel().getStatus()==Status.FAIL){
			result.setStatus(ITestResult.FAILURE);
		}
		TestReporter.endTest();
	}

	@Override
	public synchronized void onTestFailure(ITestResult result) {
		if(!(TestReporter.eTest.get().getModel().getStatus()==Status.FAIL)){
			TestReporter.eTest.get().getModel().setStatus(Status.FAIL);
		}

		if(TestReporter.eTest.get().getModel().getStatus()==Status.PASS){
			result.setStatus(ITestResult.SUCCESS);
		}
		TestReporter.endTest();
	}

	@Override
	public synchronized void onTestSkipped(ITestResult result) {
		TestReporter.endTest();
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		System.out.println(("onTestFailedButWithinSuccessPercentage for " + result.getMethod().getMethodName()));
	}

}
