/**
 *  SpecialRule.java
 *  ~~~~~~~~~~~~~~~~
 *
 *  TODO: Javadoc explanations.
 *  Description:     This class handles the Special Rule that happens when there
 *                   positive zeros.
 *
 *  Created:         2016.06.23
 *  Updated:         2016.06.23
 *  Author:          github/serong
 */

package function;

import java.lang.reflect.Array;

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
            Float[] zeroCandidates = tff.getZeros();
            float zeroCandidate = 0f;
            int zeroCandidateIndex = 0;

            Float[] poleCandidates = tff.getPoles();
            float poleCandidate = 0.f;
            int poleCandidateIndex = 0;

            // Find the zero candidate.
            int i = 0;
            while (i < zeroCandidates.length) {
                if (zeroCandidates[i] > 0) {
                    zeroCandidate = zeroCandidates[i];
                    zeroCandidateIndex = i;
                    break;
                }

                i++;
            }

            // Finding the nearest pole to the candidate zero.
            poleCandidateIndex = nearestPoleIndex(zeroCandidate, tff);
            poleCandidate = poleCandidates[poleCandidateIndex];

            // Special rule checks.
            if (cZPD(tff.getDelay(), poleCandidate, zeroCandidate)) {

                // TODO: Remove
                System.out.println("@SPEC: \t >>> ZPD rule is used.");

                float newGain = tff.getGain();
                newGain *= (zeroCandidate / poleCandidate);
                tff.setGain(newGain);

                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
            }
            else if (cZDP(tff.getDelay(), poleCandidate, zeroCandidate)) {

                // TODO: Remove
                System.out.println("@SPEC: \t >>> ZDP rule is used.");

                float newGain = tff.getGain();
                newGain *= (zeroCandidate / tff.getDelay());
                tff.setGain(newGain);

                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
            }
            else if (cDZP(tff.getDelay(), poleCandidate, zeroCandidate)) {

                // TODO: Remove
                System.out.println("@SPEC: \t >>> DZP rule is used.");

                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
            }
            else if (cPZD(tff.getDelay(), poleCandidate, zeroCandidate)) {

                // TODO: Remove
                System.out.println("@SPEC: \t >>> PZD rule is used.");

                float newGain = tff.getGain();
                newGain *= (zeroCandidate / poleCandidate);
                tff.setGain(newGain);

                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
            }
            else if (cMin(tff.getDelay(), poleCandidate, zeroCandidate)) {

                // TODO: Remove
                System.out.println("@SPEC: \t >>> MIN rule is used.");

                float temp = Math.min(poleCandidate, 5*tff.getDelay());

                float newGain = tff.getGain();
                newGain *= temp;
                tff.setGain(newGain);

                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
                tff.addToPoles(Math.abs(poleCandidate-zeroCandidate));
            }
            else {
                // This shouldn't happen... but just in case. :)

                // TODO: Remove
                System.out.println("@SPEC: \t >>> This SHOULDN'T have happened.");

                // FIXME: Just removing the zero is not the right solution.
                tff.removeZero(zeroCandidateIndex);
            }

            // Checking for specialRule attribute.
            boolean flag = false;
            for (float z : tff.getZeros()) {
                if (z > 0) {
                    flag = true;

                    // TODO: Remove
                    System.out.println("@SPEC: \t >>> A new positive zero is found.");
                }
            }
            tff.setSpecialRule(flag);

            // TODO: Remove
            System.out.println("@SPEC \t >>> WHILE loop.");
        }

        // After all this, specialRule should be set to false.
        // FIXME: Most likely redundant.
        tff.setSpecialRule(false);

        // TODO: Remove
        System.out.println("@SPEC: \t >>> Returning new TF.");

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
    public int nearestPoleIndex(float zero, TransferFunction tf) {

        // J: Index.
        int j = 0;
        float diff = 9999.f;

        Float[] poles = tf.getPoles();

        int i = 0;
        while (i < poles.length) {
            if ( Math.abs(zero - poles[i]) < diff) {
                j = i;
                diff = Math.abs(zero - poles[i]);
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
    public boolean cZPD(float delay, float pole, float zero) {
        double dDelay = (double) delay;
        double dPole = (double) pole;
        double dZero = (double) zero;

        return (dZero >= dPole && dPole >= dDelay);
    }

    public boolean cZDP(float delay, float pole, float zero) {
        double dDelay = (double) delay;
        double dPole = (double) pole;
        double dZero = (double) zero;

        return (dZero >= dDelay && dDelay >= dPole);
    }

    public boolean cDZP(float delay, float pole, float zero) {
        double dDelay = (double) delay;
        double dPole = (double) pole;
        double dZero = (double) zero;

        return (dDelay >= dZero && dZero >= dPole);
    }

    public boolean cPZD(float delay, float pole, float zero) {
        double dDelay = (double) delay;
        double dPole = (double) pole;
        double dZero = (double) zero;

        return (dPole >= dZero && dZero >= 5*dDelay);
    }

    public boolean cMin(float delay, float pole, float zero) {
        double dDelay = (double) delay;
        double dPole = (double) pole;
        double dZero = (double) zero;

        return (Math.min(dPole, 5*dDelay) >= dZero);
    }

    /* --------------------------------------------------------------------------------------------
    END: SPECIAL RULE HELPERS
    -------------------------------------------------------------------------------------------- */
}
