/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.api;

/**
 *
 * @author warsztat
 */
public class Order {

    public Order(String symbol) {
        this.symbol = symbol;
    }

    private String symbol;
    private int maxLimitOreders = 1;
    private int limitOrders = 1;
    private double tolerance = 1;
    private double volume = 0.01;
    private double maxVolume = 0.1;
    private double trigerGetProfut = 0.1;
    private double profit = 0.1;
    private int numerClosedOrders = 0;
    private boolean edge = false;
    private double oldPrice=0;

    public void serchMinimumEdge(double actualPrice) {
        if(actualPrice<oldPrice){
            
        }
        
        
    }

    ;
    
    
    public void setMaxLimitOrders(int maxLimitOrders) {
        this.maxLimitOreders = maxLimitOrders;
    }

    public void setTolerance(double tolerance) {

        this.tolerance = tolerance;
    }

    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public boolean isEdge() {
        return edge;
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
        profit = 0;
        if (limitOrders < maxLimitOreders) {

            limitOrders = increment(limitOrders);
            System.out.println("Increment limit orders: " + limitOrders + " " + symbol);
        }
    }

    public void decrementLimitOrder() {
        limitOrders = decrement(limitOrders);
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
        System.out.println("Increment numer close order: " + numerClosedOrders + " " + symbol);
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
