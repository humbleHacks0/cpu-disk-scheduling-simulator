/**
 * Shared interface for every disk scheduling algorithm (agreed by the group).
 */
public interface DiskScheduler
{
    /** Name used in window titles (e.g. "SCAN"). */
    String getName();

    /** true if the return jump is a circular jump (C-SCAN / C-LOOK), used by the graph. */
    boolean isCircular();

    /**
     * Runs the algorithm and returns the seek sequence.
     * The first element is always the initial head position.
     * direction is "left" or "right"; cylinders run from 0 to diskSize - 1.
     */
    int[] schedule(int[] requests, int head, String direction, int diskSize);
}
