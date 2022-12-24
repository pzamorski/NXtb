/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.neuron;

import com.mycompany.nxtb.api.Candle;
import com.mycompany.nxtb.tools.Memory;
import java.io.FileWriter;
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
import org.neuroph.util.TransferFunctionType;

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

    Thread threadLerning;

    public NetworkN(List<Integer> neuronsInLayers) {
        super(neuronsInLayers);
        createDynamicBackPropagation();
        
    }

    public NetworkN(int... neuronsInLayers) {
        super(TransferFunctionType.SIGMOID, neuronsInLayers);
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

        //dataSetWalidation = DataSet.createFromFile(symbolNameWalidation, input, output, separator);
    }

    private void addListener(LerningListenerDynamicBackProbagation learningListener) {
        super.getLearningRule().addListener(learningListener);
    }

    public void setWeight() {

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

    public void setMomentumChange(double MomentumChange) {
        dbp.setMomentumChange(MomentumChange);
        setLearningRule(dbp);
    }

    public void setMomentumChange() {
        dbp.setMomentumChange(0.99926d);
    }

    public void setMaxMomentum(double MaxMomentum) {
        dbp.setMaxMomentum(MaxMomentum);
        setLearningRule(dbp);
    }

    public void setMaxMomentum() {
        dbp.setMaxMomentum(0.9d);
    }

    public Thread lernThred() {

        threadLerning = new Thread(() -> {
            this.lern();

        });
        threadLerning.start();
        return threadLerning;
    }

    public boolean checkLernThredAlive() {

        return threadLerning.isAlive();
    }

    public void lern() {

        this.randomizeWeights();
        this.setWeight();
        addListener(new LerningListenerDynamicBackProbagation());

        super.learn(dataSet);

    }

    public void lern(boolean withBackup) {
        if (withBackup) {
            //this.setWeight();
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

        FileWriter dataLern = null;
        try {
            dataLern = new FileWriter("dalaLern.csv");
        } catch (IOException ex) {
            Logger.getLogger(NetworkN.class.getName()).log(Level.SEVERE, null, ex);
        }

        double[] data = new Memory().loadDouble(symbolName);
        double[] buffInput = new double[input];
        double[] buffOutput = new double[output];

        datamax = -9999.0D;
        datamin = 9999.0D;

        for (int i = 0; i < data.length - (input + output); i = i + input + output) {

            for (int j = 0; j < input; j++) {
                if (data[i + j] > datamax) {
                    datamax = data[i + j];

                }
                if (data[i + j] < datamin) {
                    datamin = data[i + j];
                }

            }

        }

        datamax = datamax * 1.2D;
        datamin = datamin * 0.8D;

        // dataSet = new DataSet(input, output);
        for (int i = 0, k = 0; i < data.length - (input + output); i = i + input + output, k++) {

            for (int j = 0; j < input; j++) {
                buffInput[j] = (data[i + j] - datamin) / datamax;
                data[i + j] = (data[i + j] - datamin) / datamax;
                try {
                    dataLern.write(String.valueOf(data[i + j]) + ",");
                } catch (IOException ex) {
                    Logger.getLogger(NetworkN.class.getName()).log(Level.SEVERE, null, ex);
                }
                //System.out.println(i+j+".  "+(data[i + j] - datamin) / datamax+" -> "+data[i + j]);
            }

            for (int j = 0; j < output; j++) {
                //buffOutput[j] = (data[i + input] - datamin) / datamax;
                buffOutput[j] = data[i + input];
                data[i + input] = data[i + input];

                try {
                    dataLern.write(String.valueOf(data[i + input]) + ",");
                } catch (IOException ex) {
                    Logger.getLogger(NetworkN.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            try {
                dataLern.write(System.lineSeparator());

            } catch (IOException ex) {
                Logger.getLogger(NetworkN.class.getName()).log(Level.SEVERE, null, ex);
            }

//            System.out.println("");
//            System.out.print("innn ");
//            for (int j = 0; j < buffInput.length; j++) {
//                double d = buffInput[j];
//                System.out.print(d+" ");
//                
//            }
//            System.out.println("");
            //dataSet.add(buffInput, buffOutput);
            //dataSet.addRow(buffInput);
        }

        try {
            dataLern.close();
        } catch (IOException ex) {
            Logger.getLogger(NetworkN.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        dataSet = DataSet.createFromFile("dalaLern.csv", input, output, ",");
//         for (int i = 0; i < dataSet.size() - 1; i++) {
//
//            
//             System.out.println(i+" "+dataSet.getRowAt(i));
//
//        }

    }

    public void getLernDataSegmen(Candle[] candles, String in_PO_PC_PH_PL_POC_PHL, String out_PO_PC_PH_PL_POC_PHL) {

        double[] data = new double[candles.length * (input + output)];

        for (int i = 0; i < candles.length - output; i++) {
            for (int j = 0; j < input; j++) {
                if (in_PO_PC_PH_PL_POC_PHL.contains("PO")) {
                    data[j] = candles[i].getOpen();
                    i++;
                }
                if (in_PO_PC_PH_PL_POC_PHL.contains("PC")) {
                    data[j] = candles[i].getClose();
                    i++;
                }
                if (in_PO_PC_PH_PL_POC_PHL.contains("PH")) {
                    data[j] = candles[i].getHigh();
                    i++;
                }
                if (in_PO_PC_PH_PL_POC_PHL.contains("PL")) {
                    data[j] = candles[i].getLow();
                    i++;
                }
                if (in_PO_PC_PH_PL_POC_PHL.contains("POC")) {
                    data[j] = candles[i].getPipsCO();
                    i++;
                }
                if (in_PO_PC_PH_PL_POC_PHL.contains("PHL")) {
                    data[j] = candles[i].getPipsHL();
                    i++;
                }
            }
            for (int j = 0; j < output; j++) {
                if (out_PO_PC_PH_PL_POC_PHL.contains("PO")) {
                    data[j] = candles[i + 1].getOpen();
                    i++;
                }
                if (out_PO_PC_PH_PL_POC_PHL.contains("PC")) {
                    data[j] = candles[i + 1].getClose();
                    i++;
                }
                if (out_PO_PC_PH_PL_POC_PHL.contains("PH")) {
                    data[j] = candles[i + 1].getHigh();
                    i++;
                }
                if (out_PO_PC_PH_PL_POC_PHL.contains("PL")) {
                    data[j] = candles[i + 1].getLow();
                    i++;
                }
                if (out_PO_PC_PH_PL_POC_PHL.contains("POC")) {
                    data[j] = candles[i + 1].getPipsCO();
                    i++;
                }
                if (out_PO_PC_PH_PL_POC_PHL.contains("PHL")) {
                    data[j] = candles[i + 1].getPipsHL();
                    i++;
                }
            }

        }

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
        double[] getLastSymbol = new double[input];

        for (int i = 0; i < getLastSymbol.length; i++) {
            getLastSymbol[i] = data[data.length - (getLastSymbol.length - i)];
        }
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

            //     System.out.printf("%s out:-> %.4f \n", Arrays.toString(neuronsInLayers), pred);
        }
        return pred;

    }

    public double inputScaner(double[] inn, int indexOutput) {

        double[] in = inn;
        double[] input = new double[this.input];
        double[] out = new double[this.output];
        double pred = 0;

//        System.out.println("");
//        System.out.print("scaner: ");
//                for (int y = 0; y != this.input; y++) {
//            
//             System.out.print(in[y]+ " ");
//        }
//                System.out.println("");
//        
//                System.out.println(datamax);
//                System.out.println(datamin);
        for (int y = 0; y != this.input; y++) {

            //in[y] = (in[y] - datamin);
            //in[y] = (in[y] - datamin) / datamax;
        }

        this.setInput(in);
        this.calculate();
        out = this.getOutput();
        for (int j = 0; j < out.length; j++) {

            pred = (out[indexOutput]) * datamax + datamin;
            //System.out.println((out[indexOutput]) * datamax + datamin);/////////////////////////////////////////////////////////////////////
        }
        return out[0];

    }

    public double selfTest(boolean showLog) {
        //getWalidationData();
        getLernDataSegmen();

        int iloscProbek = 0;
        double err = 0;
        double min = 1;
        double max = 0;
        for (DataSetRow testSetRow : dataSet.getRows()) {

            iloscProbek++;
            this.setInput(testSetRow.getInput());
            this.calculate();
            double[] networkOutput = this.getOutput();
            double desiredNetworkOutput = (testSetRow.getDesiredOutput()[0]);

            if (desiredNetworkOutput == 1) {

                if (min > networkOutput[0] && min > max) {
                    min = networkOutput[0];
                }
            }

            if (desiredNetworkOutput == 0) {
                if (max < networkOutput[0] && max < min) {
                    max = networkOutput[0];
                }
            }

        }
        max = max + (max - min);
        min = min - (max - min);

        System.out.println("max: " + max + " min: " + min);

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

    public void setDefaultParameter() {
        setLearningRate(0.1);
        setMaxError(0.01);
        setMaxIteration(2400000);
        setMomentumChange(0.2);
        setMaxMomentum(0.2);
    }

    public class LerningListenerDynamicBackProbagation implements LearningEventListener {

        @Override
        public void handleLearningEvent(LearningEvent event) {

            DynamicBackPropagation bp = (DynamicBackPropagation) event.getSource();
            int curentIteration = bp.getCurrentIteration();
            if (curentIteration % 100 == 0 || curentIteration == 1) {
                double Error = bp.getTotalNetworkError();

                System.out.printf(" I: %d/%d E: %.6f/%.6f M: %.0f/%.0f \n", curentIteration, bp.getMaxIterations(), bp.getTotalNetworkError(), bp.getMaxError(), bp.getMomentum() * 1.0E6, bp.getMaxMomentum() * 1.0E6);

            }
        }
    }

}
