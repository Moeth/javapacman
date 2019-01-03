package de.moeth.tictactoe;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeuralNetAlgorithm implements KIAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(NeuralNetAlgorithm.class);
    private static final int ACTION_COUNT = 9;
    public static final int RNG_SEED = 123;
    // number of "pixel rows" in an mnist digit
    public static final int PIXELS = 28;

    private final MultiLayerNetwork multiLayerNetwork;

    public NeuralNetAlgorithm() {
        multiLayerNetwork = buildTheNeuralNetwork();
    }

    @Override
    public INDArray getReward(final INDArray board) {
        return multiLayerNetwork.output(board);
    }

    @Override
    public void changeValue(final INDArray state, final int action, final double reward) {
    }

    @Override
    public void storeData() {

    }

//    public static void main(String[] args) throws IOException {
//
//        ///////////////////////////////////////////
////        Prepare data for loading
//        ///////////////////////////////////////////
//
//        int batchSize = 16; // how many examples to simultaneously train in the network
//        EmnistDataSetIterator.Set emnistSet = EmnistDataSetIterator.Set.MNIST;
//        EmnistDataSetIterator emnistTrain = new EmnistDataSetIterator(emnistSet, batchSize, true);
//        EmnistDataSetIterator emnistTest = new EmnistDataSetIterator(emnistSet, batchSize, false);
//
/////////////////////////////////////////////
////        Train the model
//        ///////////////////////////////////////////
//
//        log.info("Train the model");
//
//// fit a dataset for a single epoch
//        network.fit(emnistTrain);
//
//
//// fit for multiple epochs
//// int numEpochs = 2
//// network.fit(new MultipleEpochsIterator(numEpochs, emnistTrain))
//
/////////////////////////////////////////////
////        Evaluate the model
/////////////////////////////////////////////
//
//        log.info("Evaluate the model");
//        // evaluate basic performance
//        Evaluation eval = network.evaluate(emnistTest);
//        eval.accuracy();
//        eval.precision();
//        eval.recall();
//
//// evaluate ROC and calculate the Area Under Curve
//        ROCMultiClass roc = network.evaluateROCMultiClass(emnistTest);
//        roc.calculateAverageAUC();
//
//        int classIndex = 0;
//        roc.calculateAUC(classIndex);
//
//// optionally, you can print all stats from the evaluations
//        System.out.print(eval.stats());
//        System.out.print(roc.stats());
//        network.save(new File("mnsit.json"), true);
//    }

    private static MultiLayerNetwork buildTheNeuralNetwork() {

        MultiLayerConfiguration multiLayerConfiguration = new NeuralNetConfiguration.Builder()
                .seed(RNG_SEED)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam())
                .l2(0.0001)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(9) // Number of input datapoints.
                        .nOut(1000) // Number of output datapoints.
                        .activation(Activation.RELU) // Activation function.
                        .weightInit(WeightInit.XAVIER) // Weight initialization.
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(1000)
                        .nOut(ACTION_COUNT)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false)
                .backprop(true)
                .build();

        // create the MLN
        MultiLayerNetwork network = new MultiLayerNetwork(multiLayerConfiguration);
        network.init();

//        network.setListeners(new ScoreIterationListener(1));

// pass a training listener that reports score every 10 iterations
        network.addListeners(new ScoreIterationListener(10));

        return network;
    }
}
