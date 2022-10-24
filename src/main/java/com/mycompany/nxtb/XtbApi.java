/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb;

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
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.ChartLastInfoRecord;
import pro.xstore.api.message.records.ChartRangeInfoRecord;
import pro.xstore.api.message.records.RateInfoRecord;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.message.response.CalendarResponse;
import pro.xstore.api.message.response.ChartResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.message.response.ServerTimeResponse;
import pro.xstore.api.message.response.SymbolResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.ServerData.ServerEnum;
import pro.xstore.api.sync.SyncAPIConnector;

public class XtbApi {

    String mySymbol;

    private final long id = 13818197L;
    private final String password = "Abrakadabra22";
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

    public String getLastSymbol() {
        return lastSymbol;
    }

    public void getCandlesOfTime(String mySymbol, PERIOD_CODE period_code, long time) {

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
                    System.out.println(symbol.getSymbol());
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

                        for (i = 0; i < rateInfoRecord.size()-1; i++) {

                            Candle candle = new Candle(rateInfoRecord.get(i));
                            Candle candleNext = new Candle(rateInfoRecord.get(i+1));

                            try {
                                myWriterO.write(candle.getOpenString() + separator);//dla ciagow czasowych
                                myWriterC.write(candle.getCloseString() + separator);
                                myWriterH.write(candle.getHighString() + separator);
                                myWriterL.write(candle.getLowString() + separator);
                                myWriterV.write(candle.getVolString() + separator);
                                
                                myWriter.write(candle.getOpenString() + separator + //wejsca
                                        candle.getCloseString() + separator+
                                        candle.getHighString()+separator+
                                        candle.getLowString()+separator+
                                        candleNext.getCloseString()+separator+//wyjscie
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

    public double[] getActualPrice() {

        try {

            if (loginResponse.getStatus() == true) {

                AllSymbolsResponse availableSymbols = APICommandFactory.executeAllSymbolsCommand(connector);
                ServerTimeResponse serverTime = APICommandFactory.executeServerTimeCommand(connector);

                // Print the message on console
                System.out.print("...");

//                 List all available symbols on console
                String separator = "\t";
                for (SymbolRecord symbol : availableSymbols.getSymbolRecords()) {
                    // System.out.println("-> " + symbol.getSymbol() + " Ask: " + symbol.getAsk() + " Bid: " + symbol.getBid());
                    ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, symbol.getSymbol(), PERIOD_CODE.PERIOD_H1, serverTime.getTime() - 5368000000L);
                    List<RateInfoRecord> RateInfoRecord = chartLastCommand.getRateInfos();

                    if (!RateInfoRecord.isEmpty() && symbol.getSymbol().contains(lastSymbol)) {
                        System.out.print("GET: " + symbol.getSymbol());

                        System.out.print("...");

                        int lastIndexInfoRecord = RateInfoRecord.size() - 1;
                        double dpriceOpen = RateInfoRecord.get(lastIndexInfoRecord).getOpen();
                        double dpriceClose = RateInfoRecord.get(lastIndexInfoRecord).getClose();
                        double dpriceHigh = RateInfoRecord.get(lastIndexInfoRecord).getHigh();
                        double dpriceLow = RateInfoRecord.get(lastIndexInfoRecord).getLow();
                        double dvolumen = RateInfoRecord.get(lastIndexInfoRecord).getVol();
                        int dPipsCO = (int) ((dpriceClose + dpriceOpen) - dpriceOpen);
                        // double dPipsHL = dpriceHigh-dpriceLow;

                        actualPrice[0] = dpriceClose / 100;
                        actualPrice[1] = dpriceHigh / 100;
                        actualPrice[2] = dpriceLow / 100;

                        break;//słaby internet do usuniecia
                    }

                }

            } else {

                // Print the error on console
                System.err.println("Error: user couldn't log in!");

            }

            // Catch errors
        } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
        }
        return actualPrice;

    }

    public void buy() {
        try {

            if (loginResponse.getStatus() == true) {

                ServerTimeResponse serverTime = APICommandFactory.executeServerTimeCommand(connector);
                APICommandFactory.executeTradeTransactionCommand(connector, TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.OPEN, Double.NaN, Double.NaN, Double.NaN, mySymbol, Double.NaN, id, mySymbol, id);

                // Print the message on console
                System.out.print("...");

            } else {

                // Print the error on console
                System.err.println("Error: user couldn't log in!");

            }

            // Catch errors
        } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
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
