package ogs.selenium.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalTime;
import java.util.List;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import ogs.selenium.pages.Output;
import ogs.util.ExcelUtil;

@Listeners({ogs.selenium.listeners.TestListener.class})
public class BaseTest  {
	/*public BaseTest(){
		System.out.println("In Constructor");
	}*/
	
	
	@AfterSuite
	public void afterSuite(){
		try {
			System.out.println("Size of List for excel report :::>>"+Output.outputData.size()+" Started at "+LocalTime.now().toString());
			ExcelUtil.writeExcel(Output.outputData);
			System.out.println(" Ended at "+LocalTime.now().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
