package com.scraper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;


public class Output {
    
    
    private XSSFWorkbook workbook = new XSSFWorkbook();
    private FileOutputStream stream;
    private String[] featuresNamesSh1 = {"rank", "sku", "upc", "item-name", 
        "item-description", "listing price", "Category", "Category", 
        "keywords", "weight", "dimensions"};
    private String[] featuresNamesSh2 = {"rank", "upc", "seller-sku", 
        "item-name", "item-description", "price", "quantity"};
    
    public Output(Collection<Amazon> list) {
        System.out.println("Output collection");        
        createFile(list);
    }
        
    public Output(Amazon amazon) {
        System.out.println("Output object");            
        createFile(amazon);        
    }
    
    
    private void createFile(Collection<Amazon> list) {
        //single file for multiple inputs
        try {
            stream = new FileOutputStream(new File("sd"));
            //here goes code
            XSSFSheet sheet1 = workbook.createSheet("Sheet1");
            XSSFSheet sheet2 = workbook.createSheet("Sheet2");
            
            workbook.write(stream);
            stream.close();
        } catch(Exception e) {}
    }
    
    
    private void createFile(Amazon amazon) {
        //single file for single input
        try {
            stream = new FileOutputStream(new File("sd"));
            XSSFRow row;
            
            Map features = (Map) amazon.getItem().get("features");
            Map imagies = (Map) amazon.getItem().get("imagies");
            
            Set <String> keyFeatures = features.keySet();
            Set <String> keyImagies = imagies.keySet();                        
            
            XSSFSheet sheet1 = workbook.createSheet("Sheet1");
            XSSFSheet sheet2 = workbook.createSheet("Sheet2");
            
            int cellIndex = 0;
            try {
                for(String key : featuresNamesSh1) {
                    row = sheet1.createRow(0);
                    Cell cell = row.createCell(cellIndex);
                    cellIndex++;
                    cell.setCellValue(key);                    
                }
            } catch(Exception e) {}
            
            workbook.write(stream);
            stream.close();
        } catch(Exception e) {}
        
    }
}
