package com.scraper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;


public class Amazon {
    
    
    private Map map = new HashMap<String, String>();
    private Map imageSet = new HashMap<String, String>();
    
    
    public Amazon(String upc) {
        String url_Amazon = "";
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
            System.out.println("Amazon url: " + url_Amazon);
            sendRequest(url_Amazon);
        } catch (Exception e) {
            System.err.println("Amazon exception" + e);
        }
    }
    
    
    private void sendRequest(String urlRequest) {
        try {
            URL url = new URL(urlRequest);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            if (conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
            }
            
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                parser(sb.toString());
            } catch(Exception e) {
                System.err.println(e);
            }
        } catch(Exception e) {
            System.err.println(e);
        }
    }
    
    
    private void parser(String response) {       
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try {            
            DocumentBuilder db = dbf.newDocumentBuilder();              
            Document doc = db.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
            try {
                doc.getElementsByTagName("Errors").item(0).getTextContent();
                map.put("Rank", "");
                map.put("UPC", "");
                map.put("SKU", "");
                map.put("item-name", "");
                map.put("item-description", "");
                map.put("price", "");
                map.put("Feature", "");
                map.put("Weight", "");
                map.put("Height", "");
                map.put("Length", "");
                map.put("Width", "");
                imageSet.put("image-url", "");
                imageSet.put("image", "");                                
            } catch(Exception exc) {
                try {
                    map.put("Rank", doc.getElementsByTagName("SalesRank").item(0).getTextContent()); //ok
                    System.out.println("SalesRank: " + doc.getElementsByTagName("SalesRank").item(0).getTextContent());
                } catch(Exception e) {
                    System.err.println(e + " Rank");
                    map.put("Rank", "");           
                }
            
                try {
                    map.put("UPC", doc.getElementsByTagName("UPC").item(0).getTextContent());//ok
                    System.out.println("UPC: " + doc.getElementsByTagName("UPC").item(0).getTextContent());
                } catch(Exception e) {
                    System.err.println(e + " UPC");
                    map.put("UPC", "");
                }
            
                try {                                                    
                    map.put("SKU", doc.getElementsByTagName("SKU").item(0).getTextContent()); //ok
                    System.out.println("SKU: " + doc.getElementsByTagName("SKU").item(0).getTextContent());
                } catch(Exception e) {
                    try {
                        map.put("SKU", doc.getElementsByTagName("MPN").item(0).getTextContent());
                        System.out.println("SKU: " + doc.getElementsByTagName("MPN").item(0).getTextContent());
                    } catch(Exception ee) {
                        map.put("SKU", "");
                        System.out.println("SKU: " + "");
                    }
                }
            
                try {
                    map.put("item-name", doc.getElementsByTagName("Title").item(0).getTextContent()); //ok
                    System.out.println("item-name: " + doc.getElementsByTagName("Title").item(0).getTextContent());
                } catch(Exception e) {
                    System.err.println(e + " item-name");
                    map.put("item-name", "");
                }
            
                try {
                    map.put("item-description", doc.getElementsByTagName("Content").item(0).getTextContent()); //ok
                    System.out.println("item-desc: " + doc.getElementsByTagName("Content").item(0).getTextContent());
                } catch(Exception e) {
                    System.err.println(e + " item-description");
                    map.put("item-description", "");
                }
                
                //if no price, so no listing price
                try {                
                    map.put("price", doc.getElementsByTagName("OfferSummary").item(0).getFirstChild().getChildNodes().item(2).getTextContent());  //ok
                    System.out.println("price: " + doc.getElementsByTagName("OfferSummary").item(0).getFirstChild().getChildNodes().item(2).getTextContent()); 
                } catch(Exception e) {
                    System.err.println(e + " price");
                    map.put("price", "");
                }                                                                  
                        
                try {
                    String features = "";
                    for(int i = 0; i < doc.getElementsByTagName("Feature").getLength(); i++) {
                        features += doc.getElementsByTagName("Feature").item(i).getTextContent() + ". ";                    
                    }
                    map.put("Feature", features);  //ok
                    System.out.println("Features: " + features);
                } catch(Exception e) {
                    System.err.println(e + " Features");
                    map.put("Feature", "");
                }
                        
                try {                                
                    String weht = doc.getElementsByTagName("PackageDimensions").item(0).getChildNodes().item(2).getTextContent();                
                    map.put("Weight", String.valueOf(Integer.parseInt(weht)/100));  //ok
                    System.out.println("Weight: " + Float.valueOf(weht)/100);
                } catch(Exception e) {
                    System.out.println(e + " weight");
                    map.put("Weight", "");
                }
                                                                                    
                try {
                    map.put("Height", Float.valueOf(doc.getElementsByTagName("PackageDimensions").item(0).getChildNodes().item(0).getTextContent())/100);  //ok
                    System.out.println("Height: " + Float.valueOf(doc.getElementsByTagName("PackageDimensions").item(0).getChildNodes().item(0).getTextContent())/100);
                } catch(Exception e) {
                    map.put("Height", "");
                }
                      
                try {
                    map.put("Length", Float.valueOf(doc.getElementsByTagName("PackageDimensions").item(0).getChildNodes().item(1).getTextContent())/100);  //ok
                    System.out.println("Length: " + Float.valueOf(doc.getElementsByTagName("PackageDimensions").item(0).getChildNodes().item(1).getTextContent())/100);
                } catch(Exception e) {
                    map.put("Length", "");
                }
            
                try {            
                    map.put("Width", Float.valueOf(doc.getElementsByTagName("PackageDimensions").item(0).getChildNodes().item(3).getTextContent())/100);  //ok
                    System.out.println("Width: " + Float.valueOf(doc.getElementsByTagName("PackageDimensions").item(0).getChildNodes().item(3).getTextContent())/100);
                } catch(Exception e) {                
                    map.put("Width", "");
                }                                                
            
                try {
                    imageSet.put("image-url", doc.getElementsByTagName("LargeImage").item(0).getFirstChild().getTextContent());  //ok
                    System.out.println("image-url: " + doc.getElementsByTagName("LargeImage").item(0).getFirstChild().getTextContent());
                } catch(Exception e) {
                    imageSet.put("image-url", "");
                }            
            
                try {
                    for(int i = 1; i < doc.getElementsByTagName("LargeImage").getLength()-1; i++) {
                        imageSet.put("image" + String.valueOf(i), doc.getElementsByTagName("LargeImage").item(i).getFirstChild().getTextContent());                    
                        System.out.println("ImageSets: " + doc.getElementsByTagName("LargeImage").item(i).getFirstChild().getTextContent());
                    }
                } catch(Exception e) {
                    imageSet.put("image", "");
                }
            } 
            } catch(Exception e) {                
                System.err.println("amazonParser exception: " + e);
            }        
      
    }
    
    
    public Map getItem() {
        Map result = new HashMap<String, Map>();
        result.put("features", map);
        result.put("imagies", imageSet);
        return result;
    }
    
    
    public void setPrice(String ePrice) {
        System.out.println("Ebay price: " + ePrice);
        System.out.println("Amazon price: " + (String) map.get("price"));
        String aPrice = (String) map.get("price");
        try {
            if(ePrice == "") {System.out.println("ePrice == ''");}  //ok
            else if(ePrice != "") {
                if(aPrice == "") {map.put("price", ePrice);}
                else {
                    float aprice = Float.valueOf(aPrice.replaceAll("[^0-9.]", ""));
                    System.out.println("aprice = " + aprice);
                    float eprice = Float.valueOf(ePrice.replaceAll("[^0-9.]", ""));
                    System.out.println("eprice = " + eprice);
                    if(eprice < aprice) {map.put("price", String.valueOf(eprice)); System.out.println("Price changed");System.out.println("Amazon map price: " + map.get("price"));}
                }
            }                        
        } catch(Exception e) {
            System.out.println("setPrice error: " + e);
        }
    }
}
