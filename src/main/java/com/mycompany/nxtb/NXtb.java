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

        String symbol = "KGH.PL";
        int Interval = 520000;
        XtbApi xtbApi;
        NetworkNeural nn = new NetworkNeural();

        nn.setFileToTraining(3, 1, symbol);
        nn.setLayer(1, 1);

        String commend = null;
        Scanner in = new Scanner(System.in);

        if (args.length == 0) {

            System.out.println("get-Pobierz swieczki");
            System.out.println("lern-Trening sieci");
            System.out.println("lern2-Trening sieci + test");
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
                    xtbApi.getCandlesOfTime(symbol, PERIOD_CODE.PERIOD_H1, 990000000L);
                    xtbApi.logout();
                    break;
                case "lern":
                    System.out.print("Load weight? y/n ");
                    String loadYN = in.nextLine();
                    if (loadYN.equals("y")) {
                        nn.loadWeight();
                    } else {
                        System.out.println("Reset weights");
                    }
                    nn.startLern();
                    break;

                case "lern2":
                    
                    System.out.println("Start");
                    long jobStart = 0;
                    for (int k = 1; k < 25; k++) {
                        
                    
                    for (int j = 2; j < 25; j++) {
                        System.out.println("Configuracja: ["+(k+j)+"]"+"["+k+"]");
                        nn = new NetworkNeural();
                        nn.setFileToTraining(3, 1, symbol);

                        nn.setLayer(k+j, k);
                        nn.saveWeight();
                        nn.loadWeight();
                        nn.setInterval(Interval);
                        jobStart = System.nanoTime();
                        while (nn.isTreningFalse() != true) {
                            
                            nn.testNeuralNetwork();
                            nn.startLern();

                            nn.saveWeight();

                        }

                        long jobTime = (long) ((System.nanoTime()-jobStart)*1.0E-9);

                        System.out.println("Czas nauki: "+ jobTime+"[s] dla konfiguracji: ["+(k+j)+"]"+"["+k+
                                "] Osiągniety błąd: "+nn.getErrorAverage()+" Sprawność: "+nn.testNeuralNetwork()+" TrenigFalse: "+nn.isTreningFalse()); 
                    }
                    
                    
                    }
                    System.out.println("Siec wytrenowana");
                    break;

                case "test":
                    nn.testNeuralNetwork();
                    break;
                case "insert":
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

        //xtbApi.getActualPrice();
    }
}
