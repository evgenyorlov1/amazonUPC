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
    private String[] featuresNamesSh1 = {"rank", "upc", "sku", "item-name", 
        "item-description", "listing price", "Category", "features",
        "keywords", "weight", "dimensions"};
    private String[] featuresNamesSh2 = {"rank", "upc", "seller-sku", 
        "item-name", "item-description", "price", "quantity"};
    private String[] featureKeysSh1 = {"Rank", "UPC", "SKU", "item-name", "item-description", 
        "price", "Category", "Feature", "Keywords", "Weight", "Dimensions"};    
    private String[] featureKeysSh2 = {"Rank", "UPC", "SKU", "item-name", "item-description", 
        "price", "Quantity"};
    private String[] imageKeys = {"image-url"};
    
    
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
            stream = new FileOutputStream(new File(System.getProperty("user.dir") + File.separator + "DelonghiProducts" +".xlsx"));            
            XSSFSheet sheet1 = workbook.createSheet("Sheet1");                                                
            XSSFSheet sheet2 = workbook.createSheet("Sheet2");  
            XSSFRow row;
            
            creatSheet(sheet1, sheet2, 15);
            
            Cell cell;
            Amazon amazon;
            Object[] amazonList = list.toArray();
            for(int i=1; i<amazonList.length; i++) {                
                amazon = (Amazon) amazonList[i];
                Map features = (Map) amazon.getItem().get("features");
                Map imagies = (Map) amazon.getItem().get("imagies");
                
                Set <String> keyFeatures = features.keySet();
                Set <String> keyImagies = imagies.keySet();                        
                Object[] imageKeys = keyImagies.toArray();
                
                row = sheet1.createRow(i);
                
                int index = 0;
                for(int j=0; j<featuresNamesSh1.length+keyImagies.size(); j++) {
                    cell = row.createCell(j);
                    if(j<featuresNamesSh1.length) {
                        cell.setCellValue((String) features.get(featureKeysSh1[j]));
                    } else {
                        cell.setCellValue((String) imagies.get(String.valueOf(imageKeys[index])));
                        index++;
                    }
                }
                
                row = sheet2.createRow(i);
                for(int j=0; j<featuresNamesSh2.length;j++) {
                    cell = row.createCell(j);                
                    cell.setCellValue((String) features.get(featureKeysSh2[j]));                      
                }
                cell = row.createCell(featuresNamesSh2.length);                
                cell.setCellValue((String) imagies.get(imageKeys[0]));                                
            }
            workbook.write(stream);
            stream.close();
        } catch(Exception e) {System.out.println("createFile error: " + e);}
    }
    
    
    private void createFile(Amazon amazon) {
        //single file for single input
        try {
            stream = new FileOutputStream(new File(System.getProperty("user.dir") + File.separator + "DelonghiProducts" +".xlsx"));
            XSSFRow row;
            
            Map features = (Map) amazon.getItem().get("features");
            Map imagies = (Map) amazon.getItem().get("imagies");
            
            Set <String> keyFeatures = features.keySet();
            Set <String> keyImagies = imagies.keySet();                        
            Object[] imageKeys = keyImagies.toArray();
            
            XSSFSheet sheet1 = workbook.createSheet("Sheet1");                                                
            XSSFSheet sheet2 = workbook.createSheet("Sheet2");            
            
            creatSheet(sheet1, sheet2, keyImagies.size());
            
            Cell cell;
            row = sheet1.createRow(1);            
            int index = 0;
            for(int i=0; i<featuresNamesSh1.length+keyImagies.size(); i++) {
                cell = row.createCell(i);
                if(i<featuresNamesSh1.length) {
                    cell.setCellValue((String) features.get(featureKeysSh1[i]));
                } else {
                    cell.setCellValue((String) imagies.get(String.valueOf(imageKeys[index])));
                    index++;
                }
            }
            
            row = sheet2.createRow(1);
            for(int i=0; i<featuresNamesSh2.length;i++) {
                cell = row.createCell(i);                
                cell.setCellValue((String) features.get(featureKeysSh2[i]));                                               
            }
            cell = row.createCell(featuresNamesSh2.length);                
            cell.setCellValue((String) imagies.get(imageKeys[0]));                
            
            workbook.write(stream);
            stream.close();
        } catch(Exception e) {}
        
    }
        
    
    private void creatSheet(XSSFSheet sheet1, XSSFSheet sheet2, int count) {        
        XSSFRow row;
        
        row = sheet1.createRow(0);            
        int i=0;
        try {
            Cell cell;
            for(i=0; i<featuresNamesSh1.length; i++) {                    
                cell = row.createCell(i);
                cell.setCellValue(featuresNamesSh1[i]);                    
            }
            for(int j=i;j<count+i;j++) {
                cell = row.createCell(j);
                cell.setCellValue("image-url");
            }
        } catch(Exception e) {System.out.println("Error creating sheet1: " + e);}        
            
        row = sheet2.createRow(0);        
        try {
            Cell cell;
            for(i=0; i<featuresNamesSh2.length; i++) {                    
                cell = row.createCell(i);
                cell.setCellValue(featuresNamesSh2[i]);                    
            }
            cell = row.createCell(i);
            cell.setCellValue("image-url");
        } catch(Exception e) {System.out.println("Error creating sheet2: " + e);}
    }
}
