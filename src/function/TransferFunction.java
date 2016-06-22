package function;

import java.util.ArrayList;
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

    /**
     * A transfer function should have the following parameters supplied.
     *
     * @param gain      Default: 1
     * @param delay     Default: 0, must always be >=0.
     * @param zeros     Default: [0]
     * @param poles     Default: [1]
     */
    public TransferFunction(float gain, float delay, Float[] zeros, Float[] poles) {
        // TODO: Check for delay >= 0
        this.delay = delay;
        this.gain = gain;

        // Poles are sorted in reverse to make sure the most dominant
        // pole is the first element in the array.
        Arrays.sort(poles, Collections.reverseOrder());
        this.poles = poles;

        // Likewise.
        Arrays.sort(zeros, Collections.reverseOrder());
        this.zeros = zeros;

        // Checking for special rule regarding the positive zeros.
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
    public void display(boolean formula) {

        if (formula) {
            System.out.println(formula());
        }
        else {
            System.out.println("Gain: \t" + gain);
            System.out.println("Delay: \t" + delay);
            System.out.println("Zeros: \t " + Arrays.toString(zeros));
            System.out.println("Poles: \t " + Arrays.toString(poles));
        }

    }

    /**
     * Returns the transfer function as a latex formula.
     *
     * @return String
     */
    public String formula() {

        // poles: (1 + 10s)(1+5s)
        String fPoles = "";
        String temp;
        for (Float p : poles) {
            if (p < 0) {
                temp = "(1 - " + Math.abs(p) + "s)";
            }
            else {
                temp = "(1 + " + p + "s)";
            }
            fPoles += temp;
        }

        // zeros: (1 + 10s)(1 - 3s) etc.
        String fZeros = "";
        for (Float z : zeros) {
            if (z < 0) {
                temp = "(1 - " + Math.abs(z) + "s)";
            }
            else {
                temp = "(1 + " + z + "s)";
            }
            fZeros += temp;

        }

        String fGain = Float.toString(gain);

        String fDelay = "";
        if (delay > 0) {
            fDelay = "e^{-" + Float.toString(delay) + "\\tau}";
        }

        String formula = "W(s) = \\frac{" + fGain + fZeros +"}{" + fPoles + "}" + fDelay;

        return formula;
    }

    /**
     * FOPDT approximation of the transfer function.
     *
     * @param skotesgad
     * @return
     */
    public TransferFunction fopdt(boolean skotesgad){

        if (specialRule) {
            // TODO: Implement.
            return this;
        }
        else {
            if (skotesgad) {
                return fopdtSkotesgad();
            }
            else {
                return fopdtGen();
            }
        }

    }

    /**
     * General FOPDT approximation.
     *
     * @return TransferFunction
     */
    private TransferFunction fopdtGen() {

        // Initial delay value.
        float newDelay = delay;

        // Adding non-dominant poles to the delay.
        int i = 1;
        while (i < poles.length) {
            newDelay += poles[i];
            i++;
        }

        // Adding negative zeros to the delay.
        for (float z : zeros) {
            newDelay += Math.abs(z);
        }

        Float[] newPoles = {poles[0]};
        Float[] newZeros = {};

        return new TransferFunction(gain, newDelay, newZeros, newPoles);
    }

    /**
     * Skotesgad method FOPDT approximation.
     *
     * @return TransferFunction
     */
    private TransferFunction fopdtSkotesgad() {

        // We need at least 2 poles for this approximation.
        if (poles.length < 2) {
            return fopdtGen();
        }

        float newDelay = delay;

        // Adding non-dominant poles to the delay.
        newDelay += poles[1] / 2;

        int i = 2;
        while (i < poles.length) {
            newDelay += poles[i];
            i++;
        }

        // Adding the zeros to the delay.
        for (float z : zeros) {
            newDelay += Math.abs(z);
        }

        Float[] newPoles = {poles[0] + (poles[1]/2)};
        Float[] newZeros = {};

        return new TransferFunction(gain, newDelay, newZeros, newPoles);
    }

    /**
     * SOPDT approximation done in the Skotesgad method.
     *
     * NOTE: If there are less than 3 poles, an FOPDT (Skotesgad) is returned
     *       because of the way the approximation is implemented.
     *
     * @return TransferFunction
     */
    public TransferFunction sopdt() {

        if (poles.length < 3) {
            // First Skotesgad method is tried, if not enough poles
            // a general approximation is returned by the fopdt method.
            return fopdt(true);
        }

        float newDelay = delay;

        // Adding poles to the delay.
        newDelay += poles[2] / 2;

        // Adding the remainder of poles.
        int i = 3;
        while (i < poles.length) {
            newDelay += poles[i];
            i++;
        }

        // Adding the negative zeros to the delay.
        for (float z : zeros) {
            newDelay += Math.abs(z);
        }

        float pole1 = poles[0];
        float pole2 = poles[1] + (poles[2] / 2);
        Float[] newPoles = {pole1, pole2};
        Float[] newZeros = {};

        return new TransferFunction(gain, newDelay, newZeros, newPoles);
    }

}
