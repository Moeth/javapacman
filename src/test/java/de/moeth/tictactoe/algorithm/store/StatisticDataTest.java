package de.moeth.tictactoe.algorithm.store;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticDataTest {

    @Test
    void getSum() {
        assertEquals(0, StatisticData.create().getSum());
        assertEquals(5, StatisticData.create(5).getSum());
    }

    @Test
    void getCount() {
        StatisticData statisticData = new StatisticData();
        assertEquals(0, statisticData.getCount());
        statisticData.addValue(5);
        assertEquals(1, statisticData.getCount());

        assertEquals(0, StatisticData.create().getCount());
        assertEquals(1, StatisticData.create(5).getCount());
    }

    @Test
    void getAverage() {
        assertEquals(NaN, StatisticData.create().getAverage());
        assertEquals(5, StatisticData.create(5).getAverage());
        assertEquals(6, StatisticData.create(5, 7).getAverage());
    }

    @Test
    void getVariance() {
        assertEquals(NaN, StatisticData.create().getVariance());
        assertEquals(0, StatisticData.create(5).getVariance());
        assertEquals(1, StatisticData.create(5, 7).getVariance());
        assertEquals(0.6666666666666643, StatisticData.create(5, 6, 7).getVariance());
        assertEquals(1, StatisticData.create(5, 5, 7, 7).getVariance());
        assertEquals(25, StatisticData.create(10, 20).getVariance());
        assertEquals(833.25, StatisticData.create(IntStream.range(0, 100).asDoubleStream()).getVariance());
    }

    @Test
    void getDeviation() {
        assertEquals(NaN, StatisticData.create().getDeviation());
        assertEquals(0, StatisticData.create(5).getDeviation());
        assertEquals(1, StatisticData.create(5, 7).getDeviation());
        assertEquals(0.8164965809277246, StatisticData.create(5, 6, 7).getDeviation());
        assertEquals(1, StatisticData.create(5, 5, 7, 7).getDeviation());
        assertEquals(5, StatisticData.create(10, 20).getDeviation());
        assertEquals(28.86607004772212, StatisticData.create(IntStream.range(0, 100).asDoubleStream()).getDeviation());
    }
}
