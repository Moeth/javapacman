package de.moeth.tictactoe.algorithm;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.util.Collection;

public interface KIAlgorithm {

    INDArray getReward(INDArray board);
//    int getAction(INDArray board);

    void train(Collection<TrainSingleEntry> trainData);

    void storeData() throws IOException;
}
