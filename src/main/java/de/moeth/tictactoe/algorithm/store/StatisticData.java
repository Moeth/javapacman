package de.moeth.tictactoe.algorithm.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.DoubleStream;

public class StatisticData {

    private static final Logger log = LoggerFactory.getLogger(StatisticData.class);
    private double sum = 0;
    private int count = 0;
    private double deviation = 0;

    public static StatisticData create(double... values) {
        StatisticData statisticData = new StatisticData();
        statisticData.addValues(values);
        return statisticData;
    }

    public static StatisticData create(DoubleStream values) {
        StatisticData statisticData = new StatisticData();
        statisticData.addValues(values);
        return statisticData;
    }

    public double getSum() {
        return sum;
    }

    public int getCount() {
        return count;
    }

    public double getVariance() {
        return deviation / count - Math.pow(getAverage(), 2);
    }

    public double getDeviation() {
        return Math.sqrt(getVariance());
    }

    public double getAverage() {
        return sum / count;
    }

    public void addValues(double... values) {
        addValues(Arrays.stream(values));
    }

    public void addValues(DoubleStream values) {
        values.forEach(this::addValue);
    }

    public void addValue(double value) {
        sum += value;
        count++;
        deviation += value * value;
    }
}
