/**
 * Shared interface for every CPU scheduling algorithm (agreed by the group).
 * The user interface only talks to this interface, so a new algorithm can be
 * plugged in without rewriting the GUI.
 */
public interface CPUScheduler
{
    /** Short name used in window titles (e.g. "SJF"). */
    String getName();

    /** Full display name (e.g. "Shortest Job First"). */
    String getFullName();

    /** Heading shown at the top of the results window. */
    String getResultsHeading(int quantum);

    /** true if the algorithm needs a time quantum (Round Robin only). */
    boolean needsQuantum();

    /**
     * Runs the algorithm. Process i has arrival[i] and burst[i].
     * quantum is ignored by algorithms that do not need it.
     */
    CPUResult schedule(int[] arrival, int[] burst, int quantum);
}
