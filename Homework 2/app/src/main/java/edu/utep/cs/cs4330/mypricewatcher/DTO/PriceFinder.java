package edu.utep.cs.cs4330.mypricewatcher.DTO;

import java.util.Random;

/**
 * Class will find the price of the item.
 */
public class PriceFinder {
//default constructor.
    PriceFinder(){
    }
/**
 * Method will generate random price.
 * @return new price.
 */
    public double createRandom() {
        double min = 250;
        double max = 400;
        Random random = new Random();
        double holder = min + (max - min) * random.nextDouble();
        return Math.round(holder * 100.00) / 100.0;
    }
}