/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.stockMarketTools;

import com.mycompany.nxtb.api.Candle;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.Precision;

import pro.xstore.api.message.records.RateInfoRecord;

/**
 *
 * @author warsztat
 */
public class RSI {

    public double calculate(List<RateInfoRecord> rateInfoRecords,int i) throws Exception {
        
        int periodLength = 14;
        ArrayList<RateInfoRecord> data = null;
            data = new ArrayList<>();

            data.add(rateInfoRecords.get(i - 14));
            data.add(rateInfoRecords.get(i - 13));
            data.add(rateInfoRecords.get(i - 12));
            data.add(rateInfoRecords.get(i - 11));
            data.add(rateInfoRecords.get(i - 10));
            data.add(rateInfoRecords.get(i - 9));
            data.add(rateInfoRecords.get(i - 8));
            data.add(rateInfoRecords.get(i - 7));
            data.add(rateInfoRecords.get(i - 6));
            data.add(rateInfoRecords.get(i - 5));
            data.add(rateInfoRecords.get(i - 4));
            data.add(rateInfoRecords.get(i - 3));
            data.add(rateInfoRecords.get(i - 2));
            data.add(rateInfoRecords.get(i - 1));
            data.add(rateInfoRecords.get(i));

            int lastBar = data.size() - 1;
            int firstBar = lastBar - periodLength + 1;
            if (firstBar < 0) {
                String msg = "Quote history length " + data.size() + " is insufficient to calculate the indicator.";
                throw new Exception(msg);
            }

            double aveGain = 0, aveLoss = 0;
            
            for (int bar = firstBar + 1; bar <= lastBar; bar++) {
                Candle candle = new Candle(data.get(bar));
                Candle candleDown = new Candle(data.get(bar-1));
                double change = (candle.getClose()+candle.getOpen()) - (candleDown.getClose()+candleDown.getOpen());
                if (change >= 0) {
                    aveGain += change;
                } else {
                    aveLoss += change;
                }
            }

            double rs = aveGain / Math.abs(aveLoss);
            int rsi = (int)(100 - 100 / (1 + rs));

           // System.out.println("I " + i + " RSI: " + rsi);
           //System.out.println(String.valueOf(rsi));
        return Precision.round(rs,3);
    }
}
