package com.scraper;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;


public class Ebay {
    
    
    private final String url_Ebay = "http://svcs.ebay.com/services/search/FindingService/v1?SECURITY-APPNAME={applicationId}&OPERATION-NAME=findItemsByProduct&SERVICE-VERSION=1.0.0&REST-PAYLOAD&productId.@type=UPC&productId={upc}&paginationInput.entriesPerPage={enteries}&itemFilter(0).name=Condition&itemFilter(0).value(0)=New&itemFilter(1).name=ListingType&itemFilter(1).value(0)=FixedPrice&itemFilter(2).name=ListedIn&itemFilter(2).value(0)=EBAY-US";
    private final String applicationId = "EvgenyOr-f29a-41ba-9145-05cb5304c582";
    private final String enteries = "1";
    private String price;
    public String upcN; // item number
    
    public Ebay(String upc) {
        upcN = upc;
        String url_ebay = url_Ebay;
        url_ebay = url_Ebay.replace("{applicationId}", applicationId).replace("{upc}", upc).replace("{enteries}", enteries);   
        System.out.println(url_ebay);
        sendRequest(url_ebay);
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
            if (doc.getElementsByTagName("ack").item(0).getTextContent() != "Failure") {
                    price = doc.getElementsByTagName("convertedCurrentPrice").item(0).getTextContent();
                    System.out.println("Ebay price(if): " + price);
            } else {
                price = "";
                System.out.println("Ebay price(else): " + price);
            }
        } catch(Exception e) {
            price ="";
            System.out.println("Ebay parser exception " + e);
        }        
    }
    
    
    public String getPrice() {
        return price;
    }
}
