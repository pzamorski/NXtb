/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb;

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

    String mySymbol;

    private final long id = 13983586;
    private final String password = "i8V.@*%R3RPr46y1";
    private final SyncAPIConnector connector;
    private LoginResponse loginResponse;
    private String lastSymbol;
    private double[] actualPrice = new double[3];

    public boolean login() throws APICommandConstructionException, APICommunicationException, APIReplyParseException, APIErrorResponse, IOException {
        if (checkIsLogin() == false) {
            Credentials credentials = new Credentials(id, password);
            loginResponse = APICommandFactory.executeLoginCommand(
                    connector, // APIConnector
                    credentials // Credentials
            );
        }
        return checkIsLogin();
    }

    public boolean login(long id, String password) throws APICommandConstructionException, APICommunicationException, APIReplyParseException, APIErrorResponse, IOException {
        if (checkIsLogin() == false) {
            Credentials credentials = new Credentials(id, password);
            loginResponse = APICommandFactory.executeLoginCommand(
                    connector, // APIConnector
                    credentials // Credentials
            );
        }

        return checkIsLogin();
    }

    public void logout() throws APICommunicationException {
        connector.close();
        System.out.println("Połączenie zamkniete");

    }

    public boolean checkIsLogin() {
        boolean stuts = false;
        if (loginResponse != null) {
            if (loginResponse.getStatus() == true) {
                System.out.println("Zalogowany");
                stuts = true;
            }
        }
        return stuts;
    }

    public void setMySymbol(String mySymbol) {
        this.mySymbol = mySymbol;
    }

    public void getSymbolData(PERIOD_CODE period_code, long time) {

        try {

            if (loginResponse.getStatus() == true) {
                lastSymbol = mySymbol;

                AllSymbolsResponse availableSymbols = APICommandFactory.executeAllSymbolsCommand(connector);

                System.out.print("...");

                FileWriter myWriterO = null;
                FileWriter myWriterC = null;
                FileWriter myWriterH = null;
                FileWriter myWriterL = null;
                FileWriter myWriterV = null;
                FileWriter myWriter = null;

                FileWriter myWriterWal = null;
//                 List all available symbols on console
                String separator = ",";
                ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, mySymbol, period_code, time);
                List<RateInfoRecord> rateInfoRecord = chartLastCommand.getRateInfos();
                if (!rateInfoRecord.isEmpty()) {

                    File dataDir = new File("data/" + mySymbol);
                    if (!dataDir.exists()) {
                        dataDir.mkdirs();
                    }

                    myWriterO = new FileWriter("data/" + mySymbol + "/" + mySymbol + "O.txt");
                    myWriterC = new FileWriter("data/" + mySymbol + "/" + mySymbol + "C.txt");
                    myWriterH = new FileWriter("data/" + mySymbol + "/" + mySymbol + "H.txt");
                    myWriterL = new FileWriter("data/" + mySymbol + "/" + mySymbol + "L.txt");
                    myWriterV = new FileWriter("data/" + mySymbol + "/" + mySymbol + "V.txt");
                    myWriter = new FileWriter("data/" + mySymbol + "/" + mySymbol + ".txt");

                    myWriterWal = new FileWriter("data/" + mySymbol + "/" + mySymbol + "_Walidation.txt");
                    System.out.print(mySymbol + " - zapis ");

                    System.out.print("...");
                    int i = 0;

                    for (i = 0; i < rateInfoRecord.size() - 1; i++) {

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
                    System.out.print(" OK" + "[" + rateInfoRecord.size() + "]");
                    System.out.println("");

                }

            } else {

                // Print the error on console
                System.err.println("Error: user couldn't log in!");

            }

            // Close connection
            // Catch errors
        } catch (UnknownHostException e) {
        } catch (IOException | APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
        }

    }

    public void getSymbolDataWithFind(PERIOD_CODE period_code, long time) {

        try {

            if (loginResponse.getStatus() == true) {
                lastSymbol = mySymbol;

                AllSymbolsResponse availableSymbols = APICommandFactory.executeAllSymbolsCommand(connector);

                System.out.print("...");

                FileWriter myWriterO = null;
                FileWriter myWriterC = null;
                FileWriter myWriterH = null;
                FileWriter myWriterL = null;
                FileWriter myWriterV = null;
                FileWriter myWriter = null;

                FileWriter myWriterWal = null;
//                 List all available symbols on console
                String separator = ",";
                for (SymbolRecord symbol : availableSymbols.getSymbolRecords()) {
                    ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, symbol.getSymbol(), period_code, time);
                    List<RateInfoRecord> rateInfoRecord = chartLastCommand.getRateInfos();
                    if (!rateInfoRecord.isEmpty() && symbol.getSymbol().contains(mySymbol)) {
                        myWriterO = new FileWriter("data/" + mySymbol + "/" + mySymbol + "O.txt");
                        myWriterC = new FileWriter("data/" + mySymbol + "/" + mySymbol + "C.txt");
                        myWriterH = new FileWriter("data/" + mySymbol + "/" + mySymbol + "H.txt");
                        myWriterL = new FileWriter("data/" + mySymbol + "/" + mySymbol + "L.txt");
                        myWriterV = new FileWriter("data/" + mySymbol + "/" + mySymbol + "V.txt");
                        myWriter = new FileWriter("data/" + mySymbol + "/" + mySymbol + ".txt");

                        myWriterWal = new FileWriter("data/" + mySymbol + "/" + mySymbol + "_Walidation.txt");
                        System.out.print(symbol.getSymbol() + " - zapis ");

                        System.out.print("...");
                        int i = 0;

                        for (i = 0; i < rateInfoRecord.size() - 1; i++) {

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
                        System.out.print(" OK" + "[" + rateInfoRecord.size() + "]");
                        System.out.println("");
                        break;//słaby internet do usuniecia
                    }

                }

            } else {

                // Print the error on console
                System.err.println("Error: user couldn't log in!");

            }

            // Close connection
            // Catch errors
        } catch (UnknownHostException e) {
        } catch (IOException | APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
        }

    }

    public XtbApi() throws IOException {
        connector = new SyncAPIConnector(ServerEnum.DEMO);

    }

    public SymbolRecord getSymbolRecord(String mySymbol) {
        SymbolRecord symbolRecord = new SymbolRecord();
        try {

            if (loginResponse.getStatus() == true) {

                SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, mySymbol);
                symbolRecord = symbolResponse.getSymbol();
                // Print the message on console
                ;

            } else {

                // Print the error on console
                System.err.println("Error: user couldn't log in!");

            }

            // Catch errors
        } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
        }
        return symbolRecord;

    }

    public void TradeTransaction(double priceFromNetwork) {
        double tolerance = 1.25;
        SymbolRecord symbolRecord = getSymbolRecord(mySymbol);
        double actualPrice = symbolRecord.getAsk();
        double priceCondition = priceFromNetwork - actualPrice;

        TRADE_OPERATION_CODE tradeOperattionCode = null;
        TRADE_TRANSACTION_TYPE trade_transaction_type = null;
        System.out.println("Przyszła cena z sieci: " + priceFromNetwork + " Cena aktualna: " + actualPrice);

        if (Math.abs(priceCondition) > tolerance) {
            if (priceFromNetwork > actualPrice) {
                System.out.println("Buying " + mySymbol);
                tradeOperattionCode = new TRADE_OPERATION_CODE(0L);//buy
                trade_transaction_type = new TRADE_TRANSACTION_TYPE(0L);//open
            }
            if (priceFromNetwork < actualPrice) {
                System.out.println("Selling " + mySymbol);
                tradeOperattionCode = new TRADE_OPERATION_CODE(1L);//sell
                trade_transaction_type = new TRADE_TRANSACTION_TYPE(0L);//open
            }
        }

        double price = symbolRecord.getAsk();
        double sl = 0.0;
        double tp = 0.0;
        String symbol = symbolRecord.getSymbol();
        double volume = 0.01;
        long order = 0;
        String customComment = "NXtb";
        long expiration = 0;

        TradeTransInfoRecord ttOpenInfoRecord = new TradeTransInfoRecord(
                tradeOperattionCode,
                trade_transaction_type,
                price, sl, tp, symbol, volume, order, customComment, expiration);

        if (tradeOperattionCode != null) {
            try {
                if (loginResponse.getStatus() == true) {

                    TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, Boolean.TRUE);
                    List<TradeRecord> listTradesResponse = tradesResponse.getTradeRecords();

                    for (int i = 0; i < listTradesResponse.size(); i++) {
                        TradeRecord record = listTradesResponse.get(i);
                        

                        TradeTransactionResponse tradeTransactionResponse;
                        if (record.getProfit() > 0.1) {
                            System.out.println("Sprzedaz z zyskiem: " + record.getProfit());
                            tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);
                            System.out.println("response: " + tradeTransactionResponse.toString());
                            ttOpenInfoRecord.setOrder(record.getOrder());
                            ttOpenInfoRecord.setType(new TRADE_TRANSACTION_TYPE(2L));
                            tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);
                            System.out.println("response: " + tradeTransactionResponse.toString());
                        }

                    }

                    //TradeTransactionResponse tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);
                    //System.out.println("response: " + tradeTransactionResponse.toString());
                } else {

                    // Print the error on console
                    System.err.println("Error: user couldn't log in!");

                }

                // Catch errors
            } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
            }
        } else {
            System.out.println("Brak transakcji");
        }

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
