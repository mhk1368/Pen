package net.kabulsoft.pen.util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import net.kabulsoft.pen.db.StudentInfo;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Reports {
	
	private Workbook workbook;
	private Sheet sheet;
	private StudentInfo info;
	
	public Reports(){
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("sheet");
		sheet.setRightToLeft(true);
	}

	public void createPaper(int id, int g, int y, int sr, int sc)
	{
		info = new StudentInfo();
		Vector<String> student = info.findStudent(id);
		ResultSet marks = info.marks(id, g, y);
		if(marks == null) return;
		
		sheet.setColumnWidth(sc+7, 1500);
		
		if(sheet.getRow(sr) == null){
			for(int i=0; i<30; i++){
				sheet.createRow(sr+i);
			}
		}
		
		CellStyle style = workbook.createCellStyle();
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		
		CellStyle style1 = workbook.createCellStyle();
		style1.setAlignment(CellStyle.ALIGN_CENTER);
		
		try{
			InputStream is1 = new FileInputStream("images/edu.jpg");
			InputStream is2 = new FileInputStream("images/logo.jpg");
			byte [] bytes1 = IOUtils.toByteArray(is1);
			byte [] bytes2 = IOUtils.toByteArray(is2);
			int picIndex1 = workbook.addPicture(bytes1, Workbook.PICTURE_TYPE_JPEG);
			int picIndex2 = workbook.addPicture(bytes2, Workbook.PICTURE_TYPE_JPEG);
			is1.close();
			is2.close();
			
			CreationHelper helper = workbook.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			
			ClientAnchor anchor1 = helper.createClientAnchor();
			ClientAnchor anchor2 = helper.createClientAnchor();
			anchor1.setCol1(sc+0);
			anchor1.setRow1(sr);
			anchor2.setCol1(sc+6);
			anchor2.setRow1(sr);
			Picture pic1 = drawing.createPicture(anchor1, picIndex1);
			Picture pic2 = drawing.createPicture(anchor2, picIndex2);
			pic1.resize();
			pic2.resize();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		sheet.addMergedRegion(new CellRangeAddress(sr+0, sr+0, sc+2, sc+5));
		sheet.addMergedRegion(new CellRangeAddress(sr+1, sr+1, sc+2, sc+5));
		sheet.addMergedRegion(new CellRangeAddress(sr+3, sr+3, sc+2, sc+5));
		
		CellUtil.createCell(sheet.getRow(sr+0), sc+2, "وزارت جلیله معارف", style1);
		CellUtil.createCell(sheet.getRow(sr+1), sc+2, "ریاست معارف هرات", style1);
		CellUtil.createCell(sheet.getRow(sr+3), sc+2, "موسسه تعلیمی خصوصی لیسه آرمان 3", style1);
		
		Row row1 = sheet.getRow(sr+5);
		Row row2 = sheet.getRow(sr+6);
		
		row1.createCell(sc+0).setCellValue("نام:");
		row1.createCell(sc+1).setCellValue(student.get(1));
		row1.createCell(sc+5).setCellValue("سال تعلیمی:");
		row1.createCell(sc+6).setCellValue(y);
		
		row2.createCell(sc+0).setCellValue("ولد:");
		row2.createCell(sc+1).setCellValue(student.get(2));
		row2.createCell(sc+5).setCellValue("صنف:");
		row2.createCell(sc+6).setCellValue(g);
		
		sr += 8;
		
		String [] texts = {"مضمون", "نمره نیمسال", "نمره نهایی", "مجموع"};
		for(int i=0; i<texts.length; i++){
			CellUtil.createCell(sheet.getRow(sr), sc+i, texts[i], style);
		}
		
		String [] texts1 = {"امضاء موسس", "امضاء مدیریت", "امضاء سرمعلم", "امضاء نگران", "امضاء ولی"};
		for(int i=0; i<texts1.length; i++){
			CellUtil.createCell(sheet.getRow(sr+i*2), sc+6, texts1[i], style);
		}
		
		for(int i=0; i<10; i+=2){
			CellRangeAddress region = new CellRangeAddress(sr+i, sr+i+1, sc+6, sc+7);
			sheet.addMergedRegion(region);
			RegionUtil.setBorderTop(CellStyle.BORDER_THIN, region, sheet, workbook);
			RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, region, sheet, workbook);
			RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, region, sheet, workbook);
			RegionUtil.setBorderRight(CellStyle.BORDER_THIN, region, sheet, workbook);
		}
		
		try{
			while(marks.next()){
				Row row = sheet.getRow(++sr);
				
				row.createCell(sc+0).setCellValue(marks.getString("sub_name"));
				row.createCell(sc+1).setCellValue(marks.getInt("half"));
				row.createCell(sc+2).setCellValue(marks.getInt("total"));
				row.createCell(sc+3).setCellValue(marks.getInt("second"));
				
				row.getCell(sc+0).setCellStyle(style);
				row.getCell(sc+1).setCellStyle(style);
				row.getCell(sc+2).setCellStyle(style);
				row.getCell(sc+3).setCellStyle(style);
			}
		}
		catch (SQLException e1) {}
	}
	
	public void build(String path){
		
		try{
			FileOutputStream output = new FileOutputStream(path);
			workbook.write(output);
			output.close();
		}
		catch (FileNotFoundException e2){
			PenDiags.showWarn("لطفا تمام برنامه هایی که از فایل مورد نظر استفاده می کنند را ببندید!");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(Desktop.isDesktopSupported()){
			try{
				Desktop desktop = Desktop.getDesktop();
				desktop.open(new File(path));
			}
			catch(Exception e){}
		}
	}
}


















