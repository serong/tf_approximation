/**
 *  SpecialRule.java
 *  ~~~~~~~~~~~~~~~~
 *
 *  @Description:     This class handles the Special Rule that happens when there
 *                    positive zeros.
 *
 *  @Created:         2016.06.23
 *  @Updated:         2016.06.23
 *  @Author:          github/serong
 */

package function;

import java.util.List;

public class SpecialRule {

    private TransferFunction tf;

    public SpecialRule(TransferFunction tFunc) {
        tf = tFunc;
    }

    /**
     * Get the new Transfer Function after Special Rule approximation.
     *
     * @return TransferFunction
     */
    public TransferFunction newTf() {

        // Temporary transfer function.
        TransferFunction tff = tf;

        // Approximate while specialRule attribute is true.
        // FIXME: Might cause some issues when there are more than 1 positive zeros.
        while (tff.getSpecialRule()) {

            // Candidates for the approximation attempt.
            List<Double> zeroCandidates = tff.getZeros();
            Double zeroCandidate = 0d;
            int zeroCandidateIndex = 0;

            List<Double> poleCandidates = tff.getPoles();
            Double poleCandidate = 0.d;
            int poleCandidateIndex = 0;

            // Find the zero candidate.
            int i = 0;
            while (i < zeroCandidates.size()) {
                if (zeroCandidates.get(i) > 0) {
                    zeroCandidate = zeroCandidates.get(i);
                    zeroCandidateIndex = i;
                    break;
                }

                i++;
            }

            // Finding the nearest pole to the candidate zero.
            poleCandidateIndex = nearestPoleIndex(zeroCandidate, tff);
            poleCandidate = poleCandidates.get(poleCandidateIndex);

            // Special rule checks.
            if (cZPD(tff.getDelay(), poleCandidate, zeroCandidate)) {

                double newGain = tff.getGain();
                newGain *= (zeroCandidate / poleCandidate);
                tff.setGain(newGain);

                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
            }
            else if (cZDP(tff.getDelay(), poleCandidate, zeroCandidate)) {

                double newGain = tff.getGain();
                newGain *= (zeroCandidate / tff.getDelay());
                tff.setGain(newGain);

                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
            }
            else if (cDZP(tff.getDelay(), poleCandidate, zeroCandidate)) {

                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
            }
            else if (cPZD(tff.getDelay(), poleCandidate, zeroCandidate)) {

                double newGain = tff.getGain();
                newGain *= (zeroCandidate / poleCandidate);
                tff.setGain(newGain);

                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
            }
            else if (cMin(tff.getDelay(), poleCandidate, zeroCandidate)) {

                double temp = Math.min(poleCandidate, 5*tff.getDelay());

                double newGain = tff.getGain();
                newGain *= temp;
                tff.setGain(newGain);

                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
                tff.addToPoles(Math.abs(poleCandidate-zeroCandidate));
            }
            else {
                // This shouldn't happen... but just in case. :)

                // FIXME: Just removing the zero is not the right solution.
                tff.removeZero(zeroCandidateIndex);
            }

            // Checking for specialRule attribute.
            boolean flag = false;
            for (Double z : tff.getZeros()) {
                if (z > 0) {
                    flag = true;
                }
            }
            tff.setSpecialRule(flag);
        }

        // After all this, specialRule should be set to false.
        // FIXME: Most likely redundant.
        tff.setSpecialRule(false);

        return tff;
    }

    /**
     * Given a zero, find the nearest pole candidate index.
     *
     * @param zero zero.
     * @param tf transfer function.
     *
     * @return index value.
     */
    public int nearestPoleIndex(double zero, TransferFunction tf) {

        // J: Index.
        int j = 0;
        double diff = 9999d;

        List<Double> poles = tf.getPoles();

        int i = 0;
        while (i < poles.size()) {
            if ( Math.abs(zero - poles.get(i)) < diff) {
                j = i;
                diff = Math.abs(zero - poles.get(i));
            }

            i++;
        }

        return j;
    }

    /* --------------------------------------------------------------------------------------------
    SPECIAL RULE HELPERS

    For reference:
    http://a-lab.ee/edu/system/files/kristina.vassiljeva/courses/ISS0065/2015_Autumn/
    materials/AV15_L4.pdf
    -------------------------------------------------------------------------------------------- */
    public boolean cZPD(double delay, double pole, double zero) {
        return (zero >= pole && pole >= delay);
    }

    public boolean cZDP(double delay, double pole, double zero) {
        return (zero >= delay && delay >= pole);
    }

    public boolean cDZP(double delay, double pole, double zero) {
        return (delay >= zero && zero >= pole);
    }

    public boolean cPZD(double delay, double pole, double zero) {
        return (pole >= zero && zero >= 5*delay);
    }

    public boolean cMin(double delay, double pole, double zero) {
        return (Math.min(pole, 5*delay) >= zero);
    }

    /* --------------------------------------------------------------------------------------------
    END: SPECIAL RULE HELPERS
    -------------------------------------------------------------------------------------------- */
}
