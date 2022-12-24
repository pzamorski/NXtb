/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nxtb.neuron;

import com.mycompany.nxtb.neuron.NetworkN.LerningListenerDynamicBackProbagation;
import com.mycompany.nxtb.tools.Memory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.error.MeanSquaredError;
import org.neuroph.eval.ClassifierEvaluator;
import org.neuroph.eval.ErrorEvaluator;
import org.neuroph.eval.Evaluation;
import org.neuroph.eval.classification.ClassificationMetrics;
import org.neuroph.eval.classification.ConfusionMatrix;
import org.neuroph.nnet.CompetitiveNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.data.norm.MaxNormalizer;
import org.neuroph.util.data.norm.Normalizer;
import pro.xstore.api.message.codes.PERIOD_CODE;

/**
 *
 * @author warsztat
 */
public class CreateModel {

    private String nameModel, pathOfModels = "models", pathOfData = "data";
    private int input, output;
    private DataSet dataSet;
    private double minError = 9999;
    private PERIOD_CODE period_code;
    

    private NeuralNetwork neuralNetwork;

    public CreateModel(String NameModel, int input, int output,PERIOD_CODE periodCode) {
        this.nameModel = NameModel;
        this.input = input;
        this.output = output;
        this.period_code=periodCode;
        dataSet = new DataSet(this.input, this.output);
        
    }

    public CreateModel() {
    }

    public CreateModel(CreateModel cm, String symbol) {
        neuralNetwork = new CreateModel().loadModel(symbol);
    }



    public void addData(double[] in, double[] out) {
        // Tworzymy zbiór danych zawierający przykłady wejściowe i odpowiadające im etykiety
        // 2 wejścia, 1 wyjście
        dataSet.add(new DataSetRow(in, out));
        createFolder(pathOfData);
        dataSet.save(pathOfData + "/" + nameModel + ".dat");

    }
    
    public void lernFromFile(){
      dataSet = DataSet.createFromFile("data/" + nameModel+"/"+nameModel+"_"+period_code+".csv", input, output, ";");
      lern();
    }

    public void lern() {
        System.out.println("Start lern");
        System.out.println("Rozmiar danych szkoleniowych: " + dataSet.size());

        neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, input,input*2+5, output);
        DynamicBackPropagation dynamicBackPropagation = new DynamicBackPropagation();
        dynamicBackPropagation.setMinLearningRate(0.1);
        dynamicBackPropagation.setMaxError(0.008);
        neuralNetwork.setLearningRule(dynamicBackPropagation);
        neuralNetwork.getLearningRule().addListener(new LerningListenerDynamicBackProbagation());

        DataSet[] trainTestSplit = dataSet.split(0.8, 0.2);
        DataSet trainingSet = trainTestSplit[0];
        DataSet testSet = trainTestSplit[1];
        
        System.out.println("Data size: "+dataSet.size() );
        System.out.println("Train size: "+trainingSet.size() );
        System.out.println("Test size: "+testSet.size() );
        
        
        Normalizer norm = new MaxNormalizer(trainingSet);
        norm.normalize(trainingSet);
        norm.normalize(testSet);

        try {

            new Memory().save("data/csv/" + nameModel + ".csv", "");
        } catch (IOException ex) {
            Logger.getLogger(CreateModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        neuralNetwork.learn(trainingSet);

        System.out.println("Network performance on the test set");
        evaluate(neuralNetwork, testSet);
        
        createFolder(pathOfModels);

        neuralNetwork.setLabel(nameModel);
        neuralNetwork.save(pathOfModels + "/" + nameModel + ".lib");
        
//        System.out.println();
//        System.out.println("Network outputs for test set");
//        testNeuralNetwork(neuralNetwork, testSet);
    }

    public void testNeuralNetwork(NeuralNetwork neuralNet, DataSet testSet) {

        System.out.println("Showing inputs, desired output and neural network output for every row in test set.");

        for (DataSetRow testSetRow : testSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();

            //System.out.println("Input: " + Arrays.toString(testSetRow.getInput()));
            //System.out.println("Output: " + Arrays.toString(networkOutput));
            System.out.println("Output: "+Arrays.toString(networkOutput)+"  Desired output: " + Arrays.toString(testSetRow.getDesiredOutput()));
        }
    }

    public NeuralNetwork loadModel(String nameModel) {
        NeuralNetwork nn = null;
        String path = pathOfModels + "/" + nameModel + ".lib";
        try {
            
            nn = NeuralNetwork.load(path);
            nn.setWeights(new Memory().loadDouble(pathOfModels + "/" + nameModel + ".wg"));
            System.out.println("Load " + pathOfModels + "/" + nameModel + ".lib");
            System.out.println("Load " +pathOfModels + "/" + nameModel + ".wg");
        } catch (Exception e) {
        }
        return nn;
    }
//        public DataSet loadData(String nameModel) {
//            System.out.println("Load " + pathOfData + "/" + nameModel + ".dat");
//            DataSet ds=DataSet.load(pathOfData + "/" + nameModel + ".dat");
//        return ds;
//    }

    private void createFolder(String pathOfModels) {

        File dataDir = new File(pathOfModels);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

    }

public void evaluate(NeuralNetwork neuralNet, DataSet dataSet) {

        System.out.println("Calculating performance indicators for neural network.");

        Evaluation evaluation = new Evaluation();
        evaluation.addEvaluator(new ErrorEvaluator(new MeanSquaredError()));

        evaluation.addEvaluator(new ClassifierEvaluator.Binary(0.5));
        evaluation.evaluate(neuralNet, dataSet);

        ClassifierEvaluator evaluator = evaluation.getEvaluator(ClassifierEvaluator.Binary.class);
        ConfusionMatrix confusionMatrix = evaluator.getResult();
        System.out.println("Confusion matrrix:\r\n");
        System.out.println(confusionMatrix.toString() + "\r\n\r\n");
        System.out.println("Classification metrics\r\n");
        ClassificationMetrics[] metrics = ClassificationMetrics.createFromMatrix(confusionMatrix);
        ClassificationMetrics.Stats average = ClassificationMetrics.average(metrics);
        for (ClassificationMetrics cm : metrics) {
            System.out.println(cm.toString() + "\r\n");
        }
        System.out.println(average.toString());
    }
 

    public class LerningListenerDynamicBackProbagation implements LearningEventListener {

        @Override
        public void handleLearningEvent(LearningEvent event) {

            DynamicBackPropagation bp = (DynamicBackPropagation) event.getSource();
            int curentIteration = bp.getCurrentIteration();
            double Error = bp.getTotalNetworkError();

            if (curentIteration % (dataSet.size()) == 0 || curentIteration == 1) {
                if (Error < minError && (minError - Error) > 0.001) {
                    minError = Error;
                    neuralNetwork.save(pathOfModels + "/" + nameModel + ".lib");

                    try {
                        new Memory().save(pathOfModels + "/" + nameModel + ".wg", neuralNetwork.getWeights());
                    } catch (IOException ex) {
                        Logger.getLogger(CreateModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                try {

                    new Memory().saveCSV(nameModel, Error);
                } catch (IOException ex) {
                    Logger.getLogger(CreateModel.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.printf("E: %d/%d E: %.6f/%.6f M: %.0f/%.0f \n", curentIteration/dataSet.size(), bp.getMaxIterations()/dataSet.size(), bp.getTotalNetworkError(), bp.getMaxError(), bp.getMomentum() * 1.0E6, bp.getMaxMomentum() * 1.0E6);

            }
        }
    }

}
