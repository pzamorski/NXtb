/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.nxtb;

import com.mycompany.nxtb.tools.TimeRange;
import com.mycompany.nxtb.api.XtbApi;
import com.mycompany.nxtb.neuron.CreateModel;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.util.Precision;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.util.Neuroph;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;

/**
 *
 * @author warsztat
 */
public class NXtb {

    public static void main(String[] args) throws IOException, APICommandConstructionException, APICommunicationException, APIReplyParseException, APIErrorResponse, Exception {

        boolean xtb = true;
        boolean lernLocalData = false;
        boolean start=false;

        System.out.println("V:" + Neuroph.getVersion() + ".3.1");
        
        PERIOD_CODE periodCode = PERIOD_CODE.PERIOD_M1;
        long periodCodeInMiliSekund=periodCode.getCode()*60000;
        String symbol = null;
        
        int numberCandleToInput = 5;
        int sizeDownload = 40000;
        long timeDownload=(periodCodeInMiliSekund*sizeDownload)+(numberCandleToInput*periodCodeInMiliSekund);
 
        int inputNetwork = numberCandleToInput * 3;
        int outputNetwork = 1;
        double[] out = new double[outputNetwork];
        symbol = "USDJPY";
        double[] InsertDataToInputScaner = new double[inputNetwork];

        
        
       
        
        
        //Thread monitThread = xtbApi.StartMonitProfitInThred();
        if (xtb) {
            XtbApi xtbApi = new XtbApi();
            xtbApi.setMySymbol(symbol);
            xtbApi.login();
            
            /////////////////////////////////////
            xtbApi.createFile(
                    symbol,
                    sizeDownload,
                    numberCandleToInput,
                    periodCode,
                    //new Date(new TimeRange().getRange(timeDownload)).getTime(),
                    timeDownload,
                    inputNetwork,
                    outputNetwork);
            xtbApi.logout();
        }
        
        NeuralNetwork neuralNetwork;
        CreateModel cm = new CreateModel(symbol, inputNetwork, outputNetwork,periodCode);
        
        

        if(lernLocalData){
        cm.lernFromFile();
        }
        

        
        if(start){
             XtbApi xtbApi = new XtbApi();
            xtbApi.setMySymbol(symbol);
            xtbApi.login();
            
            neuralNetwork=new CreateModel().loadModel(symbol);
            
        for (;;) {

            if (59 - new Date(xtbApi.getServerTime()).getSeconds() == 59) {
                double[] ret = xtbApi.getLastCandles(periodCode, new Date().getTime() - (numberCandleToInput * 60000), inputNetwork);//15000000 ofset dla minut
                if (!Arrays.equals(ret, InsertDataToInputScaner)) {

                    InsertDataToInputScaner = ret;
                    neuralNetwork.setInput(ret);
                    neuralNetwork.calculate();
                    out = neuralNetwork.getOutput();
                    out[0] = Precision.round(out[0], 6);
                    System.out.println(new Date(xtbApi.getServerTime()) + " out: " + out[0]);
                }
            }

            // Thread.sleep(1000);
        }}















//        for (;;) {
//
////----------------------------------get data
//            symbol = "ELROND";
//            orders.add(new Order(symbol));
//            
//            xtbApi.setMySymbol(symbol);
//
//            //xtbApi.getCandlesDataToFile(symbol, PERIOD_CODE.PERIOD_M30, dateRange.getTime());
////-------------------------------------------------------------------------------
//            //network master
//            double outNetwork = 0;
//
//            if (!lern) {
//
//                dateRange = new Date(new TimeRange().getRange(new Date().getTime(), dayBackRange));
//                System.out.print("Run " + symbol + " data: " + dateRange);
//                xtbApi.getCandlesDataToFile(symbol, periodCode, dateRange.getTime());//15000000 ofset dla minut
//
//                networkMaster = new NetworkN(inputNetwork, 2 * inputNetwork+6, outputNetwork);
//                networkMaster.setFileDataTrennig(symbol, NetworkType.TYPE_MASTER[0]);
//                networkMaster.setDefaultParameter();
//                networkMaster.getLernDataSegmen();
//                //networkMaster.setData();
//                try {
//                    networkMaster.lern(true);
//                    //networkMaster.setWeight();
//                } catch (Exception e) {
//                    networkMaster.reset();
//                }
//                lern = true;
//
//            }
//
//            //System.out.println(symbol+" RSI:  "+new RSI(symbol).getCurrentRSI());
//            dateRange = new Date(new TimeRange().getRange(new Date().getTime(), 0));
//            //System.out.print("Run " + symbol + " data: " + dateRange);
//            //networkMaster.getLernDataSegmen();
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            double[] ret = xtbApi.getLastCandles(periodCode, new Date().getTime() - 99200000);//15000000 ofset dla minut
//
//            if (!Arrays.equals(ret, InsertDataToInputScaner)) {
//
//                InsertDataToInputScaner = ret;
//                //networkMaster.selfTest(true);
//                outNetwork = networkMaster.inputScaner(InsertDataToInputScaner, 0);
//                outNetwork = Precision.round(outNetwork, 4);
//
//                xtbApi.TradeTransactionV3(outNetwork, "");
//                System.out.println("");
//            }
//
//        }
    }

    
    
  private static int calculateNumberOfDaysBetween(Date startDate, Date endDate) {
    if (startDate.after(endDate)) {
        throw new IllegalArgumentException("End date should be grater or equals to start date");
    }

    long startDateTime = startDate.getTime();
    long endDateTime = endDate.getTime();
    long milPerDay = 1000*60*60*24; 
    
    int freeDey=0;

    int numOfDays = (int) ((endDateTime - startDateTime) / milPerDay); // calculate vacation duration in days
System.out.println("numberOFDays "+numOfDays);
      for (int i = 0; i < numOfDays; i++) {
          if(numOfDays%6==0||numOfDays%7==0){
          freeDey++;
          }
      }
    
    return freeDey+1; // add one day to include start date in interval
}
    
    
    
    
    
    
    
    
    
    private static void Sleep(PERIOD_CODE period_code) {
        if (period_code == PERIOD_CODE.PERIOD_M1) {
            try {
                Thread.sleep(1000);//30sekund
            } catch (InterruptedException ex) {
                Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (period_code == PERIOD_CODE.PERIOD_M5) {
            try {
                Thread.sleep(200000);//
            } catch (InterruptedException ex) {
                Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (period_code == PERIOD_CODE.PERIOD_M15) {
            try {
                Thread.sleep(600000);//10 minut
            } catch (InterruptedException ex) {
                Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (period_code == PERIOD_CODE.PERIOD_M30) {
            try {
                Thread.sleep(1500000);//25 minut
            } catch (InterruptedException ex) {
                Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (period_code == PERIOD_CODE.PERIOD_H1) {
            try {
                Thread.sleep(3000000);//50 minut
            } catch (InterruptedException ex) {
                Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (period_code == PERIOD_CODE.PERIOD_H4) {
            try {
                Thread.sleep(10800000 + 3000000);//3h50 minut
            } catch (InterruptedException ex) {
                Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }


    
    
}
