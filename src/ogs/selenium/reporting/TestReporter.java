package ogs.selenium.reporting;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import ogs.selenium.web.DriverFactory;

public class TestReporter {

	public static ThreadLocal<ExtentTest> eTest = new ThreadLocal<ExtentTest>();
	private static ThreadLocal<SoftAssert> softAssert = new ThreadLocal<SoftAssert>();
	
	public static void logInfo(String info,Status status){
		if(eTest.get()==null){
			eTest.set(ExtentManager.getInstance().createTest(Test.class.getName()));
		}
		switch(status){
		case PASS:
			eTest.get().log(status,info);
			break;
		case FAIL:
			//eTest.get().log(status, info, MediaEntityBuilder.createScreenCaptureFromPath("Screenshots/"+takeScreenShot()).build());
			eTest.get().log(status,info);
			//	softAssert.get().fail(info);
			break;
		case INFO:
			eTest.get().log(status, info);
		case DEBUG:
			break;
		case ERROR:
			break;
		case FATAL:
			break;
		case SKIP:
			break;
		case WARNING:
			eTest.get().warning(info);
			break;
		default:
			break;
		}
	}

	public static synchronized String takeScreenShot(){
		try{
			java.io.File src = (java.io.File) ((TakesScreenshot) DriverFactory.getInstance().getDriver()).getScreenshotAs(OutputType.FILE);
			/*BufferedImage originalImage =ImageIO.read(src);
			int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			BufferedImage resizeImageHintPng = resizeImageWithHint(originalImage, type);*/
			String scrPath = System.getProperty("user.dir")+"/TestReport/Screenshots/";//
			ExtentManager.createReportPath(scrPath);
			String scrName = "Screenshot"+System.currentTimeMillis()+".png";
			scrPath = scrPath+scrName;
			//ImageIO.write(resizeImageHintPng, "png", new java.io.File(scrPath));
			FileUtils.copyFile(src, new File(scrPath));
			return scrName;
		}catch(Exception e){
			eTest.get().log(Status.FAIL, "Failed while taking screen shot");
		}
		return "";
	}

	@SuppressWarnings("unused")
	private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type){

		BufferedImage resizedImage = new BufferedImage(1000, 300, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, 1000, 300, null);
		g.dispose();	
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}

	public static void startTest(String str){
		if(eTest.get()==null){
			eTest.set(ExtentManager.getInstance().createTest(str));
			//softAssert.set(new SoftAssert());
		}
	}

	public static void endTest(){
		if(eTest!=null){
			eTest.get().getModel().end();
			//softAssert.get().assertAll();
			//softAssert.remove();
			eTest.remove();
		}
	}

	/*public static void endTest(ITestResult result){
		if(eTest.get()!=null){
			try{
				if(eTest.get().getModel().getStatus()==Status.FAIL){
					//result.setStatus(ITestResult.FAILURE);
					Assert.assertTrue(false, eTest.get().getModel()+" is failed");
				}
			}finally{
				eTest.get().getModel().end();
				eTest.remove();
			}
		}
	}*/

	public static void setTestScenarioName(String scenario){
		eTest.get().getModel().setName(scenario);
	}
	
	public static String getTestScenarioName(){
		return eTest.get().getModel().getName();
	}

	public static void endSuite(){
		ExtentManager.getInstance().flush();
	}


}
