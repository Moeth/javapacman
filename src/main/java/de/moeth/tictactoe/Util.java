package de.moeth.tictactoe;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;

public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);
    public static final Gson gson = new Gson();

    public static final Comparator<INDArray> IND_ARRAY_COMPARATOR = (r1, r2) -> {
        Preconditions.checkArgument(r1.equalShapes(r2));

        DataBuffer d1 = r1.data();
        DataBuffer d2 = r2.data();
        Preconditions.checkArgument(d1.length() == d2.length());
        for (int i = 0; i < d1.length(); i++) {
            int compare = Double.compare(d1.getDouble(i), d2.getDouble(i));
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    };

    public static void assertShape(INDArray indArray, long[] shape) {
        long[] current = indArray.shape();
        Preconditions.checkArgument(Arrays.equals(current, shape), "expect " + Arrays.toString(shape) + " get " + Arrays.toString(current));
    }

    public static String toString(INDArray array) {
        return gson.toJson(array.data().asDouble());
    }

    public static void norm(INDArray array) {
        double norm2Number = Math.abs(array.norm2Number().doubleValue());
//        if (norm2Number == 0.0) {
//            return;
//        }
        Preconditions.checkArgument(norm2Number > 0.0, array + " is null");
        array.divi(norm2Number);

        assertNorm(array);
    }

    public static void assertNorm(final INDArray array) {
        final Number norm2Number = array.norm2Number();
        Preconditions.checkArgument(Math.abs(norm2Number.doubleValue() - 1.0) < 0.0001, "norm2 " + norm2Number);
    }
}
