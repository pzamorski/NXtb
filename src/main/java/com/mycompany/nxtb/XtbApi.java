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

    private long id = 13818197L;
    private String password = "Abrakadabra22";
    private SyncAPIConnector connector;
    private LoginResponse loginResponse;
    private String lastSymbol;
    private double[] actualPrice = new double[3];

    public boolean login() throws APICommandConstructionException, APICommunicationException, APIReplyParseException, APIErrorResponse, IOException {
        Credentials credentials = new Credentials(id, password);
        loginResponse = APICommandFactory.executeLoginCommand(
                connector, // APIConnector
                credentials // Credentials
        );

        return checkIsLogin();
    }

    public boolean login(long id, String password) throws APICommandConstructionException, APICommunicationException, APIReplyParseException, APIErrorResponse, IOException {
        Credentials credentials = new Credentials(id, password);
        loginResponse = APICommandFactory.executeLoginCommand(
                connector, // APIConnector
                credentials // Credentials
        );

        return checkIsLogin();
    }

    public void logout() throws APICommunicationException {
        connector.close();
        System.out.println("Połączenie zamkniete");

    }

    public boolean checkIsLogin() {
        if (loginResponse.getStatus() == true) {
            System.out.println("Zalogowany");
        }
        return loginResponse.getStatus();
    }

    public String getLastSymbol() {
        return lastSymbol;
    }

    public void getCandlesOfTime(String mySymbol, PERIOD_CODE period_code, long time) {
        try {

            if (loginResponse.getStatus() == true) {
                lastSymbol = mySymbol;

                AllSymbolsResponse availableSymbols = APICommandFactory.executeAllSymbolsCommand(connector);
                ServerTimeResponse serverTime = APICommandFactory.executeServerTimeCommand(connector);

                System.out.print("...");

                FileWriter myWriter = null;
                FileWriter myWriterWal = null;
//                 List all available symbols on console
                String separator = ",";
                for (SymbolRecord symbol : availableSymbols.getSymbolRecords()) {
                    // System.out.println("-> " + symbol.getSymbol() + " Ask: " + symbol.getAsk() + " Bid: " + symbol.getBid());
                    ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, symbol.getSymbol(), period_code, serverTime.getTime() - time);
                    List<RateInfoRecord> RateInfoRecord = chartLastCommand.getRateInfos();

                    if (!RateInfoRecord.isEmpty() && symbol.getSymbol().contains(mySymbol)) {
                        myWriter = new FileWriter("data/"+mySymbol+"/" + mySymbol + ".txt");
                        myWriterWal = new FileWriter("data/"+mySymbol+"/" + mySymbol + "_Walidation.txt");
                        System.out.print(symbol.getSymbol() + " - zapis ");

                        System.out.print("...");
                        int i = 0;

                        for (i = 0; i < RateInfoRecord.size() - 4; i++) {

                            RateInfoRecord next1Info = RateInfoRecord.get(i);
                            RateInfoRecord next2Info = RateInfoRecord.get(i + 1);
                            RateInfoRecord next3Info = RateInfoRecord.get(i + 2);
                            RateInfoRecord next4Info = RateInfoRecord.get(i + 3);
                            RateInfoRecord next5Info = RateInfoRecord.get(i + 4);

                            double pipsclose1 = next1Info.getClose() + next1Info.getOpen();
                            double pipsopen1 = next1Info.getOpen();
                            
                            double pipsclose2 = next2Info.getClose() + next2Info.getOpen();
                            double pipsopen2 = next2Info.getOpen();
                            
                            double pipsclose3 = next3Info.getClose() + next3Info.getOpen();
                            double pipsopen3  = next3Info.getOpen();
                            
                            double pipsclose4 = next4Info.getClose() + next4Info.getOpen();
                            double pipsopen4 = next4Info.getOpen();
                            
                            double pipsclose5 = next5Info.getClose() + next5Info.getOpen();
                            double pipsopen5 = next5Info.getOpen();

                            String pips1Close = String.valueOf(pipsclose1);
                            String pips1Open=String.valueOf(pipsopen1);
                            
                            String pips2Close = String.valueOf(pipsclose2);
                            String pips2Open=String.valueOf(pipsopen2);
                            
                            String pips3Close = String.valueOf(pipsclose3);
                            String pips3Open=String.valueOf(pipsopen3);
                            
                            String pips4Close = String.valueOf(pipsclose4);
                            String pips4Open=String.valueOf(pipsopen4);
                            
                           String pips5Close = String.valueOf(pipsclose5);
                            String pips5Open=String.valueOf(pipsopen5);
                            

                            try {

                                if (i > (RateInfoRecord.size() / 2)) {

                                    myWriter.write(pips1Close+separator +pips1Open+ separator + 
                                            pips2Close+separator +pips2Open+ separator +
                                            pips3Close+separator +pips3Open+ separator +
                                            pips4Close+separator +pips4Open+ separator +
                                            pips5Open+ separator );
                                    myWriter.write(System.lineSeparator());
                                } else {

                                    myWriterWal.write(pips1Close+separator +pips1Open+ separator + 
                                            pips2Close+separator +pips2Open+ separator +
                                            pips3Close+separator +pips3Open+ separator +
                                            pips4Close+separator +pips4Open+ separator +
                                            pips5Open+ separator );
                                    myWriterWal.write(System.lineSeparator());
                                }

                            } catch (IOException e) {
                                System.out.println("error");
                                e.printStackTrace();
                            }

                        }

                        myWriter.close();
                        myWriterWal.close();
                        System.out.print(" OK" + "[" + i + "]");
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

}
