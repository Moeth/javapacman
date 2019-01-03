package de.moeth.tictactoe;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface KIAlghorithm {

    double getReward(INDArray board, int action);

    void changeValue(INDArray state, int action, double reward);

    void storeData();
}
