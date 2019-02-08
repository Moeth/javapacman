package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;
import de.moeth.tictactoe.Util;
import de.moeth.tictactoe.history.ActionHistory;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.ROCMultiClass;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NeuralNetAlgorithm implements KIAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(NeuralNetAlgorithm.class);
    private static final int ACTION_COUNT = 9;
    private static final int RNG_SEED = 123;
    private static final long[] SHAPE = {1, 18};

    private final String name;
    private final MultiLayerNetwork multiLayerNetwork;
    private List<DataSet> dataSets = new ArrayList<>();

    private NeuralNetAlgorithm(final String name, final MultiLayerNetwork multiLayerNetwork) {
        this.name = name;
        this.multiLayerNetwork = multiLayerNetwork;
    }

    public static NeuralNetAlgorithm load(String name) {
        try {
            MultiLayerNetwork multiLayerNetwork = ModelSerializer.restoreMultiLayerNetwork(name);
            return new NeuralNetAlgorithm(name, multiLayerNetwork);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static NeuralNetAlgorithm create(String name) {
        return new NeuralNetAlgorithm(name, buildTheNeuralNetwork());
    }

    @Override
    public INDArray getReward(final INDArray board) {
        Util.assertShape(board, Board.BOARD_LEARNING_SHAPE);

        return multiLayerNetwork.output(board.reshape(SHAPE));
    }

    public void changeValue(final ActionHistory.HistoryEntry historyEntry, final double reward) {
        INDArray result = zeroo(historyEntry.getAction(), reward);
        DataSet dataSet = new DataSet(historyEntry.getState(), result);
        dataSets.add(dataSet);

        Collections.shuffle(dataSets);
        ListDataSetIterator listDataSetIterator = new ListDataSetIterator(dataSets, 50);
        log.info("train " + dataSets.size());
        multiLayerNetwork.fit(listDataSetIterator);
    }

    public void trainWhole(final Collection<TrainWholeEntry> trainData) {
        dataSets.clear();
        convert(trainData);
        log.info("train " + dataSets.size());
    }

    private void convert(final Collection<TrainWholeEntry> trainData) {
        trainData.stream().map(this::createDataSet).forEach(dataSets::add);

        Collections.shuffle(dataSets);
        multiLayerNetwork.fit(new ListDataSetIterator(dataSets, 50), 10);
    }

    @Override
    public void train(final Collection<TrainSingleEntry> trainData) {
        if (dataSets.size() > 10) {
            dataSets = dataSets.subList(0, 10);
        }
        trainData.stream().map(this::createDataSet).forEach(dataSets::add);
        Collections.shuffle(dataSets);
        dataSets.forEach(d -> multiLayerNetwork.fit(d));
        log.info("train " + dataSets.size());
    }

    private DataSet createDataSet(final TrainWholeEntry train) {
        INDArray features = train.getState().reshape(SHAPE);
        INDArray labels = train.getResult();

        Util.assertShape(labels, Board.ACTION_SHAPE);
        Util.assertShape(features, SHAPE);
        return new DataSet(features, labels);
    }

    private DataSet createDataSet(final TrainSingleEntry train) {
        INDArray labels = train.getRewardChange();
        Util.norm(labels);

        INDArray features = train.getState().reshape(SHAPE);

        Util.assertShape(labels, Board.ACTION_SHAPE);
        Util.assertShape(features, SHAPE);
        return new DataSet(features, labels);
    }

    private INDArray zeroo(final long index, final double value) {
        INDArray result = Nd4j.zeros(Board.ACTION_SHAPE);
        result.putScalar(index, value);
        return result;
    }

    @Override
    public void storeData() throws IOException {
        ModelSerializer.writeModel(multiLayerNetwork, name, true);
    }

    public double evaluate() {
        Collections.shuffle(dataSets);

        Collections.shuffle(dataSets);
        double ddd = dataSets
                .stream()
                .map(d -> {
                    INDArray reward = multiLayerNetwork.output(d.getFeatures());
                    double distance2 = reward.dup().distance2(d.getLabels());
//                    log.info(
//                            "\ntest:      " + Util.toString(d.getFeatures())  +
//                            "\nexpect:    " + Util.toString(d.getLabels()) +
//                            "\nget:       " + Util.toString(reward) +
//                            "\ndistance2: " + distance2
//                    );
                    return distance2;
                })
                .collect(Collectors.summarizingDouble(d -> d))
                .getAverage();
        log.info("avarage diff " + ddd);
        return ddd;
    }

    public void evaluate(final DataSetIterator emnistTest) {
        /////////////////////////////////////////////
////        Evaluate the model
/////////////////////////////////////////////

        log.info("Evaluate the model");
        // evaluate basic performance
        Evaluation eval = multiLayerNetwork.evaluate(emnistTest);
        eval.accuracy();
        eval.precision();
        eval.recall();

// evaluate ROC and calculate the Area Under Curve
        ROCMultiClass roc = multiLayerNetwork.evaluateROCMultiClass(emnistTest);
        roc.calculateAverageAUC();

        int classIndex = 0;
        roc.calculateAUC(classIndex);

// optionally, you can print all stats from the evaluations
        log.info(eval.stats());
        log.info(roc.stats());
//        multiLayerNetwork.save(new File("mnsit.json"), true);
    }

    private static final int HIDDEN_LAYER_CONT = 3;
    private static final int HIDDEN_LAYER_WIDTH = 100;

    private static MultiLayerNetwork buildTheNeuralNetwork() {

        NeuralNetConfiguration.ListBuilder listBuilder = new NeuralNetConfiguration.Builder()
                .seed(RNG_SEED)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam())
//                .updater(new Nesterovs(0.01, 0.9))
//                .l2(0.01)
//                .l2(0.001)
                .l2(0.0001)
                .list();

        for (int i = 0; i < HIDDEN_LAYER_CONT; i++) {
            listBuilder.layer(new DenseLayer.Builder()
                    .nIn(i == 0 ? 18 : HIDDEN_LAYER_WIDTH) // Number of input datapoints.
                    .nOut(HIDDEN_LAYER_WIDTH) // Number of output datapoints.
                    .activation(Activation.RELU) // Activation function.
//                    .activation(Activation.SIGMOID) // Activation function.
                    .weightInit(WeightInit.XAVIER) // Weight initialization.
//                    .weightInit(WeightInit.DISTRIBUTION) // Weight initialization.
//                    .dist(new GaussianDistribution(0, 0.005))
//                    .hasBias(true)
//                    .biasInit(1)
//                    .dropOut(0.5)
                    .build());
        }
        MultiLayerConfiguration multiLayerConfiguration = listBuilder
//                .setInputType(InputType.convolutional(2,3,3))

//                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
//                        .kernelSize(3,3)
//                        .stride(3,3)
//                        .build())

//                .layer(new Convolution1DLayer.Builder(3,3)
//                        //Note that nIn need not be specified in later layers
//                        .nIn(18)
//                        .stride(1, 1)
//                        .nOut(18)
//                        .activation(Activation.IDENTITY)
//                        .build())

//                .layer(new ConvolutionLayer.Builder(3,3)
//                        //Note that nIn need not be specified in later layers
//                        .nIn(18)
//                        .stride(1, 1)
//                        .nOut(18)
//                        .activation(Activation.IDENTITY)
//                        .build())

//                .layer(new DenseLayer.Builder()
//                        .nIn(18) // Number of input datapoints.
//                        .nOut(1000) // Number of output datapoints.
//                        .activation(Activation.SIGMOID) // Activation function.
//                        .weightInit(WeightInit.XAVIER) // Weight initialization.
//                        .hasBias(true)
//                        .build())
//                .layer(new DenseLayer.Builder()
//                        .nIn(1000) // Number of input datapoints.
//                        .nOut(1000) // Number of output datapoints.
//                        .activation(Activation.SIGMOID) // Activation function.
//                        .weightInit(WeightInit.XAVIER) // Weight initialization.
//                        .hasBias(true)
//                        .build())
//                .layer(new DenseLayer.Builder()
//                        .nIn(100) // Number of input datapoints.
//                        .nOut(100) // Number of output datapoints.
//                        .activation(Activation.SIGMOID) // Activation function.
//                        .weightInit(WeightInit.XAVIER) // Weight initialization.
//                        .hasBias(true)
//                        .build())
//                .layer(new DenseLayer.Builder()
//                        .nIn(100) // Number of input datapoints.
//                        .nOut(100) // Number of output datapoints.
//                        .activation(Activation.RELU) // Activation function.
//                        .weightInit(WeightInit.XAVIER) // Weight initialization.
////                        .hasBias(true)
//                        .build())
//                .layer(new DenseLayer.Builder()
//                        .nIn(100) // Number of input datapoints.
//                        .nOut(100) // Number of output datapoints.
//                        .activation(Activation.SIGMOID) // Activation function.
//                        .weightInit(WeightInit.XAVIER) // Weight initialization.
////                        .hasBias(true)
//                        .build())
//                .layer(new DenseLayer.Builder()
////                        .nIn(81) // Number of input datapoints.
//                        .nOut(81) // Number of output datapoints.
//                        .activation(Activation.RELU) // Activation function.
//                        .weightInit(WeightInit.XAVIER) // Weight initialization.
//                        .build())
//                .layer(new DenseLayer.Builder()
////                        .nIn(81) // Number of input datapoints.
//                        .nOut(ACTION_COUNT) // Number of output datapoints.
//                        .activation(Activation.RELU) // Activation function.
//                        .weightInit(WeightInit.XAVIER) // Weight initialization.
//                        .build())

                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(HIDDEN_LAYER_WIDTH)
                        .nOut(ACTION_COUNT)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
//                        .hasBias(true)
                        .build())
                .pretrain(false)
                .backprop(true)
                .build();

        // create the MLN
        MultiLayerNetwork network = new MultiLayerNetwork(multiLayerConfiguration);
        network.init();

        network.setListeners(new ScoreIterationListener(100));

// pass a training listener that reports score every 10 iterations
//        network.addListeners(new ScoreIterationListener(10));

        return network;
    }
}
