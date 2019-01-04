package de.moeth.tictactoe;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);
    public final static Gson gson = new Gson();

    public static void assertShape(INDArray indArray, long[] shape) {
        long[] current = indArray.shape();
        Preconditions.checkArgument(Arrays.equals(current, shape), "expect " + Arrays.toString(shape) + " get " + Arrays.toString(current));
    }

    public static String toString(INDArray array) {
        return Util.gson.toJson(array.data().asDouble());
    }
}
