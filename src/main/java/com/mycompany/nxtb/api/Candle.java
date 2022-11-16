/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.api;

import pro.xstore.api.message.records.RateInfoRecord;

/**
 *
 * @author warsztat
 */
public class Candle extends RateInfoRecord {

    double mnoznik;

    public Candle(RateInfoRecord rateInfoRecord) {
        //super();
        super.setClose(rateInfoRecord.getClose());
        super.setHigh(rateInfoRecord.getHigh());
        super.setLow(rateInfoRecord.getLow());
        super.setOpen(rateInfoRecord.getOpen());
        super.setVol(rateInfoRecord.getVol());
        super.setCtm((long) rateInfoRecord.getCtm());
    }

    @Override
    public double getOpen() {
        double openPric = super.getOpen();

        if (openPric >= 1000) {
            mnoznik = 100;
            if (openPric >= 100000) {
                mnoznik = 1000;
            }
        }

        return super.getOpen() / mnoznik;
    }

    @Override
    public double getClose() {
        return (super.getClose() + (getOpen() * mnoznik)) / mnoznik;
    }

    @Override
    public double getLow() {
        return (super.getLow() + (getOpen() * mnoznik)) / mnoznik;
    }

    @Override
    public double getHigh() {
        return (super.getHigh() + (getOpen() * mnoznik)) / mnoznik;
    }

    @Override
    public double getVol() {
        double volume = super.getVol();
        for (int i = 0; i < 20; i++) {

            volume = volume / 10;
            if (volume < getOpen()) {
                break;
            }
        }
        return volume;
    }

    public String getCloseString() {
        return String.valueOf(this.getClose());
    }

    public String getOpenString() {
        return String.valueOf(getOpen());
    }

    public String getHighString() {
        return String.valueOf(getHigh());
    }

    public String getLowString() {
        return String.valueOf(getLow());
    }

    public String getVolString() {
        return String.valueOf(getVol());
    }

    public double getPipsCO() {
        return this.getClose() - getOpen();
    }

    public double getPipsHL() {
        return getHigh() - getClose();
    }

    public String getPipsCoString() {
        return String.valueOf(getPipsCO());
    }

    public String getPipsHlString() {
        return String.valueOf(getPipsHL());
    }

    @Override
    public String toString() {
        return "RateInfoRecord{" + "ctm=" + getCtm() + ", open=" + getOpen() + ", high=" + getHigh() + ", low=" + getLow() + ", close=" + getClose() + ", vol=" + getVol() + '}';
    }

}
