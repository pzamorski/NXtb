/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.neuron;

/**
 *
 * @author warsztat
 */
public class NetworkType {

    private static final String PRICE_OPEN = "O";
    private static final String PRICE_CLOSE = "C";
    private static final String PRICE_LOW = "L";
    private static final String PRICE_HIGH = "H";
    private static final String VOLUMEN = "V";
    private static final String PIPSOC = "POC";
    private static final String PIPSHL = "PHL";
    private static final String MASTER = "M";

    public static final String[] TYPE_SLAVE = {PRICE_OPEN, PRICE_CLOSE, PRICE_LOW, PRICE_HIGH};
//    public static final String[] TYPE_SLAVE = {PRICE_OPEN, PRICE_CLOSE, PRICE_LOW, PRICE_HIGH, VOLUMEN, PIPSOC,PIPSHL};
    public static final String[] TYPE_MASTER = {MASTER};

    private String type;

    public NetworkType(String type) {
        this.type = type;
    }

    public String getFileName(String symbol) {
        StringBuilder sb = new StringBuilder("data/" + symbol + "/" + symbol);
        if (type.equals(PRICE_OPEN)) {
            sb.append("O.txt");
        }
        if (type.equals(PRICE_CLOSE)) {
            sb.append("C.txt");
        }
        if (type.equals(PRICE_LOW)) {
            sb.append("L.txt");
        }
        if (type.equals(PRICE_HIGH)) {
            sb.append("H.txt");
        }
        if (type.equals(VOLUMEN)) {
            sb.append("V.txt");
        }
        if (type.equals(PIPSOC)) {
            sb.append("POC.txt");
        }
        if (type.equals(PIPSHL)) {
            sb.append("PHL.txt");
        }
        if (type.equals(MASTER)) {
            sb.append(".txt");
        }

        return sb.toString();
    }

}
