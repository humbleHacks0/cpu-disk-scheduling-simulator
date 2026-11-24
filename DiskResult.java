/**
 * Output of a disk scheduling run: the seek sequence, the head movement of each
 * step, the total head movement and the average seek distance
 * (total head movement / number of requests).
 */
public class DiskResult
{
    public final int initialHead, diskSize, totalHeadMovement;
    public final String direction;
    public final int[] requests, seekSequence, stepDistances;
    public final double averageSeekDistance;

    public DiskResult(int[] requests, int initialHead, String direction, int diskSize, int[] seekSequence)
    {
        this.requests = requests;
        this.initialHead = initialHead;
        this.direction = direction;
        this.diskSize = diskSize;
        this.seekSequence = seekSequence;

        stepDistances = new int[Math.max(0, seekSequence.length - 1)];
        int total = 0;
        for (int i = 1; i < seekSequence.length; i++)
        {
            stepDistances[i - 1] = Math.abs(seekSequence[i] - seekSequence[i - 1]);
            total += stepDistances[i - 1];
        }
        totalHeadMovement = total;
        averageSeekDistance = (requests.length == 0) ? 0.0 : (double) total / requests.length;
    }
}
