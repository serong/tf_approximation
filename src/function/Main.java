/**
 *
 */
package function;

import java.util.DoubleSummaryStatistics;

public class Main {

    public static void main(String[] args) {
	// write your code here
        // TransferFunction tf = new TransferFunctionBuilder().buildTransferFunction();
        Float[] z = {-1f, -6f, -3f};
        Float[] p = {5f, 1f, 10f};

        TransferFunction tf = new TransferFunction(1, 3, z, p);
        tf.display(false);
        tf.fopdt(false).display(false);
    }



}
