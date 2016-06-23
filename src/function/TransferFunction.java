/**
 *  TransferFunction.java
 *  ~~~~~~~~~~~~~~~~~~~~~
 *
 *  @Description:     This class deals with Transfer Function and its
 *                    approximations.
 *
 *  @Created:         2016.06.22
 *  @Updated:         2016.06.23
 *  @Author:          github/serong
 */

package function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TransferFunction {

    List<Double> zeros;
    List<Double> poles;

    // Flag for special that is applied when there is
    // one or more positive zeros.
    private boolean specialRule;

    private double gain;
    private double delay;

    /**
     * A transfer function should have the following parameters supplied.
     *
     * @param gain      Default: 1
     * @param delay     Default: 0, must always be >=0.
     * @param zeros     Default: [0]
     * @param poles     Default: [1]
     */
    public TransferFunction(double gain, double delay, List<Double> zeros, List<Double> poles) {

        this.delay = Math.abs(delay);
        this.gain = gain;

        // Poles are sorted in reverse to make sure the most dominant
        // pole is the first element in the array.
        Collections.sort(poles);
        Collections.reverse(poles);
        this.poles = poles;

        // Likewise.
        Collections.sort(zeros);
        Collections.reverse(zeros);
        this.zeros = zeros;

        // Checking for special rule regarding the positive zeros.
        this.specialRule = false;

        for (Double zero : zeros) {
            if (zero > 0) {
                this.specialRule = true;
                break;
            }
        }

    }

    /* --------------------------------------------------------------------------------------------
    GETTERS and SETTERS
    -------------------------------------------------------------------------------------------- */
    public Double getGain() {
        return gain;
    }

    public Double getDelay() {
        return delay;
    }

    public List<Double> getPoles() {
        return poles;
    }

    public List<Double> getZeros() {
        return zeros;
    }

    public boolean getSpecialRule() {
        return specialRule;
    }

    public void setGain(double g) {
        gain = g;
    }

    public void setDelay(double d) {
        delay = d;
    }

    public void setSpecialRule(boolean b) {
        specialRule = b;
    }

    /**
     * Remove a zero from the zeros list.
     *
     * @param i zero's index.
     */
    public void removeZero(int i) {
        zeros.remove(i);
    }

    /**
     * Remove a pole from the poles list.
     *
     * @param i pole's index.
     */
    public void removePole(int i) {
        poles.remove(i);
    }

    /**
     * Add given pole to the poles.
     *
     * @param pole the pole.
     */
    public void addToPoles(Double pole) {
        poles.add(pole);
    }

    /* --------------------------------------------------------------------------------------------
    END: GETTERS and SETTERS
    -------------------------------------------------------------------------------------------- */

    @Override
    public String toString() {
        String result = "Transfer Function parameters: \n" +
                "-----------------------------\n" +
                "Gain: \t" + gain + "\n" +
                "Delay: \t" + delay + "\n" +
                "Poles: \t" + poles.toString() + "\n" +
                "Zeros: \t" + zeros.toString() + "\n";

        return result;
    }

    /**
     * Returns the transfer function as a latex formula.
     *
     * @return String
     */
    public String latex() {

        // poles: (1 + 10s)(1+5s)
        String fPoles = "";
        String temp;

        for (Double p : poles) {
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
        for (Double z : zeros) {
            if (z < 0) {
                temp = "(1 - " + Math.abs(z) + "s)";
            }
            else {
                temp = "(1 + " + z + "s)";
            }
            fZeros += temp;

        }

        String fGain = Double.toString(gain);

        String fDelay = "";
        if (delay > 0) {
            fDelay = "e^{-" + Double.toString(delay) + "\\tau}";
        }

        String formula = "W(s) = \\frac{" + fGain + fZeros +"}{" + fPoles + "}" + fDelay;

        return formula;
    }

    /**
     * FOPDT approximation of the transfer function.
     *
     * @param skotesgad whether S will be used or not.
     * @return TransferFunction
     */
    public TransferFunction fopdt(boolean skotesgad){

        if (specialRule) {
            TransferFunction newTf = new SpecialRule(this).newTf();

            return newTf.fopdt(skotesgad);
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
        double newDelay = delay;

        // Adding non-dominant poles to the delay.
        int i = 1;
        while (i < poles.size()) {
            newDelay += poles.get(i);
            i++;
        }

        // Adding negative zeros to the delay.
        for (Double z : zeros) {
            newDelay += Math.abs(z);
        }

        List<Double> newPoles = new ArrayList<>();
        newPoles.add(poles.get(0));

        List<Double> newZeros = new ArrayList<>();

        return new TransferFunction(gain, newDelay, newZeros, newPoles);
    }

    /**
     * Skotesgad method FOPDT approximation.
     *
     * @return TransferFunction
     */
    private TransferFunction fopdtSkotesgad() {

        // We need at least 2 poles for this approximation.
        if (poles.size() < 2) {
            return fopdtGen();
        }

        double newDelay = delay;

        // Adding non-dominant poles to the delay.
        newDelay += poles.get(1) / 2;

        int i = 2;
        while (i < poles.size()) {
            newDelay += poles.get(i);
            i++;
        }

        // Adding the zeros to the delay.
        for (Double z : zeros) {
            newDelay += Math.abs(z);
        }

        List<Double> newPoles = new ArrayList<>();
        newPoles.add(poles.get(0) + (poles.get(1)/2));

        List<Double> newZeros = new ArrayList<>();

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

        if (poles.size() < 3) {
            // First Skotesgad method is tried, if not enough poles
            // a general approximation is returned by the fopdt method.
            return fopdt(true);
        }

        if (specialRule) {
            TransferFunction newTf = new SpecialRule(this).newTf();

            return newTf.sopdt();
        }

        double newDelay = delay;

        // Adding poles to the delay.
        newDelay += poles.get(2) / 2;

        // Adding the remainder of poles.
        int i = 3;
        while (i < poles.size()) {
            newDelay += poles.get(i);
            i++;
        }

        // Adding the negative zeros to the delay.
        for (Double z : zeros) {
            newDelay += Math.abs(z);
        }

        Double pole1 = poles.get(0);
        Double pole2 = poles.get(1) + (poles.get(2) / 2);
        List<Double> newPoles = new ArrayList<>();
        newPoles.add(pole1);
        newPoles.add(pole2);

        List<Double> newZeros = new ArrayList<>();

        return new TransferFunction(gain, newDelay, newZeros, newPoles);
    }

}
