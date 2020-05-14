package ogs.selenium.web;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import com.aventstack.extentreports.Status;
import ogs.selenium.reporting.TestReporter;

public class ElementImpl extends RemoteWebElement implements Element{

	private final WebElement element;

	/**
	 * Creates a Element for a given WebElement.
	 *
	 * @param element element to wrap up
	 */
	public ElementImpl(final WebElement element) {
		this.element = element;
	}

	@Override
	public void click() {
		try{
			element.click();
			TestReporter.logInfo(" Clicked on "+element.toString(), Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(" Exception has occured while clicking on "+element.toString()+" Exception details::> "+e.getLocalizedMessage(), Status.FAIL);
		}
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		try{
			element.sendKeys(keysToSend);
			TestReporter.logInfo("Entered the "+keysToSend+" text in to "+element.toString(), Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(" Exception has occured while entering text into "+element.toString()+" Exception details::> "+e.getLocalizedMessage(), Status.FAIL);
		}
	}

	@Override
	public Point getLocation() {
		return element.getLocation();
	}

	@Override
	public void submit() {
		element.submit();
	}

	@Override
	public String getAttribute(String name) {
		return element.getAttribute(name);
	}

	@Override
	public String getCssValue(String propertyName) {
		return element.getCssValue(propertyName);
	}

	@Override
	public Dimension getSize() {
		return element.getSize();
	}

	@Override
	public List<WebElement> findElements(By by) {
		return element.findElements(by);
	}

	@Override
	public String getText() {
		return element.getText();
	}

	@Override
	public String getTagName() {
		return element.getTagName();
	}

	@Override
	public boolean isSelected() {
		return element.isSelected();
	}

	@Override
	public WebElement findElement(By by) {
		return element.findElement(by);
	}

	@Override
	public boolean isEnabled() {
		return element.isEnabled();
	}

	@Override
	public boolean isDisplayed() {
		return element.isDisplayed();
	}

	@Override
	public void clear() {
		element.clear();
	}

	@Override
	public WebElement getWrappedElement() {
		return element;
	}

	@Override
	public Coordinates getCoordinates() {
		return ((Locatable) element).getCoordinates();
	}

	@Override
	public boolean elementWired() {
		return (element != null);
	}

	@Override
	public Rectangle getRect() {
		return element.getRect();
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> arg0) throws WebDriverException {
		// TODO Auto-generated method stub
		return element.getScreenshotAs(arg0);
	}

	@Override
	public void verifyText(String strToVerify) {
		if(element.getText().equals(strToVerify)){
			TestReporter.logInfo(element.toString()+" text is same as "+strToVerify, Status.PASS);
		}else{
			TestReporter.logInfo(element.toString()+" Element text is not same as "+strToVerify, Status.FAIL);
		}
	}

	public void verifyTextPresent(String textPresent) {
		if(element.getText().equals(textPresent)){
			TestReporter.logInfo(element.toString()+" contains "+textPresent, Status.PASS);
		}else{
			TestReporter.logInfo(element.toString()+" does not contain "+textPresent, Status.FAIL);
		}
	}

	@SuppressWarnings("unused")
	private String getVarName(){
		for(StackTraceElement ele : Thread.currentThread().getStackTrace()){
			System.out.println(ele.getClassName());
		}
		return "element";
	}

	public WebElement waitForElement() {
		FluentWait<WebElement> wait = new FluentWait<WebElement>(this.element);
		wait.pollingEvery(2, TimeUnit.SECONDS).withTimeout(60, TimeUnit.SECONDS).ignoring(NoSuchElementException.class)
		.ignoring(StaleElementReferenceException.class);
		return (WebElement) wait.until((Function<? super WebElement, WebElement>) input -> {
			if (input != null && input.isDisplayed()) {
				return this.element;
			} else {
				return null;
			}
		});
	}

	public WebElement waitForNotVisible() {
		FluentWait<WebElement> wait = new FluentWait<WebElement>(this.element);
		wait.pollingEvery(2, TimeUnit.SECONDS).withTimeout(60, TimeUnit.SECONDS).ignoring(NoSuchElementException.class)
		.ignoring(StaleElementReferenceException.class);
		return (WebElement) wait.until((Function<? super WebElement, WebElement>) input -> {
			if (input != null && !input.isDisplayed()) {
				return this.element;
			} else {
				return null;
			}
		});
	}

	public void verifyElementIsVisible(){
		if (element != null && isDisplayed()) {
			TestReporter.logInfo("Element is visible",Status.PASS);
		} else {
			TestReporter.logInfo("Element is not visible",Status.FAIL);
		}
	}

	public void verifyElementIsNotVisible(){
		if (element != null && isDisplayed()) {
			TestReporter.logInfo("Element is visible",Status.FAIL);
		} else {
			TestReporter.logInfo("Element is not visible",Status.PASS);
		}
	}

	public void verifyElementIsEnabled() {
		if (element != null && isDisplayed() && isEnabled()) {
			TestReporter.logInfo("Element is enable", Status.PASS);
		} else {
			TestReporter.logInfo("Element is not enable", Status.FAIL);
		}
	}

	public void verifyElementIsNotEnabled() {
		if (element != null && isDisplayed() && isEnabled()) {
			TestReporter.logInfo("Element is visible", Status.FAIL);
		} else {
			TestReporter.logInfo("Element is not visible", Status.PASS);
		}
	}

	public void verifyTextNotPresent(String text) {

		if (element.getText().equals(text)) {
			TestReporter.logInfo(element.toString() + " contains " + text, Status.PASS);
		} else {
			TestReporter.logInfo(element.toString() + " does not contain " + text, Status.FAIL);
		}
	}

	public void selectByVisibleText(String visibleText){
		try{
			Select select = new Select(this.element);
			select.selectByVisibleText(visibleText);
			TestReporter.logInfo(visibleText+" is selected from dropdown ", Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(e.getLocalizedMessage()+" No matching option elements are found for "+visibleText, Status.FAIL);
		}
	}

	public void selectByPartialText(String partialText) {
		try {
			Select select = new Select(this.element);
			select.getOptions().stream().filter(option -> option.getText().contains(partialText))
			.forEach(option -> select.selectByVisibleText(option.getText()));
			TestReporter.logInfo(partialText + " is selected from dropdown ", Status.INFO);
		} catch (Exception e) {
			TestReporter.logInfo(e.getMessage() + " No matching option elements are found for " + partialText,
					Status.FAIL);
		}
	}

	public void selectByValue(String dropDownValue){
		try{
			Select select = new Select(this.element);
			select.selectByValue(dropDownValue);
			TestReporter.logInfo(dropDownValue+" is selected from dropdown ", Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(e.getMessage()+" No matching option elements are found for "+dropDownValue, Status.FAIL);
		}
	}

	public void selectByIndex(int index){
		try{
			Select select = new Select(this.element);
			select.selectByIndex(index);
			TestReporter.logInfo("Value at index "+index+" is selected from dropdown ", Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(e.getLocalizedMessage()+" Error occured while selecting value from dropdown at index "+index, Status.FAIL);
		}
	}

	public List<String> getAllOptions(){
		List<String> options = new ArrayList<String>();
		new Select(this.element).getOptions().stream().forEach(option ->options.add(option.getText()));
		return options;
	}

	public void verifyDropDownOptions(List<String> options, boolean partially){
		List<String> actOptions = getAllOptions();
		if(!partially){
			if(actOptions.size()==options.size()){
				boolean optionsPresent = true;
				for(String option : options){
					if(!actOptions.contains(option)){
						optionsPresent = false;
						TestReporter.logInfo(option+" is not present in the dropdown ", Status.FAIL);
					}
				}
				if(optionsPresent){
					TestReporter.logInfo(options+" are present in the dropdown with values "+actOptions, Status.PASS);
				}
			}else{
				TestReporter.logInfo(options+" size is not same as the actual options size from dropdown "+actOptions, Status.FAIL);
			}
		}else{
			boolean optionsPresent = true;
			for(String option : options){
				if(!actOptions.contains(option)){
					optionsPresent = false;
					TestReporter.logInfo(option+" is not present in the dropdown ", Status.FAIL);
				}
			}
			if(optionsPresent){
				TestReporter.logInfo(options+" are present in the dropdown with values "+actOptions, Status.PASS);
			}
		}
	}

	public void verifySelectedOption(String optionToBeVerified){
		Select select =  new Select(this.element);
		if(select.getFirstSelectedOption().equals(optionToBeVerified)){
			TestReporter.logInfo(optionToBeVerified+" option is selected in the dropdown ", Status.PASS);
		}else{
			TestReporter.logInfo(optionToBeVerified+" option is not selected in the dropdown ", Status.FAIL);
		}
	}

	public void verifyElementIsChecked(String ObjName){
		if(this.element.isSelected()){
			TestReporter.logInfo(ObjName+" element is checked ",Status.PASS);
		}else{
			TestReporter.logInfo(ObjName+" element is not checked ",Status.FAIL);
		}
	}

	public void verifyElementIsNotChecked(String ObjName){
		if(this.element.isSelected()){
			TestReporter.logInfo(ObjName+" element is checked ",Status.FAIL);
		}else{
			TestReporter.logInfo(ObjName+" element is not checked ",Status.PASS);
		}
	}

	public void verifyTRUE(Boolean expression, String successMessage, String failureMessage) {
		if(expression){
			TestReporter.logInfo(successMessage,Status.PASS);
		}else{
			TestReporter.logInfo(failureMessage,Status.FAIL);
		}
	}

	public void verifyFALSE(Boolean expression, String successMessage, String failureMessage) {
		if(!expression){
			TestReporter.logInfo(successMessage,Status.PASS);
		}else{
			TestReporter.logInfo(failureMessage,Status.FAIL);
		}
	}

	public void actionsClick(){
		try{
			Actions action =  new Actions(DriverFactory.getInstance().getDriver());
			action.moveToElement(element).click().build().perform();
			TestReporter.logInfo(" Clicked on "+element.toString(), Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(" Exception has occured while clicking on "+element.toString()+" Exception details::> "+e.getLocalizedMessage(), Status.FAIL);
		}
	}

	public void moveToElement(){
		try{
			Actions action =  new Actions(DriverFactory.getInstance().getDriver());
			action.moveToElement(element).build().perform();
			TestReporter.logInfo(" Clicked on "+element.toString(), Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(" Exception has occured while clicking on "+element.toString()+" Exception details::> "+e.getLocalizedMessage(), Status.FAIL);
		}
	}

	public void rightClick(){
		try{
			Actions action =  new Actions(DriverFactory.getInstance().getDriver());
			action.moveToElement(element).contextClick(element).build().perform();
			TestReporter.logInfo(" Clicked on "+element.toString(), Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(" Exception has occured while clicking on "+element.toString()+" Exception details::> "+e.getLocalizedMessage(), Status.FAIL);
		}
	}

	public void doubleClick(){
		try{
			Actions action =  new Actions(DriverFactory.getInstance().getDriver());
			action.moveToElement(element).doubleClick(element).build().perform();
			TestReporter.logInfo(" Clicked on "+element.toString(), Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(" Exception has occured while clicking on "+element.toString()+" Exception details::> "+e.getLocalizedMessage(), Status.FAIL);
		}
	}

	public void dropAndDrop(WebElement destElement){
		try{
			Actions action =  new Actions(DriverFactory.getInstance().getDriver());
			action.dragAndDrop(element, destElement).build().perform();
			TestReporter.logInfo(" Clicked on "+element.toString(), Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(" Exception has occured while clicking on "+element.toString()+" Exception details::> "+e.getLocalizedMessage(), Status.FAIL);
		}
	}

	public void jsClick(){
		try{
			JavascriptExecutor executor = (JavascriptExecutor)DriverFactory.getInstance().getDriver();
			executor.executeScript("arguments[0].click();", element);
			TestReporter.logInfo(" Clicked on "+element.toString(), Status.INFO);
		}catch(Exception e){
			TestReporter.logInfo(" Exception has occured while clicking on "+element.toString()+" Exception details::> "+e.getLocalizedMessage(), Status.FAIL);
		}

	}
	
	

}
