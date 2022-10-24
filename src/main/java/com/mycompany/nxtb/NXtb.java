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

        int step = 0;

        String symbol = "KGH.PL";
        StringBuilder buf = new StringBuilder();
        String commend = null;

        double averageOutput = 0;

        Scanner in = new Scanner(System.in);

        NetworkN networkMaster = null;
        NetworkN[] n = new NetworkN[5];

        System.out.println("get-Pobierz swieczki");
        System.out.println("lern-Wyszkol siec");
        System.out.println("insert [data]-insert " + input + " input");
        System.out.println("exit");
        System.out.print(">>");

        if (args.length < 1) {
            String[] execut = in.nextLine().split(" ");
            args = new String[execut.length];
            args = execut;
        }
        for (int i = 0; i < args.length; i++) {
            commend = args[i];
            switch (commend) {
                case "get" -> {
                    XtbApi xtbApi = new XtbApi();
                    xtbApi.login();

                    Date dateRange = new Date(new TimeRange().getRange(new Date().getTime(), 3));
                    System.out.println("Download data: " + dateRange);
                    xtbApi.getCandlesOfTime(symbol, PERIOD_CODE.PERIOD_H1, dateRange.getTime());
                    xtbApi.logout();
                }
                case "lern" -> {
                    for (int j = 0; j < n.length - 1; j++) {

                        System.out.println("Network: " + j);

                        averageOutput = 0;
                        for (int k = 1; k < lernIteration; k++) {
                            n[j] = new NetworkN(input, 3 * input + 2, output);
                            n[j].setFileDataTrennig(symbol);
                            n[j].setLearningRate(0.003);
                            n[j].setMaxError(0.0001);
                            n[j].setMaxIteration(120000);
                            n[j].setMomentumChange(10);
                            n[j].setMaxMomentum(10);
                            n[j].getLernDataTimeSeries(n[j].fileNameArray[j]);
                            try {

                                n[j].lern(false);
                            } catch (Exception e) {
                                n[j].reset();
                            }
                            //n.selfTest();
//                            if (args.length - 1 > i) {
//                                averageOutput = averageOutput + n[j].inputScaner(args[i + 1], 0);
//                            }
                        }
//                        if (args.length - 1 > i) {
//                            System.out.println("Srednia wyjscia: " + averageOutput);
//                        }
                    }
                    //network master
                    for (int j = 0; j < 10; j++) {

                        System.out.println("Network master");
                        networkMaster = new NetworkN(input, 4 * input + 2, output);
                        networkMaster.setFileDataTrennig(symbol);
                        networkMaster.setLearningRate(0.003);
                        networkMaster.setMaxError(0.00001);
                        networkMaster.setMaxIteration(120000);
                        networkMaster.setMomentumChange(10);
                        networkMaster.setMaxMomentum(10);
                        networkMaster.getLernDataSegmen();
                        try {

                            networkMaster.lern(false);
                        } catch (Exception e) {
                            networkMaster.reset();
                        }

                        double out = networkMaster.inputScaner(new double[]{
                            n[0].inputScaner("91.1,91.46,90.74,90.28", 0),
                            n[1].inputScaner("91.46,90.74,90.26,90.52", 0),
                            n[2].inputScaner("91.7,92.3,90.86,90.94", 0),
                            n[3].inputScaner("90.06,90.52,89.96,90.1", 0),}, 0);
                        averageOutput = averageOutput + out;

                    }
                    System.out.println("Master out: " + averageOutput / 10);
                }
                case "insert" -> {
                    if (args.length - 1 > i && networkMaster != null) {
                        System.out.println("output: " + networkMaster.inputScaner(args[i + 1], 0));
                    } else {
                        System.out.println("Brak danych wejsciowych lub sieć nie wyszkolona");
                    }
                }
                case "insert2" -> {
//                    fileNameArray[0] = this.fileO;
//                    fileNameArray[1] = this.fileC;
//                    fileNameArray[2] = this.fileH;
//                    fileNameArray[3] = this.fileL;
                    double out = networkMaster.inputScaner(new double[]{
                        n[0].inputScaner("91.1,91.46,90.74,90.28", 0),
                        n[1].inputScaner("91.46,90.74,90.26,90.52", 0),
                        n[2].inputScaner("91.7,92.3,90.86,90.94", 0),
                        n[3].inputScaner("90.06,90.52,89.96,90.1", 0),}, 0);
                    System.out.println("Master out: " + out);
                }
                case "reset" -> {
                }
                case "load" -> {
                }
                case "exit" ->
                    System.exit(0);
                case "conwert" -> {
                }
                default -> {

                }
            }
        }
        System.exit(0);
    }

}
