package ogs.selenium.web;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;

import ogs.selenium.factory.ImplementedBy;

@ImplementedBy(ElementImpl.class)
public interface Element extends WebElement, WrapsElement, Locatable{

	public void verifyText(String strToVerify);

	boolean elementWired();
	
	public String toString();
	
	public void verifyTextPresent(String textPresent);

	public WebElement waitForElement();

	public WebElement waitForNotVisible();

	public void verifyElementIsVisible();

	public void verifyElementIsNotVisible();

	public void verifyElementIsEnabled();

	public void verifyElementIsNotEnabled();

	public void verifyTextNotPresent(String text);

	public void selectByVisibleText(String visibleText);

	public void selectByPartialText(String partialText);

	public void selectByValue(String dropDownValue);

	public void selectByIndex(int index);

	public List<String> getAllOptions();

	public void verifyDropDownOptions(List<String> options, boolean partially);

	public void verifySelectedOption(String optionToBeVerified);

	public void verifyElementIsChecked(String ObjName);

	public void verifyElementIsNotChecked(String ObjName);

	public void verifyTRUE(Boolean expression, String successMessage, String failureMessage);

	public void verifyFALSE(Boolean expression, String successMessage, String failureMessage);
	
	public void actionsClick();
	
	public void moveToElement();
	
	public void rightClick();
	
	public void doubleClick();
	
	public void dropAndDrop(WebElement destElement);
	
	public void jsClick();

	
}
