package ogs.util;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import com.aventstack.extentreports.Status;
import ogs.selenium.reporting.TestReporter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class that extracts properties from the prop file.
 */
public class PropertyLoader {

  private static final String DEBUG_PROPERTIES = "debug.properties";

  public static Capabilities getCapabilities() throws IOException {
    return getCapabilities(System.getProperty("application.properties", DEBUG_PROPERTIES));
  }

  public static Capabilities getCapabilities(String fromResource) throws IOException {
    Properties props = new Properties();
    props.load(new FileInputStream(fromResource));

    DesiredCapabilities capabilities = new DesiredCapabilities();
    if (props.getProperty("browser.name") != null) {
      capabilities.setBrowserName(props.getProperty("browser.name"));
    }
    if (props.getProperty("browser.version") != null) {
      capabilities.setVersion(props.getProperty("browser.version"));
    }
    if (props.getProperty("browser.platform") != null && !props.getProperty("browser.platform").equals("")) {
      capabilities.setPlatform(Platform.valueOf(props.getProperty("browser.platform")));
    }

    return capabilities;
  }

  public static String getProperty(String name) throws IOException {
    return getProperty(name, System.getProperty("application.properties", DEBUG_PROPERTIES));
  }

  public static String getProperty(String name, String fromResource) throws IOException {
    Properties props = new Properties();
    props.load(new FileInputStream(fromResource));

    return props.getProperty(name);
  }
  
  public static Properties getAllProperties(){
	  return getAllProperties(System.getProperty("application.properties", DEBUG_PROPERTIES));
  }
  
  public static Properties getAllProperties(String fromResource){
	  Properties pro = new Properties();
	  try {
		pro.load(new FileInputStream(fromResource));
	} catch (IOException e) {
		TestReporter.logInfo("Exception has occured while reading property file "+e.getLocalizedMessage(), Status.FAIL);
	}
	  return pro;
  }

}