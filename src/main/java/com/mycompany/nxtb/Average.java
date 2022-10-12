/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author warsztat
 */
public class Average {

    double[] average;
    int index = 0;
    int size;
    double av = 0;

    public Average(int size) {
        this.size = size;
        average=new double[size];
    }

    public double getAverage(double av) {

        average[index]=av;
        index++;
        if(index>=size){index=0;}
        for (int i = 0; i < average.length; i++) {
            av = av + average[i];
            
        }
        

        return av/size;
    }

}
