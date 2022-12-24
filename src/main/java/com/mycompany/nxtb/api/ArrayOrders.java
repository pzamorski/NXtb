/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.api;

import java.util.ArrayList;

/**
 *
 * @author warsztat
 */
public class ArrayOrders extends ArrayList<Order> {

    public Order get(String symbolName) {
        int index = 0;
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getSymbol().equals(symbolName)) {
                index = i;
            }

        }
    
            return this.get(index);

    }

}
