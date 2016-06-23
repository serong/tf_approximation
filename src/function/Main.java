/**
 *
 */
package function;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // TransferFunction tf = new TransferFunctionBuilder().buildTransferFunction();
        List<Double> z = new ArrayList<>();
        z.add(-1d);
        z.add(-6d);
        z.add(-3d);

        List<Double> p = new ArrayList<>();
        p.add(4d);
        p.add(1d);
        p.add(10d);
        p.add(20d);

        TransferFunction tf = new TransferFunction(1, 5, z, p);
        System.out.println(tf.toString());
        System.out.println(tf.fopdt(false).toString());
        System.out.println(tf.sopdt().toString());

    }
}
