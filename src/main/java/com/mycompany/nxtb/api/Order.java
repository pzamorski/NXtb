/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.api;

import com.mycompany.nxtb.tools.Average;

/**
 *
 * @author warsztat
 */
public class Order {

    public Order(String symbol) {
        this.symbol = symbol;
    }

    private Average averageCurrentPOC = new Average(5);

    private double currentPOC = 0;
    private double openPozytionPOC = 0;

    private String symbol;
    private int maxLimitOreders = 1;
    private int limitOrders = 0;

    private double tolerance = 0;

    private double volume = 0.01;
    private double maxVolume = 0.01;

    private double trigerGetProfut = 0.1;
    private double profit = 0.0;
    private int numerClosedOrders = 0;

    private double maxProfit = 0;

    private double maxPrice = 0, minPrice = 999.999;

    public boolean getMinPriceToBuy(double actualPrice) {
        if (minPrice > actualPrice) {
            minPrice = actualPrice;
        }
        return minPrice < actualPrice;
    }

    public boolean getMaxPriceToSell(double actualPrice) {
        if (maxPrice < actualPrice) {
            maxPrice = actualPrice;
        }
        return maxPrice > actualPrice;
    }

    public boolean getMaxProfit(double curentProfit) {

        int pecenTriger = 15;
        if (curentProfit < -2) {
            limitOrders = 0;
        }

        if (maxProfit < curentProfit) {
            maxProfit = curentProfit;
        }

        double percentDownProfit = 100 - (curentProfit * 100 / maxProfit);

        if ((maxProfit > curentProfit) && curentProfit > 0) {
            incrementLimitOrder();
        }

        return (percentDownProfit > pecenTriger) && curentProfit > 1;
    }

    public double getCurrentPOC() {
        return currentPOC;
    }

    public double getAveragePOC() {
        return averageCurrentPOC.getAverage();
    }

    public void setCurrentPOC(double currentPOC) {
        averageCurrentPOC.setAverage(currentPOC);
        this.currentPOC = currentPOC;
    }

    public double getOpenPozytionPOC() {
        return openPozytionPOC;
    }

    public void setOpenPozytionPOC(double openPozytionPOC) {
        this.openPozytionPOC = openPozytionPOC;
    }

    public void setMaxLimitOrders(int maxLimitOrders) {
        this.maxLimitOreders = maxLimitOrders;
    }

    public void setTolerance(double tolerance) {

        this.tolerance = tolerance;
    }

    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public int geLimitOrders() {
        return limitOrders;
    }

    public double getTolerance() {
        return tolerance;
    }

    public double getVolume() {
        return volume;
    }

    public double getMaxVolume() {
        return maxVolume;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getProfit() {
        return profit;
    }

    public int getNumerClosedOrders() {
        return numerClosedOrders;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public void incementProfit() {
        profit = increment(profit);
        System.out.println("Increment profit: " + profit + " " + symbol);

    }

    public void decementProfit() {
        profit = decrement(profit);

    }

    public void incrementLimitOrder() {

        if (limitOrders < maxLimitOreders) {

            limitOrders = increment(limitOrders);
            // System.out.println("Increment limit orders to: " + limitOrders + " " + symbol);
        }
    }

    public void decrementLimitOrder() {
        if (limitOrders != 1) {

            limitOrders = decrement(limitOrders);
            System.out.println("Decrement limit orders to: " + limitOrders + " " + symbol);
        }
    }

    public void incerementTolerance() {
        tolerance = increment(tolerance);
        System.out.println("Increment toleracne: " + tolerance + " " + symbol);
    }

    public void decerementTolerance() {
        tolerance = decrement(tolerance);
    }

    public void incrementVolume() {
        volume = increment(volume);
        System.out.println("Increment volume: " + volume + " " + symbol);
    }

    public void decementVolume() {
        volume = decrement(volume);
    }

    public void incrementNumerClosedOrders() {
        numerClosedOrders = increment(numerClosedOrders);
    }

    public void addProfir(double p) {
        profit = profit + p;

    }

    private double increment(double i) {
        return i = i + 0.01;
    }

    private int increment(int i) {
        return i = i + 1;
    }

    private double decrement(double i) {
        return i--;
    }

    private int decrement(int i) {
        return i--;
    }

}
