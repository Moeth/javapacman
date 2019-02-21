package de.moeth.tictactoe.algorithm;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractKIAlgorithm implements KIAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(AbstractKIAlgorithm.class);

    //    public void updateReward(final double reward);

    @Override
    public final void evaluate(final List<TrainWholeEntry> dataAsTrainingData) {
//        Optional<INDArray> rewardEntry = arrayMap.findValue(train.getState());

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
                .getSum();
        log.info("diff " + diff);
    }

    abstract Optional<INDArray> output(final INDArray state);
}
