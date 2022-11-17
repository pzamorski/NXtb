/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.nxtb;

import com.mycompany.nxtb.api.ArrayOrders;
import com.mycompany.nxtb.api.Order;
import com.mycompany.nxtb.neuron.NetworkN;
import com.mycompany.nxtb.neuron.NetworkType;
import com.mycompany.nxtb.tools.Memory;
import com.mycompany.nxtb.tools.TimeRange;
import com.mycompany.nxtb.api.XtbApi;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
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

        String arraySymbol[] = new Memory().loadString("symbol.txt");
        String symbol = null;
        boolean nextBuy = false;

        

        int inputSlaveNetwork = 20;
        int sizeOFNetworkSlave = 7;
        
        int inputMasterNetwork=sizeOFNetworkSlave;
        int outputMasterNetwork = 1;
        
        
        XtbApi xtbApi = new XtbApi();

        NetworkN networkMaster = null;
        NetworkN[] networkSlave = new NetworkN[sizeOFNetworkSlave];
        Thread[] thredLerniSlave = new Thread[sizeOFNetworkSlave];
        double averageOutput = 0;

        xtbApi.login();
        xtbApi.setMySymbol(symbol);
        Thread monitThread = xtbApi.StartMonitProfitInThred();

        ArrayOrders orders = new ArrayOrders();

        for (int i = 0; i < arraySymbol.length - 1; i++) {
            orders.add(new Order(arraySymbol[i]));

            //orders.get(i).toString();
        }
        xtbApi.insertParaOrders(orders);

        for (;;) {

            for (int i = 0; i < arraySymbol.length - 1; i++) {

                if (monitThread.isAlive()) {
                    System.out.println("Monit thred runing.");

                } else {
                    System.out.println("Reset monit thred");
                    monitThread = xtbApi.StartMonitProfitInThred();
                }

                if (args.length > 0) {
                    symbol = args[0];
                } else {
                    symbol = arraySymbol[i];
                    if (symbol == null) {

                        try {
                            Thread.sleep(120000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        break;
                    }
                }

                Date dateRange = new Date(new TimeRange().getRange(new Date().getTime(), 50));

                xtbApi.setMySymbol(symbol);
                System.out.print("Run " + symbol + " data: " + dateRange);
                xtbApi.getSymbolData(PERIOD_CODE.PERIOD_D1, dateRange.getTime());

                for (int j = 0; j < networkSlave.length; j++) {

                    System.out.print("Network->" + j + " ");

                    averageOutput = 0;

                    networkSlave[j] = new NetworkN(inputSlaveNetwork, 3 * inputSlaveNetwork + 2, outputMasterNetwork);
                    networkSlave[j].setFileDataTrennig(symbol, NetworkType.TYPE_SLAVE[j]);
                    networkSlave[j].setLearningRate(0.001);
                    networkSlave[j].setMaxError(0.0001);
                    networkSlave[j].setMaxIteration(120000);
                    networkSlave[j].setMomentumChange(10);
                    networkSlave[j].setMaxMomentum(10);
                    networkSlave[j].getLernDataTimeSeries();

                    try {

                        thredLerniSlave[j] = networkSlave[j].lernThred();
                    } catch (Exception e) {
                        networkSlave[j].reset();
                    }
                }
//-------------------------------------------------------------------------------
                //network master
                while ((thredLerniSlave[0].isAlive()
                        || thredLerniSlave[1].isAlive()
                        || thredLerniSlave[2].isAlive()
                        || thredLerniSlave[3].isAlive()
                        || thredLerniSlave[4].isAlive()
                        || thredLerniSlave[5].isAlive()
                        || thredLerniSlave[6].isAlive())) {

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                int interationNetworkMaster = 50;
                System.out.println("Network master");
                for (int j = 0; j < interationNetworkMaster; j++) {

                    networkMaster = new NetworkN(inputMasterNetwork, 4 * inputMasterNetwork + 2, outputMasterNetwork);
                    networkMaster.setFileDataTrennig(symbol, NetworkType.TYPE_MASTER[0]);
                    networkMaster.setLearningRate(0.001);
                    networkMaster.setMaxError(0.001);
                    networkMaster.setMaxIteration(120000);
                    networkMaster.setMomentumChange(1);
                    networkMaster.setMaxMomentum(1);
                    networkMaster.getLernDataSegmen();
                    try {

                        networkMaster.lern();
                    } catch (Exception e) {
                        networkMaster.reset();
                    }

                    double[] buildDataToInputMasterScaner = new double[networkSlave.length];
                    for (int k = 0; k < networkSlave.length; k++) {
                        buildDataToInputMasterScaner[k] = networkSlave[k].inputScaner(networkSlave[k].getLastSymbol(), 0);
                    }
                    
                    double out = networkMaster.inputScaner(buildDataToInputMasterScaner, 0);

                    averageOutput = averageOutput + out;

                }
                averageOutput = (averageOutput / interationNetworkMaster);
                xtbApi.TradeTransaction(averageOutput);

                System.out.println("");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(NXtb.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }

}
