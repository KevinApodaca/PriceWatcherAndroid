package cs4330.cs.utep.edu.models;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebParser {

    private Document doc;
    private String store;

    public WebParser(String url) throws IOException, URISyntaxException {
        // Pase URL
        this.doc = Jsoup.connect(url).timeout(0).userAgent("Opera").get();
        this.store = getDomainName(url);
    }

    /**
     * Method goes to web and extracts the price
     * For homedepot.com (Comes in 3 parts <span>s (For HomeDepot) (0: "$", 1: "<number> Cost USD", 2: <number> Cents )
     * For www.lowes.com ... //TODO
     * @return Array of Strings with the price
     * @throws IOException
     */
    private String webPrice() throws IOException, URISyntaxException {

        StringBuilder stringPrice = new StringBuilder();


        switch (this.store) {
            case "homedepot.com":
                int counter = 0;

                // Get Price from web
                Elements priceParts = this.doc.select("#ajaxPrice span");

                // Fill array for 3 parts
                for(Element nw : priceParts){
                    if(counter != 0) {
                        if(counter == 1) {
                            stringPrice.append(nw.text());
                            stringPrice.append(".");
                        }else {
                            stringPrice.append(nw.text());
                        }
                    }
                    counter++;
                }
                break;

            case "walmart.com":
                Element wpricepart = doc.select(".price-characteristic").first();
                stringPrice.append(wpricepart.attr("content"));
                break;
        }

        return stringPrice.toString();

    }

    /**
     * Method goes to web and extracts the name of the product
     * @return String of the name of the Item
     * @throws IOException
     */
    private String productName() throws IOException {
        // Get Name
        Element name = doc.selectFirst(".product-title__title");

        // Print Name
        return name.text();
    }

    /**
     * Method will return the domain name from the URL
     * @param url URL from the website
     * @return String containing the domainName.com/net/co/org etc..
     * @throws URISyntaxException
     */
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public double getPrice() throws IOException, URISyntaxException {
        return Double.parseDouble(webPrice());
    }

    public String getName() throws IOException {
        return productName();
    }


}
