package com.scraper;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
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
        } catch(Exception e) {System.out.println("Error in fileInput" + e);}                       
    }
    
    
    public static void lineInput(String upc) {
        try {
            Amazon amazon = new Amazon(upc);        
            amazon.setPrice(new Ebay(upc).getPrice());
            Output output = new Output(amazon);
        } catch(Exception e) {System.out.println("Error in lineInput" + e);}        
    }            
    
}
