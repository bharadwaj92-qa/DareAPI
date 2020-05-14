package ogs.selenium.web;

import java.io.IOException;
import java.net.URL;

import org.openqa.selenium.Platform;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

	
import ogs.selenium.listeners.DriverEventListener;
import ogs.util.PropertyLoader;

public class DriverFactory {
	
	private DriverFactory(){
		
	}
	
	private static DriverFactory instance = new DriverFactory();
	
	public static DriverFactory getInstance(){
		
		return instance;
	}
	
	private ThreadLocal<EventFiringWebDriver> localDriver = new ThreadLocal<EventFiringWebDriver>()
			{
				protected EventFiringWebDriver initialValue(){
					System.setProperty("webdriver.chrome.driver", "C:\\googlr\\Media\\chromedriver.exe");
					ChromeOptions options = new ChromeOptions();
					options.setCapability(CapabilityType.PLATFORM, Platform.WINDOWS);
					options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
					options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
					options.setExperimentalOption("useAutomationExtension", false);
					options.addArguments("--start-maximized");
					EventFiringWebDriver eventFire = new EventFiringWebDriver(new ChromeDriver(options));
					return eventFire.register(new DriverEventListener());
				}
			};
	
	public void setDriver(String platform,String browserName){
		switch(platform){
		case "local":
			switch(BrowserFactory.valueOf(browserName)){
			case chrome:
				System.setProperty("webdriver.chrome.driver", "C:\\googlr\\Media\\chromedriver.exe");
				ChromeOptions options = new ChromeOptions();
				options.setCapability(CapabilityType.PLATFORM, Platform.WINDOWS);
				options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
				options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				options.setExperimentalOption("useAutomationExtension", false);
				options.addArguments("--start-maximized");
				EventFiringWebDriver eventFire = new EventFiringWebDriver(new ChromeDriver(options));
				this.localDriver.set(eventFire.register(new DriverEventListener()));
				break;
			case firefox:
				break;
			case ie:
				break;
			case remote:
				break;
			default:
				break;

			}
			break;
		case "saucelabs":
			URL url = null;
			try {
				url = new URL("http://" + PropertyLoader.getProperty("sauce.userid") + ":" + PropertyLoader.getProperty("sauce.accesskey") + "@ondemand.saucelabs.com:80/wd/hub");
			} catch (IOException e) {
				e.printStackTrace();
			}
			switch(BrowserFactory.valueOf(browserName)){
			case chrome:
				//System.setProperty("webdriver.chrome.driver", "C:\\googlr\\Media\\chromedriver.exe");
				DesiredCapabilities chromeCaps = DesiredCapabilities.chrome();
				chromeCaps.setCapability("platform", "Windows 7");
				chromeCaps.setCapability("version", "65.0");
				chromeCaps.setCapability("parentTunnel", "sauce_admin");
				chromeCaps.setCapability("tunnelIdentifier", "OptumSharedTunnel-Stg");
				EventFiringWebDriver eventFire = new EventFiringWebDriver(new RemoteWebDriver(url,chromeCaps));
				this.localDriver.set(eventFire.register(new DriverEventListener()));
				break;
			case firefox:
				DesiredCapabilities fireCaps = DesiredCapabilities.firefox();
				fireCaps.setCapability("platform", "Windows 7");
				fireCaps.setCapability("version", "60.0");
				EventFiringWebDriver ffEventFire = new EventFiringWebDriver(new RemoteWebDriver(url,fireCaps));
				this.localDriver.set(ffEventFire.register(new DriverEventListener()));
				break;
			case ie:
				DesiredCapabilities ieCaps = DesiredCapabilities.internetExplorer();
				ieCaps.setCapability("autoAcceptsAlerts", true);
				ieCaps.setCapability("parentTunnel", "sauce_admin");
				ieCaps.setCapability("tunnelIdentifier", "OptumSharedTunnel-Stg");
				ieCaps.setCapability("platform", "Windows 7");
				ieCaps.setCapability("version", "11.0");
				EventFiringWebDriver ieEventFire = new EventFiringWebDriver(new RemoteWebDriver(url,ieCaps));
				this.localDriver.set(ieEventFire.register(new DriverEventListener()));
				break;
			case remote:
				break;
			default:
				break;

			}

		}
	}
			
	public WebDriver getDriver(){
		return this.localDriver.get();
	}
	
	public void killDriver(){
		this.localDriver.get().quit();
		this.localDriver.remove(); 
	}
}
