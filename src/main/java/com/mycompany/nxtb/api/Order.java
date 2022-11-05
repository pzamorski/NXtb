/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.api;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.TradeTransactionResponse;
import pro.xstore.api.message.response.TradesResponse;
import pro.xstore.api.sync.SyncAPIConnector;

/**
 *
 * @author warsztat
 */
public class Order implements Runnable {

    private SyncAPIConnector connector;
    private SymbolRecord symbolRecord;
    private int limitOreders = 1;
    private int maxLimitOrders = 5;
    private double tolerance = 2;
    private double volume = 0.01;
    private double maxVolume = 0.1;
    private double priceFromNetwork;
    private double trigerGetProfut = 0.1;
    private double profit = 0;
    private boolean thresIsActive = false;

    public void setPriceFromNetwork(double priceFromNetwork) {
        this.priceFromNetwork = priceFromNetwork;
    }

    public void setConnector(SyncAPIConnector connector) {
        this.connector = connector;
    }

    public void setSymbolRecord(SymbolRecord symbolRecord) {
        this.symbolRecord = symbolRecord;
    }

    public void setLimitOreders(int limitOreders) {
        this.limitOreders = limitOreders;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public TradeTransInfoRecord calculateCondition(String tradeOperationActive) {
        TradeTransInfoRecord ttOpenInfoRecord = null;

        double actualPrice = symbolRecord.getAsk();
        double priceCondition = priceFromNetwork - actualPrice;
        TRADE_OPERATION_CODE trade_operation_code;
        TRADE_TRANSACTION_TYPE trade_transaction_type;

        boolean activeBuy = tradeOperationActive.contains("buy");
        boolean actveSell = tradeOperationActive.contains("sell");

        if (Math.abs(priceCondition) > tolerance) {
            if (priceFromNetwork > actualPrice && activeBuy) {
                ttOpenInfoRecord = CreateTradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.OPEN);
            }
            if (priceFromNetwork < actualPrice && actveSell) {
                ttOpenInfoRecord = CreateTradeTransInfoRecord(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.OPEN);
            }
        } else {
            System.out.println("Tolerancja przekroczona " + symbolRecord.getSymbol());
        }
        return ttOpenInfoRecord;

    }

    public void executTrade(TradeTransInfoRecord ttOpenInfoRecord) {

        if (ttOpenInfoRecord != null) {

            try {
                //set take profit
                //ttOpenInfoRecord.setTp(actualPrice + 0.5);

                TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, Boolean.TRUE);
                List<TradeRecord> listTradesResponse = tradesResponse.getTradeRecords();
                TradeTransactionResponse tradeTransactionResponse;

                int iloscZlecenDlaSymbolu = 0;
                for (int i = 0; i < listTradesResponse.size(); i++) {
                    TradeRecord get = listTradesResponse.get(i);
                    if (get.getSymbol().equals(symbolRecord.getSymbol())) {
                        iloscZlecenDlaSymbolu++;
                    }
                }
                if (iloscZlecenDlaSymbolu <= limitOreders) {//nie wiecej jak limitZleceń 
                    if (ttOpenInfoRecord.getCmd() == TRADE_OPERATION_CODE.BUY) {
                        System.out.println("Buying " + symbolRecord.getSymbol());
                    }
                    if (ttOpenInfoRecord.getCmd() == TRADE_OPERATION_CODE.SELL) {
                        System.out.println("Selling " + symbolRecord.getSymbol());
                    }

                    tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);

                    System.out.println("Response: " + tradeTransactionResponse.toString() +" "+ symbolRecord.getSymbol());

                } else {
                    System.err.println("Maksimum zlecń dla " + symbolRecord.getSymbol());
                }

            } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
            }
        } else {
            System.out.println("Brak transakcji " + symbolRecord.getSymbol());
        }

    }

    public void execut(SyncAPIConnector connector, double priceFromNetwork, SymbolRecord symbolRecord) {
        setConnector(connector);
        setSymbolRecord(symbolRecord);

        if (this.connector != null && !thresIsActive) {
            this.run();
        }

        setPriceFromNetwork(priceFromNetwork);
        executTrade(calculateCondition("buy"));

    }

    private TradeTransInfoRecord CreateTradeTransInfoRecord(TRADE_OPERATION_CODE tradeOperattionCode, TRADE_TRANSACTION_TYPE trade_transaction_type) {

        double sl = 0.0;
        double tp = 0.0;

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

    private void customizeOrder() {

        if (profit >= 4) {
            if (volume <= maxVolume) {
                volume = volume + 0.01;
            }
            if (limitOreders <= maxLimitOrders) {
                limitOreders++;
            }

        }

        if (profit <= -10) {
            volume = 0.01;
            limitOreders = 1;
        }
        if (profit <= -15) {
            volume = 0.00;
            limitOreders = 0;
        }

    }

    public String getSymbol() {
        return symbolRecord.getSymbol();
    }

    @Override
    public void run() {

        thresIsActive = true;

        TradeTransInfoRecord ttOpenInfoRecord = CreateTradeTransInfoRecord(TRADE_OPERATION_CODE.BUY, TRADE_TRANSACTION_TYPE.CLOSE);
        while (Boolean.FALSE) {

            customizeOrder();

            try {

                TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, Boolean.TRUE);
                List<TradeRecord> listTradesResponse = tradesResponse.getTradeRecords();
                TradeTransactionResponse tradeTransactionResponse;

                for (int i = 0; i < listTradesResponse.size(); i++) {
                    TradeRecord record = listTradesResponse.get(i);
                    double currentProfit = record.getProfit();
                    profit = profit + currentProfit;
                    if (currentProfit > trigerGetProfut) {

                        ttOpenInfoRecord.setOrder(record.getOrder());
                        ttOpenInfoRecord.setVolume(record.getVolume());
                        profit = +record.getProfit();
                        tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);
                        System.out.println("response: " + tradeTransactionResponse.toString());
                    }

                }

            } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse e) {
            }

            try {
                Thread.sleep(10000);

            } catch (InterruptedException ex) {
                Logger.getLogger(XtbApi.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
