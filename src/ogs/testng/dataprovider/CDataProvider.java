package ogs.testng.dataprovider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CDataProvider {
	
	public enum params {
		DATAFILE, SHEETNAME, KEY, HASHEADERROW, SQLQUERY, BEANCLASS, JSON_DATA_TABLE, DATAPROVIDER, DATAPROVIDERCLASS;
	}

	public enum dataproviders {
		isfw_csv, isfw_database, isfw_excel, isfw_excel_table, isfw_json, isfw_property;
	}
	
	public String dataFile() default "";
	
	public String sheetName() default "";
	
	public String key() default "";
	
	public String sqlQuery() default "";
	
	public String dbEnv() default "";

}
