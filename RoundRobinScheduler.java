import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Round Robin (preemptive) - CPU scheduling.
 * Owner: Matthew Aguirre
 *
 * A new arrival is queued before a preempted process that re-enters the queue
 * at the same instant. Process = -1 in a Gantt segment means the CPU is idle.
 */
public class RoundRobinScheduler implements CPUScheduler
{
    public String getName()
    {
        return "Round Robin";
    }

    public String getFullName()
    {
        return "Round Robin";
    }

    public String getResultsHeading(int MCAQuantum)
    {
        return "Round Robin (Time Quantum: " + MCAQuantum + ") Results";
    }

    public boolean needsQuantum()
    {
        return true;
    }

    // runs Round Robin, fills completion times and the gantt segments
    public CPUResult schedule(int[] MCAArrival, int[] MCABurst, int MCAQuantum)
    {
        int MCAN = MCAArrival.length;
        int[] MCACompletion = new int[MCAN];
        List<int[]> MCAGantt = new ArrayList<>();
        int[] MCARemaining = new int[MCAN];
        boolean[] MCAInQueue = new boolean[MCAN];
        Queue<Integer> MCAQueue = new LinkedList<>();
        int MCATime = 0;
        int MCAFinished = 0;

        for (int MCAI = 0; MCAI < MCAN; MCAI++)
            MCARemaining[MCAI] = MCABurst[MCAI];

        while (MCAFinished < MCAN)
        {
            addArrivals(MCAArrival, MCAInQueue, MCAQueue, MCATime);

            // nothing in the queue, CPU is idle until the next arrival
            if (MCAQueue.isEmpty())
            {
                int MCANextTime = Integer.MAX_VALUE;
                for (int MCAI = 0; MCAI < MCAN; MCAI++)
                {
                    if (!MCAInQueue[MCAI] && MCAArrival[MCAI] < MCANextTime)
                        MCANextTime = MCAArrival[MCAI];
                }
                addToGantt(MCAGantt, -1, MCATime, MCANextTime);
                MCATime = MCANextTime;
                continue;
            }

            int MCAP = MCAQueue.poll();
            int MCARunTime = Math.min(MCAQuantum, MCARemaining[MCAP]);

            addToGantt(MCAGantt, MCAP, MCATime, MCATime + MCARunTime);
            MCATime += MCARunTime;
            MCARemaining[MCAP] -= MCARunTime;

            addArrivals(MCAArrival, MCAInQueue, MCAQueue, MCATime);

            if (MCARemaining[MCAP] > 0)
            {
                MCAQueue.add(MCAP);
            }
            else
            {
                MCACompletion[MCAP] = MCATime;    // finished, save the completion time
                MCAFinished++;
            }
        }

        return new CPUResult(MCAArrival, MCABurst, MCACompletion, MCAGantt);
    }

    // puts every process that has arrived but is not in the queue yet
    private static void addArrivals(int[] MCAArrival, boolean[] MCAInQueue, Queue<Integer> MCAQueue, int MCATime)
    {
        for (int MCAI = 0; MCAI < MCAArrival.length; MCAI++)
        {
            if (!MCAInQueue[MCAI] && MCAArrival[MCAI] <= MCATime)
            {
                MCAQueue.add(MCAI);
                MCAInQueue[MCAI] = true;
            }
        }
    }

    // adds one gantt box (joins it to the last box if it is the same process and continues it)
    private static void addToGantt(List<int[]> MCAGantt, int MCAProcess, int MCAStart, int MCAEnd)
    {
        int MCALast = MCAGantt.size() - 1;   // last box (-1 if there are no boxes yet)

        if (MCALast >= 0 && MCAGantt.get(MCALast)[0] == MCAProcess && MCAGantt.get(MCALast)[2] == MCAStart)
            MCAGantt.get(MCALast)[2] = MCAEnd;
        else
            MCAGantt.add(new int[] { MCAProcess, MCAStart, MCAEnd });
    }
}
