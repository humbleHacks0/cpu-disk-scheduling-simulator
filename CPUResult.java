import java.util.Arrays;
import java.util.List;

/**
 * Output of a CPU scheduling run:
 *   Turnaround  = Completion - Arrival
 *   Waiting     = Turnaround - Burst
 *   Response    = First CPU time - Arrival
 *   Utilization = busy time / total time * 100
 *   Throughput  = processes / total time
 * Gantt segment format: { process index (-1 = idle), start, end }.
 */
public class CPUResult
{
    public final int[] arrival, burst, completion, turnaround, waiting, response;
    public final List<int[]> gantt;
    public final int totalTime, busyTime;
    public final double avgCompletion, avgTurnaround, avgWaiting, avgResponse;
    public final double utilization, throughput;

    public CPUResult(int[] arrival, int[] burst, int[] completion, List<int[]> gantt)
    {
        int n = arrival.length;
        this.arrival = arrival;
        this.burst = burst;
        this.completion = completion;
        this.gantt = gantt;
        turnaround = new int[n];
        waiting = new int[n];
        response = new int[n];

        // first time each process gets the CPU and total busy time
        int[] firstStart = new int[n];
        Arrays.fill(firstStart, -1);
        int busy = 0;
        for (int[] seg : gantt)
        {
            if (seg[0] != -1)
            {
                busy += seg[2] - seg[1];
                if (firstStart[seg[0]] == -1)
                    firstStart[seg[0]] = seg[1];
            }
        }

        double sumCt = 0, sumTat = 0, sumWt = 0, sumRt = 0;
        for (int i = 0; i < n; i++)
        {
            turnaround[i] = completion[i] - arrival[i];
            waiting[i] = turnaround[i] - burst[i];
            response[i] = firstStart[i] - arrival[i];
            sumCt += completion[i];
            sumTat += turnaround[i];
            sumWt += waiting[i];
            sumRt += response[i];
        }

        busyTime = busy;
        totalTime = gantt.isEmpty() ? 0 : gantt.get(gantt.size() - 1)[2];
        avgCompletion = sumCt / n;
        avgTurnaround = sumTat / n;
        avgWaiting = sumWt / n;
        avgResponse = sumRt / n;
        utilization = (totalTime == 0) ? 0 : 100.0 * busyTime / totalTime;
        throughput = (totalTime == 0) ? 0 : (double) n / totalTime;
    }
}
