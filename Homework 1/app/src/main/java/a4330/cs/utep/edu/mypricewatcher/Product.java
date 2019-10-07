/*
* CS 4330 Mobile Application Development
* @author Kevin Apodaca
* @since 9/28/19
*
* In this assignment, you are to write the first version of My Price
Watcher app that tracks "simulated" prices of a single, fixed item.
Your app has to meet the following functional and non-functional
requirements.

R1. The app shall show the name of an item, its initial price, current
    price, and the percentage change of the prices (see R4 below to
    simulate the prices of an item). You may assume that the app knows
    a single item to watch over the price; you don't have to provide a
    UI for the user to enter the item information such as name and
    URL.

R2. The app shall provide a way to find the current price of the item
    and calculate a new price change. You may use a button for this.

R3. The app shall provide a way to view the webpage of the item, e.g.,
    starting a built-in web browser with the item's URL.

R4. You should define a class, say PriceFinder, to simulate the price
    of an item. Given the URL of an item, the class returns a
    "simulated" price of the item, e.g., by generating a random or
    normally-distributed price. The idea is to apply the Strategy
    design pattern [1] in later assignments by introducing a subclass
    that actually downloads and parses the Web document of the given
    URL to find the price.

R5. You should use the Model-View-Control (MVC) metaphor [2], and your
    model classes should be completely separated from the view and
    control classes. There should be no dependency from model classes
    to view/control classes to make your design extensible for future
    improvements.

R6. The app shall provide a custom launch icon. Use the Image Asset
    Studio of Android Studio to create custom icons (File > New >
    Image Asset). Read an online document entitled "Create App Icons
    with Image Asset Studio" (https://developer.android.com/studio/
    write/image-asset-studio.html) or an online tool like Android
    Asset Studio (https://romannurik.github.io/AndroidAssetStudio/).
* */
package a4330.cs.utep.edu.mypricewatcher;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Product {

    private String url;
    private String productName;
    private double currentPrice;
    private double priceChange;
    private double initialPrice;
    private String sharedURL;

    // This is the default constructor.
    public Product() {
    }

    /**
     * This method will create the product information, given the parameters.
     * @param url - the url of the product
     * @param productName - the name of the item
     * @param currentPrice - the current price of the item
     * @param priceChange - how much the price changed by
     * @param initialPrice - the original price of the item
     */
    public Product(String url, String productName, double currentPrice, double priceChange, double initialPrice) {
        this.url = url;
        this.productName = productName;
        this.currentPrice = currentPrice;
        this.priceChange = priceChange;
        this.initialPrice = initialPrice;

    }
/* Setters and Getters for the item information. */
    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double price) {
        this.currentPrice = price;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public double getInitialPrice() {
        return initialPrice;
    }

    public void setStartingPrice(double initialPrice) {
        this.initialPrice = initialPrice;
    }

    public String getSharedURL() {
        return sharedURL;
    }

    public void setSharedURL(String sharedURL) {
        this.sharedURL = sharedURL;
    }


    /**
     * This method will check the price of the item
     * @param price
     */
    public void checkPrice(double price) {
        setCurrentPrice(price);
        setPriceChange(new BigDecimal(calculateChange(getInitialPrice(), getCurrentPrice())).setScale(2, RoundingMode.CEILING).doubleValue());
    }

    /**
     * This method will compare the difference between the simulated price change and the original price of the item.
     * @param newPrice - the price after the change.
     * @param initialPrice - the original price.
     * @return the percentage of difference.
     */
    private double calculateChange(double newPrice, double initialPrice) {
        return ((newPrice - initialPrice) / initialPrice) * 100;
    }
}
