/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.nxtb;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static void main(String[] args) throws IOException, APICommandConstructionException, APICommunicationException, APIReplyParseException, APIErrorResponse {

        int input = 4;
        int output = 1;
        int lernIteration = 100;

        String arraySymbol[] = {"KGH.PL_9","06N.PL","CDR.PL_9"};
        
        String symbol = arraySymbol[2];
        
        String commend = null;

        double averageOutput = 0;

        Scanner in = new Scanner(System.in);

        NetworkN networkMaster = null;
        NetworkN[] n = new NetworkN[5];

        System.out.println("get-Pobierz swieczki");
        System.out.println("lern-Wyszkol siec");
        System.out.println("insert [data]-insert " + input + " input");
        System.out.println("exit");
        System.out.print(">>");
        


        if (args.length < 1) {
            //String[] execut = in.nextLine().split(" ");
            String[] execut = {"lern"};
            args = new String[execut.length];
            
            args = execut;
        }
        
        for (int i = 0; i < args.length; i++) {
            commend = args[i];
            switch (commend) {
                case "get" -> {
                    XtbApi xtbApi = new XtbApi();
                    xtbApi.login();

                    Date dateRange = new Date(new TimeRange().getRange(new Date().getTime(), 3));
                    System.out.println("Download data: " + dateRange);
                    xtbApi.getSymbolData(symbol, PERIOD_CODE.PERIOD_H1, dateRange.getTime());
                    //xtbApi.getCandlesOfTime(symbol, PERIOD_CODE.PERIOD_H1, dateRange.getTime());
                    xtbApi.logout();
                }
                case "lern" -> {
                    
                    
                    for (int j = 0; j < n.length - 1; j++) {

                        System.out.println("Network: " + j);

                        averageOutput = 0;
                        
                        
                       
                            n[j] = new NetworkN(input, 3 * input + 2, output);
                            n[j].setFileDataTrennig(symbol, NetworkType.TYPE_SLAVE[j]);
                            n[j].setLearningRate(0.001);
                            n[j].setMaxError(0.00001);
                            n[j].setMaxIteration(120000);
                            n[j].setMomentumChange(10);
                            n[j].setMaxMomentum(10);
                            n[j].getLernDataTimeSeries();
                            
                            try {
                                
                                //n[j].lern(false);
                                n[j].lernThred();
                            } catch (Exception e) {
                                n[j].reset();
                            }
                    }
//-------------------------------------------------------------------------------
                    //network master
                        
                    while(Thread.activeCount()!=1){
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    int interationNetworkMaster = 10; 
                    for (int j = 0; j < interationNetworkMaster; j++) {

                        System.out.println("Network master");
                        networkMaster = new NetworkN(input, 4 * input + 2, output);
                        networkMaster.setFileDataTrennig(symbol, NetworkType.TYPE_MASTER[0]);
                        networkMaster.setLearningRate(0.001);
                        networkMaster.setMaxError(0.001);
                        networkMaster.setMaxIteration(120000);
                        networkMaster.setMomentumChange(10);
                        networkMaster.setMaxMomentum(10);
                        networkMaster.getLernDataSegmen();
                        try {

                            networkMaster.lern(false);
                        } catch (Exception e) {
                            networkMaster.reset();
                        }

                        double out = networkMaster.inputScaner(new double[]{
                            n[0].inputScaner(n[0].getLastSymbol(), 0),
                            n[1].inputScaner(n[1].getLastSymbol(), 0),
                            n[2].inputScaner(n[2].getLastSymbol(), 0),
                            n[3].inputScaner(n[3].getLastSymbol(), 0),}, 0);
                        averageOutput = averageOutput + out;

                    }
                    System.out.println(symbol+ " price: " + averageOutput / interationNetworkMaster);
                }
                case "insert" -> {
                    if (args.length - 1 > i && networkMaster != null) {
                        System.out.println("output: " + networkMaster.inputScaner(args[i + 1], 0));
                    } else {
                        System.out.println("Brak danych wejsciowych lub sieć nie wyszkolona");
                    }
                }
                case "insert2" -> {
                    double out = networkMaster.inputScaner(new double[]{
                        n[0].inputScaner("91.1,91.46,90.74,90.28", 0),
                        n[1].inputScaner("91.46,90.74,90.26,90.52", 0),
                        n[2].inputScaner("91.7,92.3,90.86,90.94", 0),
                        n[3].inputScaner("90.06,90.52,89.96,90.1", 0),}, 0);
                    System.out.println(symbol+ " price: " + out);
                }
                case "exit" ->
                    System.exit(0);
                case "conwert" -> {
                }
                default -> {

                }
            }
        }
        System.exit(0);
    }
    

}
