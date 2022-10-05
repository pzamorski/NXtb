/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;


/**
 *
 * @author warsztat
 */
public class NetworkNeural {

    private String inputFileName;
    private int numberInput;
    private int numberOutput;
    private final int MaxIterations = 1200000;
    private final double LearningRate = 0.9;
    private final double MaxError = 0.01;
    private DataSet dataSet;
    private int BackUpInterval = 15000;
    private String fileNameWeight;
    private int sprawnosc = 0;

    private MultiLayerPerceptron network;
    

    public void setBackUpInterval(int BackUpInterval) {
        this.BackUpInterval = BackUpInterval;
    }

    public void setFileToTraining(int numberInput, int numberOutput, String fileName) {
        this.numberInput = numberInput;
        this.numberOutput = numberOutput;
        this.inputFileName = "data/" + fileName + ".txt";
        this.fileNameWeight = fileName;
    }

    public void setLayer(int L1, int L2) {
        
        // create MultiLayerPerceptron neural network
        network = new MultiLayerPerceptron(numberInput, 1000, numberOutput);

     
        // create training set from file
        dataSet = DataSet.createFromFile(inputFileName, numberInput, numberOutput, "\t");
        // train the network with training set
        

        DynamicBackPropagation db = new DynamicBackPropagation();
        
        db.setMomentumChange(1000000);
        db.setMaxMomentum(1000000);
        network.setLearningRule(db);
        network.getLearningRule().addListener(new LearningListener());
    

        
      network.getLearningRule().setLearningRate(LearningRate);
        network.getLearningRule().setMaxError(MaxError);
        network.getLearningRule().setMaxIterations(MaxIterations);

        network.save("data.nnet");

    }

    public void saveWeight() throws IOException {
        System.out.println("Save Weights");
        FileWriter myWriter = new FileWriter("data/" + fileNameWeight + "_Weights.txt");
        Double[] weight = network.getWeights();

        for (int i = 0; i < weight.length; i++) {
            myWriter.write(String.valueOf(weight[i]) + ",");

        }
        myWriter.close();

    }

    public void loadWeight() {

        System.out.println("Load Weight...");
        try {
            File file = new File("data/" + fileNameWeight + "_Weights.txt");    //creates a new file instance  
            FileReader fr = new FileReader(file);   //reads the file  
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
            StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);      //appends line to string buffer  
                sb.append("\n");     //line feed   
            }
            fr.close();    //closes the stream and release the resources  
            String[] arrayTemp = sb.toString().split(",");
            double[] arrayDoubleWeight = new double[arrayTemp.length];
            for (int i = 0; i < arrayTemp.length - 1; i++) {

                arrayDoubleWeight[i] = Double.valueOf(arrayTemp[i]);
            }
            network.setWeights(arrayDoubleWeight);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startLern() {

        network.learn(dataSet);
    }

    public void setInterval(int interval) {
        network.getLearningRule().setMaxIterations(interval);
    }

    public double[] inputScaner() {

        double[] input = new double[numberInput];
        double[] out = new double[numberOutput];
        double OpenPrice = 0;
        Scanner in = new Scanner(System.in);

        System.out.println("Help: priceClose priceHigh  priceLow");
        for (int i = 0; i < 10; i++) {

            for (int y = 0; y != numberInput; y++) {

                System.out.print("Podaj wartość wejscia numer " + (y + 1) + ": ");
                input[y] = OpenPrice - Double.valueOf(in.nextLine());
            }
            network.setInput(input);
            network.calculate();
            out = network.getOutput();
            for (int j = 0; j < out.length; j++) {

                System.out.println("wyjście " + (j + 1) + " = " + out[0]);
                System.out.println("");
            }
        }
        return out;

    }

   
    
    public int testNeuralNetwork() {

        int iloscProbek = 0;
        int punktySprawnosci = 0;
        for (DataSetRow testSetRow : dataSet.getRows()) {

            iloscProbek++;
            network.setInput(testSetRow.getInput());
            network.calculate();
            double[] networkOutput = network.getOutput();

            //System.out.print("In: " + Arrays.toString(testSetRow.getInput()));
            //System.out.println(networkOutput[0] + "=" + testSetRow.getDesiredOutput()[0]);
            double tempOutWar = 0;
            if (networkOutput[0] >= 0.5) {
                tempOutWar = 1;
            }
            if (tempOutWar == testSetRow.getDesiredOutput()[0]) {
                punktySprawnosci++;
            }

        }
        int procentSprawnosci = (punktySprawnosci * 100) / iloscProbek;
        if (sprawnosc != procentSprawnosci) {
            System.out.println("Sprawność: " + procentSprawnosci + "%");
        }
        sprawnosc = procentSprawnosci;
        return procentSprawnosci;
    }

    public ArrayList<double[]> getResult() {
        ArrayList<double[]> list = new ArrayList<>();
        for (DataSetRow testSetRow : dataSet.getRows()) {

            network.setInput(testSetRow.getInput());
            network.calculate();
            double[] networkOutput = network.getOutput();

            System.out.print("In: " + Arrays.toString(testSetRow.getInput()));
            System.out.println(" Out: " + Arrays.toString(networkOutput));
            list.add(networkOutput);
        }
        return list;
    }

    public double[] getResultByInput(double[] input) {
        DataSetRow testSetRow = new DataSetRow();
        testSetRow.setInput(input);
        network.setInput(testSetRow.getInput());
        network.calculate();

        double[] networkOutput = network.getOutput();

        System.out.print("In: " + Arrays.toString(testSetRow.getInput()));
        System.out.println(" Out: " + Arrays.toString(networkOutput));

        return networkOutput;
    }

    class LearningListener implements LearningEventListener {

        @Override
        public void handleLearningEvent(LearningEvent event) {

            DynamicBackPropagation bp = (DynamicBackPropagation) event.getSource();
            
            if (bp.getCurrentIteration() % 1000 == 0) {
                
                
                System.out.println("Er: " + bp.getTotalNetworkError()+" Momentum: "+bp.getMomentum());
//                try {
//                    System.out.print("Backup...");
//                    saveWeight();
//                    System.out.println("ok");
//                } catch (IOException ex) {
//                    Logger.getLogger(NetworkNeural.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
            
                        if (bp.getCurrentIteration() % (BackUpInterval-1000) == 0) {
                
                
                try {
                    System.out.print("Backup...");
                    saveWeight();
                    System.out.println("ok");
                } catch (IOException ex) {
                }
            }
                        
        }

    }

}
