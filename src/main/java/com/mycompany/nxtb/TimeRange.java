/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author warsztat
 */
public class TimeRange {

    int StockExchangeOpeningTime = 9;
    int StockExchangeCloseTime = 17;
    int dayOfWeek;
    long day = 0;
    long hour, minute, second;

    public long getRange() {
        Date actualTime = new Date();

        getNow();

        TimeRange.this.deyToWorkTime(actualTime);

        toMillis();

        return actualTime.getTime() - (hour + minute + second + this.day);
    }

    public long getRange(int day) {
        Date actualTime = new Date();

        getNow();

        this.day = day;

        toMillis();

        return actualTime.getTime() - (hour + minute + second + this.day);
    }

    public long getRange(long actualTime) {

        getNow();

        TimeRange.this.deyToWorkTime(new Date(actualTime));

        toMillis();

        return actualTime - (hour + minute + second + this.day);
    }

    public long getRange(long actualTime, int day_offset) {

        getNow();
        TimeRange.this.deyToWorkTime(new Date(actualTime));
        this.day = this.day+day_offset;
        toMillis();

        return actualTime - (hour + minute + second + this.day);
    }

    private void getNow() {
        Calendar now = Calendar.getInstance();
        hour = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE);
        second = now.get(Calendar.SECOND);

    }

    private void deyToWorkTime(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        dayOfWeek = now.get(Calendar.DAY_OF_WEEK);//niedziela = 1 sobota = 7
        if (dayOfWeek == 1) {
            this.day = 2;
        }
        if (dayOfWeek == 7) {
            this.day = 1;
        }
        if (this.hour < 9 && dayOfWeek == 2) {
            this.day = 3;
        }
    }

    private void deyToWorkTime() {
        Calendar now = Calendar.getInstance();
        dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            this.day = 2;
        }
        if (dayOfWeek == 7) {
            this.day = 1;
        }
        if (this.hour < 9 && dayOfWeek == 2) {
            this.day = 3;
        }
    }

    private void toMillis() {

        hour = TimeUnit.HOURS.toMillis(hour);
        minute = TimeUnit.MINUTES.toMillis(minute);
        second = TimeUnit.SECONDS.toMillis(second);
        day = TimeUnit.DAYS.toMillis(day);

    }

}
