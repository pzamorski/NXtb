/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.tools;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.Precision;

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
        average = new double[size];
    }

    public double setAverage(double av) {

        average[0] = this.av;
        average[index] = av;
        index++;
        if (index >= size) {
            index = 1;
        }
        for (int i = 0; i < average.length; i++) {
            av = av + average[i];

        }
        this.av = av / size;

        return this.av;
    }

    public double getAverage() {
        
        return Precision.round(this.av,3);
    }

}
