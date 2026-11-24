import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * C-LOOK - disk scheduling.
 * Owner: Maxine Basada
 *
 * The head serves requests in the chosen direction up to the last pending
 * request (it does not go to the disk end), then jumps to the farthest pending
 * request on the other side and continues in the same direction.
 * The return jump is counted in the total head movement.
 */
public class CLookScheduler implements DiskScheduler
{
    public String getName()
    {
        return "C-LOOK";
    }

    public boolean isCircular()
    {
        return true;
    }

    // runs C-LOOK and returns the seek sequence (starts with the initial head position)
    public int[] schedule(int[] fmb_requests, int fmb_head, String fmb_direction, int fmb_diskSize)
    {
        List<Integer> fmb_firstPass, fmb_secondPass;
        List<Integer> fmb_lower = new ArrayList<>();
        List<Integer> fmb_upper = new ArrayList<>();
        boolean fmb_goingRight = "right".equalsIgnoreCase(fmb_direction);

        // split the requests into lower and upper groups around the head
        for (int fmb_request : fmb_requests)
        {
            if (fmb_goingRight)
            {
                if (fmb_request < fmb_head)
                    fmb_lower.add(fmb_request);
                else
                    fmb_upper.add(fmb_request);
            }
            else if (fmb_request <= fmb_head)
            {
                fmb_lower.add(fmb_request);
            }
            else
            {
                fmb_upper.add(fmb_request);
            }
        }
        Collections.sort(fmb_lower);
        Collections.sort(fmb_upper);

        // first pass goes in the chosen direction, second pass is after the jump
        if (fmb_goingRight)
        {
            fmb_firstPass = fmb_upper;
            fmb_secondPass = fmb_lower;
        }
        else
        {
            fmb_firstPass = new ArrayList<>(fmb_lower);
            Collections.reverse(fmb_firstPass);
            fmb_secondPass = new ArrayList<>(fmb_upper);
            Collections.reverse(fmb_secondPass);
        }

        List<Integer> fmb_seekSequence = new ArrayList<>();
        fmb_seekSequence.add(fmb_head);
        fmb_seekSequence.addAll(fmb_firstPass);
        fmb_seekSequence.addAll(fmb_secondPass);

        int[] fmb_out = new int[fmb_seekSequence.size()];
        for (int fmb_i = 0; fmb_i < fmb_out.length; fmb_i++)
            fmb_out[fmb_i] = fmb_seekSequence.get(fmb_i);
        return fmb_out;
    }
}
