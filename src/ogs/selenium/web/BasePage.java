package ogs.selenium.web;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.aventstack.extentreports.Status;
import ogs.selenium.factory.ElementFactory;
import ogs.selenium.reporting.TestReporter;
import ogs.util.ClassUtil;
import ogs.util.PageIdentifier;


/**
 * The Class BasePage is base class for all pages where we declare webelements and some common methods and should extend this class
 */
public class BasePage implements IBasePage{

	/**
	 * This method initializes all webelements of a page class that user passes to it and returns page class object
	 *
	 * @param <TPage> the generic type
	 * @param pageClass the page class
	 * @return the page
	 */
	public static  <TPage extends BasePage> TPage getPage (Class<TPage> pageClass) {
		try {
			//Initialize the Page with its elements and return it.
			return ElementFactory.initElements(DriverFactory.getInstance().getDriver(),  pageClass);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * This method will wait for page to load by checking readyState attribute.
	 *
	 * @return the boolean
	 */
	public  Boolean waitForPageToLoadUsingJS() {
		WebDriverWait wait = new WebDriverWait(getDriver(), 60); //give up after 10 seconds
		//keep executing the given JS till it returns "true", when page is fully loaded and ready
		return (Boolean) wait.until((Function<? super WebDriver, Boolean>) input -> (
				(JavascriptExecutor) input).executeScript("return document.readyState").equals("complete"));
	}

	/* This method is used to wait for the elements which are annotated with @PageIdentifier
	 * @see ogs.selenium.web.IBasePage#waitForPageLoad()
	 */
	@Override
	public void waitForPageLoad(){
		FluentWait<List<WebElement>> wait = new FluentWait<List<WebElement>>(pageElements());
		wait.withTimeout(60,TimeUnit.SECONDS).pollingEvery(3, TimeUnit.SECONDS).ignoring(StaleElementReferenceException.class);
		Boolean isPageLoaded = wait.until(new Function<List<WebElement>,Boolean>(){
			@Override
			public Boolean apply(List<WebElement> elts) {
				for(WebElement ele: elts){
					if(!ele.isDisplayed()){
						return false;
					}
				}
				return true;
			}
		});

		if(isPageLoaded){
			TestReporter.logInfo(BasePage.class.getClass().getName()+" is loaded", Status.PASS);
		}else{
			TestReporter.logInfo(BasePage.class.getClass().getName()+" is loaded", Status.FAIL);
		}
	}

	@SuppressWarnings("unchecked")
	private List<WebElement> pageElements() {

		Field[] fields = ClassUtil.getAnnotatedDeclaredFields(this.getClass(),PageIdentifier.class,false);
		List<WebElement> identifiers = new ArrayList<WebElement>();

		for (Field fld : fields) {
			if(WebElement.class.isAssignableFrom(fld.getType())){
				try {
					fld.setAccessible(true);
					identifiers.add((WebElement) fld.get(this));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			if(fld.getGenericType().getTypeName().equals("java.util.List<org.openqa.selenium.WebElement>")||List.class.isAssignableFrom(fld.getType())){
				try {
					fld.setAccessible(true);
					List<WebElement> elmnts = (List<WebElement>) fld.get(this);
					for(WebElement ele : elmnts){
						identifiers.add(ele);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return identifiers;

	}

	@Override
	public void openPage(String pageURL) {

		getDriver().navigate().to(getDriver().getCurrentUrl()+pageURL);
	}


	private WebDriver getDriver(){
		return DriverFactory.getInstance().getDriver();
	}

	public void verifyTextPresentInPage(String textPresent){
		if (getDriver().getPageSource().contains(textPresent)) {
			TestReporter.logInfo("Page contains " + textPresent, Status.PASS);
		} else {
			TestReporter.logInfo("Page does not contain " + textPresent, Status.FAIL);
		}
	}

	public void verifyPageTitle(String pageTitle){
		String actPageTitle = getDriver().getTitle();
		if (actPageTitle.equals(pageTitle)) {	
			TestReporter.logInfo("Page title is " + pageTitle, Status.PASS);
		} else {
			TestReporter.logInfo("Page title is not " + pageTitle+" Actual page title is "+actPageTitle, Status.FAIL);
		}
	}

}
