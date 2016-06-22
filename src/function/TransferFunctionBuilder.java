package function;

/**
 * Created by xserkan on 22.06.2016.
 */
public class TransferFunctionBuilder {
    private float gain = 1;
    private float delay = 0;
    private Float[] zeros = {0f};
    private Float[] poles = {1f};

    public TransferFunction buildTransferFunction() {
        return new TransferFunction(gain, delay, zeros, poles);
    }
}
