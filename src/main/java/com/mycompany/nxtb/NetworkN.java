/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.DynamicBackPropagation;

/**
 *
 * @author warsztat
 */
public class NetworkN extends MultiLayerPerceptron {

    private int input, output;
    private int[] neuronsInLayers;
    private String symbolName, symbolNameWalidation, symbolNameWeights, symbolNameConf;
    private String separator = ",";
    private String fileConf = "conf.txt";
    private String fileO, fileC, fileH, fileL, fileV;
    public String fileNameArray[] = new String[4];
    private NetworkType networkType;

    private double datamax;
    private double datamin;

    DynamicBackPropagation dbp;

    private DataSet dataSet, dataSetWalidation;

    public NetworkN(List<Integer> neuronsInLayers) {
        super(neuronsInLayers);
        createDynamicBackPropagation();
    }

    public NetworkN(int... neuronsInLayers) {
        super(neuronsInLayers);
        this.input = neuronsInLayers[0];
        this.output = neuronsInLayers[neuronsInLayers.length - 1];
        this.neuronsInLayers = neuronsInLayers;
        createDynamicBackPropagation();
        setLearningRule(dbp);

    }

    private void createDynamicBackPropagation() {
        this.dbp = new DynamicBackPropagation();
    }

    private void setLearningRule(DynamicBackPropagation db) {
        super.setLearningRule(db);
    }

    public void setData() {
        dataSet = DataSet.createFromFile(symbolName, input, output, separator);

        dataSetWalidation = DataSet.createFromFile(symbolNameWalidation, input, output, separator);

    }

    private void addListener(LerningListenerDynamicBackProbagation learningListener) {
        super.getLearningRule().addListener(learningListener);
    }

    private void setWeight() {

        int[] loadedConf = new Memory().loadInt(fileConf);
        int eqularCound = 0;

        int arrayLenghtMin = 0;
        int lenghtNeuronsInLayers = neuronsInLayers.length;
        int lenghtLoadConf = loadedConf.length;

        if (lenghtNeuronsInLayers > lenghtLoadConf) {
            arrayLenghtMin = lenghtLoadConf;
        }
        if (lenghtLoadConf > lenghtNeuronsInLayers) {
            arrayLenghtMin = lenghtNeuronsInLayers;
        }

        for (int i = 0; i < arrayLenghtMin - 1; i++) {
            if (neuronsInLayers[i] == loadedConf[i]) {
                eqularCound++;

            }
        }
        if (eqularCound == arrayLenghtMin - 1) {
            super.setWeights(new Memory().loadDouble(symbolNameWeights));
        } else {
            this.reset();
        }

    }

    private void getWalidationData() {

        double[] data = new Memory().loadDouble(symbolNameWalidation);
        datamax = -9999.0D;
        datamin = 9999.0D;

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

        dataSet = new DataSet(input, output);

        for (int i = 0; i < data.length - (input + output); i++) {

            double[] buffInput = new double[input];
            for (int j = 0; j < input; j++) {
                buffInput[j] = (data[i + j] - datamin) / datamax;
            }

            double[] buffOutput = new double[output];
            for (int j = 0; j < output; j++) {
                buffOutput[j] = (data[input + i + j] - datamin) / datamax;
            }

            dataSet.add(buffInput, buffOutput);
        }

    }

    public void setFileDataTrennig(String symbolName, String type) {
        networkType = new NetworkType(type);
        this.symbolName = networkType.getFileName(symbolName);

        this.symbolNameWalidation = "data/" + symbolName + "/" + symbolName + "_walidation.txt";
        this.symbolNameWeights = "data/" + symbolName + "/" + symbolName + "_weights.txt";
        this.symbolNameConf = "data/" + symbolName + "/" + symbolName + "_conf.txt";
    }

    public void setInput(int input) {
        this.input = input;
    }

    public void setOutput(int output) {
        this.output = output;
    }

    public void setLearningRate(double LerningRate) {
        super.getLearningRule().setLearningRate(LerningRate);
    }

    public void setMaxError(double MaxError) {
        super.getLearningRule().setMaxError(MaxError);
    }

    public void setMaxIteration(int MaxIteration) {
        this.getLearningRule().setMaxIterations(MaxIteration);
    }

    public void setMomentumChange(int MomentumChange) {
        dbp.setMomentumChange(MomentumChange);
        setLearningRule(dbp);
    }

    public void setMomentumChange() {
        dbp.setMomentumChange(0.99926d);
    }

    public void setMaxMomentum(int MaxMomentum) {
        dbp.setMaxMomentum(MaxMomentum);
        setLearningRule(dbp);
    }

    public void setMaxMomentum() {
        dbp.setMaxMomentum(0.9d);
    }

    public void lernThred() {

        Thread thread = new Thread(() -> {
            this.lern();

        });
        thread.start();

    }

    public void lern() {

        this.randomizeWeights();
        this.setWeight();
        addListener(new LerningListenerDynamicBackProbagation());

        super.learn(dataSet);

    }

    public void lern(boolean withBackup) {
        if (withBackup) {
            this.setWeight();
            try {

                new Memory().save(fileConf, neuronsInLayers);
            } catch (IOException ex) {
                Logger.getLogger(NetworkN.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        addListener(new LerningListenerDynamicBackProbagation());

        super.learn(dataSet);
        if (withBackup) {
            try {

                new Memory().save(symbolNameWeights, super.getWeights());

            } catch (IOException ex) {
                Logger.getLogger(NetworkN.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void getLernDataTimeSeries() {

        double[] data = new Memory().loadDouble(symbolName);
        datamax = -9999.0D;
        datamin = 9999.0D;

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

        dataSet = new DataSet(input, output);

        for (int i = 0; i < data.length - (input + output); i++) {

            double[] buffInput = new double[input];
            for (int j = 0; j < input; j++) {
                buffInput[j] = (data[i + j] - datamin) / datamax;
            }

            double[] buffOutput = new double[output];
            for (int j = 0; j < output; j++) {
                buffOutput[j] = (data[input + i + j] - datamin) / datamax;
            }

            dataSet.add(buffInput, buffOutput);
        }

    }

    public void getLernDataSegmen() {

        double[] data = new Memory().loadDouble(symbolName);
        datamax = -9999.0D;
        datamin = 9999.0D;

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

        dataSet = new DataSet(input, output);

        for (int i = 0; i < data.length - (i + input + output); i = i + input + output) {

            double[] buffInput = new double[input];
            double[] buffOutput = new double[output];
            for (int j = 0; j < input; j++) {
                buffInput[j] = (data[i + j] - datamin) / datamax;
            }

            for (int j = 0; j < output; j++) {
                buffOutput[j] = (data[i + input] - datamin) / datamax;
            }

            dataSet.add(buffInput, buffOutput);
        }

    }

    public double[] getLastSymbol() {

        double[] data = new Memory().loadDouble(symbolName);
        double[] getLastSymbol = new double[4];

        getLastSymbol[0] = data[data.length - 5];
        getLastSymbol[1] = data[data.length - 4];
        getLastSymbol[2] = data[data.length - 3];
        getLastSymbol[3] = data[data.length - 2];
        return getLastSymbol;

    }

    public double[] inputScaner(int iter) {

        double[] input = new double[this.input];
        double[] out = new double[this.output];
        Scanner in = new Scanner(System.in);

        for (int i = 0; i < iter; i++) {

            System.out.println("Insert " + this.input + " wejsc.");

            String[] execut = in.nextLine().split(",");

            for (int y = 0; y != this.input; y++) {

                // System.out.print("Podaj wartość wejscia numer " + (y + 1) + ": ");
                input[y] = Double.valueOf(execut[y]);
                input[y] = (input[y] - datamin) / datamax;
            }
            this.setInput(input);
            this.calculate();
            out = this.getOutput();
            for (int j = 0; j < out.length; j++) {

                double pred = (out[0]) * datamax + datamin;

                System.out.printf("Wyjście: %d = %.4f \n", j + 1, pred);
                System.out.println("");
            }
        }
        return out;

    }

    public double inputScaner(String in, int indexOutput) {

        double[] input = new double[this.input];
        double[] out = new double[this.output];
        double pred = 0;

        String[] execut = in.split(",");

        for (int y = 0; y != this.input; y++) {

            // System.out.print("Podaj wartość wejscia numer " + (y + 1) + ": ");
            input[y] = Double.valueOf(execut[y]);
            input[y] = (input[y] - datamin) / datamax;
        }
        this.setInput(input);
        this.calculate();
        out = this.getOutput();
        for (int j = 0; j < out.length; j++) {

            pred = (out[indexOutput]) * datamax + datamin;

            System.out.printf("%s out:-> %.4f \n", Arrays.toString(neuronsInLayers), pred);
        }
        return pred;

    }

    public double inputScaner(double[] in, int indexOutput) {

        double[] input = new double[this.input];
        double[] out = new double[this.output];
        double pred = 0;

        for (int y = 0; y != this.input; y++) {

            // System.out.print("Podaj wartość wejscia numer " + (y + 1) + ": ");
            in[y] = (in[y] - datamin) / datamax;
        }
        this.setInput(input);
        this.calculate();
        out = this.getOutput();
        for (int j = 0; j < out.length; j++) {

            pred = (out[indexOutput]) * datamax + datamin;

            System.out.printf("%s out:-> %.4f \n", Arrays.toString(neuronsInLayers), pred);
        }
        return pred;

    }

    public double selfTest(boolean showLog) {
        getWalidationData();
        int iloscProbek = 0;
        double err = 0;
        double r;
        for (DataSetRow testSetRow : dataSet.getRows()) {

            iloscProbek++;
            this.setInput(testSetRow.getInput());
            this.calculate();
            double[] networkOutput = this.getOutput();
            networkOutput[0] = (networkOutput[0] * datamax + datamin);
            double desiredNetworkOutput = (testSetRow.getDesiredOutput()[0] * datamax + datamin);

//            System.out.print("In: " + Arrays.toString(testSetRow.getInput()));
            if (networkOutput[0] > desiredNetworkOutput) {
                r = networkOutput[0] - desiredNetworkOutput;
            } else {
                r = desiredNetworkOutput - networkOutput[0];
            }

            err = err + r;

            if (showLog == true) {
                System.out.printf("I: %d  NO: %.0f DO: %.0f  r: %.0f \n", iloscProbek, networkOutput[0], desiredNetworkOutput, r);
            }

        }
        err = err / iloscProbek;
        System.out.println("err: " + err);
        return err;

    }

    public double selfTest() {
        getWalidationData();
        int iloscProbek = 0;
        double err = 0;
        double r;
        for (DataSetRow testSetRow : dataSet.getRows()) {

            iloscProbek++;
            this.setInput(testSetRow.getInput());
            this.calculate();
            double[] networkOutput = this.getOutput();
            networkOutput[0] = (networkOutput[0] * datamax + datamin);
            double desiredNetworkOutput = (testSetRow.getDesiredOutput()[0] * datamax + datamin);

            if (networkOutput[0] > desiredNetworkOutput) {
                r = networkOutput[0] - desiredNetworkOutput;
            } else {
                r = desiredNetworkOutput - networkOutput[0];
            }

            err = err + r;
        }
        err = err / iloscProbek;
        System.out.println("err: " + err);
        return err;

    }

    public class LerningListenerDynamicBackProbagation implements LearningEventListener {

        @Override
        public void handleLearningEvent(LearningEvent event) {

            DynamicBackPropagation bp = (DynamicBackPropagation) event.getSource();
            int curentIteration = bp.getCurrentIteration();
            if (curentIteration % 5000 == 0 || curentIteration == 1) {
                double Error = bp.getTotalNetworkError();

                System.out.printf("I: %d/%d E: %.6f/%.6f M: %.0f/%.0f \n", curentIteration, bp.getMaxIterations(), bp.getTotalNetworkError(), bp.getMaxError(), bp.getMomentum() * 1.0E6, bp.getMaxMomentum() * 1.0E6);

            }
        }
    }

}
