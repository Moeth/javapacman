package de.moeth.tictactoe.algorithm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.nd4j.linalg.api.ndarray.INDArray;

@AllArgsConstructor
@ToString
@Getter
public class TrainWholeEntry {

    private final INDArray state;
    private final INDArray result;
}
