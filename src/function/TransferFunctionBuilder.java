/**
 *  TransferFunctionBuilder.java
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  @Description:     This class deals with creating TransferFunction object
 *                    with default values.
 *
 *  @Created:         2016.06.22
 *  @Updated:         2016.06.23
 *  @Author:          github/serong
 */
package function;

import java.util.ArrayList;
import java.util.List;

public class TransferFunctionBuilder {

    public TransferFunction buildTransferFunction() {
        double g = 1d;
        double d = 0d;
        List<Double> z = new ArrayList<>();
        z.add(0d);

        List<Double> p = new ArrayList<>();
        p.add(1d);

        return new TransferFunction(g, d, z, p);
    }
}
