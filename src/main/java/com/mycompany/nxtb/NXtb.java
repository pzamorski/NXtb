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
        int Interval = 20000;
        XtbApi xtbApi;
        NetworkNeural nn = new NetworkNeural();

        nn.setFileToTraining(4, 1, symbol);
        nn.setLayer(10, 10);
        nn.setBackUpInterval(Interval);
        String commend = null;
        Scanner in = new Scanner(System.in);

        if (args[0] == null) {
            commend = "lern2";
        } else {
            System.out.println("get-Pobierz swieczki");
            System.out.println("lern-Trening sieci");
            System.out.println("lern2-Trening sieci + test");
            System.out.println("test-Sprawność sieci");
            System.out.println("insert-Podaj dane");
            System.out.println("reset-Reset wag");
            System.out.println("load-Załadowanie wag");
            System.out.println("exit");
            System.out.print(">>");
            
            commend = in.nextLine();
        }

        while (true) {

            

//                    nn.loadWeight();
//                    nn.setInterval(Interval);
//                    while (nn.testNeuralNetwork() < 100) {
//
//                        nn.startLern();
//                        //nn.saveWeight();
//                    }
            switch (commend) {
                case "get":
                    xtbApi = new XtbApi();
                    xtbApi.login();
                    xtbApi.getCandlesOfTime(symbol, PERIOD_CODE.PERIOD_H1, 20390000000L);
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
                    //nn.startLern(1);

                    nn.loadWeight();
                    nn.setInterval(Interval);
                    while (nn.testNeuralNetwork() < 100) {

                        nn.startLern();
                        //nn.saveWeight();
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
        //xtbApi.getActualPrice();

    }
}
