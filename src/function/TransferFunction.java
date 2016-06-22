package function;

import java.util.Arrays;
import java.util.Collections;

/**
 * Transfer function class.
 *
 * TODO: Javadoc explanations.
 * Description:     This class deals with Transfer Function and its
 *                  approximations.
 * Created:         2016.06.22
 * Updated:         2016.06.22
 * Author:          github/serong
 */
public class TransferFunction {

    private float gain;
    private float delay;
    private Float[] zeros;
    private Float[] poles;

    // Flag for special that is applied when there is
    // one or more positive zeros.
    private boolean specialRule;

    public TransferFunction(float gain, float initialDelay, Float[] zeros, Float[] poles) {
        this.gain = gain;
        this.delay = initialDelay;
        this.zeros = zeros;

        Arrays.sort(poles, Collections.reverseOrder());
        this.poles = poles;

        /**
         * The check for special rule regarding the positive zeros.
         */
        this.specialRule = false;
        for (float zero : zeros) {
            if (zero > 0) {
                this.specialRule = true;
                break;
            }
        }
    }

    /**
     * Get information about the transfer function.
     */
    public void info() {
        System.out.println("Gain: \t" + gain);
        System.out.println("Delay: \t" + delay);

        System.out.print("Zeros: \t ");
        for (float zero : zeros) {
            System.out.print(zero + " ");
        }
        System.out.println(" ");


        System.out.print("Zeros: \t ");
        for (float pole : poles) {
            System.out.print(pole + " ");
        }
        System.out.println(" ");
    }

}
