import java.util.ArrayList;
import java.util.List;

/**
 * Shortest Job First (non-preemptive) - CPU scheduling.
 * Owner: Regina Dela Llarte
 *
 * Tie-break: shorter burst first, then earlier arrival, then lower process number.
 * Process = -1 in a Gantt segment means the CPU is idle.
 */
public class SJFScheduler implements CPUScheduler
{
    public String getName()
    {
        return "SJF";
    }

    public String getFullName()
    {
        return "Shortest Job First";
    }

    public String getResultsHeading(int rdl_quantum)
    {
        return "Shortest Job First (Non-Preemptive) Results";
    }

    public boolean needsQuantum()
    {
        return false;
    }

    // runs non-preemptive SJF (the quantum is not used)
    public CPUResult schedule(int[] rdl_at, int[] rdl_bt, int rdl_quantum)
    {
        int rdl_n = rdl_at.length;
        int[] rdl_ct = new int[rdl_n];
        List<int[]> rdl_gantt = new ArrayList<>();
        boolean[] rdl_done = new boolean[rdl_n];
        int rdl_time = 0;
        int rdl_completed = 0;

        // keep going until all processes are finished
        while (rdl_completed < rdl_n)
        {
            // pick the arrived process with the shortest burst time
            // (if tie choose earlier arrival, then lower process number)
            int rdl_pick = -1;
            for (int rdl_i = 0; rdl_i < rdl_n; rdl_i++)
            {
                if (!rdl_done[rdl_i] && rdl_at[rdl_i] <= rdl_time)
                {
                    if (rdl_pick == -1
                        || rdl_bt[rdl_i] < rdl_bt[rdl_pick]
                        || (rdl_bt[rdl_i] == rdl_bt[rdl_pick] && rdl_at[rdl_i] < rdl_at[rdl_pick]))
                        rdl_pick = rdl_i;
                }
            }

            // nothing has arrived yet, CPU is idle until the next arrival
            if (rdl_pick == -1)
            {
                // find the earliest arrival among unfinished processes
                int rdl_next = Integer.MAX_VALUE;
                for (int rdl_i = 0; rdl_i < rdl_n; rdl_i++)
                {
                    if (!rdl_done[rdl_i] && rdl_at[rdl_i] < rdl_next)
                        rdl_next = rdl_at[rdl_i];
                }
                rdl_gantt.add(new int[] { -1, rdl_time, rdl_next });
                rdl_time = rdl_next;
                continue;
            }

            // run the chosen process until it finishes (non-preemptive)
            rdl_gantt.add(new int[] { rdl_pick, rdl_time, rdl_time + rdl_bt[rdl_pick] });
            rdl_time += rdl_bt[rdl_pick];
            rdl_ct[rdl_pick] = rdl_time;   // completion time
            rdl_done[rdl_pick] = true;
            rdl_completed++;
        }

        return new CPUResult(rdl_at, rdl_bt, rdl_ct, rdl_gantt);
    }
}
