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

        XtbApi xtbApi = new XtbApi();
        
        String arraySymbol[] = {"KGH.PL_9","06N.PL","CDR.PL_9","USDJPY","OIL"};
        
        String symbol = arraySymbol[3];
        
        String commend = null;

        double averageOutput = 0;

        Scanner in = new Scanner(System.in);

        NetworkN networkMaster = null;
        NetworkN[] networkSlave = new NetworkN[5];

        System.out.println("get-Pobierz swieczki");
        System.out.println("lern-Wyszkol siec");
        System.out.println("insert [data]-insert " + input + " input");
        System.out.println("exit");
        System.out.print(">>");
        


        if (args.length < 1) {
            //String[] execut = in.nextLine().split(" ");
            String[] execut = {"get","lern"};
            args = new String[execut.length];
            
            args = execut;
        }
        
        for (int i = 0; i < args.length; i++) {
            commend = args[i];
            switch (commend) {
                case "get" -> {
                    xtbApi.login();

                    Date dateRange = new Date(new TimeRange().getRange(new Date().getTime(), 0));
                    System.out.println("Download data: " + dateRange);
                    xtbApi.setMySymbol(symbol);
                    xtbApi.getSymbolData(PERIOD_CODE.PERIOD_H1, dateRange.getTime());
                    
                    //xtbApi.logout();
                }
                case "lern" -> {                   
                    for (int j = 0; j < networkSlave.length - 1; j++) {

                        System.out.println("Network: " + j);

                        averageOutput = 0;

                            networkSlave[j] = new NetworkN(input, 3 * input + 2, output);
                            networkSlave[j].setFileDataTrennig(symbol, NetworkType.TYPE_SLAVE[j]);
                            networkSlave[j].setLearningRate(0.001);
                            networkSlave[j].setMaxError(0.0001);
                            networkSlave[j].setMaxIteration(120000);
                            networkSlave[j].setMomentumChange(10);
                            networkSlave[j].setMaxMomentum(10);
                            networkSlave[j].getLernDataTimeSeries();
                            
                            try {                             
                                //n[j].lern(false);
                                networkSlave[j].lernThred();
                            } catch (Exception e) {
                                networkSlave[j].reset();
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
                    int interationNetworkMaster = 50; 
                    for (int j = 0; j < interationNetworkMaster; j++) {

                        System.out.println("Network master");
                        networkMaster = new NetworkN(input, 4 * input + 2, output);
                        networkMaster.setFileDataTrennig(symbol, NetworkType.TYPE_MASTER[0]);
                        networkMaster.setLearningRate(0.001);
                        networkMaster.setMaxError(0.001);
                        networkMaster.setMaxIteration(120000);
                        networkMaster.setMomentumChange(1);
                        networkMaster.setMaxMomentum(1);
                        networkMaster.getLernDataSegmen();
                        try {

                            networkMaster.lern(false);
                        } catch (Exception e) {
                            networkMaster.reset();
                        }

                        double out = networkMaster.inputScaner(new double[]{
                            networkSlave[0].inputScaner(networkSlave[0].getLastSymbol(), 0),
                            networkSlave[1].inputScaner(networkSlave[1].getLastSymbol(), 0),
                            networkSlave[2].inputScaner(networkSlave[2].getLastSymbol(), 0),
                            networkSlave[3].inputScaner(networkSlave[3].getLastSymbol(), 0),}, 0);
                        averageOutput = averageOutput + out;

                    }
                    averageOutput=(averageOutput/interationNetworkMaster)/10;
                    xtbApi.TradeTransaction(averageOutput);
                    xtbApi.logout();
                    
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
                }
                NXtb x = new NXtb();
                NXtb.main(args);
                    
                }
                case "insert" -> {
                    if (args.length - 1 > i && networkMaster != null) {
                        System.out.println("output: " + networkMaster.inputScaner(args[i + 1], 0));
                    } else {
                        System.out.println("Brak danych wejsciowych lub sieć nie wyszkolona");
                    }
                }
                case "exit" ->
                    System.exit(0);
                default -> {

                }
            }
        }
        System.exit(0);
    }
    

}
