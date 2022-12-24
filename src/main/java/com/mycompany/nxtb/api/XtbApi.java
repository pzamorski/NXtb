/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.api;

import com.mycompany.nxtb.neuron.CreateModel;
import com.mycompany.nxtb.stockMarketTools.RSI;
import com.mycompany.nxtb.tools.Memory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.util.Precision;
import org.neuroph.core.NeuralNetwork;
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

public class XtbApi {

    private static final String DATA_O = "O.txt";
    private static final String DATA_C = "C.txt";
    private static final String DATA_H = "H.txt";
    private static final String DATA_L = "L.txt";
    private static final String DATA_V = "V.txt";
    private static final String DATA_POC = "POC.txt";
    private static final String DATA_PHL = "PHL.txt";
    private static final String DATA_RSI = "RSI.txt";
    private static final String DATA = ".txt";

    private static final String DATA_CHART = ".csv";

    private final long id = 14106453;
    private final String password = "Mojehaslo2";
    private final String separator = ",";

    private final SyncAPIConnector connector;
    private LoginResponse loginResponse;
    private String mySymbol = null;

    double oldPrice = 0;
    double oldPOC = 0;
    double outNetworkZero = 0;
    double outNetworkOne = 1;

    ArrayOrders orders = new ArrayOrders();

    public XtbApi() throws IOException {
        connector = new SyncAPIConnector(ServerEnum.DEMO);

    }

    public boolean login() throws APICommandConstructionException, APICommunicationException, APIReplyParseException, APIErrorResponse, IOException {
        if (checkIsLogin() == false) {
            Credentials credentials = new Credentials(id, password);
            loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
        }
        return checkIsLogin();
    }

    public void logout() throws APICommunicationException {
        connector.close();
        System.out.println("Logout");
    }

    public void setMySymbol(String mySymbol) {
        this.mySymbol = mySymbol;
    }

    public void getCandlesDataToFile(String symbol, PERIOD_CODE period_code, long time) throws Exception {

        setMySymbol(symbol);

        try {

            if (checkIsLogin() && checkConditionSymbol()) {
                System.out.print("...");

                FileWriter myWriter = null;

                ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, mySymbol, period_code, time);
                List<RateInfoRecord> rateInfoRecord = chartLastCommand.getRateInfos();
                if (!rateInfoRecord.isEmpty()) {

                    File dataDir = new File("data/" + mySymbol);
                    if (!dataDir.exists()) {
                        dataDir.mkdirs();
                    }
                    ;
                    myWriter = new FileWriter(buildPathFile(DATA));

                    System.out.print(mySymbol + " - zapis ... ");

                    int sizeDownload = 0;

                    String outCandle = null;
                    for (int i = rateInfoRecord.size() - 100; i < rateInfoRecord.size() - 20; i++) {

                        Candle candle = new Candle(rateInfoRecord.get(i));
                        Candle candleNext1 = new Candle(rateInfoRecord.get(i + 1));
                        Candle candleNext2 = new Candle(rateInfoRecord.get(i + 2));
                        Candle candleNext3 = new Candle(rateInfoRecord.get(i + 3));
                        Candle candleNext4 = new Candle(rateInfoRecord.get(i + 4));
                        Candle candleNext5 = new Candle(rateInfoRecord.get(i + 5));

                        if (candleNext5.getClose() > 0) {
                            outCandle = "1";
                        } else {
                            outCandle = "0";
                        }

//                        Candle candleNext6 = new Candle(rateInfoRecord.get(i + 6));
                        try {
                            myWriter.write(
                                    //                                    candle.getOpenString() + separator
                                    //                                    + candle.getCloseString() + separator
                                    //                                    + candle.getHighString() + separator
                                    //                                    + candle.getLowString() + separator
                                    //                                            //--------------------
                                    //                                    +candleNext1.getOpenString() + separator
                                    //                                    + candleNext1.getCloseString() + separator
                                    //                                    + candleNext1.getHighString() + separator
                                    //                                    + candleNext1.getLowString() + separator
                                    //                                            
                                    //                                    //                                    + candle.getVolString() + separator
                                    //                                    //                                    + candle.getPipsCoString() + separator
                                    //                                    //                                    + candle.getPipsHlString() + separator
                                    //                                    + candleNext2.getCloseString() + separator
                                    candle.getCloseString() + separator
                                    + candle.getHighString() + separator
                                    + candle.getLowString() + separator
                                    + candleNext1.getCloseString() + separator
                                    + candleNext1.getHighString() + separator
                                    + candleNext1.getLowString() + separator
                                    + candleNext2.getCloseString() + separator
                                    + candleNext2.getHighString() + separator
                                    + candleNext2.getLowString() + separator
                                    + candleNext3.getCloseString() + separator
                                    + candleNext3.getHighString() + separator
                                    + candleNext3.getLowString() + separator
                                    + candleNext4.getCloseString() + separator
                                    + candleNext4.getHighString() + separator
                                    + candleNext4.getLowString() + separator
                                    //+ String.valueOf(new RSI().calculate(rateInfoRecord, i)) + separator
                                    + outCandle + separator
                                    + System.lineSeparator());

                        } catch (IOException e) {
                            System.out.println("error");
                            e.printStackTrace();
                        }

                    }
                    myWriter.close();
                    System.out.println(" OK" + "[" + (rateInfoRecord.size() - sizeDownload) + "]");

                    try {
                    } catch (Exception ex) {
                        Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else {
                System.err.println("Error: user couldn't log in!");
            }

        } catch (UnknownHostException e) {
        } catch (IOException | APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
        }
    }

    public void createFile(String symbol, int sizeDownload, int offset, PERIOD_CODE period_code, long time, int input, int output) {

        //CreateModel cm = new CreateModel(symbol, input, output);
        File dataDir = new File("data/" + mySymbol);
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
        setMySymbol(symbol);
        int outSaveOK = 0;


        
        long currentTime=new Date().getTime();
        long timeStart=new Date(currentTime - time).getTime();
long difTime=new Date(time).getTime();

        

        for (int m = 0; outSaveOK <= sizeDownload || m < 100; m++) {
             
            
            
            
            try {

                if (checkIsLogin()) {

                    //ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, mySymbol, period_code, time);
                   
                    ChartResponse chartResponse = APICommandFactory.executeChartRangeCommand(connector, symbol, period_code, timeStart, currentTime, id);
                    List<RateInfoRecord> rateInfoRecord = chartResponse.getRateInfos();

                        
                    if (!rateInfoRecord.isEmpty()) {
                        
                        
                        
                        System.out.print("...");


                        int numberOfInputCandle = input / 3;
                        int numberOfOutputCandle = 1;
                        String outCandle = null;

                        for (int i = 0; i < rateInfoRecord.size() - offset; i = i + offset) {

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
                                + new Date(timeStart) + " " + new Date(currentTime)
                        );
                         currentTime=new Date(currentTime-((currentTime-difTime)/10)).getTime();
                         timeStart=new Date(timeStart-(difTime/10)).getTime();

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

            } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
            }

            try {
                Thread.sleep(3000);
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

    public SymbolRecord getSymbolRecord(String mySymbol) {
        SymbolRecord symbolRecord = new SymbolRecord();
        try {

            if (checkIsLogin() && checkConditionSymbol()) {
                SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, mySymbol);
                symbolRecord = symbolResponse.getSymbol();

            } else {
                System.err.println("Error: user couldn't log in!");
            }

        } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
        }
        return symbolRecord;
    }

    public void TradeTransactionV3(double POC, String coment) throws IOException {

        Order order = orders.get(mySymbol);

        double tolerance = order.getTolerance();
        order.setCurrentPOC(POC);

        TradeTransInfoRecord ttOpenInfoRecord = null;
        SymbolRecord symbolRecord = getSymbolRecord(mySymbol);

        double actualPrice = symbolRecord.getBid();
        double priceFromNetwork = POC + symbolRecord.getBid();

        double priceCondition = (priceFromNetwork) - actualPrice;

        new Memory().saveCSV(buildPathFile(DATA_CHART), POC + actualPrice, actualPrice, coment);

        System.out.println("[" + mySymbol + "] OUT: " + POC + " Aktualna cena: " + actualPrice);
        //if (Math.abs(priceCondition) > tolerance) {
        if (true) {
            if (POC > 0.7) {
                ttOpenInfoRecord = CreateTradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.OPEN, order.getVolume());
            }
            if (POC < 0.4) {
                ttOpenInfoRecord = CreateTradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN, order.getVolume());
            }
        } else {
            System.out.println("Tolerancja przekroczona");
        }

        if (ttOpenInfoRecord != null) {

            try {
                if (loginResponse.getStatus() == true) {

                    TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, Boolean.TRUE);
                    List<TradeRecord> listTradesResponse = tradesResponse.getTradeRecords();
                    TradeTransactionResponse tradeTransactionResponse;

                    int iloscZlecenDlaSymbolu = 0;
                    for (int i = 0; i < listTradesResponse.size(); i++) {//Sprawdza ilosc aktualnych zlecen
                        TradeRecord tradeRecord = listTradesResponse.get(i);

                        System.out.println(tradeRecord.getVolume());
                        if (tradeRecord.getSymbol().equals(mySymbol)) {
                            iloscZlecenDlaSymbolu++;
                        }
                    }

                    if (iloscZlecenDlaSymbolu <= order.geLimitOrders()) {//nie wiecej jak limitZleceń 

                        if (ttOpenInfoRecord.getCmd() == TRADE_OPERATION_CODE.BUY) {
                            System.out.print("Buying " + mySymbol + " ");
                            order.setOpenPozytionPOC(1);

                        }
                        if (ttOpenInfoRecord.getCmd() == TRADE_OPERATION_CODE.SELL) {
                            System.out.print("Selling " + mySymbol + " ");
                            order.setOpenPozytionPOC(0);
                        }

                        tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);//otworz pozycje

                        System.out.println("[" + tradeTransactionResponse.getOrder() + "]");

                    } else {
                        System.err.println("Maksimum zlecń dla " + mySymbol);
                    }
                }

            } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
            }
        } else {
        }

    }

    public void StartMonitProfitV3() {

        while (true) {
            //System.out.println("Start thred monit profit");
            Order order = orders.get(mySymbol);

            //if(order.getOpenPozytionPOC()==0){order.setOpenPozytionPOC(order.getCurrentPOC());}
            TradeTransInfoRecord ttOpenInfoRecord = CreateTradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.CLOSE);
            boolean loginStatus = loginResponse.getStatus();
            try {
                TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, Boolean.TRUE);

                if (tradesResponse != null) {
                    if (loginStatus && tradesResponse.getStatus()) {

                        List<TradeRecord> listTradesResponse = tradesResponse.getTradeRecords();
                        TradeTransactionResponse tradeTransactionResponse;

                        for (int i = 0; i < listTradesResponse.size(); i++) {
                            TradeRecord record = listTradesResponse.get(i);

                            // if ( orders.get(mySymbol).getProfit()) {
                            if (((order.getOpenPozytionPOC() == 0 && order.getCurrentPOC() > 0.5) || (order.getOpenPozytionPOC() == 1 && order.getCurrentPOC() < 0.9)) && record.getProfit() > 0) {

                                ttOpenInfoRecord.setOrder(record.getOrder());
                                ttOpenInfoRecord.setVolume(record.getVolume());

                                for (int j = 0; j < 10; j++) {
                                    tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);

                                    System.out.println("");
                                    System.out.println("Nazwa: " + ttOpenInfoRecord.getSymbol() + " Status: " + tradeTransactionResponse.getStatus() + " Profit: " + order.getProfit());

                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    if (tradeTransactionResponse != null) {
                                        if (tradeTransactionResponse.getStatus() == true) {
                                            break;
                                        }
                                    }
                                }

//                        if (tradeTransactionResponse.getStatus()) {
//                            System.out.println("response: " + tradeTransactionResponse.toString());
//                        }
                            }

                        }

                    }
                }
            } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public Thread StartMonitProfitInThred() {
        Thread monitProfitInThred = new Thread(() -> {
            StartMonitProfitV3();

        });

        monitProfitInThred.start();
        return monitProfitInThred;
    }

    public long getServerTime() {
        ServerTimeResponse serverTime = null;
        try {
            serverTime = APICommandFactory.executeServerTimeCommand(connector);
        } catch (APICommandConstructionException ex) {
            Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (APICommunicationException ex) {
            Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (APIReplyParseException ex) {
            Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (APIErrorResponse ex) {
            Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return serverTime.getTime();
    }

    public void insertParaOrders(ArrayOrders orders) {
        this.orders = orders;
    }

    public double[] getLastCandles(PERIOD_CODE period_code, long time, int input) throws Exception {

        double[] returnDataToInputNeuron = new double[input];
        int numberOFCandle = input / 3;
        int setOneBackCandle = numberOFCandle;
        try {

            if (checkIsLogin() && checkConditionSymbol()) {
                ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, mySymbol, period_code, time);
                List<RateInfoRecord> rateInfoRecord = chartLastCommand.getRateInfos();
                Candle candle;
                if (!rateInfoRecord.isEmpty()) {

                    for (int i = 0; i < returnDataToInputNeuron.length; i = i + 3) {
                        candle = new Candle(rateInfoRecord.get(rateInfoRecord.size() - setOneBackCandle));
                        returnDataToInputNeuron[i] = candle.getClose();
                        returnDataToInputNeuron[i + 1] = candle.getHigh();
                        returnDataToInputNeuron[i + 2] = candle.getLow();

//                        returnDataToInputNeuron[i] = Precision.round(candle.getClose() * 100, 1);
//                        returnDataToInputNeuron[i + 1] = Precision.round(candle.getHigh() * 100, 1);
//                        returnDataToInputNeuron[i + 2] = Precision.round(candle.getLow() * 100, 1);
                        setOneBackCandle--;

                    }

                }

            } else {
                System.err.println("Error: user couldn't log in!");
            }

        } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
        }

        return returnDataToInputNeuron;
    }

    public double[] getLastCandlesWithCurrentCandle(PERIOD_CODE period_code, long time) throws Exception {

        double[] returnDataToInputNeuron = new double[15];

        try {

            if (checkIsLogin() && checkConditionSymbol()) {
                ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, mySymbol, period_code, time);
                List<RateInfoRecord> rateInfoRecord = chartLastCommand.getRateInfos();
                Candle candle;
                if (!rateInfoRecord.isEmpty()) {

                    candle = new Candle(rateInfoRecord.get(rateInfoRecord.size() - 5));
                    returnDataToInputNeuron[0] = candle.getClose();
                    returnDataToInputNeuron[1] = candle.getHigh();
                    returnDataToInputNeuron[2] = candle.getLow();

                    candle = new Candle(rateInfoRecord.get(rateInfoRecord.size() - 4));
                    returnDataToInputNeuron[3] = candle.getClose();
                    returnDataToInputNeuron[4] = candle.getHigh();
                    returnDataToInputNeuron[5] = candle.getLow();

                    candle = new Candle(rateInfoRecord.get(rateInfoRecord.size() - 3));
                    returnDataToInputNeuron[6] = candle.getClose();
                    returnDataToInputNeuron[7] = candle.getHigh();
                    returnDataToInputNeuron[8] = candle.getLow();

                    candle = new Candle(rateInfoRecord.get(rateInfoRecord.size() - 2));
                    returnDataToInputNeuron[9] = candle.getClose();
                    returnDataToInputNeuron[10] = candle.getHigh();
                    returnDataToInputNeuron[11] = candle.getLow();

                    candle = new Candle(rateInfoRecord.get(rateInfoRecord.size() - 1));
                    returnDataToInputNeuron[12] = candle.getClose();
                    returnDataToInputNeuron[13] = candle.getHigh();
                    returnDataToInputNeuron[14] = candle.getLow();

                    //returnDataToInputNeuron[15] = new RSI().calculate(rateInfoRecord, rateInfoRecord.size() - 1);
                    System.out.println(" OK" + "[" + rateInfoRecord.size() + "]");
                }

            } else {
                System.err.println("Error: user couldn't log in!");
            }

        } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
        }

        return returnDataToInputNeuron;
    }

    private String buildPathFile(String type) {
        StringBuilder returnPathFile = null;
        if (checkConditionSymbol()) {
            returnPathFile = new StringBuilder("data/" + mySymbol + "/" + mySymbol);
            switch (type) {
                case DATA_O:
                    returnPathFile.append(type);
                    break;
                case DATA_C:
                    returnPathFile.append(type);
                    break;
                case DATA_H:
                    returnPathFile.append(type);
                    break;
                case DATA_L:
                    returnPathFile.append(type);
                    break;
                case DATA_V:
                    returnPathFile.append(type);
                    break;
                case DATA_POC:
                    returnPathFile.append(type);
                    break;
                case DATA_PHL:
                    returnPathFile.append(type);
                    break;
                case DATA_RSI:
                    returnPathFile.append(type);
                    break;
                case DATA:
                    returnPathFile.append(type);
                    break;
                case DATA_CHART:
                    returnPathFile = new StringBuilder("data/csv/" + mySymbol);
                    returnPathFile.append(type);
                    break;
            }
        }
        return returnPathFile.toString();
    }

    private TradeTransInfoRecord CreateTradeTransInfoRecord(TRADE_OPERATION_CODE tradeOperattionCode, TRADE_TRANSACTION_TYPE trade_transaction_type) {

        SymbolRecord symbolRecord = getSymbolRecord(mySymbol);

        double sl = 0.0;
        double tp = 0.0;
        long order = 0;
        String customComment = "NXtb";
        long expiration = 0;

        TradeTransInfoRecord ttOpenInfoRecord = new TradeTransInfoRecord(
                tradeOperattionCode,
                trade_transaction_type,
                symbolRecord.getBid(),
                sl,
                tp,
                symbolRecord.getSymbol(),
                symbolRecord.getLotMin(),
                order,
                customComment,
                expiration
        );

        return ttOpenInfoRecord;

    }

    private TradeTransInfoRecord CreateTradeTransInfoRecord(TRADE_OPERATION_CODE tradeOperattionCode, TRADE_TRANSACTION_TYPE trade_transaction_type, double volume) {

        SymbolRecord symbolRecord = getSymbolRecord(mySymbol);

        double sl = 0.0;
        double tp = 0.0;
        long order = 0;
        String customComment = "NXtb";
        long expiration = 0;

        TradeTransInfoRecord ttOpenInfoRecord = new TradeTransInfoRecord(
                tradeOperattionCode,
                trade_transaction_type,
                symbolRecord.getBid(),
                sl,
                tp,
                symbolRecord.getSymbol(),
                volume,
                order,
                customComment,
                expiration
        );

        return ttOpenInfoRecord;

    }

    private boolean checkIsLogin() {
        boolean loginResponseStatus = false;
        if (loginResponse != null) {
            loginResponseStatus = loginResponse.getStatus();
        }
        return loginResponseStatus;
    }

    private boolean checkConditionSymbol() {
        return mySymbol != null;
    }

}
