package ogs.selenium.listeners;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.aventstack.extentreports.Status;

import ogs.selenium.reporting.TestReporter;

public class DriverEventListener extends AbstractWebDriverEventListener implements WebDriverEventListener{

	public void afterNavigateTo(String url, WebDriver driver) {
		TestReporter.logInfo("Navigated to "+url, Status.INFO);
	}

	public void afterClickOn(WebElement element, WebDriver driver) {
		//TestReporter.logInfo("Clicked on "+element, Status.PASS);
	}
	
	public void afterSendKeys(){
		
	}

	public void onException(Throwable error, WebDriver driver) {
		TestReporter.logInfo("Exception has occured. Details are : "+error.getMessage(), Status.FAIL);
	}
	
	public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
		System.out.println("Element "+element.getAttribute("placeholder")+" is set to "+element.getAttribute("value")+"/"+keysToSend);
	}
}
