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

    public void logout() throws APICommunicationException{
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


                // Print the message on console
                System.out.print("...");

                FileWriter myWriter = null;
//                 List all available symbols on console
                String separator = "\t";
                for (SymbolRecord symbol : availableSymbols.getSymbolRecords()) {
                    // System.out.println("-> " + symbol.getSymbol() + " Ask: " + symbol.getAsk() + " Bid: " + symbol.getBid());
                    ChartResponse chartLastCommand = APICommandFactory.executeChartLastCommand(connector, symbol.getSymbol(), period_code, serverTime.getTime() - time);
                    List<RateInfoRecord> RateInfoRecord = chartLastCommand.getRateInfos();

                    if (!RateInfoRecord.isEmpty() && symbol.getSymbol().contains(mySymbol)) {
                        myWriter = new FileWriter("data/" + mySymbol + ".txt");
                        System.out.print(symbol.getSymbol() + " - zapis ");

                        System.out.print("...");
                        int i = 0;

                        for (i = 0; i < RateInfoRecord.size() - 4; i++) {

                            double dpriceOpen = RateInfoRecord.get(i).getOpen();
                            double dpriceClose = RateInfoRecord.get(i).getClose();
                            double dpriceHigh = RateInfoRecord.get(i).getHigh();
                            double dpriceLow = RateInfoRecord.get(i).getLow();
                            double dvolumen = RateInfoRecord.get(i).getVol();
                            int dPipsCO = (int) ((dpriceClose + dpriceOpen) - dpriceOpen);
                            // double dPipsHL = dpriceHigh-dpriceLow;

                            String priceClose = String.valueOf(dpriceClose / 100);
                            String priceOpen = String.valueOf(dpriceOpen / 100);
                            String priceHigh = String.valueOf(dpriceHigh / 100);
                            String priceLow = String.valueOf(dpriceLow / 100);
                            String volumen = String.valueOf(dvolumen);
                            String pipsCO = String.valueOf(dPipsCO);
                            // String pipsHL = String.valueOf(dPipsHL);

                            double dpriceOpen1 = RateInfoRecord.get(i + 1).getOpen();
                            double dpriceClose1 = RateInfoRecord.get(i + 1).getClose();
                            double dpriceHigh1 = RateInfoRecord.get(i + 1).getHigh();
                            double dpriceLow1 = RateInfoRecord.get(i + 1).getLow();
                            double dvolumen1 = RateInfoRecord.get(i + 1).getVol();
                            int dPipsCO1 = (int) ((dpriceClose1 + dpriceOpen1) - dpriceOpen1);

                            String priceClose1 = String.valueOf(dpriceClose1 / 100);
                            String priceOpen1 = String.valueOf(dpriceOpen1 / 100);
                            String priceHigh1 = String.valueOf(dpriceHigh1 / 100);
                            String priceLow1 = String.valueOf(dpriceLow1 / 100);
                            String volumen1 = String.valueOf(dvolumen1);
                            String pipsCO1 = String.valueOf(dPipsCO1);

//                            double dpriceOpen12 = RateInfoRecord.get(i + 2).getOpen();
//                            double dpriceClose12 = RateInfoRecord.get(i + 2).getClose();
//                            double dpriceHigh12 = RateInfoRecord.get(i + 2).getHigh();
//                            double dpriceLow12 = RateInfoRecord.get(i + 2).getLow();
//                            double dvolumen12 = RateInfoRecord.get(i + 2).getVol();
//                            int dPipsCO12 = (int) ((dpriceClose12 + dpriceOpen12) - dpriceOpen12);
//
//                            String priceClose12 = String.valueOf(dpriceClose12 / 100);
//                            String priceOpen12 = String.valueOf(dpriceOpen12 / 100);
//                            String priceHigh12 = String.valueOf(dpriceHigh12 / 100);
//                            String priceLow12 = String.valueOf(dpriceLow12 / 100);
//                            String volumen12 = String.valueOf(dvolumen12);
//                            String pipsCO12 = String.valueOf(dPipsCO12);
//                            double dpriceOpen123 = RateInfoRecord.get(i + 3).getOpen();
//                            double dpriceClose123 = RateInfoRecord.get(i + 3).getClose();
//                            double dpriceHigh123 = RateInfoRecord.get(i + 3).getHigh();
//                            double dpriceLow123 = RateInfoRecord.get(i + 3).getLow();
//                            double dvolumen123 = RateInfoRecord.get(i + 3).getVol();
//                            int dPipsCO123 = (int) ((dpriceClose123 + dpriceOpen123) - dpriceOpen123);
//
//                            String priceClose123 = String.valueOf(dpriceClose123 / 100);
//                            String priceOpen123 = String.valueOf(dpriceOpen123 / 100);
//                            String priceHigh123 = String.valueOf(dpriceHigh123 / 100);
//                            String priceLow123 = String.valueOf(dpriceLow123 / 100);
//                            String volumen123 = String.valueOf(dvolumen123);
//                            String pipsCO123 = String.valueOf(dPipsCO123);
//                            
//                            double dpriceOpen1234 = RateInfoRecord.get(i + 4).getOpen();
//                            double dpriceClose1234 = RateInfoRecord.get(i + 4).getClose();
//                            double dpriceHigh1234 = RateInfoRecord.get(i + 4).getHigh();
//                            double dpriceLow1234 = RateInfoRecord.get(i + 4).getLow();
//                            double dvolumen1234 = RateInfoRecord.get(i + 4).getVol();
//                            int dPipsCO1234 = (int) ((dpriceClose1234 + dpriceOpen1234) - dpriceOpen1234);
//
//                            String priceClose1234 = String.valueOf(dpriceClose1234 / 100);
//                            String priceOpen1234 = String.valueOf(dpriceOpen1234 / 100);
//                            String priceHigh1234 = String.valueOf(dpriceHigh1234 / 100);
//                            String priceLow1234 = String.valueOf(dpriceLow1234 / 100);
//                            String volumen1234 = String.valueOf(dvolumen1234);
//                            String pipsCO1234 = String.valueOf(dPipsCO1234);

 String result1 = "1";
                            if (dPipsCO <= 0) {
                                result1 = "0";
                            }
                            
                            String result = "1";
                            if (dPipsCO1 <= 0) {
                                result = "0";
                            }

                            //String ctm = String.valueOf(RateInfoRecord.get(i).getCtm());
                            try {

                                myWriter.write(priceClose + separator + priceHigh + separator + priceLow + separator + result1 + separator +
//                                        priceClose1 + separator + priceHigh1 + separator + priceLow1 + separator +
//                                        priceClose12 + separator + priceHigh12 + separator + priceLow12 + separator +
//                                        priceClose123 + separator + priceHigh123 + separator + priceLow123 + separator +
                                        result);
                                myWriter.write(System.lineSeparator());

                            } catch (IOException e) {
                                System.out.println("error");
                                e.printStackTrace();
                            }

                        }

                        myWriter.close();
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
    
    public double[] getActualPrice(){
    
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
                        System.out.print("GET: "+symbol.getSymbol());

                        System.out.print("...");



                            int lastIndexInfoRecord=RateInfoRecord.size()-1;
                            double dpriceOpen = RateInfoRecord.get(lastIndexInfoRecord).getOpen();
                            double dpriceClose = RateInfoRecord.get(lastIndexInfoRecord).getClose();
                            double dpriceHigh = RateInfoRecord.get(lastIndexInfoRecord).getHigh();
                            double dpriceLow = RateInfoRecord.get(lastIndexInfoRecord).getLow();
                            double dvolumen = RateInfoRecord.get(lastIndexInfoRecord).getVol();
                            int dPipsCO = (int) ((dpriceClose + dpriceOpen) - dpriceOpen);
                            // double dPipsHL = dpriceHigh-dpriceLow;


                            actualPrice[0]=dpriceClose/100;
                            actualPrice[1]=dpriceHigh/100;
                            actualPrice[2]=dpriceLow/100;
                            
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
    
    public void buy(){
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
