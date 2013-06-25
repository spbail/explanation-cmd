package uk.ac.manchester.cs.owl.explanation;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorInterruptedException;
import org.semanticweb.owl.explanation.api.ExplanationProgressMonitor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Set;

/**
 * Created by
 * User: Samantha Bail
 * Date: 29/01/2012
 * Time: 20:17
 * The University of Manchester
 */


public class TimeoutProgressMonitor<E> implements ExplanationProgressMonitor<E> {

    private long JUST_TIMEOUT_NS;
    private long ENT_TIMEOUT_NS;

    private static ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    private long justificationStartTime = bean.getCurrentThreadUserTime();
    private long entailmentStartTime = bean.getCurrentThreadUserTime();

    boolean entailmentInterrupted = false;
    boolean justificationInterrupted = false;

    public TimeoutProgressMonitor(long justificationTimeoutMinutes, long entailmentTimeoutMinutes) {
        JUST_TIMEOUT_NS = Util.minutesToNanoSeconds(justificationTimeoutMinutes);
        ENT_TIMEOUT_NS = Util.minutesToNanoSeconds(entailmentTimeoutMinutes);
    }

    /**
     * @param explanationGenerator explanation Generator
     * @param explanation          found explanation
     * @param allExplanations      set of all found explanations
     */

    public void foundExplanation(ExplanationGenerator<E> explanationGenerator,
                                 Explanation<E> explanation,
                                 Set<Explanation<E>> allExplanations) {

        System.out.print(".");

        // reset the timer that times how long it takes to find 1 justification
        resetJustificationTimer();

        // then check whether we have already used up all the time for this entailment
        long elapsedTime = bean.getCurrentThreadUserTime() - entailmentStartTime;
        boolean timeOut = elapsedTime > ENT_TIMEOUT_NS;

        if (timeOut) {
            System.err.println("Timing out on entailment.");
            entailmentInterrupted = true;
            resetEntailmentTimer();
            throw new ExplanationGeneratorInterruptedException();
        }
    }


    /**
     * @return true if the operation has timed out, false if it has not timed out yet.
     */
    public boolean isCancelled() {
        long elapsedTime = bean.getCurrentThreadUserTime() - justificationStartTime;
        boolean timeOut = elapsedTime > JUST_TIMEOUT_NS;
        if (timeOut) {
            System.err.println("Timing out on justification.");
            justificationInterrupted = true;
            resetJustificationTimer();
        }
        return (timeOut);
    }

    /**
     * @return true if the justification generation has been interrupted at some point
     */
    public boolean entailmentInterrupted() {
        return entailmentInterrupted;
    }

    public boolean justificationInterrupted() {
        return justificationInterrupted;
    }


    /**
     * resets the timer per entailment
     */
    private void resetEntailmentTimer() {
        entailmentStartTime = bean.getCurrentThreadUserTime();
    }

    /**
     * resets the timer per justification
     */
    private void resetJustificationTimer() {
        justificationStartTime = bean.getCurrentThreadUserTime();
    }


}

