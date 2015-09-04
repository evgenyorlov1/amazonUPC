package com.scraper;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;


public class Scraper{
                
    
    private static XSSFWorkbook book;//= new XSSFWorkbook();
    private static FileOutputStream out;
    
    
    public static void fileInput(File file) throws FileNotFoundException {        
        List<Amazon> list = new ArrayList<Amazon>();
        try {
            String upc = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            while((upc = br.readLine()) != null) {
                upc.replaceAll("\\s+","");                
                Amazon amazon = new Amazon(upc);
                Ebay ebay = new Ebay(upc);
                amazon.setPrice(ebay.getPrice());                
                list.add(amazon);
                System.out.println("One more entery");
            }
            Output output = new Output(list);
        } catch(Exception e) {System.out.println("Error in fileInput");}                       
    }
    
    
    public static void lineInput(String upc) {
        try {
            Amazon amazon = new Amazon(upc);        
            amazon.setPrice(new Ebay(upc).getPrice());
            Output output = new Output(amazon);
        } catch(Exception e) {}        
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
