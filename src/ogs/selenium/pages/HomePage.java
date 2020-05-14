package ogs.selenium.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import ogs.selenium.web.BasePage;
import ogs.selenium.web.Element;
import ogs.util.PageIdentifier;

public class HomePage extends BasePage{
	
	@FindBy(how = How.LINK_TEXT, using="Global Self Service (HR Transactions)")
	public Element test;
	
	@FindBy(how = How.NAME, using="q")
	public Element searchText;
	
	@PageIdentifier
	@FindBy(xpath="//button[@class='btn btn-primary']")
	public List<WebElement> btns;

	@Override
	public void openPage(String mappingPage) {
		
	}


}
