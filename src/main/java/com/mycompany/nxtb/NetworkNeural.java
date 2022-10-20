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
    private String walidationinputFileName;
    private int numberInput;
    private int numberOutput;
    private final int MaxIterations = 12000;
    private final double LearningRate = 0.001;
    private final double MaxError = 0.00001;
    private DataSet dataSet;
    private DataSet waldationdataSet;
    private String fileNameWeight;
    private int sprawnosc = 0;
    private double[] ErrorAverage = new double[5];
    private double prevError;
    private boolean treningFalse = false;
    private static double minimulError = 1;
    private static int layer1;
    private static int layer2;
    private int n1;
    private int n2;
    Average av;

    double datamax = -9999.0D;
    double datamin = 9999.0D;

    private MultiLayerPerceptron network;

    public void setFileToTraining(int numberInput, int numberOutput, String fileName) {
        this.numberInput = numberInput;
        this.numberOutput = numberOutput;
        this.inputFileName = "data/" + fileName + ".txt";
        this.walidationinputFileName = "data/" + fileName + "_Walidation.txt";
        this.fileNameWeight = fileName;

    }

    public void setListener(LearningListener learningListener) {
        network.getLearningRule().addListener(learningListener);
    }

    public void setLayer(int n1, int n2) {

        this.n1 = n1;
        this.n2 = n2;
        av = new Average(20);
        // create MultiLayerPerceptron neural network
        if (n2 == 0) {
            network = new MultiLayerPerceptron(numberInput, this.n1, numberOutput);
        } else {
            network = new MultiLayerPerceptron(numberInput, this.n1, this.n2, numberOutput);
        }

        //network = new MultiLayerPerceptron(inputlLayerOutput);
        // create training set from file
        dataSet = DataSet.createFromFile(inputFileName, numberInput, numberOutput, "\t");

        // train the network with training set
        DynamicBackPropagation db = new DynamicBackPropagation();

        db.setMomentumChange(10000000);
        db.setMaxMomentum(100000);
        network.setLearningRule(db);

        network.getLearningRule().setLearningRate(LearningRate);
        network.getLearningRule().setMaxError(MaxError);
        network.getLearningRule().setMaxIterations(MaxIterations);

        network.save("data.nnet");
        

    }

    public boolean isTreningFalse() {
        return treningFalse;
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

    public boolean startLern() {

        network.getLearningRule().addListener(new LearningListener());
        network.learn(dataSet);
        return isTreningFalse();

    }

    void startFind() {
        network.getLearningRule().addListener(new FinderListener());
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

        for (int i = 0; i < 10; i++) {

            System.out.println("Help: pips1 pips2 pips3 pips4");
            
            String[] execut = in.nextLine().split("\t");
            
            for (int y = 0; y != numberInput; y++) {

               // System.out.print("Podaj wartość wejscia numer " + (y + 1) + ": ");
                input[y] = Double.valueOf(execut[y]);
                input[y] = (input[y] - datamin) / datamax;
            }
            network.setInput(input);
            network.calculate();
            out = network.getOutput();
            for (int j = 0; j < out.length; j++) {

                double pred = (out[0]) * datamax + datamin;

                System.out.printf("Wyjście: %d = %.4f \n", j + 1, pred);
                System.out.println("");
            }
        }
        return out;

    }

    public int testNeuralNetwork() {
        waldationdataSet = DataSet.createFromFile(walidationinputFileName, numberInput, numberOutput, "\t");
        int iloscProbek = 0;
        int punktySprawnosci = 0;
        for (DataSetRow testSetRow : waldationdataSet.getRows()) {

            iloscProbek++;
            network.setInput(testSetRow.getInput());
            network.calculate();
            double[] networkOutput = network.getOutput();

            //System.out.print("In: " + Arrays.toString(testSetRow.getInput()));
            //System.out.println(networkOutput[0] + "=" + testSetRow.getDesiredOutput()[0]);
            double tempOutWar = 0;
            if (networkOutput[0] >= 0.4) {
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

    public double getErrorAverage() {
        double average = 0;
        for (int i = 0; i < ErrorAverage.length; i++) {
            average += ErrorAverage[i];
        }
        average += average / ErrorAverage.length;

        return average;
    }

    class LearningListener implements LearningEventListener {

        @Override
        public void handleLearningEvent(LearningEvent event) {

            DynamicBackPropagation bp = (DynamicBackPropagation) event.getSource();
int curentIteration=bp.getCurrentIteration();
            if (((curentIteration - 1) % (100) == 0)) {
                double Error = bp.getTotalNetworkError();

                double momentum = bp.getMomentum();
                double changeError = Math.abs(prevError - Error);
                double avarageChcngeError = av.getAverage(changeError);

                System.out.printf("I: %d E: %.6f M: %.0f R: %.0f \n", curentIteration,Error, momentum * 1.0E6, avarageChcngeError * 1.0E10);

                if (bp.getTotalNetworkError() < bp.getMaxError()) {
                    treningFalse = true;
                    network.stopLearning();

                }

            }

        }

    }

    class FinderListener implements LearningEventListener {

        @Override
        public void handleLearningEvent(LearningEvent event) {

            DynamicBackPropagation bp = (DynamicBackPropagation) event.getSource();

            // if (((bp.getCurrentIteration()-1) % (MaxIterations/10) == 0)||((int)(prevError*1.0E3)!=(int)(bp.getTotalNetworkError()*1.0E3))) {
            if (((bp.getCurrentIteration() - 1) % (bp.getMaxIterations() / 4) == 0)) {
                double Error = bp.getTotalNetworkError();
                if (Error <= 0.001) {
                    treningFalse = false;
                    network.stopLearning();

                }
                if (minimulError > Error) {
                    minimulError = Error;
                    layer1 = n1;
                    layer2 = n2;
                }

                double momentum = bp.getMomentum();
                double changeError = Math.abs(prevError - Error);
                double avarageChcngeError = av.getAverage(changeError);

                //System.out.println("E: " +Error+ "  M: " + momentum+"  R: "+changeError);
                System.out.printf("E: %.3f M: %.0f R: %.0f MinE: %.3f ->[%d][%d]\n", Error, momentum * 1.0E6, avarageChcngeError * 1.0E10, minimulError, layer1, layer2);

                if (avarageChcngeError < 1E-10 || avarageChcngeError == 0) {
                    System.out.println("Przebudowa sieci");
                    treningFalse = true;
                    network.stopLearning();

                }

            }
//            if (((bp.getCurrentIteration() - 1) % (MaxIterations/2) == 0)) {
//                try {
//                    saveWeight();
//                } catch (IOException ex) {
//                    Logger.getLogger(NetworkNeural.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            
//            }
            prevError = bp.getTotalNetworkError();

        }

    }

    public void conwertData() throws IOException {

        System.out.println("Load data");
        double[] data = null;
        try {
            File file = new File(inputFileName);    //creates a new file instance  
            FileReader fr = new FileReader(file);   //reads the file  
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
            StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);      //appends line to string buffer  
                sb.append("\n");     //line feed   
            }
            fr.close();    //closes the stream and release the resources  
            String[] arrayTemp = sb.toString().split("\t");
            data = new double[arrayTemp.length];
            for (int i = 0; i < arrayTemp.length - 1; i++) {

                data[i] = Double.valueOf(arrayTemp[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < data.length - 1; i++) {

            if (data[i] > datamax) {
                datamax = data[i];

            }
            if (data[i] < datamin) {
                datamin = data[i];
            }

        }

        datamax = datamax * 1.2D;
        datamin = datamin * 0.8D;

        System.out.println("Konwert");
        FileWriter myWriter = new FileWriter(inputFileName);
        String separator = "\t";
        for (int i = 0; i < data.length - 8; i++) {
            String d0 = String.valueOf((data[i] - datamin) / datamax);
            String d1 = String.valueOf((data[i + 1] - datamin) / datamax);
            String d2 = String.valueOf((data[i + 2] - datamin) / datamax);
            String d3 = String.valueOf((data[i + 3] - datamin) / datamax);
            String d4 = String.valueOf((data[i + 4] - datamin) / datamax);
            String d5 = String.valueOf((data[i + 5] - datamin) / datamax);
            String d6 = String.valueOf((data[i + 6] - datamin) / datamax);
            String d7 = String.valueOf((data[i + 7] - datamin) / datamax);
            String d8 = String.valueOf((data[i + 8] - datamin) / datamax);
            myWriter.write(d0 + separator + d1 + separator + d2 + separator + d3 + separator + d4 + separator + d5 + separator + d6 + separator + d7 + separator + d8);
            myWriter.write(System.lineSeparator());
        }

        myWriter.close();

    }

}
