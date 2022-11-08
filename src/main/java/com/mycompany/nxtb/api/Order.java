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
    private int limitOreders = 1;
    private int maxLimitOrders = 1;
    private double tolerance = 1;
    private double volume = 0.01;
    private double maxVolume = 0.1;
    private double trigerGetProfut = 0.1;
    private double profit = 0;


    public void setMaxLimitOrders(int maxLimitOrders) {
        this.maxLimitOrders = maxLimitOrders;
    }

    public void setTolerance(double tolerance) {
        
        this.tolerance = tolerance;
    }

    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public int getMaxLimitOrders() {
        return maxLimitOrders;
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

    public void setProfit(double profit) {
        this.profit = profit;
    }
    
    

    
}
