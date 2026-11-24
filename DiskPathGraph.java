import java.awt.*;
import javax.swing.JPanel;

/**
 * Graph of the disk head's path (shared by SCAN and C-LOOK).
 * The cylinder line is on top, every step of the seek sequence goes one row down.
 * For circular algorithms (C-LOOK) the return jump is drawn red and dashed.
 */
class DiskPathGraph extends JPanel
{
    private static final int MARGIN = 50;
    private static final int TOP_Y = 60;
    private static final int ROW_HEIGHT = 30;
    private static final int LEGEND_SPACE = 90;

    private static final BasicStroke SOLID_STROKE = new BasicStroke(2.0F);
    private static final BasicStroke THIN_STROKE = new BasicStroke(1.0F);
    private static final BasicStroke DASHED_STROKE = new BasicStroke(
        2.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0F, new float[] { 10.0F }, 0.0F);

    private final int fmb_initialHead;
    private final int[] fmb_requests;
    private final int[] fmb_seekSequence;
    private final int fmb_diskSize;
    private final String fmb_direction;
    private final boolean fmb_circular;   // true for C-LOOK (the jump back is red and dashed)

    public DiskPathGraph(int fmb_initialHead, int[] fmb_requests, int[] fmb_seekSequence,
                         int fmb_diskSize, String fmb_direction, boolean fmb_circular)
    {
        this.fmb_initialHead = fmb_initialHead;
        this.fmb_requests = fmb_requests;
        this.fmb_seekSequence = fmb_seekSequence;
        this.fmb_diskSize = fmb_diskSize;
        this.fmb_direction = fmb_direction;
        this.fmb_circular = fmb_circular;
        int fmb_steps = Math.max(1, fmb_seekSequence.length - 1);
        int fmb_height = 100 + fmb_steps * ROW_HEIGHT + LEGEND_SPACE;
        setPreferredSize(new Dimension(900, Math.max(400, fmb_height)));
        setBackground(Color.WHITE);
    }

    protected void paintComponent(Graphics fmb_g)
    {
        super.paintComponent(fmb_g);
        Graphics2D fmb_g2d = (Graphics2D) fmb_g;
        fmb_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int fmb_lineWidth = Math.max(1, getWidth() - 2 * MARGIN);
        int fmb_maxCylinder = Math.max(1, fmb_diskSize - 1);
        double fmb_scale = (double) fmb_lineWidth / fmb_maxCylinder;

        // cylinder line with tick marks
        fmb_g2d.setColor(Color.BLACK);
        fmb_g2d.setStroke(SOLID_STROKE);
        fmb_g2d.drawLine(MARGIN, TOP_Y, MARGIN + fmb_lineWidth, TOP_Y);
        fmb_g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        int fmb_maxTicks = Math.max(1, fmb_lineWidth / 40);
        int fmb_tickInterval = Math.max(1, (int) Math.ceil(fmb_maxCylinder / fmb_maxTicks));
        if (fmb_tickInterval > 5)
            fmb_tickInterval = (fmb_tickInterval + 4) / 5 * 5;
        for (int fmb_i = 0; fmb_i <= fmb_maxCylinder; fmb_i += fmb_tickInterval)
        {
            int fmb_x = MARGIN + (int) Math.round(fmb_i * fmb_scale);
            fmb_g2d.drawLine(fmb_x, TOP_Y - 5, fmb_x, TOP_Y + 5);
            fmb_g2d.drawString(String.valueOf(fmb_i), fmb_x - 10, TOP_Y - 10);
        }
        if (fmb_maxCylinder % fmb_tickInterval >= fmb_tickInterval / 2)
        {
            int fmb_x = MARGIN + fmb_lineWidth;
            fmb_g2d.drawLine(fmb_x, TOP_Y - 5, fmb_x, TOP_Y + 5);
            fmb_g2d.drawString(String.valueOf(fmb_maxCylinder), fmb_x - 10, TOP_Y - 10);
        }

        // request positions
        fmb_g2d.setColor(Color.ORANGE);
        for (int fmb_req : fmb_requests)
        {
            int fmb_x = MARGIN + (int) Math.round(fmb_req * fmb_scale);
            fmb_g2d.fillOval(fmb_x - 4, TOP_Y - 4, 8, 8);
        }

        int fmb_steps = fmb_seekSequence.length - 1;
        int fmb_bottomLineY = 80 + Math.max(1, fmb_steps) * ROW_HEIGHT;
        fmb_g2d.setColor(Color.LIGHT_GRAY);
        fmb_g2d.setStroke(THIN_STROKE);
        fmb_g2d.drawLine(MARGIN, fmb_bottomLineY, MARGIN + fmb_lineWidth, fmb_bottomLineY);

        // head path, one row per step
        boolean fmb_goingRight = "right".equalsIgnoreCase(fmb_direction);
        int fmb_currentY = TOP_Y;
        for (int fmb_j = 0; fmb_j < fmb_steps; fmb_j++)
        {
            int fmb_currentPos = fmb_seekSequence[fmb_j];
            int fmb_nextPos = fmb_seekSequence[fmb_j + 1];
            int fmb_x1 = MARGIN + (int) Math.round(fmb_currentPos * fmb_scale);
            int fmb_x2 = MARGIN + (int) Math.round(fmb_nextPos * fmb_scale);
            int fmb_y1 = fmb_currentY;
            int fmb_y2 = fmb_currentY + ROW_HEIGHT;

            boolean fmb_isJump = fmb_circular
                && (fmb_goingRight ? (fmb_nextPos < fmb_currentPos) : (fmb_nextPos > fmb_currentPos));
            if (fmb_isJump)
            {
                fmb_g2d.setColor(Color.RED);
                fmb_g2d.setStroke(DASHED_STROKE);
            }
            else
            {
                fmb_g2d.setColor(Color.BLUE);
                fmb_g2d.setStroke(SOLID_STROKE);
            }
            fmb_g2d.drawLine(fmb_x1, fmb_y1, fmb_x2, fmb_y2);

            // arrow head and point label
            fmb_g2d.setStroke(SOLID_STROKE);
            double fmb_angle = Math.atan2(fmb_y2 - fmb_y1, fmb_x2 - fmb_x1);
            int fmb_arrowSize = 8;
            int fmb_ax1 = (int) (fmb_x2 - fmb_arrowSize * Math.cos(fmb_angle - Math.PI / 6));
            int fmb_ay1 = (int) (fmb_y2 - fmb_arrowSize * Math.sin(fmb_angle - Math.PI / 6));
            int fmb_ax2 = (int) (fmb_x2 - fmb_arrowSize * Math.cos(fmb_angle + Math.PI / 6));
            int fmb_ay2 = (int) (fmb_y2 - fmb_arrowSize * Math.sin(fmb_angle + Math.PI / 6));
            fmb_g2d.drawLine(fmb_x2, fmb_y2, fmb_ax1, fmb_ay1);
            fmb_g2d.drawLine(fmb_x2, fmb_y2, fmb_ax2, fmb_ay2);
            fmb_g2d.fillOval(fmb_x2 - 4, fmb_y2 - 4, 8, 8);
            fmb_g2d.setColor(Color.BLACK);
            fmb_g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
            fmb_g2d.drawString(String.valueOf(fmb_nextPos), fmb_x2 + 6, fmb_y2 - 6);
            fmb_currentY = fmb_y2;
        }

        // start position
        fmb_g2d.setColor(Color.GREEN);
        int fmb_startX = MARGIN + (int) Math.round(fmb_initialHead * fmb_scale);
        fmb_g2d.fillOval(fmb_startX - 6, TOP_Y - 6, 12, 12);

        // legend
        int fmb_legendY = fmb_bottomLineY + 40;
        fmb_g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        drawLegendDot(fmb_g2d, 50, fmb_legendY, Color.GREEN, "Start Position");
        drawLegendDot(fmb_g2d, 200, fmb_legendY, Color.ORANGE, "Request Positions");
        drawLegendLine(fmb_g2d, 370, fmb_legendY, Color.BLUE, SOLID_STROKE, "Seek Movement");
        if (fmb_circular)
            drawLegendLine(fmb_g2d, 550, fmb_legendY, Color.RED, DASHED_STROKE, "Circular Jump");
    }

    private void drawLegendDot(Graphics2D fmb_g2d, int fmb_x, int fmb_y, Color fmb_color, String fmb_label)
    {
        fmb_g2d.setColor(fmb_color);
        fmb_g2d.fillOval(fmb_x, fmb_y, 10, 10);
        fmb_g2d.setColor(Color.BLACK);
        fmb_g2d.drawString(fmb_label, fmb_x + 15, fmb_y + 10);
    }

    private void drawLegendLine(Graphics2D fmb_g2d, int fmb_x, int fmb_y, Color fmb_color,
                                BasicStroke fmb_stroke, String fmb_label)
    {
        fmb_g2d.setColor(fmb_color);
        fmb_g2d.setStroke(fmb_stroke);
        fmb_g2d.drawLine(fmb_x, fmb_y + 5, fmb_x + 30, fmb_y + 5);
        fmb_g2d.setColor(Color.BLACK);
        fmb_g2d.drawString(fmb_label, fmb_x + 35, fmb_y + 10);
    }
}
