package de.moeth.tictactoe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.nd4j.linalg.api.ndarray.INDArray;

@AllArgsConstructor
@ToString
@Getter
class HistoryEntry {

    private final INDArray state;
    private final INDArray reward;
    private final int action;
}
