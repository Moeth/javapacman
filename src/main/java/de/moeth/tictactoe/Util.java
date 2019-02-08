package de.moeth.tictactoe;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);
    public static final Gson gson = new Gson();

    public static void assertShape(INDArray indArray, long[] shape) {
        long[] current = indArray.shape();
        Preconditions.checkArgument(Arrays.equals(current, shape), "expect " + Arrays.toString(shape) + " get " + Arrays.toString(current));
    }

    public static String toString(INDArray array) {
        return gson.toJson(array.data().asDouble());
    }

    public static void norm(INDArray array) {
        Number norm2Number = array.norm1Number();
        Preconditions.checkArgument(norm2Number.doubleValue() > 0.0);
        array.divi(norm2Number);

        assertNorm(array);
    }

    public static void assertNorm(final INDArray array) {
        final Number norm2Number = array.norm1Number();
        Preconditions.checkArgument(Math.abs(norm2Number.doubleValue() - 1.0) < 0.0001, "norm2 " + norm2Number);
    }
}
