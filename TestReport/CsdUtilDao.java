/**
 * Title:        Clinical Systems Development<p>
 * Description:  <p>
 * Copyright:    Copyright (c) UBH Developers<p>
 * Company:      UBH<p>
 * @author UBH Developers
 * @version 1.0
 */
package com.uhc.ubh.csd.bobj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.uhc.ubh.common.util.DBPool;
import com.uhc.ubh.csd.bean.CsdMemberCaseMBean;
import com.uhc.ubh.csd.bean.CsdUserMBean;
import com.uhc.ubh.csd.bean.LinxTraceMBean;
import com.uhc.ubh.csd.bean.ServerWatcherBean;
import com.uhc.ubh.csd.bh.bobj.IBHCaseDao;
import com.uhc.ubh.csd.chart.bh.bean.BhCaseContactMBean;
import com.uhc.ubh.csd.chart.bh.bean.BhCaseContactNoteMBean;
import com.uhc.ubh.csd.chart.bh.bean.ChartReferralMBean;
import com.uhc.ubh.csd.chart.bh.bean.DmpHolidayMBean;
import com.uhc.ubh.csd.chart.bh.bean.FBCCaseContactNotesMBean;
import com.uhc.ubh.csd.chart.bh.bean.SmsRestrictedWordsMBean;
import com.uhc.ubh.csd.chart.bh.mo.DmpHolidayMO;
import com.uhc.ubh.csd.chart.bh.mo.SmsRestrictedWordsMO;
import com.uhc.ubh.csd.chart.bobj.IChartNoteDao;
import com.uhc.ubh.csd.chart.common.ChartConstant;
import com.uhc.ubh.csd.common.CsdConstant;
import com.uhc.ubh.csd.mo.LinxTraceMO;
import com.uhc.ubh.csd.reports.bean.EventsLogsMBean;
import com.uhc.ubh.csd.servlet.CsdHtmlCache;
import com.uhc.ubh.csd.servlet.base.TspFormGenBase;
import com.uhc.ubh.csd.util.DeleteCBASFormDataAPI;
import com.uhc.ubh.csd.util.EmailResponseObject;
import com.uhc.ubh.csd.util.SMSTextMessagingAPIWrapper;
import com.uhc.ubh.csd.util.SecureEmailAPIWrapper;
import com.uhc.ubh.csd.util.property.PropertyUtils;
import com.uhc.ubh.csd.workflow.mo.BhAdHocItemMO;
import com.uhc.ubh.dt.benefit.bobj.DtBenefitGroupPlanEligSBO;
import com.uhc.ubh.dt.benefit.mo.DtBenefitGroupPlanEligMO;
import com.uhc.ubh.dt.benefit.util.DtBenefitUtil;
import com.uhc.ubh.service.bean.MBeanList;
import com.uhc.ubh.service.bobj.PiFactory;
import com.uhc.ubh.service.mima.MQuery;
import com.uhc.ubh.service.mima.MRow;
import com.uhc.ubh.service.mima.MTable;
import com.uhc.ubh.service.mima.MTimestamp;
import com.uhc.ubh.service.mima.MTimestamp.DateTimeEnum;
import com.uhc.ubh.service.mima.format.PhoneFormat;
import com.uhc.ubh.service.objmgr.ServerInfo;
import com.uhc.ubh.service.util.DB;
import com.uhc.ubh.service.util.Log;

public class CsdUtilDao implements ICsdUtilDao {

  private static ServerWatcherBean serverWatcherBean = new ServerWatcherBean();
  private static Map<String, Integer> userCountByLoc = Collections.synchronizedMap(new HashMap<String, Integer>());
  private static Map<String, MTimestamp> userLastLogged = Collections.synchronizedMap(new HashMap<String, MTimestamp>());
  private static long lastLoggedUserStats = 0;
  private static String unknownLocationCode = "unk";

  public CsdUtilDao() {
  }

  public void saveServerWatcherBean(ServerWatcherBean swb) {
    long preSaveId = serverWatcherBean.getId();
    serverWatcherBean.setListOfServerProperties(swb.getListOfServerProperties());
    serverWatcherBean.setListOfEntCodes(swb.getListOfEntCodes());
    serverWatcherBean.setRefreshAllClientCaches(swb.isRefreshAllClientCaches());
    serverWatcherBean.setUserMessage(swb.getUserMessage());
    serverWatcherBean.setClientSidePoisonPill(swb.isClientSidePoisonPill());
    serverWatcherBean.setClientSidePoisonPillMinutes(swb.getClientSidePoisonPillMinutes());
    if (preSaveId != serverWatcherBean.getId()) {
      serverWatcherBean.initializeServerData();
    }
    Log.write("Server watcher bean saved: " + swb);
  }

  public String getStaticHtmlView(int id) {
    boolean replacePropertyValues = false;
    return getStaticHtmlView(id, replacePropertyValues);
  }

  public String getStaticHtmlView(int id,
                                  boolean replacePropertyValues) {
    try {
      TspFormGenBase tspFormGenBase = new TspFormGenBase(CsdHtmlCache.getCsdHC().getParsedHtml(id, "static", 0), null);
      tspFormGenBase.run();
      String htmlRet = tspFormGenBase.getProcessedHtmlAsString();
      if (replacePropertyValues) {
        htmlRet = TspFormGenBase.replacePropertyValues(htmlRet);
      }
      return htmlRet;
    } catch (Exception ex) {
      throw com.uhc.ubh.service.exception.ExceptionUtils.toRuntimeException(ex);
    }
  }

  public ServerWatcherBean getServerWatcherBean(long lastReceivedBeanId) {
    populateUserLocationHashMap();
    logUserCountByLocation();
    if (serverWatcherBean.getId() == 0 || serverWatcherBean.getId() == lastReceivedBeanId) {
      return null; // Client already has the latest
    } else {
      return serverWatcherBean;
    }
  }

  /**
   * 
   */
  private void logUserCountByLocation() {
    long waitTimeMillis = Long.parseLong(PropertyUtils.getProperty("serverWatcher.pauseTimeMillis", CsdConstant.DEFAULT_SERVER_WATCHER_PAUSETIME_MILLIS));
    if (System.currentTimeMillis() - lastLoggedUserStats >= waitTimeMillis) {
      lastLoggedUserStats = System.currentTimeMillis();
      StringBuffer msg = new StringBuffer();
      int total = 0;
      for (String loc : userCountByLoc.keySet()) {
        int count = userCountByLoc.get(loc);
        msg.append(loc + ":" + count + ", ");
        total += count;
        userCountByLoc.put(loc, 0);
      }
      Log.write("UserCountByLocation: total: " + total + ", " + msg.toString());
    }
  }

  /**
   * 
   */
  private void populateUserLocationHashMap() {
    String userLocation = "";
    try {
      String userId = ServerInfo.getSI().getSecurityInfo().getID();
      CsdUserMBean user = PiFactory.getImplementation(ICsdUserDao.class).getUser(userId);
      userLocation = user == null ? unknownLocationCode : user.getLoc();
      userLastLogged.put(userId + "-" + userLocation, new MTimestamp(DateTimeEnum.TIMESTAMP));
    } catch (Exception ex) {
      userLocation = unknownLocationCode;
    }
    if (userCountByLoc.containsKey(userLocation)) {
      userCountByLoc.put(userLocation, userCountByLoc.get(userLocation) + 1);
    } else {
      userCountByLoc.put(userLocation, 1);
    }
  }

  
  public Map<String, MTimestamp> getUserLastLoggedList() {
    return userLastLogged;
  }
  
  @Override
  public String getLogEntriesForString(String logName,
                                       String searchString) {
    Process proc;
    try {
      StringBuffer command = new StringBuffer();
      command.append("perl ");
      String scriptPath;
      String filePath;
      String env = ServerInfo.getProperty("env", "m2");
      if (SystemUtils.IS_OS_UNIX) {
        // if (true) {
        scriptPath = "/approot/linx/" + env + "/objtier/bin/find_log_entries_script.pl";
        filePath = "/approot/linx/" + env + "/objtier/logs/";
      } else {
        final String userDir = System.getProperty("user.dir");
        String path = userDir.substring(0, userDir.indexOf("approot"));
        System.out.println(path);
        scriptPath = path + "scripts\\shell\\" + "find_log_entries_script.pl";
        filePath = path + "approot\\logs\\";
        System.out.println(scriptPath);
      }

      command.append(scriptPath);
      command.append(" ");
      command.append(filePath);
      command.append(logName);
      command.append(" \"\" ");
      command.append(searchString);
      Log.write("run command: " + command.toString());
      proc = Runtime.getRuntime().exec(command.toString());
      BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      StringBuffer sb = new StringBuffer();
      String line = null;
      while ((line = in.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
      }
      return sb.toString();
    } catch (IOException e) {
      throw com.uhc.ubh.service.exception.ExceptionUtils.toRuntimeException(e);
    }
  }  
  
  public void writeToServerLog(String logMsg) {
    Log.write("Log msg from client: " + logMsg);
  }

  public void saveTrace(LinxTraceMBean linxTraceMBean) {
    DB.getDB(DBPool.CSD).save(linxTraceMBean);
  }
  
  public LinxTraceMBean getLatestTrace(String traceType, String refId) {
    MQuery query = new MQuery(LinxTraceMO.I);
    query.addCriteria(LinxTraceMO.I.TRACE_TYPE, traceType);
    query.addCriteria(LinxTraceMO.I.REF_ID, refId);
    query.addOrderByColumn(LinxTraceMO.I.UPDATE_DATE, false);
    MBeanList<LinxTraceMBean> linxTraceMBeans = DB.getDB(DBPool.CSD).getMBeanList(LinxTraceMBean.class, query, 1);
    return linxTraceMBeans.size() > 0 ? linxTraceMBeans.get(0) : null;
  }
  
  
  public MBeanList<DmpHolidayMBean> getDmpHolidayMBeanList() {
    MQuery query = new MQuery(DmpHolidayMO.I);
    return DB.getDB().getMBeanList(DmpHolidayMBean.class, query);
  }
  
  public String sendTextMessage(String textContent, String phoneNumber) {
    SMSTextMessagingAPIWrapper wrapper = new SMSTextMessagingAPIWrapper();
    return wrapper.sendMessage(textContent, phoneNumber);
  }
  public int sendSecureEmail(Map<String,String> contentMap, String emailContent) {
    SecureEmailAPIWrapper api = new SecureEmailAPIWrapper(); 
    String userId = ServerInfo.getSI().getTicket().getID();
    CsdUserMBean user = PiFactory.getImplementation(ICsdUserDao.class).getUser(userId);
    contentMap.put("agentFirstName", user.getFirstName());
    contentMap.put("agentLastName", user.getLastName());
    contentMap.put("agentPhoneNumber", user.getWorkPhone());
    contentMap.put("agentUserId", user.getAppUserId());
    contentMap.put("agentRole", user.getPosition());
    contentMap.put("searchResults", emailContent);
    EmailResponseObject apiResponseObject= api.sendEmailLinxApplication(contentMap);
    return apiResponseObject.getHTTPStatusCode();
  }
  
  public int createCaseContactNoteForTextEmail (String textMsg , String phoneNumberOrEmail, int memberId, boolean isSecureEmail) {
    String userId = ServerInfo.getSI().getTicket().getID();
    CsdUserMBean csdUserMBean = PiFactory.getImplementation(ICsdUserDao.class).getUser(userId);
    String agentFullName = "";
    BhCaseContactNoteMBean bhCaseContactNoteMBean = new BhCaseContactNoteMBean();
    if(csdUserMBean!=null){
       agentFullName = csdUserMBean.getFirstName() +" " + csdUserMBean.getLastName().charAt(0);
    }
    String noteSummary = "";
    if(isSecureEmail) {
    noteSummary = "Provider referrals sent via Secure email on " + new MTimestamp().toString().substring(0, 11) + " ." +  "\n" + "\n"+
            "Verbal consent received to send search results to the email address "+ phoneNumberOrEmail +"."+"\n" + "\n"+
            "Linx search criteria: " + "\n" +textMsg ;
    } else {
      noteSummary = "Provider referrals sent via SMS Text on " + new MTimestamp().toString().substring(0, 11) + " ." +  "\n" + "\n"+
              "Verbal consent received to send search results to the phone number "+ phoneNumberOrEmail +"."+"\n" + "\n"+
              "Linx search criteria: " + "\n" +textMsg ;
    }

    String caseId = null;
    final ICsdCaseDao csdCaseDao = PiFactory.getImplementation(ICsdCaseDao.class);
    caseId = csdCaseDao.findCreatePopulateBhCaseForMember(memberId);

    BhCaseContactMBean bhCaseContactMBean = new BhCaseContactMBean(); // Contact tab
    bhCaseContactMBean.setContactName("");
    bhCaseContactMBean.setContactPhone("");
    bhCaseContactMBean.setContactMethodCode((short)0);
    bhCaseContactMBean.setRelationCode((short)5);
    bhCaseContactMBean.setContactCategoryCode((short)0);

    bhCaseContactNoteMBean.setBhCaseContactMBean(bhCaseContactMBean);  //Notes tab
    bhCaseContactNoteMBean.setContactSummary(noteSummary);

    bhCaseContactNoteMBean.setUserName(agentFullName);
    bhCaseContactNoteMBean.setFkId(caseId);
    bhCaseContactNoteMBean.setChartTypeId(ChartConstant.BH_CASE_CONTACT_V3);
    bhCaseContactNoteMBean.getBhCaseContactMBean().setContactMethodCode(ChartConstant.CONTACT_SERVICE_CONTACT_METHOD_TELEPHONE);
    bhCaseContactNoteMBean.getBhCaseContactMBean().setContactCategoryCode(ChartConstant.CONTACT_CATEGORY_CODE_NOT_APPLICABLE);

    ChartReferralMBean chartReferralMBean = new ChartReferralMBean();
    chartReferralMBean.setReferralGivenTypeCode("norefer");
    bhCaseContactNoteMBean.setChartReferralMBean(chartReferralMBean);

    bhCaseContactNoteMBean.setStatusCode(ChartConstant.CN_STATUS_OPEN);
    PiFactory.getImplementation(IChartNoteDao.class).saveChartMBean(bhCaseContactNoteMBean);
    return bhCaseContactNoteMBean.getChartId();
}
  
  public int createCaseContactNoteForFBCCallOut (Object[][] caseContactRows) {
    String userId = ServerInfo.getSI().getTicket().getID();
    String caseId="";
    String ur_name = "";
    String ur_phone="";
    String providerName="";
    int refId = 0;
    CsdUserMBean csdUserMBean = PiFactory.getImplementation(ICsdUserDao.class).getUser(userId);
    String agentFullName = "";
    
    if(csdUserMBean!=null){
       agentFullName = csdUserMBean.getFullName();
    }
    for (int i = 0; i<caseContactRows.length; i++){
      for (int j = 0; j<caseContactRows[i].length; j++){
        if (j==0 && caseContactRows[i][j] != null)
          refId=  (Integer)caseContactRows[i][j];
        else if(j==1 && caseContactRows[i][j] != null)
          providerName = (String)(caseContactRows[i][j]!=null?caseContactRows[i][j]: "");
        else if(j==2 && caseContactRows[i][j] != null)
          ur_name = (String)(caseContactRows[i][j]!=null?caseContactRows[i][j]: "");
        else if(j==3 && caseContactRows[i][j] != null)
          ur_phone = (String)(caseContactRows[i][j]!=null?caseContactRows[i][j]: "");
      } 
      
      String urphonemasked = ur_phone;
      if (StringUtils.isNotEmpty(urphonemasked)) {
        if(urphonemasked.length() > 10) {
          urphonemasked = PhoneFormat.EXTENSION.format(urphonemasked);
        } else {
          urphonemasked = PhoneFormat.DEFAULT.format(urphonemasked);
        }
      }
      
      // In case of empty UrName,UrPhone,ProviderName Case Contact Not should not be created
      if (StringUtils.isNotEmpty(ur_name) || StringUtils.isNotEmpty(ur_phone) || StringUtils.isNotEmpty(providerName)) {
        caseId = getCaseId(refId);
        String noteSummary = "Care Advocate made outreach call to " + providerName + " Utilization Reviewer at " + urphonemasked
                + " to request updated clinical information on member." + "\n"
                + "Care Advocate left name, contact information and requested call back as soon as possible today.";

        BhCaseContactNoteMBean bhCaseContactNoteMBean = new BhCaseContactNoteMBean();
        BhCaseContactMBean bhCaseContactMBean = new BhCaseContactMBean(); // Contact tab
        bhCaseContactMBean.setContactName(ur_name);
        bhCaseContactMBean.setContactPhone(ur_phone);
        bhCaseContactMBean.setContactMethodCode((short)0);
        bhCaseContactMBean.setRelationCode((short)7);
        bhCaseContactMBean.setContactCategoryCode((short)0);

        bhCaseContactNoteMBean.setBhCaseContactMBean(bhCaseContactMBean); // Notes tab
        bhCaseContactNoteMBean.setContactSummary(noteSummary);

        bhCaseContactNoteMBean.setUserName(agentFullName);
        bhCaseContactNoteMBean.setFkId(caseId);
        bhCaseContactNoteMBean.setChartTypeId(ChartConstant.BH_CASE_CONTACT_V3);
        bhCaseContactNoteMBean.getBhCaseContactMBean().setContactMethodCode(ChartConstant.CONTACT_SERVICE_CONTACT_METHOD_TELEPHONE);
        bhCaseContactNoteMBean.getBhCaseContactMBean().setContactCategoryCode(ChartConstant.CONTACT_CATEGORY_CODE_FBC_MORNING_CALL_OUT);

        ChartReferralMBean chartReferralMBean = new ChartReferralMBean();
        chartReferralMBean.setReferralGivenTypeCode("norefer");
        bhCaseContactNoteMBean.setChartReferralMBean(chartReferralMBean);

        bhCaseContactNoteMBean.setStatusCode(ChartConstant.CN_STATUS_COMPLETED);
        bhCaseContactNoteMBean.setInsertStatus();

        PiFactory.getImplementation(IChartNoteDao.class).saveChartMBean(bhCaseContactNoteMBean);
        
        // Saving FBCCaseContactNotesMBean for reporting
        CsdMemberCaseMBean csdMemberCaseMBean = PiFactory.getImplementation(IBHCaseDao.class).getCsdMemberCaseMBean(caseId);
        if(csdMemberCaseMBean!=null){
        	FBCCaseContactNotesMBean fbcCaseContactNotesMBean = new FBCCaseContactNotesMBean();
        	fbcCaseContactNotesMBean.setCaseId(caseId);
        	fbcCaseContactNotesMBean.setMemberId(csdMemberCaseMBean.getMemberId());
        	fbcCaseContactNotesMBean.setVolumeOfCaseContactNotes(1);
        	fbcCaseContactNotesMBean.setFacilityProviderName(providerName);
        	fbcCaseContactNotesMBean.setCreateUserName(agentFullName);
        	fbcCaseContactNotesMBean.setCreateUserId(userId);
        	DB.getDB(DBPool.CSD).save(fbcCaseContactNotesMBean);
        }
      }
    }
    return 1;
}

  public void deleteQCTablesCBAS(Map<String,Integer> contentMap) {
    DeleteCBASFormDataAPI api = new DeleteCBASFormDataAPI(); 
    api.deleteQCTablesCBAS(contentMap);
  }
  
  
  public String getCaseId(int refId) {
    MQuery query = new MQuery(BhAdHocItemMO.I);
    query.addCriteria(BhAdHocItemMO.I.REF_ID, refId);
    MRow[] bhAdhocItems = DB.getDB(DBPool.CSD).getMRows(query);
    return bhAdhocItems.length > 0 ? bhAdhocItems[0].getString(BhAdHocItemMO.I.CASE_ID) : "";
  }
  
  public void deleteCBASDischargeTable(Map<String,Integer> contentMap) {
    DeleteCBASFormDataAPI api = new DeleteCBASFormDataAPI(); 
    api.deleteCBASDischargeTable(contentMap);
  }
  
  public void setupCBASDischargeTable(Map<String,String> contentMap) {
    DeleteCBASFormDataAPI api = new DeleteCBASFormDataAPI(); 
    api.setupCBASDischargeTable(contentMap);
  }

  public void saveEventsLog(EventsLogsMBean eventBean) {
     DB.getDB(DBPool.CSD).save(eventBean);
  }
  @Override
  public MBeanList<SmsRestrictedWordsMBean> getSmsRestrictedWords() {
    MQuery query = new MQuery(SmsRestrictedWordsMO.I);
    return DB.getDB(DBPool.CSD).getMBeanList(SmsRestrictedWordsMBean.class, query);
  } 
  
//Checking Presumptive members
  @Override
  public boolean hasPresumtive(int packageId, int accountId)  {
    boolean hasPresumtive = false;
    DB dbCBMS = DB.getDB(DtBenefitUtil.CBMS_DB_POOL);
    String xml;
    try {
      xml = new DtBenefitGroupPlanEligSBO().getGroupPlanEligListByPackageId(dbCBMS, packageId, accountId);
      MRow[] mrows = new MTable( xml, DtBenefitGroupPlanEligMO.I ).getUnchangedRows();
      for(int i =0; i < mrows.length; i++){
        if (mrows[i].getInt(DtBenefitGroupPlanEligMO.I.ENROLLMENT_CODE) == 4) { // presumptive
          hasPresumtive = true;
          break;
        }
      }
    } catch (Exception e) {
      //
    }
     return hasPresumtive;
  }
  
}