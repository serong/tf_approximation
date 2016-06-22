/**
 *
 */
package function;

public class Main {

    public static void main(String[] args) {
	// write your code here
        // TransferFunction tf = new TransferFunctionBuilder().buildTransferFunction();
        Float[] z = {-1f, -4f, -3f};
        Float[] p = {5f, 1f, 10f};
        TransferFunction tf = new TransferFunction(1, 5, z, p);
        tf.display(true);
        tf.fopdt(false).display(true);
        tf.fopdt(true).display(true);
    }
}
