package cs4330.cs.utep.pricewatcher.model;

import android.annotation.SuppressLint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import cs4330.cs.utep.pricewatcher.controller.PriceFinder;

public class Product {

    private PriceFinder priceFinder = new PriceFinder();
    private String url;
    private String name;
    private double currentPrice;
    private double change;
    private double startingPrice;
    private String date;

    public Product() {
    }

    public Product(String url, String name, double currentPrice, double startingPrice, double change) {
        this.date = getCurrentDate();
        this.url = url;
        this.name = name;
        this.currentPrice = currentPrice;
        this.change = change;
        this.startingPrice = startingPrice;
        if (this.url.contains("amazon")) {
            urlSanitize();
        }
    }

    public void checkPrice(double price) {
        setCurrentPrice(price);
        setChange(new BigDecimal(calcChange(getCurrentPrice(), getInitialPrice())).setScale(2, RoundingMode.CEILING).doubleValue());
    }

    private double calcChange(double newPrice, double initialPrice) {
        return ((newPrice - initialPrice) / initialPrice) * 100;
    }

    public String getDate() {
        return date;
    }

    private String getCurrentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double price) {
        this.currentPrice = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getInitialPrice() {
        return startingPrice;
    }

    public void setInitialPrice(double price) {
        this.startingPrice = price;
    }


    public void refreshPrice() {
        checkPrice(priceFinder.getPrice(getCurrentPrice()));
    }

    private void urlSanitize() {
        String[] sanitize = url.split("/");
        StringBuilder newUrl = new StringBuilder();
        for (int i = 0; i < sanitize.length; i++) {
            if (sanitize[i].contains("ref") && !sanitize[i].contains("?ref")) {
                sanitize[i] = "";
            }
            if (sanitize[i].contains("?ref")) {
                String[] fixURL = sanitize[i].split("[?]");
                sanitize[i] = fixURL[0];
            }
            if (sanitize[i].equals("/") || sanitize[i].equals("//")) {
                sanitize[i] = "";
            }
            newUrl.append(sanitize[i]).append("/");
        }
        if (newUrl.toString().contains("//")) {
            newUrl = new StringBuilder(newUrl.substring(0, newUrl.length() - 1));
        }
        setURL(newUrl.toString());
    }
}