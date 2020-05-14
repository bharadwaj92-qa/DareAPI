package ogs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.aventstack.extentreports.Status;

import ogs.selenium.pages.Output;
import ogs.selenium.reporting.TestReporter;

public class ExcelUtil {
	public Workbook workBook;

	public  ExcelUtil(String excelFilePath) {
		try {
			workBook = WorkbookFactory.create(new File(excelFilePath));
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
		}

	} 

	public ExcelUtil(){

	}

	public Sheet getExcelSheet(String sheetName){
		return this.getExcelSheet(sheetName);
	}

	public static List<Map<String,String>> getData(String filePath,String sheetName,String key) throws EncryptedDocumentException, IOException{

		File file = null;Workbook wBook = null;List<Map<String,String>> lst = null;Map<String,String>  map = null;
		try{
			file = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+filePath);
			wBook = WorkbookFactory.create(file);
			lst = new ArrayList<Map<String,String>>();
			for(String sName : sheetName.split(",")) {
				Sheet sheet = wBook.getSheet(sName);
				int totalRows = sheet.getPhysicalNumberOfRows();
				int tRows = sheet.getLastRowNum()-sheet.getFirstRowNum();
				for(int row=1;row<=totalRows;row++){
					Row rowData = sheet.getRow(row);
					if(rowData!=null){
						Cell cell = rowData.getCell(0, MissingCellPolicy.RETURN_BLANK_AS_NULL);
						if(cell!=null){
							if(new DataFormatter().formatCellValue(cell).equals(key)){
								int colNumber = sheet.getRow(0).getLastCellNum();
								map = new LinkedHashMap<String,String>(colNumber);
								for(int col=0;col<colNumber;col++){
									map.put(new DataFormatter().formatCellValue(sheet.getRow(0).getCell(col)).trim(), new DataFormatter().formatCellValue(rowData.getCell(col)).trim());
								}
								lst.add(map);
							}
						}
					}
				}
			}
		}catch(Exception e){
			TestReporter.logInfo("Error while reading excel file reading..Please close the file if it is opened", Status.FAIL);
		}finally{
			if(wBook!=null){
				wBook.close();
			}
			if(file!=null){
				file = null;
			}
			if(map!=null){
				map = null;
			}
		}
		return lst;
	}

	public static void main(String ...args) throws IOException, InvalidFormatException{
		//List<Output> lst = new ArrayList<Output>();
		//Output op = new Output("NO","NO","NO","NO","NO","Alabama","Alabama","Alabama","17","Peer Review","NO STATE SPECIFIC LICENSURE REQUIRED   -   - -   -  A&G, USE NCQA DOL and URAC","NO STATE SPECIFIC LICENSURE REQUIRED   -   - -   -  A&G, USE NCQA DOL and URAC","","","","",LocalDateTime.now().toString(),Status.PASS);
		//Output op1 = new Output("NO","NO","NO","NO","NO","Alabama","Alabama","Alabama","17","Peer Review","NO STATE SPECIFIC LICENSURE REQUIRED   -   - -   -  A&G, USE NCQA DOL and URAC","NO STATE SPECIFIC LICENSURE REQUIRED   -   - -   -  A&G, USE NCQA DOL and URAC","","","","",LocalDateTime.now().toString(),Status.PASS,Status.FAIL,Status.PASS,Status.PASS);

		/*lst.add(op);
		lst.add(op1);*/

		//writeExcel(lst);
		/*String jsonBody = "{"
				+ "\"isFullyInsured\":\""+"No"
				+"\",\"isMedicare\":\""+"No"
				+"\",\"isMedicaid\":\""+"NO"
				+"\",\"isHMO\":\""+"NO"
				+"\",\"isAllSameselected\":"+true
				+","+"\"isAttendingProvider\":\""+"NO"
				+"\",\"stateOfResidence\":\""+"36"
				+"\",\"stateOfIssue\":\""+"36"
				+"\",\"stateOfService\":\""+"36"
				+"\",\"age\":\""+"21"
				+"\",\"resultTypeDrpDwn\":\""+"Peer Review"
				+"\" }";
		System.out.println(jsonBody);*/

		//System.out.println(getData("DARESameStatesMain.xlsx", "SameState1,SameState2", "dare-output").size());
		System.out.println(getData("TestData/tblToolLicensure.xlsx", "tblToolLicensure", "dare-toolLicensure").get(0));
	}

	public static void writeExcel(List<Output> data) throws IOException{

		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
		String columns[] = {"IsFullyInsured","IsMedicare","IsMedicaid","IsHMO","IsAttendingProvider","StateOfResidence","StateOfIssue","StateOfService","Age","ResultTypeDrpDwn","ExpectedMessage","ActualMessage","ExpectedSM","ActualSM","ExpectedSOG","ActualSOG","Execution Time","TCStatus"};
		CreationHelper createHelper = workbook.getCreationHelper();
		// Create a Sheet
		Sheet sheet = workbook.createSheet("TestData");
		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.BROWN.getIndex());
		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		Font passFont = workbook.createFont();
		passFont.setColor(IndexedColors.GREEN.getIndex());
		passFont.setFontHeightInPoints((short) 11);
		// Create a CellStyle with the font
		CellStyle passCellStyle = workbook.createCellStyle();
		passCellStyle.setFont(passFont);

		Font failFont = workbook.createFont();
		failFont.setColor(IndexedColors.RED.getIndex());
		failFont.setFontHeightInPoints((short) 11);
		// Create a CellStyle with the font
		CellStyle failCellStyle = workbook.createCellStyle();
		failCellStyle.setFont(failFont);

		// Create a Row
		Row headerRow = sheet.createRow(0);
		// Create cells
		for(int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}

		data.forEach(rowData ->{
			Row row = sheet.createRow(data.indexOf(rowData)+1);
			row.createCell(0).setCellValue(rowData.getIsFullyInsured());
			row.createCell(1).setCellValue(rowData.getIsMedicare());
			row.createCell(2).setCellValue(rowData.getIsMedicaid());
			row.createCell(3).setCellValue(rowData.getIsHMO());
			row.createCell(4).setCellValue(rowData.getIsAttendingProvider());
			row.createCell(5).setCellValue(rowData.getStateOfResidence());
			row.createCell(6).setCellValue(rowData.getStateOfIssue());
			row.createCell(7).setCellValue(rowData.getStateOfService());
			row.createCell(8).setCellValue(rowData.getAge());
			row.createCell(9).setCellValue(rowData.getResultTypeDrpDwn());
			if(rowData.getMsgStatus()==Status.PASS){
				row.createCell(10).setCellValue(rowData.getExpectedMessage());
				row.getCell(10).setCellStyle(passCellStyle);
				row.createCell(11).setCellValue(rowData.getActualMessage());
				row.getCell(11).setCellStyle(passCellStyle);
			}else{
				row.createCell(10).setCellValue(rowData.getExpectedMessage());
				row.getCell(10).setCellStyle(failCellStyle);
				row.createCell(11).setCellValue(rowData.getActualMessage());
				row.getCell(11).setCellStyle(failCellStyle);
			}
			if(rowData.getSmStatus()==Status.PASS){
				row.createCell(12).setCellValue(rowData.getExpectedSM());
				row.getCell(12).setCellStyle(passCellStyle);
				row.createCell(13).setCellValue(rowData.getActualSM());
				row.getCell(13).setCellStyle(passCellStyle);
			}else{
				row.createCell(12).setCellValue(rowData.getExpectedSM());
				row.getCell(12).setCellStyle(failCellStyle);
				row.createCell(13).setCellValue(rowData.getActualSM());
				row.getCell(13).setCellStyle(failCellStyle);
			}
			if(rowData.getSogStatus()==Status.PASS){
				row.createCell(14).setCellValue(rowData.getExpectedSOG());
				row.getCell(14).setCellStyle(passCellStyle);
				row.createCell(15).setCellValue(rowData.getActualSOG());
				row.getCell(15).setCellStyle(passCellStyle);
			}else{
				row.createCell(14).setCellValue(rowData.getExpectedSOG());
				row.getCell(14).setCellStyle(failCellStyle);
				row.createCell(15).setCellValue(rowData.getActualSOG());
				row.getCell(15).setCellStyle(failCellStyle);
			}
			row.createCell(16).setCellValue(rowData.getExecutionTime());
			row.createCell(17).setCellValue(rowData.getTcStatus().toString());
		});

		// Resize all columns to fit the content size
		for(int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}
		
		new FileChecker().deleteFile(System.getProperty("user.dir")+System.getProperty("file.separator")+"TestReport", ".xlsx");
		// Write the output to a file
		FileOutputStream fileOut = null;
		try {
			LocalDate ld = LocalDate.now();
			LocalTime lt = LocalTime.now();
			String opFileName = "Output"+ld.getYear()+"_"+ld.getMonthValue()+"_"+ld.getDayOfMonth()+"_"+lt.getHour()+"_"+lt.getMinute()+"_"+lt.getSecond()+"_"+lt.getNano()+".xlsx";
			fileOut = new FileOutputStream(System.getProperty("user.dir")+System.getProperty("file.separator")+"TestReport"+System.getProperty("file.separator")+opFileName);
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
