package ogs.selenium.tests;

import java.io.IOException;
import java.util.Map;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import ogs.selenium.reporting.TestReporter;
import ogs.selenium.web.BaseTest;
import ogs.testng.dataprovider.CDataProvider;
import ogs.util.DBUtils;
import ogs.util.StringUtility;

public class AccessDareDBCompare extends BaseTest {
	
	//static List<Map<String, String>> dareData = null;
	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"}, enabled=false)
	@CDataProvider(dataFile="TestData/tblPhrases.xlsx",sheetName="tblPhrases",key="dare-phrase")
	public void verifyPhrases(Map<String,String> data) throws IOException, InterruptedException{
		String query = "select * from dare01.dbo_tblphrases where ID='"+data.get("ID")+"'";
		Map<String, String> dareData =DBUtils.getDataFromDB(query,"oldqa").get(0); //ExcelUtil.getData("TestData/tblPhrases.xlsx", "Sheet2",data.get("ID").trim() ).get(0);//(Map<String, String>) DBUtils.getDareData(query);
		System.out.println(data);
		System.out.println(dareData);
		String regex = "\\d+";
		data.forEach((colName,value) -> {
			if(!colName.equalsIgnoreCase("Key")){
			if(dareData.get(colName)!=null?dareData.get(colName).trim().equals(value.trim()):"".equals(value)){
				TestReporter.logInfo("Access DB > "+colName+" with value> "+value.trim()+" is matching with Dare Column> "+colName+" with value> "+dareData.get(colName), Status.PASS);
			}else{
				TestReporter.logInfo("Access DB > "+colName+" with value> "+value.trim()+" is not matching with Dare Column> "+colName+" with value> "+dareData.get(colName), Status.FAIL);
			}
			}
		});
	}
	
	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"}, enabled=true)
	@CDataProvider(dataFile="TestData/tblToolLicensureV8_30_19.xlsx",sheetName="tblToolLicensure",key="tblToolLicensure")
	public void verifyTblToolLicensure(Map<String,String> data) throws IOException, InterruptedException{
		TestReporter.setTestScenarioName(TestReporter.getTestScenarioName()+"_"+data.get("State"));
		//String columns[]={"ID","State","Postal","StateAge","POM","MaidContract","MaidDel","SSGMaid","SSG","StateIRS","I","R","S","TATI","TATR","TATS","POM1","POM2","POM2noMD","MCSphCode","MCSopCode","MCStmsCode","tblStateSpecificSSGMaidMin","tblStateSpecificSSGMaid","tblStateSpecificSSG","sMDABD","sMDABDHMO","sMDABDChild","sMDAppeal1","sMDAppeal1HMO","sMDAppeal1Child","sNoMDABD","sNoMDABDHMO","sNoMDABDChild","sNoMDAppeal1","sNoMDAppeal1HMO","sNoMDAppeal1Child","sMDEA","sMDEAHMO","sMDEAChild","sNoMDEA","sNoMDEAHMO","sNoMDEAChild","sMDRetro","sMDRetroHMO","sMDRetroChild","sNoMDRetro","sNoMDRetroHMO","sNoMDRetroChild","sMDRecon","sMDReconHMO","sMDReconChild","sNoMDRecon","sNoMDReconHMO","sNoMDReconChild","sMDABDHMOChild","sNoMDABDHMOChild","sMDRetroHMOChild","sNoMDRetroHMOChild","sMDReconHMOChild","sNoMDReconHMOChild","sMDAppeal1HMOChild","sNoMDAppeal1HMOChild","sMDEAHMOChild","sNoMDEAHMOChild","MedicaidMDABD","MedicaidMDAppeal1","MedicaidMDEA","MedicaidMDRetro","MedicaidMDRecon","MedicaidNoMDABD","MedicaidNoMDAppeal1","MedicaidNoMDEA","MedicaidNoMDRetro","MedicaidNoMDRecon","sMDAppeal2","sMDAppeal2HMO","sMDAppeal2Child","sNoMDAppeal2","sNoMDAppeal2HMO","sNoMDAppeal2Child","MedicaidMDAppeal2","MedicaidNoMDAppeal2","sMDAppeal2HMOChild","sNoMDAppeal2HMOChild","MedicaidMDABDChild","MedicaidMDAppeal1Child","MedicaidMDEAChild","MedicaidMDRetroCHild","MedicaidMDReconChild","MedicaidNoMDABDChild","MedicaidNoMDAppeal1Child","MedicaidNoMDEAChild","MedicaidNoMDRetroChild","MedicaidNoMDReconChild","MedicaidMDAppeal2Child","MedicaidNoMDAppeal2Child","HMO"};
		String query = "select * from dare01.tbltoollicensureirs where ID='"+data.get("ID")+"'";
		
			Map<String, String> dareData = DBUtils.getDataFromDB(query,"oldqa").get(0);//ExcelUtil.getData("TestData/tblToolLicensure.xlsx", "Sheet2",data.get("State").trim() ).get(0);//(Map<String, String>) DBUtils.mySqlDBConnection(query);
		// && !column.equalsIgnoreCase("Ihmo") && !column.equalsIgnoreCase("Rhmo") && !column.equalsIgnoreCase("Shmo")
		data.forEach((column,value) ->{
			if(!column.equalsIgnoreCase("Key")){
				if(dareData.get(column)!=null) {
					if(value.trim().equalsIgnoreCase(dareData.get(column).trim())){
						TestReporter.logInfo("Access DB > "+column+" with value> "+value+" is matching with Dare Column> "+column+" with value> "+dareData.get(column).trim(), Status.PASS);
					}else{
						TestReporter.logInfo("Access DB > "+column+" with value> "+value+" is not matching with Dare Column> "+column+" with value> "+dareData.get(column).trim(), Status.FAIL);
					}
				}else {
					TestReporter.logInfo("Access DB > "+column+" is not there in Dare", Status.FAIL);
				}
			}
		});
		
	}
	
	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"}, enabled=false)
	@CDataProvider(dataFile="TestData/tblToolSSNotes7_18_19.xlsx",sheetName="tblToolSSNotes",key="dare-notes")
	public void verifyTblToolssNotes(Map<String,String> data) throws IOException, InterruptedException{
		TestReporter.setTestScenarioName(TestReporter.getTestScenarioName()+"_"+data.get("State"));
		String query = "select * from dare01.dbo_tbltoolssnotes where ID='"+data.get("ID")+"'";
		Map<String, String> dareData =DBUtils.getDataFromDB(query,"oldstage").get(0);//ExcelUtil.getData("TestData/tblToolSSNotes.xlsx", "Sheet2",data.get("State").trim() ).get(0);//(Map<String, String>) DBUtils.getDareData(query);
		data.forEach((column,value) ->{
			if(!column.equalsIgnoreCase("Key")){
				if(dareData.get(column)!=null) {
					if(dareData.get(column)!=null?StringUtility.trimAllWhitespace(dareData.get(column)).trim().contentEquals(StringUtility.trimAllWhitespace(value).trim()):"".equalsIgnoreCase(value)){
						TestReporter.logInfo("Access DB > "+column+" with value> "+value+" is matching with Dare Column> "+column+" with value> "+dareData.get(column).trim(), Status.PASS);
					}else{
						TestReporter.logInfo("Access DB > "+column+" with value> "+value+" is matching with Dare Column> "+column+" with value> "+dareData.get(column).trim(), Status.FAIL);
					}
				}else {
					TestReporter.logInfo("Access DB > "+column+" is not there in Dare", Status.FAIL);
				}
			}
		});
		
	}
	
	
}
