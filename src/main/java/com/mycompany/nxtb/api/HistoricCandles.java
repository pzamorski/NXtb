/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.api;

import com.mycompany.nxtb.neuron.CreateModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.RateInfoRecord;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.message.response.ChartResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.message.response.ServerTimeResponse;
import pro.xstore.api.message.response.SymbolResponse;
import pro.xstore.api.message.response.TradeTransactionResponse;
import pro.xstore.api.message.response.TradesResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.ServerData.ServerEnum;
import pro.xstore.api.sync.SyncAPIConnector;

/**
 *
 * @author Patryk
 */
public class HistoricCandles {

    private final long id = 14106453;
    private final String password = "Mojehaslo2";
    private final String separator = ",";
    
    private int input,output;

    private SyncAPIConnector connector;
    private LoginResponse loginResponse;

    public void get(String symbol, int sizeDownload, int offset, PERIOD_CODE period_code, CreateModel cm) throws IOException, APICommandConstructionException, APICommunicationException, APIReplyParseException, APIErrorResponse {

        
        input=cm.getInput();
        output=cm.getOutput();
        
        //CreateModel cm = new CreateModel(symbol, input, output);
        File dataDir = new File("data/" + symbol);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("data/" + symbol + "/" + symbol + "_" + period_code + ".csv");
        } catch (IOException ex) {
            Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        


        //NeuralNetwork neuralNetwork = new CreateModel().loadModel(symbol);
        int outSaveOK = 0;

        long timeStop = System.currentTimeMillis();
        long timeStart = System.currentTimeMillis()- TimeUnit.DAYS.toMillis(2);

        //long difTime=time;
        for (int m = 0; outSaveOK <= sizeDownload; m++) {
            System.out.print(m+1);
            connector = new SyncAPIConnector(ServerEnum.DEMO);
            Credentials credentials = new Credentials(id, password);
            loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
            if (m > 20) {
                break;
            }
            if (loginResponse.getStatus()) {

                //ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, mySymbol, period_code, time);
                ChartResponse chartResponse = APICommandFactory.executeChartRangeCommand(connector, symbol, period_code, timeStart, timeStop, id);
                List<RateInfoRecord> rateInfoRecord = chartResponse.getRateInfos();

                if (!rateInfoRecord.isEmpty()) {
                    
                    timeStop = timeStart;
                    timeStart = timeStart - TimeUnit.DAYS.toMillis(2);
                    

                    System.out.print("...");

                    int numberOfInputCandle = input / 3;
                    int numberOfOutputCandle = 1;
                    String outCandle = null;

                    int itemDownload = 0;
                    for (int i = 0; i < rateInfoRecord.size() - offset; i = i + offset) {
                        itemDownload++;
                        double[] dataIN = new double[input];
                        double[] dataOut = new double[output];

                        Candle[] candlesInput = new Candle[numberOfInputCandle];
                        Candle[] candlesOutput = new Candle[numberOfOutputCandle];

                        int j;
                        for (j = 0; j < candlesInput.length; j++) {
                            candlesInput[j] = new Candle(rateInfoRecord.get(i + j));
                        }

                        for (int k = j + 1, index = 0; index < candlesOutput.length; k++, index++) {
                            candlesOutput[index] = new Candle(rateInfoRecord.get(i + k - 1));
                        }

                        //Candle candleNext5 = new Candle(rateInfoRecord.get(i + 5));
                        for (int k = 0, l = 0; k < dataIN.length; k = k + 3) {
                            dataIN[k] = candlesInput[l].getClose();
                            dataIN[k + 1] = candlesInput[l].getHigh();
                            dataIN[k + 2] = candlesInput[l].getLow();
                            l++;
                        }

                        if (candlesOutput[0].getClose() > 0) {
                            dataOut[0] = 1;
                        }
                        if (candlesOutput[0].getClose() < 0) {
                            dataOut[0] = 0;
                        }
                        if (candlesOutput[0].getClose() == 0) {
                            dataOut[0] = 0.5;
                        }

                        //cm.addData(dataIN, dataOut);
                        String dataToFile = Arrays.toString(dataIN) + separator + Arrays.toString(dataOut) + System.lineSeparator();
                        dataToFile = dataToFile.replace("[", "").replace("]", "").replace(" ", "").replace(",", ";");
                        try {
                            myWriter.write(dataToFile);
                            outSaveOK++;
                        } catch (IOException ex) {
                            Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                    //cm.lern();
                    System.out.println(" Download size:" + "[" + outSaveOK + "]/[" + sizeDownload + "] "
                            + new Date(timeStart) + " " + new Date(timeStop)
                    );


//System.out.println(time);
                    try {
                    } catch (Exception ex) {
                        Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.err.println("Empty response.");
                }

            } else {
                System.err.println("Error: user couldn't log in!");
            }
            connector.close();
            //timeStop = timeStart;
            //timeStart = timeStart - time;//TimeUnit.DAYS.toMillis(2);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        try {
            myWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
