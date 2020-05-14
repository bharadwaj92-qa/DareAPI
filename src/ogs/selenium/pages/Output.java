package ogs.selenium.pages;

import java.util.ArrayList;
import java.util.List;

import com.aventstack.extentreports.Status;

public class Output {
	
	private String isFullyInsured;
	private String isMedicare;
	private String isMedicaid;
	private String isHMO;
	private String isAttendingProvider;
	private String stateOfResidence;
	private String stateOfIssue;
	private String stateOfService;
	private String age;
	private String resultTypeDrpDwn;
	private String actualMessage;
	private String expectedMessage;
	private String actualSM;
	private String expectedSM;
	private String actualSOG;
	private String expectedSOG;
	private String executionTime;
	private Status tcStatus;
	private Status msgStatus;
	private Status smStatus;
	private Status sogStatus;
		
	public static List<Output> outputData= new ArrayList<Output>();
	public Output(String isFullyInsured, String isMedicare, String isMedicaid, String isHMO, String isAttendingProvider,
			String stateOfResidence, String stateOfIssue, String stateOfService, String age, String resultTypeDrpDwn,
			String expectedMessage,String actualMessage,String expectedSM,String actualSM,String expectedSOG,String actualSOG,String executionTime,Status tcStatus,Status msgStatus,Status smStatus,Status sogStatus) {
		super();
		this.isFullyInsured = isFullyInsured;
		this.isMedicare = isMedicare;
		this.isMedicaid = isMedicaid;
		this.isHMO = isHMO;
		this.isAttendingProvider = isAttendingProvider;
		this.stateOfResidence = stateOfResidence;
		this.stateOfIssue = stateOfIssue;
		this.stateOfService = stateOfService;
		this.age = age;
		this.resultTypeDrpDwn = resultTypeDrpDwn;
		this.expectedMessage = expectedMessage;
		this.actualMessage = actualMessage;
		this.expectedSM = expectedSM;
		this.actualSM = actualSM;
		this.actualSOG = actualSOG;
		this.expectedSOG = expectedSOG;
		this.executionTime = executionTime;
		this.tcStatus = tcStatus;
		this.msgStatus = msgStatus;
		this.smStatus = smStatus;
		this.sogStatus = sogStatus;
		
	}
	public String getIsFullyInsured() {
		return isFullyInsured;
	}
	public void setIsFullyInsured(String isFullyInsured) {
		this.isFullyInsured = isFullyInsured;
	}
	public String getIsMedicare() {
		return isMedicare;
	}
	public void setIsMedicare(String isMedicare) {
		this.isMedicare = isMedicare;
	}
	public String getIsMedicaid() {
		return isMedicaid;
	}
	public void setIsMedicaid(String isMedicaid) {
		this.isMedicaid = isMedicaid;
	}
	public String getIsHMO() {
		return isHMO;
	}
	public void setIsHMO(String isHMO) {
		this.isHMO = isHMO;
	}
	public String getIsAttendingProvider() {
		return isAttendingProvider;
	}
	public void setIsAttendingProvider(String isAttendingProvider) {
		this.isAttendingProvider = isAttendingProvider;
	}
	public String getStateOfResidence() {
		return stateOfResidence;
	}
	public void setStateOfResidence(String stateOfResidence) {
		this.stateOfResidence = stateOfResidence;
	}
	public String getStateOfIssue() {
		return stateOfIssue;
	}
	public void setStateOfIssue(String stateOfIssue) {
		this.stateOfIssue = stateOfIssue;
	}
	public String getStateOfService() {
		return stateOfService;
	}
	public void setStateOfService(String stateOfService) {
		this.stateOfService = stateOfService;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getResultTypeDrpDwn() {
		return resultTypeDrpDwn;
	}
	public void setResultTypeDrpDwn(String resultTypeDrpDwn) {
		this.resultTypeDrpDwn = resultTypeDrpDwn;
	}
	
	public String getActualMessage() {
		return actualMessage;
	}
	public void setActualMessage(String actualMessage) {
		this.actualMessage = actualMessage;
	}
	public String getExpectedMessage() {
		return expectedMessage;
	}
	public void setExpectedMessage(String expectedMessage) {
		this.expectedMessage = expectedMessage;
	}
	public String getActualSM() {
		return actualSM;
	}
	public void setActualSM(String actualSM) {
		this.actualSM = actualSM;
	}
	public String getExpectedSM() {
		return expectedSM;
	}
	public void setExpectedSM(String expectedSM) {
		this.expectedSM = expectedSM;
	}
	public String getActualSOG() {
		return actualSOG;
	}
	public void setActualSOG(String actualSOG) {
		this.actualSOG = actualSOG;
	}
	public String getExpectedSOG() {
		return expectedSOG;
	}
	public void setExpectedSOG(String expectedSOG) {
		this.expectedSOG = expectedSOG;
	}
	
	public String getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}
	public Status getTcStatus() {
		return tcStatus;
	}
	public void setTcStatus(Status tcStatus) {
		this.tcStatus = tcStatus;
	}
	
	public Status getMsgStatus() {
		return msgStatus;
	}
	public void setMsgStatus(Status msgStatus) {
		this.msgStatus = msgStatus;
	}
	public Status getSmStatus() {
		return smStatus;
	}
	public void setSmStatus(Status smStatus) {
		this.smStatus = smStatus;
	}
	public Status getSogStatus() {
		return sogStatus;
	}
	public void setSogStatus(Status sogStatus) {
		this.sogStatus = sogStatus;
	}

}
