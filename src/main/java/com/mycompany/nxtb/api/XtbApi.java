/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import netscape.javascript.JSObject;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.command.ChartLastCommand;
import pro.xstore.api.message.command.TradesCommand;
import pro.xstore.api.message.command.TradesHistoryCommand;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.ChartLastInfoRecord;
import pro.xstore.api.message.records.ChartRangeInfoRecord;
import pro.xstore.api.message.records.RateInfoRecord;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.message.response.CalendarResponse;
import pro.xstore.api.message.response.ChartResponse;
import pro.xstore.api.message.response.CurrentUserDataResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.message.response.ServerTimeResponse;
import pro.xstore.api.message.response.SymbolResponse;
import pro.xstore.api.message.response.TradeTransactionResponse;
import pro.xstore.api.message.response.TradesHistoryResponse;
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
    private static final String DATA = ".txt";

    private final long id = 13983586;
    private final String password = "i8V.@*%R3RPr461y";
    private final SyncAPIConnector connector;
    private LoginResponse loginResponse;
    private String lastSymbol;
    private String mySymbol = null;
    private String pathSaveBuilder = null;
    private String separator = ",";

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

    public void setMySymbol(String mySymbol) {
        this.mySymbol = mySymbol;
    }

    public String buildPathFile(String type) {
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
                case DATA:
                    returnPathFile.append(type);
                    break;
            }
        }
        return returnPathFile.toString();
    }

    public void getSymbolData(PERIOD_CODE period_code, long time) {

        try {

            if (checkIsLogin() && checkConditionSymbol()) {
                System.out.print("...");

                FileWriter myWriterO = null,
                        myWriterC = null,
                        myWriterH = null,
                        myWriterL = null,
                        myWriterV = null,
                        myWriter = null;

                ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, mySymbol, period_code, time);
                List<RateInfoRecord> rateInfoRecord = chartLastCommand.getRateInfos();
                if (!rateInfoRecord.isEmpty()) {

                    File dataDir = new File("data/" + mySymbol);
                    if (!dataDir.exists()) {
                        dataDir.mkdirs();
                    }

                    myWriterO = new FileWriter(buildPathFile(DATA_O));
                    myWriterC = new FileWriter(buildPathFile(DATA_C));
                    myWriterH = new FileWriter(buildPathFile(DATA_H));
                    myWriterL = new FileWriter(buildPathFile(DATA_L));
                    myWriterV = new FileWriter(buildPathFile(DATA_V));
                    myWriter = new FileWriter(buildPathFile(DATA));

                    System.out.print(mySymbol + " - zapis ");

                    System.out.print("...");

                    for (int i = 0; i < rateInfoRecord.size() - 1; i++) {

                        Candle candle = new Candle(rateInfoRecord.get(i));
                        Candle candleNext = new Candle(rateInfoRecord.get(i + 1));

                        try {
                            myWriterO.write(candle.getOpenString() + separator);//dla ciagow czasowych
                            myWriterC.write(candle.getCloseString() + separator);
                            myWriterH.write(candle.getHighString() + separator);
                            myWriterL.write(candle.getLowString() + separator);
                            myWriterV.write(candle.getVolString() + separator);

                            myWriter.write(candle.getOpenString() + separator
                                    + candle.getCloseString() + separator
                                    + candle.getHighString() + separator
                                    + candle.getLowString() + separator
                                    + candleNext.getCloseString() + separator
                                    + System.lineSeparator());

                        } catch (IOException e) {
                            System.out.println("error");
                            e.printStackTrace();
                        }

                    }
                    myWriterO.close();
                    myWriterC.close();
                    myWriterH.close();
                    myWriterL.close();
                    myWriterV.close();
                    myWriter.close();
                    System.out.println(" OK" + "[" + rateInfoRecord.size() + "]");
                }

            } else {
                System.err.println("Error: user couldn't log in!");
            }
        } catch (UnknownHostException e) {
        } catch (IOException | APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
        }
    }

    public void getSymbolDataWithFindSymbol(PERIOD_CODE period_code, long time) {

        try {

            if (checkIsLogin() && checkConditionSymbol()) {
                AllSymbolsResponse availableSymbols = APICommandFactory.executeAllSymbolsCommand(connector);
                System.out.print("...");

                FileWriter myWriterO = null,
                        myWriterC = null,
                        myWriterH = null,
                        myWriterL = null,
                        myWriterV = null,
                        myWriter = null;

                for (SymbolRecord symbol : availableSymbols.getSymbolRecords()) {
                    ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, symbol.getSymbol(), period_code, time);
                    List<RateInfoRecord> rateInfoRecord = chartLastCommand.getRateInfos();
                    if (!rateInfoRecord.isEmpty() && symbol.getSymbol().contains(mySymbol)) {

                        File dataDir = new File("data/" + mySymbol);
                        if (!dataDir.exists()) {
                            dataDir.mkdirs();
                        }

                        myWriterO = new FileWriter(buildPathFile(DATA_O));
                        myWriterC = new FileWriter(buildPathFile(DATA_C));
                        myWriterH = new FileWriter(buildPathFile(DATA_H));
                        myWriterL = new FileWriter(buildPathFile(DATA_L));
                        myWriterV = new FileWriter(buildPathFile(DATA_V));
                        myWriter = new FileWriter(buildPathFile(DATA));

                        System.out.print(symbol.getSymbol() + " - zapis ");

                        System.out.print("...");

                        for (int i = 0; i < rateInfoRecord.size() - 1; i++) {

                            Candle candle = new Candle(rateInfoRecord.get(i));
                            Candle candleNext = new Candle(rateInfoRecord.get(i + 1));

                            try {
                                myWriterO.write(candle.getOpenString() + separator);//dla ciagow czasowych
                                myWriterC.write(candle.getCloseString() + separator);
                                myWriterH.write(candle.getHighString() + separator);
                                myWriterL.write(candle.getLowString() + separator);
                                myWriterV.write(candle.getVolString() + separator);

                                myWriter.write(candle.getOpenString() + separator
                                        + //wejsca
                                        candle.getCloseString() + separator
                                        + candle.getHighString() + separator
                                        + candle.getLowString() + separator
                                        + candleNext.getCloseString() + separator
                                        +//wyjscie
                                        System.lineSeparator());

                            } catch (IOException e) {
                                System.out.println("error");
                                e.printStackTrace();
                            }

                        }

                        myWriterO.close();
                        myWriterC.close();
                        myWriterH.close();
                        myWriterL.close();
                        myWriterV.close();
                        myWriter.close();
                        System.out.println(" OK" + "[" + rateInfoRecord.size() + "]");
                    }
                }
            } else {
                System.err.println("Error: user couldn't log in!");
            }
        } catch (UnknownHostException e) {
        } catch (IOException | APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
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

    public void TradeTransaction(double priceFromNetwork) {

        int limitZleceń = 10;
        double tolerance = 4;
        TradeTransInfoRecord ttOpenInfoRecord = null;

        

        SymbolRecord symbolRecord = getSymbolRecord(mySymbol);
        double actualPrice = symbolRecord.getAsk();
        double priceCondition = priceFromNetwork - actualPrice;

        System.out.println("Przewidywana cena: " + priceFromNetwork + " Aktualna cena: " + actualPrice);

        if (Math.abs(priceCondition) > tolerance) {
            if (priceFromNetwork > actualPrice) {
                ttOpenInfoRecord = CreateTradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.OPEN);
            }
//            if (priceFromNetwork < actualPrice) {
//                ttOpenInfoRecord = CreateTradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN);
//            }
        } else {
            System.out.println("Tolerancja przekroczona");
        }

        if (ttOpenInfoRecord != null) {

            try {
                if (loginResponse.getStatus() == true) {
                    this.StartMonitProfitInThred();
                    //set take profit
                    //ttOpenInfoRecord.setTp(actualPrice + 0.5);

                    TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, Boolean.TRUE);
                    List<TradeRecord> listTradesResponse = tradesResponse.getTradeRecords();
                    TradeTransactionResponse tradeTransactionResponse;

                    int iloscZlecenDlaSymbolu = 0;
                    for (int i = 0; i < listTradesResponse.size(); i++) {
                        TradeRecord get = listTradesResponse.get(i);
                        if (get.getSymbol().equals(mySymbol)) {
                            iloscZlecenDlaSymbolu++;
                        }
                    }
                    if (iloscZlecenDlaSymbolu <= limitZleceń) {//nie wiecej jak limitZleceń 
                        if (ttOpenInfoRecord.getCmd() == TRADE_OPERATION_CODE.BUY) {
                            System.out.println("Buying " + mySymbol);
                        }
//                        if (ttOpenInfoRecord.getCmd() == TRADE_OPERATION_CODE.SELL) {
//                            System.out.println("Selling " + mySymbol);
//                        }

                        tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);

                        System.out.println("response: " + tradeTransactionResponse.toString());

                    } else {
                        System.err.println("Maksimum zlecń dla " + mySymbol);
                    }
                }

            } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
            }
        } else {
            System.out.println("Brak transakcji");
        }

    }

    public void StartMonitProfit() {

        while (true) {

            TradeTransInfoRecord ttOpenInfoRecord = CreateTradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.CLOSE);
            boolean loginStatus = loginResponse.getStatus();
            try {
                if (loginStatus) {

                    TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, Boolean.TRUE);
                    List<TradeRecord> listTradesResponse = tradesResponse.getTradeRecords();
                    TradeTransactionResponse tradeTransactionResponse;

                    for (int i = 0; i < listTradesResponse.size(); i++) {
                        TradeRecord record = listTradesResponse.get(i);
                        if (record.getProfit() > 0.1) {
                            ttOpenInfoRecord.setOrder(record.getOrder());
                            ttOpenInfoRecord.setVolume(record.getVolume());
                            tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);

                            System.out.println("response: " + tradeTransactionResponse.toString());
                        }

                    }

                }
            } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(XtbApi.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public Thread StartMonitProfitInThred() {
        Thread monitProfitInThred = new Thread(() -> {
            StartMonitProfit();

        });
        System.out.println("Start thred monit profit");
        monitProfitInThred.start();
        return monitProfitInThred;
    }

    private TradeTransInfoRecord CreateTradeTransInfoRecord(TRADE_OPERATION_CODE tradeOperattionCode, TRADE_TRANSACTION_TYPE trade_transaction_type) {

        SymbolRecord symbolRecord = getSymbolRecord(mySymbol);

        double sl = 0.0;
        double tp = 0.0;
        double volume = 0.05;
        long order = 0;
        String customComment = "NXtb";
        long expiration = 0;

        TradeTransInfoRecord ttOpenInfoRecord = new TradeTransInfoRecord(
                tradeOperattionCode,
                trade_transaction_type,
                symbolRecord.getAsk(),
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

}