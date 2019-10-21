package cs4330.cs.utep.pricewatcher.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class PriceFinder {
    public double getPrice(Double medValue) {
        Random rand = new Random();
        double minValue = medValue - (medValue / 10);
        double maxValue = medValue + (medValue / 10);
        return (new BigDecimal(minValue + (maxValue - minValue) * rand.nextDouble()).setScale(2, RoundingMode.CEILING).doubleValue());
    }
}