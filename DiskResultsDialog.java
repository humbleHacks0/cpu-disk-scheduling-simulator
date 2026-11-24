import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Results window shared by SCAN and C-LOOK: the order of service (seek sequence),
 * the head movement of each step, the total head movement, the average seek
 * distance and the graph of the head's path.
 */
class DiskResultsDialog extends JDialog
{
    public DiskResultsDialog(JFrame fmb_parent, DiskScheduler fmb_scheduler, DiskResult fmb_result)
    {
        super(fmb_parent, fmb_scheduler.getName() + " Results", true);
        setSize(1100, 750);
        setLocationRelativeTo(fmb_parent);
        setLayout(new BorderLayout(10, 10));

        JLabel fmb_title = new JLabel(fmb_scheduler.getName() + " Disk Scheduling Results", SwingConstants.CENTER);
        fmb_title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(fmb_title, BorderLayout.NORTH);

        // summary labels
        JPanel fmb_resultsPanel = new JPanel();
        fmb_resultsPanel.setLayout(new BoxLayout(fmb_resultsPanel, BoxLayout.Y_AXIS));
        fmb_resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        fmb_resultsPanel.add(createLabel("Initial Head Position: " + fmb_result.initialHead));
        fmb_resultsPanel.add(createLabel("Disk Size: " + fmb_result.diskSize
                                         + " (cylinders 0 to " + (fmb_result.diskSize - 1) + ")"));
        fmb_resultsPanel.add(createLabel("Direction: " + fmb_result.direction));
        fmb_resultsPanel.add(createLabel("Request Queue: " + Arrays.toString(fmb_result.requests)));
        fmb_resultsPanel.add(Box.createVerticalStrut(8));
        fmb_resultsPanel.add(createLabel("Seek Sequence (order of service): " + Arrays.toString(fmb_result.seekSequence)));
        fmb_resultsPanel.add(Box.createVerticalStrut(8));
        JLabel fmb_totalLabel = new JLabel("Total Head Movement: " + fmb_result.totalHeadMovement + " cylinders");
        fmb_totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        fmb_totalLabel.setForeground(new Color(0, 100, 0));
        fmb_totalLabel.setAlignmentX(0.0F);
        fmb_resultsPanel.add(fmb_totalLabel);
        fmb_resultsPanel.add(createLabel(String.format("Average Seek Distance: %.2f cylinders",
                                                       fmb_result.averageSeekDistance)));

        // head movement for each step
        DefaultTableModel fmb_model = new DefaultTableModel(
            new String[] { "Step", "From", "To", "Head Movement" }, 0)
        {
            public boolean isCellEditable(int fmb_row, int fmb_col)
            {
                return false;
            }
        };
        for (int fmb_i = 0; fmb_i < fmb_result.stepDistances.length; fmb_i++)
            fmb_model.addRow(new Object[] { fmb_i + 1, fmb_result.seekSequence[fmb_i],
                                            fmb_result.seekSequence[fmb_i + 1], fmb_result.stepDistances[fmb_i] });
        JTable fmb_table = new JTable(fmb_model);
        fmb_table.setRowHeight(22);
        JScrollPane fmb_tableScroll = new JScrollPane(fmb_table);
        fmb_tableScroll.setBorder(BorderFactory.createTitledBorder("Head Movement per Step"));

        // graph of the head's path
        DiskPathGraph fmb_graph = new DiskPathGraph(fmb_result.initialHead, fmb_result.requests,
            fmb_result.seekSequence, fmb_result.diskSize, fmb_result.direction, fmb_scheduler.isCircular());
        JScrollPane fmb_graphScroll = new JScrollPane(fmb_graph);
        fmb_graphScroll.setBorder(BorderFactory.createTitledBorder("Graph of the Head's Path"));

        JSplitPane fmb_split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fmb_tableScroll, fmb_graphScroll);
        fmb_split.setDividerLocation(300);

        JPanel fmb_mainPanel = new JPanel(new BorderLayout(10, 10));
        fmb_mainPanel.add(fmb_resultsPanel, BorderLayout.NORTH);
        fmb_mainPanel.add(fmb_split, BorderLayout.CENTER);
        add(fmb_mainPanel, BorderLayout.CENTER);

        JPanel fmb_buttonPanel = new JPanel();
        JButton fmb_closeButton = new JButton("Close");
        fmb_closeButton.addActionListener(e -> dispose());
        fmb_buttonPanel.add(fmb_closeButton);
        add(fmb_buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String fmb_text)
    {
        JLabel fmb_label = new JLabel(fmb_text);
        fmb_label.setAlignmentX(0.0F);
        return fmb_label;
    }
}
