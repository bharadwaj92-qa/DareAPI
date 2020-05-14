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

public class DAREDBCompare extends BaseTest{
	//static List<Map<String, String>> dareData = null;
	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"}, enabled=false)
	@CDataProvider(sqlQuery="select * from dbo_tblphrases",dbEnv="oldstage")
	public void verifyPhrases(Map<String,String> data) throws IOException, InterruptedException{
		String query = "select * from dbo_tblphrases where ID='"+data.get("ID")+"'";
		Map<String, String> dareData =DBUtils.getDataFromDB(query,"oldprod").get(0); //ExcelUtil.getData("TestData/tblPhrases.xlsx", "Sheet2",data.get("ID").trim() ).get(0);//(Map<String, String>) DBUtils.getDareData(query);
		System.out.println(data);
		System.out.println(dareData);
		String regex = "\\d+";
		dareData.forEach((colName,value) -> {
			if(data.get(colName)!=null?data.get(colName).trim().equals(value.trim()):"".equals(value)){
				TestReporter.logInfo("Old Dare> "+colName+" with value> "+data.get(colName)+" is matching with new Dare Column> "+colName+" with value> "+value.trim(), Status.PASS);
			}else{
				TestReporter.logInfo("Old Dare> "+colName+" with value> "+data.get(colName)+" is not matching with new Dare Column> "+colName+" with value> "+value.trim(), Status.FAIL);
			}
		});
	}
	
	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"}, enabled=true)
	@CDataProvider(sqlQuery="select * from tbltoollicensureirs",dbEnv="oldstage")
	public void verifyTblToolLicensure(Map<String,String> data) throws IOException, InterruptedException{
		TestReporter.setTestScenarioName(TestReporter.getTestScenarioName()+"_"+data.get("State"));
		//String columns[]={"ID","State","Postal","StateAge","POM","MaidContract","MaidDel","SSGMaid","SSG","StateIRS","I","R","S","TATI","TATR","TATS","POM1","POM2","POM2noMD","MCSphCode","MCSopCode","MCStmsCode","tblStateSpecificSSGMaidMin","tblStateSpecificSSGMaid","tblStateSpecificSSG","sMDABD","sMDABDHMO","sMDABDChild","sMDAppeal1","sMDAppeal1HMO","sMDAppeal1Child","sNoMDABD","sNoMDABDHMO","sNoMDABDChild","sNoMDAppeal1","sNoMDAppeal1HMO","sNoMDAppeal1Child","sMDEA","sMDEAHMO","sMDEAChild","sNoMDEA","sNoMDEAHMO","sNoMDEAChild","sMDRetro","sMDRetroHMO","sMDRetroChild","sNoMDRetro","sNoMDRetroHMO","sNoMDRetroChild","sMDRecon","sMDReconHMO","sMDReconChild","sNoMDRecon","sNoMDReconHMO","sNoMDReconChild","sMDABDHMOChild","sNoMDABDHMOChild","sMDRetroHMOChild","sNoMDRetroHMOChild","sMDReconHMOChild","sNoMDReconHMOChild","sMDAppeal1HMOChild","sNoMDAppeal1HMOChild","sMDEAHMOChild","sNoMDEAHMOChild","MedicaidMDABD","MedicaidMDAppeal1","MedicaidMDEA","MedicaidMDRetro","MedicaidMDRecon","MedicaidNoMDABD","MedicaidNoMDAppeal1","MedicaidNoMDEA","MedicaidNoMDRetro","MedicaidNoMDRecon","sMDAppeal2","sMDAppeal2HMO","sMDAppeal2Child","sNoMDAppeal2","sNoMDAppeal2HMO","sNoMDAppeal2Child","MedicaidMDAppeal2","MedicaidNoMDAppeal2","sMDAppeal2HMOChild","sNoMDAppeal2HMOChild","MedicaidMDABDChild","MedicaidMDAppeal1Child","MedicaidMDEAChild","MedicaidMDRetroCHild","MedicaidMDReconChild","MedicaidNoMDABDChild","MedicaidNoMDAppeal1Child","MedicaidNoMDEAChild","MedicaidNoMDRetroChild","MedicaidNoMDReconChild","MedicaidMDAppeal2Child","MedicaidNoMDAppeal2Child","HMO"};
		String query = "select * from tbltoollicensureirs where ID='"+data.get("ID")+"'";
		
			Map<String, String> dareData = DBUtils.getDataFromDB(query,"oldprod").get(0);//ExcelUtil.getData("TestData/tblToolLicensure.xlsx", "Sheet2",data.get("State").trim() ).get(0);//(Map<String, String>) DBUtils.mySqlDBConnection(query);
		
		data.forEach((column,value) ->{
			if(!column.equalsIgnoreCase("Key")){
				if(dareData.get(column)!=null) {
					if(data.get(column).trim().equalsIgnoreCase(dareData.get(column).trim())){
						TestReporter.logInfo("Old Dare> "+column+" with value> "+value+" is matching with new Dare Column> "+column+" with value> "+dareData.get(column).trim(), Status.PASS);
					}else{
						TestReporter.logInfo("Old Dare> "+column+" with value> "+value+" is matching with new Dare Column> "+column+" with value> "+dareData.get(column).trim(), Status.FAIL);
					}
				}else {
					TestReporter.logInfo("Old Dare> "+column+" is not there in new Dare", Status.FAIL);
				}
			}
		});
		
	}
	
	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"}, enabled=false)
	@CDataProvider(sqlQuery="select * from dbo_tbltoolssnotes",dbEnv="oldstage")
	public void verifyTblToolssNotes(Map<String,String> data) throws IOException, InterruptedException{
		TestReporter.setTestScenarioName(TestReporter.getTestScenarioName()+"_"+data.get("State"));
		String query = "select * from dare01.dbo_tbltoolssnotes where ID='"+data.get("ID")+"'";
		Map<String, String> dareData =DBUtils.getDataFromDB(query,"oldprod").get(0);//ExcelUtil.getData("TestData/tblToolSSNotes.xlsx", "Sheet2",data.get("State").trim() ).get(0);//(Map<String, String>) DBUtils.getDareData(query);
		System.out.println(data);
		System.out.println(dareData);
		dareData.forEach((colName,value) -> {
			if(data.get(colName)!=null?StringUtility.trimAllWhitespace(data.get(colName)).trim().contentEquals(StringUtility.trimAllWhitespace(value).trim()):"".equalsIgnoreCase(value)){
				TestReporter.logInfo("Old Dare> "+colName+" with value> "+data.get(colName)+" is matching with new Dare Column> "+colName+" with value> "+value.trim(), Status.PASS);
			}else{
				TestReporter.logInfo("Old Dare> "+colName+" with value> "+data.get(colName)+" is not matching with new Dare Column> "+colName+" with value> "+value.trim(), Status.FAIL);
			}
		});
	}
	
	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"}, enabled=false)
	@CDataProvider(sqlQuery="select * from temp_review_reference",dbEnv="oldstage")
	public void verifyReviewReference(Map<String,String> data) throws IOException, InterruptedException{
		TestReporter.setTestScenarioName(TestReporter.getTestScenarioName()+"_"+data.get("review_ref_name"));
		String query = "select * from temp_review_reference where review_ref_sys_id='"+data.get("review_ref_sys_id")+"'";
		Map<String, String> dareData =DBUtils.getDataFromDB(query,"oldprod").get(0);//ExcelUtil.getData("TestData/tblToolSSNotes.xlsx", "Sheet2",data.get("State").trim() ).get(0);//(Map<String, String>) DBUtils.getDareData(query);
		System.out.println(data);
		System.out.println(dareData);
		dareData.forEach((colName,value) -> {
			if(data.get(colName)!=null?StringUtility.trimAllWhitespace(data.get(colName)).trim().contentEquals(StringUtility.trimAllWhitespace(value).trim()):"".equalsIgnoreCase(value)){
				TestReporter.logInfo("Old Dare> "+colName+" with value> "+data.get(colName)+" is matching with new Dare Column> "+colName+" with value> "+value.trim(), Status.PASS);
			}else{
				TestReporter.logInfo("Old Dare> "+colName+" with value> "+data.get(colName)+" is not matching with new Dare Column> "+colName+" with value> "+value.trim(), Status.FAIL);
			}
		});
	}
	
	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"}, enabled=false)
	@CDataProvider(sqlQuery="select * from temp_state_reference",dbEnv="oldstage")
	public void verifyStateReference(Map<String,String> data) throws IOException, InterruptedException{
		TestReporter.setTestScenarioName(TestReporter.getTestScenarioName()+"_"+data.get("state_ref_name"));
		String query = "select * from temp_state_reference where state_ref_sys_id='"+data.get("state_ref_sys_id")+"'";
		Map<String, String> dareData =DBUtils.getDataFromDB(query,"oldprod").get(0);//ExcelUtil.getData("TestData/tblToolSSNotes.xlsx", "Sheet2",data.get("State").trim() ).get(0);//(Map<String, String>) DBUtils.getDareData(query);
		System.out.println(data);
		System.out.println(dareData);
		dareData.forEach((colName,value) -> {
			if(data.get(colName)!=null?StringUtility.trimAllWhitespace(data.get(colName)).trim().contentEquals(StringUtility.trimAllWhitespace(value).trim()):"".equalsIgnoreCase(value)){
				TestReporter.logInfo("Old Dare> "+colName+" with value> "+data.get(colName)+" is matching with new Dare Column> "+colName+" with value> "+value.trim(), Status.PASS);
			}else{
				TestReporter.logInfo("Old Dare> "+colName+" with value> "+data.get(colName)+" is not matching with new Dare Column> "+colName+" with value> "+value.trim(), Status.FAIL);
			}
		});
	}
	
}
