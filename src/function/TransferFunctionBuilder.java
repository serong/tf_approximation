/**
 *  TransferFunctionBuilder.java
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  TODO: Javadoc explanations.
 *  Description:     This class deals with creating TransferFunction object
 *                   with default values.
 *
 *  Created:         2016.06.22
 *  Updated:         2016.06.23
 *  Author:          github/serong
 */
package function;

public class TransferFunctionBuilder {
    private float gain = 1;
    private float delay = 0;
    private Float[] zeros = {0f};
    private Float[] poles = {1f};

    public TransferFunction buildTransferFunction() {
        return new TransferFunction(gain, delay, zeros, poles);
    }
}
