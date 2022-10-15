/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.nxtb;

import java.io.IOException;
import java.util.Scanner;
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

        int l1=2,l2=0;
        
        String symbol = "KGH.PL";
        int Interval = 20000;
        XtbApi xtbApi;
        NetworkNeural nn = new NetworkNeural();

        nn.setFileToTraining(8, 1, symbol);

        String commend = null;
        Scanner in = new Scanner(System.in);

        if (args.length == 0) {

            System.out.println("get-Pobierz swieczki");
            System.out.println("conf-manual setlayer");
            System.out.println("lern-Trening sieci");
            System.out.println("find-Szukanie optymalnej sieci");
            System.out.println("test-Sprawność sieci");
            System.out.println("insert-Podaj dane");
            System.out.println("reset-Reset wag");
            System.out.println("load-Załadowanie wag");
            System.out.println("exit");
            System.out.print(">>");

            String[] execut = in.nextLine().split(" ");
            args = new String[execut.length];
            args = execut;

        }

        for (int i = 0; i < args.length; i++) {
            commend = args[i];

            switch (commend) {
                case "get":
                    xtbApi = new XtbApi();
                    xtbApi.login();
                    xtbApi.getCandlesOfTime(symbol, PERIOD_CODE.PERIOD_H1, 6090000000L);
                    xtbApi.logout();
                    break;
                case "conf":
                    if (args.length > 1) {
                        l1 = Integer.valueOf(args[i + 1]);
                        if (args.length > 2) {
                            l2 = Integer.valueOf(args[i + 2]);
                        }

                    } else {
                        System.out.print("Set layer: ");
                        l1 = Integer.valueOf(in.nextLine());
                        l2 = Integer.valueOf(in.nextLine());

                    }
                    nn.setLayer(l1, l2);
                    break;
                case "lern":

                    if (args.length > 1) {
                        l1 = Integer.valueOf(args[i + 1]);
                        if (args.length > 2) {
                            l2 = Integer.valueOf(args[i + 2]);
                        }

                    } else {
                        System.out.print("Set layer: ");
                        l1 = Integer.valueOf(in.nextLine());
                        l2 = Integer.valueOf(in.nextLine());

                    }
                    nn.setLayer(l1, l2);
                    System.out.println("Set layer:[" + l1 + "][" + l2 + "]");
                    while (nn.testNeuralNetwork()<=100) {

                        nn.startLern();
                        nn.saveWeight();

                    }
                    break;

                case "find":

                    System.out.println("Start");
                    long jobStart = 0;
                    for (int k = 0; k < 75; k++) {

                        for (int j = 1; j < 75; j++) {

                            System.out.println("Configuracja: [" + (k + j) + "]" + "[" + k + "]");
                            nn = new NetworkNeural();
                            nn.setFileToTraining(8, 1, symbol);

                            nn.setLayer(k + j, k);
                            nn.saveWeight();
                            nn.loadWeight();
                            nn.setInterval(Interval);
                            jobStart = System.nanoTime();
                            while (nn.isTreningFalse() != true) {

                                nn.testNeuralNetwork();
                                nn.startFind();

                                nn.saveWeight();

                            }

                            long jobTime = (long) ((System.nanoTime() - jobStart) * 1.0E-9);

                            System.out.println("Czas nauki: " + jobTime + "[s] dla konfiguracji: [" + (k + j) + "]" + "[" + k
                                    + "] Osiągniety błąd: " + nn.getErrorAverage() + " Sprawność: " + nn.testNeuralNetwork() + " TrenigFalse: " + nn.isTreningFalse());
                        }

                    }
                    System.out.println("Siec wytrenowana");
                    break;

                case "test":
                    nn.testNeuralNetwork();
                    break;
                case "insert":
                    nn.loadWeight();
                    nn.inputScaner();
                    break;
                case "reset":
                    nn.saveWeight();
                    break;
                case "load":
                    nn.loadWeight();
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:

            }
        }
        System.exit(0);

    }
}
