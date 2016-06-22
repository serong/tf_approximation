/**
 *
 */
package function;

public class Main {

    public static void main(String[] args) {
	// write your code here
        TransferFunction tf = new TransferFunctionBuilder().buildTransferFunction();

        tf.info();
    }
}
