package function;

/**
 * Created by xserkan on 23.06.2016.
 */
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

        TransferFunction tff = tf;

        // TODO: Remove
        System.out.println("@SPEC: \t >>> TFF SpecialRule - " + tff.getSpecialRule());

        while (tff.getSpecialRule()) {

            Float[] zeroCandidates = tff.getZeros();
            float zeroCandidate = 0f;
            int zeroCandidateIndex = 0;

            Float[] poleCandidates = tff.getPoles();
            float poleCandidate = 0.f;
            int poleCandidateIndex = 0;

            int i = 0;

            while (i < zeroCandidates.length) {
                if (zeroCandidates[i] > 0) {
                    zeroCandidate = zeroCandidates[i];
                    zeroCandidateIndex = i;
                    break;
                }

                i++;
            }

            // Finding the nearest
            poleCandidateIndex = nearestPoleIndex(zeroCandidate, tff);
            poleCandidate = poleCandidates[poleCandidateIndex];

            if (cZPD(tff.getDelay(), poleCandidate, zeroCandidate)) {

                // TODO: Remove
                System.out.println("@SPEC: \t >>> ZPD rule is used.");

                float newGain = tff.getGain();
                newGain *= (zeroCandidate / poleCandidate);
                tff.setGain(newGain);
                tff.removePole(poleCandidateIndex);
                tff.removeZero(zeroCandidateIndex);
            }

            // Special rule check.
            boolean flag = false;
            for (float z : tff.getZeros()) {
                if (z > 0) {
                    flag = true;

                    // TODO: Remove
                    System.out.println("A new positive zero.");
                }

            }


            tff.setSpecialRule(flag);

            // TODO: Remove
            System.out.println("WHILE:");
        }

        tff.setSpecialRule(false);

        // TODO: Remove
        System.out.println("Setting special to false.");

        return tff;
    }

    public int nearestPoleIndex(float zero, TransferFunction tf) {
        int i = 0;
        int j = 0;
        float diff = 9999.f;
        float poleCandidate = 0f;

        Float[] poles = tf.getPoles();

        while (i < poles.length) {
            if ( Math.abs(zero - poles[i]) < diff) {
                poleCandidate = poles[i];
                j = i;
                diff = Math.abs(zero - poles[i]);
            }

            i++;
        }

        return j;
    }

    // Comparation helpers.
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
}
