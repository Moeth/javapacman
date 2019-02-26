package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractKIAlgorithm implements KIAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(AbstractKIAlgorithm.class);

    @Override
    public final void evaluate(final List<TrainWholeEntry> dataAsTrainingData) {
        double diff = dataAsTrainingData.stream()
                .map(data -> {
                    Optional<INDArray> output = output(data.getState());
                    Optional<INDArray> ddd = output
                            .map(d -> d.sub(data.getResult()));
                    double value = ddd
                            .orElse(data.getResult())
                            .norm2Number()
                            .doubleValue();

                    return value;
                })
                .collect(Collectors.summarizingDouble(d -> d))
                .getAverage();
        log.info("diff " + diff);

        double actionDiff = dataAsTrainingData.stream()
                .map(data -> {

                    Double aDouble = output(data.getState())
                            .map(o -> {
                                Board board = Board.createFromLearningData(data.getState());
                                INDArray resultArray = data.getResult();
                                Integer bestAction = AlgorithmUtil.rewardToAction(board, resultArray);
                                Integer action = AlgorithmUtil.rewardToAction(board, o);
                                return resultArray.getDouble(bestAction) - resultArray.getDouble(action);
                            })

//                            .map(i -> AlgorithmUtil.getPosition(board, data.getResult(), i))
                            .orElseThrow(() -> new IllegalArgumentException());
                    return aDouble;
                })
                .collect(Collectors.summarizingDouble(d -> d))
                .getAverage();
        log.info("actionDiff " + actionDiff);
    }

    public abstract Optional<INDArray> output(final INDArray state);
}
