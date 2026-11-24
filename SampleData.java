/**
 * Load Data Sample Button (practice problems from previous discussions)
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
