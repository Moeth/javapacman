package de.moeth.tictactoe.algorithm;

import com.google.common.base.Preconditions;
import de.moeth.tictactoe.Board;
import de.moeth.tictactoe.Util;
import org.deeplearning4j.datasets.iterator.ExistingDataSetIterator;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NeuralNetAlgorithm extends AbstractKIAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(NeuralNetAlgorithm.class);
    private static final int ACTION_COUNT = 9;
    private static final int RNG_SEED = 123;
    private static final long[] SHAPE = {1, 18};

    private final String name;
    private final MultiLayerNetwork multiLayerNetwork;

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
    public int getBestAction(final Board board, final int playerNumber) {
        Integer action = AlgorithmUtil.rewardToAction(board, getReward(board, playerNumber));
        Preconditions.checkArgument(board.isAllowedAction(action));
        return action;
    }

    @Override
    public INDArray getReward(final Board board, final int playerNumber) {
        return getReward(board.getBoard(playerNumber));
    }

    @Override
    public void trainWhole(final List<TrainWholeEntry> trainData) {
        log.info("train " + trainData.size());
        List<DataSet> collect = trainData.stream()
                .map(trainWholeEntry -> new DataSet(mapState(trainWholeEntry.getState()), trainWholeEntry.getResult()))
                .collect(Collectors.toList());
        multiLayerNetwork.fit(new MultipleEpochsIterator(5, new ExistingDataSetIterator(collect)));
    }

    @Override
    public List<TrainWholeEntry> getTrainWholeData() {
//        return null;
        throw new IllegalArgumentException();
    }

    private INDArray getReward(final INDArray board) {
        Util.assertShape(board, Board.BOARD_LEARNING_SHAPE);

        return multiLayerNetwork.output(mapState(board));
    }

    //    @Override
    public void train(final List<TrainSingleEntry> trainData) {
        List<INDArray> preLearn = trainData.stream().map(d -> outputIntern(d.getState())).collect(Collectors.toList());
        List<DataSet> collect = trainData.stream().map(this::createDataSet)
                .collect(Collectors.toList());
        multiLayerNetwork.fit(new MultipleEpochsIterator(5, new ExistingDataSetIterator(collect)));

        List<INDArray> learn = trainData.stream().map(d -> outputIntern(d.getState())).collect(Collectors.toList());
        double sum = 0;
        for (int i = 0; i < trainData.size(); i++) {
//            diff =
            INDArray pre = preLearn.get(i);
            INDArray res = learn.get(i);

            INDArray target = collect.get(i).getLabels();

            double before = pre.add(target.mul(-1)).norm2Number().doubleValue();
            double after = res.add(target.mul(-1)).norm2Number().doubleValue();

            double diff = after - before;
//            if (diff > 0.1) {
//                log.info(String.format("%f -> %f (%f) (\n" +
//                        "pre:    %s\n" +
//                        "res:    %s\n" +
//                        "target: %s)", before, after, diff, pre, res, target));
//            }
            sum += diff;
//            log.info(String.format("%s -> %s (%s)", pre, res, target.getReward()));
        }
        if (sum > 0.1) {
            log.error("What " + sum);
        }
    }

    //    @Override
    public List<TrainSingleEntry> getDataAsTrainingData() {
        throw new IllegalArgumentException();
    }

    public Optional<INDArray> output(INDArray state) {
        return Optional.of(outputIntern(state));
    }

    private INDArray outputIntern(INDArray state) {
        return multiLayerNetwork.output(mapState(state));
    }

    private DataSet createDataSet(final TrainSingleEntry train) {
        INDArray features = mapState(train.getState());

        INDArray output = outputIntern(train.getState());
        output.putScalar(train.getAction(), train.getReward());
//        output.addi(train.getRewardChange());
//        Util.norm(output);
//        result.putScalar(action, reward);

//        INDArray labels = train.getRewardChange();
//        Util.norm(labels);

        Util.assertShape(output, Board.ACTION_SHAPE);
        return new DataSet(features, output);
    }

    private INDArray mapState(INDArray state) {
        INDArray reshape = state.reshape(SHAPE);
        Util.assertShape(reshape, SHAPE);
        return reshape;
    }

    @Override
    public void storeData() throws IOException {
        ModelSerializer.writeModel(multiLayerNetwork, name, true);
    }

    private static final int HIDDEN_LAYER_CONT = 8;
    private static final int HIDDEN_LAYER_WIDTH = 1000;

    private static MultiLayerNetwork buildTheNeuralNetwork() {

        NeuralNetConfiguration.ListBuilder listBuilder = new NeuralNetConfiguration.Builder()
                .seed(RNG_SEED)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam())
//                .updater(new Nesterovs(0.01, 0.9))
                .l2(1e-4)
//                .l2(0.01)
//                .l2(0.001)
//                .l2(0.0001)
                .list();

        for (int i = 0; i < HIDDEN_LAYER_CONT; i++) {
            listBuilder = listBuilder.layer(new DenseLayer.Builder()
                    .nIn(i == 0 ? AlgorithmUtil.shadeToNodeCount(Board.BOARD_LEARNING_SHAPE) : HIDDEN_LAYER_WIDTH) // Number of input datapoints.
                    .nOut(HIDDEN_LAYER_WIDTH) // Number of output datapoints.
                    .activation(Activation.SIGMOID) // Activation function.
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
