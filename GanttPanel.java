import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Gantt chart panel for the CPU algorithms. Drawn on a time scale: the width of
 * every box is proportional to its length, and idle time is shown as "Idle".
 * Segment format: { process index (-1 = idle), start, end }.
 */
class GanttPanel extends JPanel
{
    static Color[] rdl_colors = {
        new Color(255, 205, 210), new Color(187, 222, 251), new Color(200, 230, 201),
        new Color(255, 236, 179), new Color(225, 190, 231), new Color(178, 235, 242),
        new Color(255, 224, 178), new Color(220, 237, 200)
    };

    static int rdl_unit = 30;   // pixels per time unit (keeps the chart on a time scale)
    List<int[]> rdl_segments = new ArrayList<>();

    GanttPanel(List<int[]> rdl_segments)
    {
        setBackground(Color.WHITE);
        setSegments(rdl_segments);
    }

    // gives the panel new segments and resizes it
    void setSegments(List<int[]> rdl_segments)
    {
        this.rdl_segments = rdl_segments;

        int rdl_total = 40;
        for (int rdl_i = 0; rdl_i < rdl_segments.size(); rdl_i++)
            rdl_total += segWidth(rdl_segments.get(rdl_i));
        setPreferredSize(new Dimension(rdl_total, 90));
        revalidate();
        repaint();
    }

    static int segWidth(int[] rdl_seg)
    {
        return (rdl_seg[2] - rdl_seg[1]) * rdl_unit;
    }

    protected void paintComponent(Graphics rdl_g)
    {
        super.paintComponent(rdl_g);
        Graphics2D rdl_g2 = (Graphics2D) rdl_g;
        rdl_g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rdl_g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics rdl_fm = rdl_g2.getFontMetrics();

        int rdl_x = 20, rdl_y = 15, rdl_h = 45;
        int rdl_lastEnd = 0;

        for (int rdl_i = 0; rdl_i < rdl_segments.size(); rdl_i++)
        {
            int[] rdl_seg = rdl_segments.get(rdl_i);
            int rdl_w = segWidth(rdl_seg);
            String rdl_label;

            if (rdl_seg[0] == -1)
            {
                rdl_g2.setColor(new Color(224, 224, 224));
                rdl_label = "Idle";
            }
            else
            {
                rdl_g2.setColor(rdl_colors[rdl_seg[0] % rdl_colors.length]);
                rdl_label = "P" + (rdl_seg[0] + 1);
            }

            rdl_g2.fillRect(rdl_x, rdl_y, rdl_w, rdl_h);
            rdl_g2.setColor(Color.BLACK);
            rdl_g2.drawRect(rdl_x, rdl_y, rdl_w, rdl_h);

            int rdl_textX = rdl_x + (rdl_w - rdl_fm.stringWidth(rdl_label)) / 2;
            int rdl_textY = rdl_y + (rdl_h - rdl_fm.getHeight()) / 2 + rdl_fm.getAscent();
            rdl_g2.drawString(rdl_label, rdl_textX, rdl_textY);

            String rdl_start = String.valueOf(rdl_seg[1]);
            rdl_g2.drawString(rdl_start, rdl_x - rdl_fm.stringWidth(rdl_start) / 2, rdl_y + rdl_h + 20);

            rdl_lastEnd = rdl_seg[2];
            rdl_x += rdl_w;
        }

        if (!rdl_segments.isEmpty())
        {
            String rdl_end = String.valueOf(rdl_lastEnd);
            rdl_g2.drawString(rdl_end, rdl_x - rdl_fm.stringWidth(rdl_end) / 2, rdl_y + rdl_h + 20);
        }
    }
}
