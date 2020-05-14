package ogs.selenium.tests;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.hamcrest.core.StringContains;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.google.common.base.Strings;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ogs.selenium.pages.HomePage;
import ogs.selenium.pages.Output;
import ogs.selenium.reporting.TestReporter;
import ogs.selenium.web.BaseTest;
import ogs.testng.dataprovider.CDataProvider;
import ogs.util.StringUtility;

public class DAREAPITest extends BaseTest{
	public int count =0;
	static List<Map<String, Object>> states = null;
	static List<Map<String, Object>> reviews = null;
	
	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"}, enabled=false)
	@CDataProvider(dataFile="TestData/SOI_Naga_Expected.xlsx",sheetName="SOI_4_Naga",key="dare-output")
	public void verifyStates(Map<String,String> data) throws IOException, InterruptedException{
		/*RestAssured.trustStore("C:/Users/vnagarj1/Documents/MyWorkSpace/cert.jks", "changeit");
		RestAssured.keyStore("C:/Users/vnagarj1/Documents/MyWorkSpace/cert.jks", "changeit");*/
		RestAssured.baseURI = "https://dare-ui-dare-prod.origin-ctc-core.optum.com";
		Response resp = RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON).get("/dare/api/v1/api/v1/states");
		/*List<String> states = resp.getBody().jsonPath().getList("state");
		if(states.size()>0){
			Arrays.stream(data.get("States").split(",")).collect(Collectors.toList()).forEach(state ->{
				if(states.contains(state.trim())){
					TestReporter.logInfo(state+ " state is fetched from API ", Status.PASS);
				}else{
					TestReporter.logInfo(state+ " state is not fetched from API ", Status.FAIL);
				}
			});
		}else{
			TestReporter.logInfo("No state is not fetched from API ", Status.FAIL);
		}*/
	}


	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"},  enabled=false)
	@CDataProvider(dataFile="TestData/MN_Linga.xlsx",sheetName="Sheet",key="dare-output")
	public void verifyDareOutput(Map<String,String> data) throws IOException, InterruptedException{
		count = count+1;
		 
		try{
			
			System.out.println("Execution started: "+count);
			RestAssured.baseURI = "https://dare-api-ohbsqa.ocp-ctc-core-nonprod.optum.com";//System.getenv("base-uri");
			System.out.println(RestAssured.baseURI);
			Map<String,String> stateIds = new HashMap<String,String>();
			if(states==null){
			states = RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON).get("/fetch-state").getBody().jsonPath().getList("$.");;
			}
			states.stream().filter(map ->map.get("state").equals(data.get("StateOfResidence").trim())).forEach(map-> stateIds.put("StateOfResidence",String.valueOf(map.get("id"))));
			states.stream().filter(map ->((String) map.get("state")).trim().equals(data.get("StateOfIssue").trim())).forEach(map-> stateIds.put("StateOfIssue",String.valueOf(map.get("id"))));
			states.stream().filter(map ->map.get("state").equals(data.get("StateOfService").trim())).forEach(map-> stateIds.put("StateOfService",String.valueOf(map.get("id"))));

			TestReporter.logInfo("Execution of test started Time :::"+LocalTime.now(),Status.INFO);
			//RestAssured.baseURI = "https://dare-api-ohbsdev.ocp-ctc-core-nonprod.optum.com";
			
			String jsonBody = "{"
                    + "\"isFullyInsured\":\""+data.get("IsFullyInsured")
                    +"\",\"isMedicare\":\""+data.get("IsMedicare")
                    +"\",\"isMedicaid\":\""+data.get("IsMedicaid")
                    +"\",\"isHMO\":\""+data.get("IsHMO")
                    +"\",\"isAttendingProvider\":\""+data.get("IsAttendingProvider")
                    +"\",\"stateOfResidence\":\""+stateIds.get("StateOfResidence")
                    +"\",\"stateOfIssue\":\""+stateIds.get("StateOfIssue")
                    +"\",\"stateOfService\":\""+stateIds.get("StateOfService")
                    +"\",\"age\":\""+data.get("Age")
                    +"\",\"resultTypeDrpDwn\":\""+data.get("ResultTypeDrpDwn")
                    +"\" }";

			Response resp = RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON).body(jsonBody).when().post("/outputText");
			System.out.println("Response code from API ==> "+resp.getStatusCode());
			if(resp.getStatusCode()==200){
				Status tcStatus = Status.PASS;
				Status msgStatus = Status.PASS;
				String expMessage = StringUtility.trimAllWhitespace(data.get("ExpectedMessage")!=null?data.get("ExpectedMessage").replaceAll("['.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("!", ""):"");
				String actualMessage = StringUtility.trimAllWhitespace(resp.getBody().jsonPath().getString("messages").replaceAll("['.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("!", ""));
				if(actualMessage.equalsIgnoreCase(expMessage)){
					TestReporter.logInfo(resp.getBody().jsonPath().getString("messages")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedMessage"), Status.PASS);
				}else{
					tcStatus = Status.FAIL;
					msgStatus = Status.FAIL;
					TestReporter.logInfo(resp.getBody().jsonPath().getString("messages")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedMessage"), Status.FAIL);
				}
				String expSpecialMsg = StringUtility.trimAllWhitespace(data.get("ExpectedSM")!=null?data.get("ExpectedSM").replaceAll("['.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("!", ""):"");
				String actualSpecialMsg = StringUtility.trimAllWhitespace(resp.getBody().jsonPath().getString("specialNotesMessage").replaceAll("['.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("!", ""));
				Status smStatus = Status.PASS;
				if(actualSpecialMsg.equalsIgnoreCase(expSpecialMsg)){
					TestReporter.logInfo(resp.getBody().jsonPath().getString("specialNotesMessage")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedSM"), Status.PASS);
				}else{
					tcStatus = Status.FAIL;
					smStatus = Status.FAIL;
					TestReporter.logInfo(resp.getBody().jsonPath().getString("specialNotesMessage")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedSM"), Status.FAIL);
				}

				String expSOG = StringUtility.trimAllWhitespace(data.get("ExpectedSOG")!=null?data.get("ExpectedSOG").replaceAll("['.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("=", ""):"");
				String actualSOG = StringUtility.trimAllWhitespace(resp.getBody().jsonPath().getString("stateOfGovernance").replaceAll("[.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("=",""));
				Status sogStatus = Status.PASS;
				if(actualSOG.equalsIgnoreCase(expSOG)){
					TestReporter.logInfo(resp.getBody().jsonPath().getString("stateOfGovernance")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedSOG"), Status.PASS);
				}else{
					tcStatus = Status.FAIL;
					sogStatus = Status.FAIL;
					TestReporter.logInfo(resp.getBody().jsonPath().getString("stateOfGovernance")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedSOG"), Status.FAIL);
				}

				Output op = new Output(data.get("IsFullyInsured"), data.get("IsMedicare"), data.get("IsMedicaid"), data.get("IsHMO"), data.get("IsAttendingProvider"), data.get("StateOfResidence"), data.get("StateOfIssue"), data.get("StateOfService"), data.get("Age"), data.get("ResultTypeDrpDwn"),data.get("ExpectedMessage"),resp.getBody().jsonPath().getString("messages"),data.get("ExpectedSM"),resp.getBody().jsonPath().getString("specialNotesMessage"),data.get("ExpectedSOG"),resp.getBody().jsonPath().getString("stateOfGovernance"),LocalDateTime.now().toString(),tcStatus,msgStatus,smStatus,sogStatus);
				Output.outputData.add(op);
				
			}else{
				TestReporter.logInfo("Not able to access API for the combination "+jsonBody+" and Status Line "+resp.getStatusLine(),Status.FAIL);
				Output op = new Output(data.get("IsFullyInsured"), data.get("IsMedicare"), data.get("IsMedicaid"), data.get("IsHMO"), data.get("IsAttendingProvider"), data.get("StateOfResidence"), data.get("StateOfIssue"), data.get("StateOfService"), data.get("Age"), data.get("ResultTypeDrpDwn"), data.get("ExpectedMessage")," StatusCode::>> "+resp.getStatusLine(),data.get("ExpectedSM"),"",data.get("ExpectedSOG"),"",LocalDateTime.now().toString(),Status.FAIL,Status.FAIL,Status.PASS,Status.PASS);
				Output.outputData.add(op);
			}
			TestReporter.logInfo("Execution of test ended Time :::"+LocalTime.now(),Status.INFO);
		}catch(Exception e){
			e.printStackTrace();
			TestReporter.logInfo("Exception has occured "+e.getMessage(),Status.FAIL);
			Output op = new Output(data.get("IsFullyInsured"), data.get("IsMedicare"), data.get("IsMedicaid"), data.get("IsHMO"), data.get("IsAttendingProvider"), data.get("StateOfResidence"), data.get("StateOfIssue"), data.get("StateOfService"), data.get("Age"), data.get("ResultTypeDrpDwn"), data.get("ExpectedMessage"),"Exception has occured "+e.getLocalizedMessage(),data.get("ExpectedSM"),"",data.get("ExpectedSOG"),"",LocalDateTime.now().toString(),Status.FAIL,Status.FAIL,Status.PASS,Status.PASS);
			Output.outputData.add(op);
		}
	}
	

	@Test(description="This is an example test case to design your testcases using framework",groups={"regression"},  enabled=true)
	@CDataProvider(dataFile="TestData/RegressionResultsUAT.xlsx",sheetName="2StateSame",key="dare-output")
	public void verifyDareOutputNewDARE(Map<String,String> data) throws IOException, InterruptedException{
		count = count+1;
		 
		try{
			
			System.out.println("Execution started: "+count);

			RestAssured.baseURI = System.getenv("base-uri");
			System.out.println(RestAssured.baseURI);
			Map<Object,Object> sor = new HashMap<>();
			Map<Object,Object> soi = new HashMap<>();
			Map<Object,Object> sos = new HashMap<>();
			Map<Object,Object> review = new HashMap<>();
			
			if(states==null){
				states = RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON).get("/api/v1/states").getBody().jsonPath().getList("$.");;
			}
			
			if(reviews==null){
				reviews = RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON).get("/api/v1/reviews").getBody().jsonPath().getList("$.");;
			}
			
			reviews.stream().filter(map ->map.get("reviewRefName").equals(data.get("ResultTypeDrpDwn").trim())).forEach(map -> {
				review.putAll(map.entrySet().stream()
			        .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
			});
			states.stream().filter(map ->map.get("stateRefName").equals(data.get("StateOfResidence").trim())).forEach(map -> {
				sor.putAll(map.entrySet().stream()
			        .collect(Collectors.toMap(entry -> entry==null?"":entry.getKey(), entry ->  entry.getValue()==null?"":entry.getValue())));
			});
			states.stream().filter(map ->map.get("stateRefName").equals(data.get("StateOfIssue").trim())).forEach(map -> {
				soi.putAll(map.entrySet().stream()
			        .collect(Collectors.toMap(entry -> entry==null?"":entry.getKey(), entry ->  entry.getValue()==null?"":entry.getValue())));
			});
			states.stream().filter(map ->map.get("stateRefName").equals(data.get("StateOfService").trim())).forEach(map -> {
				sos.putAll(map.entrySet().stream()
			        .collect(Collectors.toMap(entry -> entry==null?"":entry.getKey(), entry ->  entry.getValue()==null?"":entry.getValue())));
			});

			TestReporter.logInfo("Execution of test started Time :::"+LocalTime.now(),Status.INFO);
			
			HashMap<String, Object> jsonBody = new HashMap<>();
			jsonBody.put("isAllStatesSame", null);
			jsonBody.put("stateOfResidence", sor);
			jsonBody.put("stateOfIssue", soi);
			jsonBody.put("stateOfService", sos);
			jsonBody.put("memberAge", Integer.parseInt(data.get("Age")));
			jsonBody.put("isFullyInsuredOrASO", data.get("IsFullyInsured")!=null?data.get("IsFullyInsured").equalsIgnoreCase("Y")?true:false:null);
			jsonBody.put("isMedicare", data.get("IsMedicare")!=null?data.get("IsMedicare").equalsIgnoreCase("Y")?true:false:null);
			jsonBody.put("isMedicaid", data.get("IsMedicaid")!=null?data.get("IsMedicaid").equalsIgnoreCase("Y")?true:false:null);
			jsonBody.put("isHMO", data.get("IsHMO")!=null?data.get("IsHMO").equalsIgnoreCase("Y")?true:false:null);
			jsonBody.put("isAttendingProviderPhysician", data.get("IsAttendingProvider")!=null?data.get("IsAttendingProvider").equalsIgnoreCase("Y")?true:false:null);
			jsonBody.put("review", review);
			
			Response resp = RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON).body(jsonBody).when().post("/api/v1/forms");
			System.out.println("Response code from API ==> "+resp.getStatusCode());
			if(resp.getStatusCode()==200){
				Status tcStatus = Status.PASS;
				Status msgStatus = Status.PASS;
				String expMessage = StringUtility.trimAllWhitespace(data.get("ExpectedMessage")!=null?data.get("ExpectedMessage").replaceAll("['.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("!", ""):"");
				String actualMessage = StringUtility.trimAllWhitespace(resp.getBody().jsonPath().getString("messages").replaceAll("['.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("!", ""));
				if(actualMessage.equalsIgnoreCase(expMessage)){
					TestReporter.logInfo(resp.getBody().jsonPath().getString("messages")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedMessage"), Status.PASS);
				}else{
					tcStatus = Status.FAIL;
					msgStatus = Status.FAIL;
					TestReporter.logInfo(resp.getBody().jsonPath().getString("messages")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedMessage"), Status.FAIL);
				}
				String expSpecialMsg = StringUtility.trimAllWhitespace(data.get("ExpectedSM")!=null?data.get("ExpectedSM").replaceAll("['.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("!", ""):"");
				String actualSpecialMsg = StringUtility.trimAllWhitespace(resp.getBody().jsonPath().getString("specialNotesMessage").replaceAll("['.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("!", ""));
				Status smStatus = Status.PASS;
				if(actualSpecialMsg.equalsIgnoreCase(expSpecialMsg)){
					TestReporter.logInfo(resp.getBody().jsonPath().getString("specialNotesMessage")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedSM"), Status.PASS);
				}else{
					tcStatus = Status.FAIL;
					smStatus = Status.FAIL;
					TestReporter.logInfo(resp.getBody().jsonPath().getString("specialNotesMessage")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedSM"), Status.FAIL);
				}

				String expSOG = StringUtility.trimAllWhitespace(data.get("ExpectedSOG")!=null?data.get("ExpectedSOG").replaceAll("['.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("=", ""):"");
				String actualSOG = StringUtility.trimAllWhitespace(resp.getBody().jsonPath().getString("stateOfGovernance").replaceAll("[.,;-]", "").replaceAll("\\[|\\]", "").replaceAll("=",""));
				Status sogStatus = Status.PASS;
				if(actualSOG.equalsIgnoreCase(expSOG)){
					TestReporter.logInfo(resp.getBody().jsonPath().getString("stateOfGovernance")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedSOG"), Status.PASS);
				}else{
					tcStatus = Status.FAIL;
					sogStatus = Status.FAIL;
					TestReporter.logInfo(resp.getBody().jsonPath().getString("stateOfGovernance")+ " is fetched for the combination "+jsonBody+"  Expected result: "+data.get("ExpectedSOG"), Status.FAIL);
				}

				Output op = new Output(data.get("IsFullyInsured"), data.get("IsMedicare"), data.get("IsMedicaid"), data.get("IsHMO"), data.get("IsAttendingProvider"), data.get("StateOfResidence"), data.get("StateOfIssue"), data.get("StateOfService"), data.get("Age"), data.get("ResultTypeDrpDwn"),data.get("ExpectedMessage"),resp.getBody().jsonPath().getString("messages"),data.get("ExpectedSM"),resp.getBody().jsonPath().getString("specialNotesMessage"),data.get("ExpectedSOG"),resp.getBody().jsonPath().getString("stateOfGovernance"),LocalDateTime.now().toString(),tcStatus,msgStatus,smStatus,sogStatus);
				Output.outputData.add(op);
				
			}else{
				TestReporter.logInfo("Not able to access API for the combination "+jsonBody+" and Status Line "+resp.getStatusLine(),Status.FAIL);
				Output op = new Output(data.get("IsFullyInsured"), data.get("IsMedicare"), data.get("IsMedicaid"), data.get("IsHMO"), data.get("IsAttendingProvider"), data.get("StateOfResidence"), data.get("StateOfIssue"), data.get("StateOfService"), data.get("Age"), data.get("ResultTypeDrpDwn"), data.get("ExpectedMessage")," StatusCode::>> "+resp.getStatusLine(),data.get("ExpectedSM"),"",data.get("ExpectedSOG"),"",LocalDateTime.now().toString(),Status.FAIL,Status.FAIL,Status.PASS,Status.PASS);
				Output.outputData.add(op);
			}
			TestReporter.logInfo("Execution of test ended Time :::"+LocalTime.now(),Status.INFO);
		}catch(Exception e){
			System.out.println(e.getMessage());
			TestReporter.logInfo("Exception has occured "+e.getMessage(),Status.FAIL);
			Output op = new Output(data.get("IsFullyInsured"), data.get("IsMedicare"), data.get("IsMedicaid"), data.get("IsHMO"), data.get("IsAttendingProvider"), data.get("StateOfResidence"), data.get("StateOfIssue"), data.get("StateOfService"), data.get("Age"), data.get("ResultTypeDrpDwn"), data.get("ExpectedMessage"),"Exception has occured "+e.getLocalizedMessage(),data.get("ExpectedSM"),"",data.get("ExpectedSOG"),"",LocalDateTime.now().toString(),Status.FAIL,Status.FAIL,Status.PASS,Status.PASS);
			Output.outputData.add(op);
		}
	}
	
	@Test(description="This is to verify the admin login api",groups={"regression"}, enabled=false)
	//@CDataProvider(dataFile="DARE.xls",sheetName="SameState",key="TestCase1")
	public void verifyAdminLoginAPI() throws IOException, InterruptedException{

		RestAssured.baseURI = System.getenv("base-uri");
		Map<String,String> request = new LinkedHashMap<String,String>();
		request.put("username", "Dareadmin1");
		request.put("password", "Admin@2019");
		Response resp = RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON).body(request).when().post("/admin/login");
		if(resp.getStatusCode()==200){
			if(resp.getBody().jsonPath().getBoolean("success")){
				TestReporter.logInfo("User is able to login into application and able to see admin page",Status.PASS);
			}else{
				TestReporter.logInfo("User does not have the access to login into application and to see admin page",Status.FAIL);
			}
		}else{
			TestReporter.logInfo("Unable to access the api "+resp.getStatusLine(),Status.FAIL);
		}
	}
	
	
}
