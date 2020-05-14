package ogs.testng.dataprovider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.testng.TestNGException;
import org.testng.annotations.DataProvider;

import ogs.util.DBUtils;
import ogs.util.ExcelUtil;



public class DataProviderUtil {
	
	@SuppressWarnings("unchecked")
	@DataProvider(name="isfw_excel",parallel=true)
	public static Iterator<Object[]> getExcelData(Method m) throws EncryptedDocumentException, IOException{
		CDataProvider dp = m.getAnnotation(CDataProvider.class);
		Class<?> types[] = m.getParameterTypes();
		if ((types.length == 1) && Map.class.isAssignableFrom(types[0])) {
			// will consider first row as header row
			List<Map<String,String>> lst = ExcelUtil.getData(dp.dataFile(), dp.sheetName(), dp.key());
			List<Object[]> lstRet = new ArrayList<Object[]>();
			for(Map<String,String> map:lst){
				lstRet.add(new Object[]{map});
			}
			System.out.println(lstRet.size());
			return lstRet.iterator();
		}
		return (Iterator<Object[]>) new TestNGException("Custom Data provider is not configured properly");
		
		
	}
	
	@SuppressWarnings("unchecked")
	@DataProvider(name="isfw_database",parallel=true)
	public static Iterator<Object[]> getDBData(Method m) throws EncryptedDocumentException, IOException{
		CDataProvider dp = m.getAnnotation(CDataProvider.class);
		Class<?> types[] = m.getParameterTypes();
		if ((types.length == 1) && Map.class.isAssignableFrom(types[0])) {
			// will consider first row as header row
			List<Map<String,String>> lst = DBUtils.getDataFromDB(dp.sqlQuery(),dp.dbEnv());
			List<Object[]> lstRet = new ArrayList<Object[]>();
			for(Map<String,String> map:lst){
				lstRet.add(new Object[]{map});
			}
			System.out.println(lstRet.size());
			return lstRet.iterator();
		}
		return (Iterator<Object[]>) new TestNGException("Custom Data provider is not configured properly");
	}
	
}
