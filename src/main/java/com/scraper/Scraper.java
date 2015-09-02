package com.scraper;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;


public class Scraper{
                
    
    private static XSSFWorkbook book;//= new XSSFWorkbook();
    private static FileOutputStream out;
    
    
    public static void craper(String args) throws FileNotFoundException {
        if(args.isEmpty()) {
            
        }
        else {
            Ebay ey = new Ebay(args);            
            Amazon an = new Amazon(args);            
        }
    }
    
    
    private static void output(Map<String, String> map, Map<String, String> image) {
        XSSFRow row;
        int rowid = 0;
        Set <String> keyid = map.keySet();
        try {
            XSSFSheet sheet = book.createSheet("Sheet1");
            for(String key : keyid) {
                row = sheet.createRow(rowid);
                rowid++;
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(key);
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(map.get(key));
            }
        } catch(Exception e) {
            System.err.println("Sheet1 exception: " + e);
        }
        try {
            XSSFSheet sheet = book.createSheet("Sheet2");
            
        } catch(Exception e) {
            System.err.println("Sheet2 exception: " + e);
        }
    }
    
}
