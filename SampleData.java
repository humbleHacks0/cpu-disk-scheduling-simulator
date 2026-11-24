/**
 * Sample inputs for the "Load Sample Data" buttons.
 * NOTE: replace these values with the facilitator's practice problem sets.
 * The CPU set has 10 processes and an idle gap (CPU idle from 29 to 40).
 * The disk set has 20 requests, with a duplicate (67), a request equal to the
 * head (53) and requests on both boundaries (0 and 199).
 */
public class SampleData
{
    // { arrival time, burst time } for P1..P10
    public static final int[][] CPU_PROCESSES = {
        { 0, 5 }, { 1, 3 }, { 2, 8 }, { 3, 2 }, { 4, 4 },
        { 6, 1 }, { 7, 6 }, { 40, 3 }, { 41, 5 }, { 42, 2 }
    };
    public static final int CPU_QUANTUM = 3;

    public static final int[] DISK_REQUESTS = {
        98, 183, 37, 122, 14, 124, 65, 67, 199, 0,
        53, 20, 150, 90, 45, 160, 30, 110, 75, 67
    };
    public static final int DISK_HEAD = 53;
    public static final int DISK_SIZE = 200;
    public static final String DISK_DIRECTION = "right";
}
