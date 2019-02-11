package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Util;
import lombok.AllArgsConstructor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.stream.Stream;

public class ArrayMap implements Storable {

    private static final Logger log = LoggerFactory.getLogger(ArrayMap.class);

    private final long[] keyShape;
    private final long[] valueShape;
    private final List<RewardEntry> entries = new ArrayList<>();

    public ArrayMap(final long[] keyShape, final long[] valueShape) {
        this.keyShape = keyShape.clone();
        this.valueShape = valueShape.clone();
    }

    @Override
    public void read(final Reader reader) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                RewardEntry rewardEntry = RewardEntry.readLine(line);
                entries.add(rewardEntry);
            }
        }
    }

    @Override
    public void write(final Writer writer) throws IOException {
        sort(Util.IND_ARRAY_COMPARATOR);
        for (final RewardEntry rewardEntry : entries) {
            writer.append(rewardEntry.writeLine());
            writer.append('\r');
            writer.append('\n');
        }
    }

    public void sort(Comparator<INDArray> comparator) {
        Collections.sort(entries, (r1, r2) -> comparator.compare(r1.key, r2.key));
    }

    public int size() {
        return entries.size();
    }

    public Stream<? extends Map.Entry<INDArray, INDArray>> stream() {
        return entries.stream();
    }

    public void add(final INDArray key, INDArray value) {
        Util.assertShape(key, keyShape);
        Util.assertShape(value, valueShape);
        entries.add(new RewardEntry(key, value));
    }

    private Optional<? extends Map.Entry<INDArray, INDArray>> find(INDArray key) {
        Util.assertShape(key, keyShape);
        return stream()
                .filter(e -> e.getKey().equals(key))
                .findFirst();
    }

    public Optional<INDArray> findValue(INDArray key) {
        Util.assertShape(key, keyShape);
        return find(key)
                .map(Map.Entry::getValue);
    }

    @AllArgsConstructor
    private static class RewardEntry implements Map.Entry<INDArray, INDArray> {

        private final INDArray key;
        private INDArray value;

        private static RewardEntry readLine(final String line) {
            String[] nextLine = line.split("\t");
            INDArray board = readArray(nextLine[0]);
            INDArray action = readArray(nextLine[1]);
            return new RewardEntry(board, action);
        }

        private static INDArray readArray(final String tempLine1) {

            String[] split = tempLine1.split("#");

            double[] result = Util.gson.fromJson(split[1], double[].class);
            int[] shape = Util.gson.fromJson(split[0], int[].class);

            return Nd4j.create(result, shape, 'c');
        }

        private String writeLine() {
//            Util.norm(value);
            return writeArray(key) + "\t" + writeArray(value);
        }

        private String writeArray(INDArray array) {
            return Util.gson.toJson(array.shape()) + "#" + Util.toString(array);
        }

        @Override
        public INDArray getKey() {
            return key;
        }

        @Override
        public INDArray getValue() {
            return value;
        }

        @Override
        public INDArray setValue(final INDArray value) {
            final INDArray old = this.value;
            this.value = value;
            return old;
        }
    }
}
