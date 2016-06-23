/**
 *
 */
package function;

public class Main {

    public static void main(String[] args) {
        // TransferFunction tf = new TransferFunctionBuilder().buildTransferFunction();
        Float[] z = {-1f, -6f, -3f};
        Float[] p = {4f, 1f, 10f, 20f};

        TransferFunction tf = new TransferFunction(1, 5, z, p);
        tf.display(false);
        tf.sopdt().display(false);
    }
}
