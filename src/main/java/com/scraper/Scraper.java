package com.scraper;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
/**
 *
 * @author pc
 */
public class Scraper{            
    
        
    private final static String url_Ebay = "http://svcs.ebay.com/services/search/FindingService/v1?SECURITY-APPNAME={applicationId}&OPERATION-NAME=findItemsByProduct&SERVICE-VERSION=1.0.0&REST-PAYLOAD&productId.@type=UPC&productId={upc}&paginationInput.entriesPerPage={enteries}";
    private final static String applicationId = "EvgenyOr-f29a-41ba-9145-05cb5304c582";     //EBAY
    private static String url_Amazon;
    private static String enteries = "1";
    private static XSSFWorkbook book ;//= new XSSFWorkbook(); 
    private static FileOutputStream out; 
    private static String upc;
    
    /**
     * @param args the command line arguments
     */            
    public static void craper(String args) throws FileNotFoundException {
        upc = args;
        
        if(args.isEmpty()) {
            
        }
        else {            
            book = new XSSFWorkbook();
            out = new FileOutputStream(new File(System.getProperty("user.dir") + File.separator + args+".xlsx"));
            amazonStart(args);            
            ebayStart(args);
        }
    }     
    
    
    private static void ebayStart(String upcc) {        
        boolean flag = false;                                      
        String url_ebay = url_Ebay;
        url_ebay = url_Ebay.replace("{applicationId}", applicationId).replace("{upc}", upcc).replace("{enteries}", enteries);                      
        sendRequest(url_ebay, flag);
    }            
    
    
    private static void ebayParser(String response) {        
        Map map = new HashMap<String, String>();
        map.put("Service", "Ebay");
        BufferedImage image = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();        
        try {            
            DocumentBuilder db = dbf.newDocumentBuilder();  
            Document doc = db.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));                                                
            for(int i=0; i<doc.getElementsByTagName("item").getLength();i++) {                
                map.put("itemId"+String.valueOf(i), doc.getElementsByTagName("itemId").item(i).getTextContent());
                map.put("title"+String.valueOf(i), doc.getElementsByTagName("title").item(i).getTextContent());
                map.put("viewItemURL"+String.valueOf(i), doc.getElementsByTagName("viewItemURL").item(i).getTextContent());                
                map.put("viewItemURL"+String.valueOf(i), doc.getElementsByTagName("viewItemURL").item(i).getTextContent());                
                map.put("Condition"+String.valueOf(i), doc.getElementsByTagName("conditionDisplayName").item(i).getTextContent());                
                map.put("listingType"+String.valueOf(i), doc.getElementsByTagName("listingType").item(i).getTextContent());                
                map.put("endTime"+String.valueOf(i), doc.getElementsByTagName("endTime").item(i).getTextContent());                
                map.put("sellingState"+String.valueOf(i), doc.getElementsByTagName("sellingState").item(i).getTextContent());                
                map.put("currentPrice$"+String.valueOf(i), doc.getElementsByTagName("currentPrice").item(i).getTextContent());                
            }            
            
        } catch(Exception e) {
            System.out.println("Exception ebayParser: " + e);
        }        
        try {
            URL url = new URL(String.valueOf(map.get("galleryURL")));
            image = ImageIO.read(url);
        } catch(Exception e) {}     
        
        ebayOutput(map);
    }            
    
    
    private static void amazonStart(String upc) {           
        boolean flag = true;
        Map map = new HashMap<String, String>();        
        map.put("Service", "AWSECommerceService");
        map.put("Version", "2013-08-01");        
        map.put("Operation", "ItemLookup");
        map.put("IdType", "UPC");        
        map.put("SearchIndex", "All");              
        map.put("ItemId", upc); 
        map.put("ResponseGroup", "Large");         
        
        try {                        
            SignedRequestsHelper signedH = new SignedRequestsHelper();            
            url_Amazon = signedH.sign(map);              
            System.out.println(url_Amazon);
            sendRequest(url_Amazon, flag);            
        } catch (Exception ex) {
            System.err.println("amazonStart exception" + ex);
        }                 
    }
    
    
    private static void sendRequest(String urlRequest, boolean flag) {          
        try {
            URL url = new URL(urlRequest);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            if (conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
            }
            
            try {
                BufferedReader rd = new BufferedReader( new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {                    
                    sb.append(line);
                }
                rd.close();
                if(flag) {                      
                    amazonParser(sb.toString());                 
                }
                else {            
                    ebayParser(sb.toString());                    
                }
            } catch(Exception e) {}
              conn.disconnect();            
        } catch (Exception e) {
            System.err.println("sendRequest exception" + e);            
        }
                
    }
    
    
    private static void amazonParser(String response) {                
        Map map = new HashMap<String, String>();
        map.put("Service", "Amazon");        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();        
        try {            
            DocumentBuilder db = dbf.newDocumentBuilder();              
            Document doc = db.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));            
            try {
                map.put("Rank", doc.getElementsByTagName("SalesRank").item(0).getTextContent()); //ok
                System.out.println("SalesRank: " + doc.getElementsByTagName("SalesRank").item(0).getTextContent());
            } catch(Exception e) {}
            
            try {
                map.put("UPC", doc.getElementsByTagName("UPC").item(0).getTextContent());//ok
                System.out.println("UPC: " + doc.getElementsByTagName("UPC").item(0).getTextContent());
            } catch(Exception e) {}
            
            try {
                map.put("SKU", doc.getElementsByTagName("SKU").item(0).getTextContent()); //ok
                System.out.println("SKU: " + doc.getElementsByTagName("SKU").item(0).getTextContent());
            } catch(Exception e) {}
            
            try {
                map.put("item-name", doc.getElementsByTagName("Title").item(0).getTextContent()); //ok
                System.out.println("item-name: " + doc.getElementsByTagName("Title").item(0).getTextContent());
            } catch(Exception e) {}
            
            try {
                map.put("item-description", doc.getElementsByTagName("Content").item(0).getTextContent()); //ok
                System.out.println("item-desc: " + doc.getElementsByTagName("Content").item(0).getTextContent());
            } catch(Exception e) {}
            
            try {
                map.put("price", doc.getElementsByTagName("OfferSummary").item(0).getFirstChild().getChildNodes().item(2).getTextContent());  //ok
                System.out.println("price: " + doc.getElementsByTagName("OfferSummary").item(0).getFirstChild().getChildNodes().item(2).getTextContent()); 
            } catch(Exception e) {}                                                                  
                        
            try {
                String features = "";
                for(int i = 0; i < doc.getElementsByTagName("Feature").getLength(); i++) {
                    features += doc.getElementsByTagName("Feature").item(i).getTextContent() + ". ";
                    //map.put("Feature", doc.getElementsByTagName("Feature").item(i).getTextContent());                    
                }
                map.put("Feature", features);                
            } catch(Exception e) {}
                        
            try {
                //map.put("weight", doc.getElementsByTagName("ItemDimensions").item(2).getTextContent());
                int weight;
                String weht = doc.getElementsByTagName("PackageDimensions").item(0).getChildNodes().item(2).getTextContent();
                //weight = Integer.getInteger(doc.getElementsByTagName("PackageDimensions").item(0).getChildNodes().item(2).getTextContent());
                System.out.println(Integer.getInteger(weht));
            } catch(Exception e) { System.out.println("bad");}
            
            
            
            
            
            
            
            try {
                map.put("height", doc.getElementsByTagName("ItemDimensions").item(0).getTextContent());
                System.out.println();
            } catch(Exception e) {}
            
            try {
                map.put("Length", doc.getElementsByTagName("ItemDimensions").item(1).getTextContent());
                System.out.println();
            } catch(Exception e) {}
            
            try {
                map.put("Width", doc.getElementsByTagName("ItemDimensions").item(3).getTextContent());
                System.out.println();
            } catch(Exception e) {}
            
            try {
                map.put("image-url", doc.getElementsByTagName("LargeImage").item(0).getFirstChild().getTextContent());            
                System.out.println();
            } catch(Exception e) {}
            
            //map.put("DetailPageURL", doc.getElementsByTagName("DetailPageURL").item(0).getTextContent());                        
            
            map.put("LowestNewPrice", "");            
            NodeList amazonOutputt =  doc.getElementsByTagName("LowestNewPrice").item(0).getChildNodes();            
            for(int i=0; i < amazonOutputt.getLength(); i++) {
                map.put(amazonOutputt.item(i).getNodeName(), amazonOutputt.item(i).getTextContent());                                
            }
        } catch(Exception e) {
            System.err.println("amazonParser exception: " + e);
        }        
        try {           
            URL url = new URL(String.valueOf(map.get("LargeImage")));
            BufferedImage image = ImageIO.read(url);
            File file = new File(System.getProperty("user.dir") + File.separator + upc+".jpg");            
            ImageIO.write(image, "jpg", file);            
        } catch(Exception e) {}    
        
        amazonOutput(map);
    }    
    
    
    private static void amazonOutput(Map<String, String> map) {         
        XSSFRow row;
        int rowid = 0;
        Set <String> keyid = map.keySet();
        try {            
            XSSFSheet sheet = book.createSheet("Amazon");                              
            for(String key : keyid) {
                row = sheet.createRow(rowid);
                rowid++;
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(key);
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(map.get(key));                
            }                        
        } catch(Exception e) {
            System.err.println("amazonOutput exception: " + e);
        }                                
    }
    
    
    private static void ebayOutput(Map<String, String> map) {          
        XSSFRow row;        
        int rowid = 0;
        Set <String> keyid = map.keySet();  
        
        //System.out.print(map);
        try {            
            XSSFSheet sheet = book.createSheet("Ebay");                  
            for(String key : keyid) {
                row = sheet.createRow(rowid);
                rowid++;
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(key);
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(map.get(key));                
            }
           
        } catch(Exception e) {
            System.err.println("ebayOutput exception" + e);
        }        
        try {
            book.write(out);            
            book.close();                    
        } catch(Exception ex) {
            System.err.println("Error writing to Excel: " + ex);
        }        
    } 
    
}
