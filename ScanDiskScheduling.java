import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class ScanDiskScheduling
{
    static int ajv_min_request = 5;
    static int ajv_max_request = 20;

    // runs SCAN and returns the result text
    static String SCAN(int[] ajv_arr, int ajv_head, String ajv_direction, int ajv_diskSize)
    {
        int ajv_seekCount = 0;
        int ajv_distance, ajv_curTrack;
        List<Integer> ajv_left = new ArrayList<>();
        List<Integer> ajv_right = new ArrayList<>();
        List<Integer> ajv_seekSequence = new ArrayList<>();

        // add the end of the disk, which must be visited
        // before reversing direction
        if (ajv_direction.equals("left"))
            ajv_left.add(0);
        else
            // numbers higher than initial head, gets diskSize - 1 kasi start from 0 (0-199)
            ajv_right.add(ajv_diskSize - 1);

        for (int ajv_i = 0; ajv_i < ajv_arr.length; ajv_i++)
        {
            if (ajv_arr[ajv_i] < ajv_head)
                ajv_left.add(ajv_arr[ajv_i]);
            if (ajv_arr[ajv_i] > ajv_head)
                ajv_right.add(ajv_arr[ajv_i]);
        }

        // sorts numbers to the left if less than head, to the right if greater than head
        Collections.sort(ajv_left);
        Collections.sort(ajv_right);

        // two passes: first in the chosen/given direction then other way
        int ajv_run = 2;
        while (ajv_run-- > 0)
        {
            if (ajv_direction.equals("left"))
            {
                for (int ajv_i = ajv_left.size() - 1; ajv_i >= 0; ajv_i--)
                {
                    ajv_curTrack = ajv_left.get(ajv_i);
                    ajv_seekSequence.add(ajv_curTrack);
                    ajv_distance = Math.abs(ajv_curTrack - ajv_head);
                    ajv_seekCount += ajv_distance;
                    ajv_head = ajv_curTrack;
                }
                ajv_direction = "right";
            }
            else
            {
                for (int ajv_i = 0; ajv_i < ajv_right.size(); ajv_i++)
                {
                    ajv_curTrack = ajv_right.get(ajv_i);
                    ajv_seekSequence.add(ajv_curTrack);
                    ajv_distance = Math.abs(ajv_curTrack - ajv_head);
                    ajv_seekCount += ajv_distance;
                    ajv_head = ajv_curTrack;
                }
                ajv_direction = "left";
            }
        }

        StringBuilder ajv_sb = new StringBuilder();
        ajv_sb.append("Total number of seek operations = ").append(ajv_seekCount).append("\n\n");
        ajv_sb.append("Seek Sequence:\n");
        for (int ajv_i = 0; ajv_i < ajv_seekSequence.size(); ajv_i++)
        {
            ajv_sb.append(ajv_seekSequence.get(ajv_i));
            if (ajv_i < ajv_seekSequence.size() - 1)
                ajv_sb.append(" -> ");
        }
        return ajv_sb.toString();
    }

    static void showError(Component ajv_parent, String ajv_message)
    {
        JOptionPane.showMessageDialog(ajv_parent, ajv_message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    // checks if valid number then runs SCAN
    static void onRun(JFrame ajv_frame, JTextField ajv_headField, JTextField ajv_sizeField,
                      JTextField ajv_requestField, JComboBox<String> ajv_dirBox)
    {
        int ajv_head, ajv_diskSize;
        try
        {
            // input diskSize
            ajv_diskSize = Integer.parseInt(ajv_sizeField.getText().trim());
        }

        // error messages
        catch (NumberFormatException ajv_e)
        {
            showError(ajv_frame, "Disk size must be a whole number.");
            return;
        }
        if (ajv_diskSize < 2)
        {
            showError(ajv_frame, "Disk size must be at least 2.");
            return;
        }

        try
        {
            // input initial head
            ajv_head = Integer.parseInt(ajv_headField.getText().trim());
        }
        catch (NumberFormatException ajv_e)
        {
            showError(ajv_frame, "Initial head position must be a whole number.");
            return;
        }
        if (ajv_head < 0 || ajv_head >= ajv_diskSize)
        {
            showError(ajv_frame, "Initial head position must be between 0 and " + (ajv_diskSize - 1) + ".");
            return;
        }

        // input queue
        String ajv_text = ajv_requestField.getText().trim();
        if (ajv_text.isEmpty())
        {
            showError(ajv_frame, "Please enter the request sequence.");
            return;
        }       
        String[] ajv_parts = ajv_text.split("[,\\s]+");
        if (ajv_parts.length < ajv_min_request || ajv_parts.length > ajv_max_request)
        {
            showError(ajv_frame, "Enter between " + ajv_min_request + " and " + ajv_max_request
                             + " requests (you entered " + ajv_parts.length + ").");
            return;
        }

        int[] ajv_requests = new int[ajv_parts.length];
        for (int ajv_i = 0; ajv_i < ajv_parts.length; ajv_i++)
        {
            try
            {
                ajv_requests[ajv_i] = Integer.parseInt(ajv_parts[ajv_i]);
            }
            catch (NumberFormatException ajv_e)
            {
                showError(ajv_frame, "\"" + ajv_parts[ajv_i] + "\" is not a whole number.");
                return;
            }
            if (ajv_requests[ajv_i] < 0 || ajv_requests[ajv_i] >= ajv_diskSize)
            {
                showError(ajv_frame, "Request " + ajv_requests[ajv_i] + " must be between 0 and " + (ajv_diskSize - 1) + ".");
                return;
            }
        }

        String ajv_direction = (String) ajv_dirBox.getSelectedItem();
        String ajv_result = SCAN(ajv_requests, ajv_head, ajv_direction, ajv_diskSize);

        JTextArea ajv_area = new JTextArea(ajv_result, 8, 40);
        ajv_area.setEditable(false);
        ajv_area.setLineWrap(true);
        ajv_area.setWrapStyleWord(true);
        ajv_area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(ajv_frame, new JScrollPane(ajv_area),
                                      "SCAN Results", JOptionPane.INFORMATION_MESSAGE);
    }

    // GUI
    static void createAndShowGui()
    {
        JFrame ajv_frame = new JFrame("SCAN Disk Scheduling Input");
        ajv_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Font ajv_labelFont = new Font("SansSerif", Font.BOLD, 14);

        JLabel ajv_title = new JLabel("SCAN Disk Scheduling Parameters", SwingConstants.CENTER);
        ajv_title.setFont(new Font("SansSerif", Font.BOLD, 22));
        ajv_title.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JTextField ajv_headField = new JTextField(22);
        JTextField ajv_sizeField = new JTextField(22);
        JTextField ajv_requestField = new JTextField(22);
        JComboBox<String> ajv_dirBox = new JComboBox<>(new String[] { "left", "right" });
        ajv_dirBox.setFont(ajv_labelFont);

        JPanel ajv_form = new JPanel(new GridBagLayout());
        ajv_form.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        GridBagConstraints ajv_c = new GridBagConstraints();
        ajv_c.insets = new Insets(8, 5, 8, 5);
        ajv_c.fill = GridBagConstraints.HORIZONTAL;

        String[] ajv_labels = { "Initial Head Position:", "Disk Size (Max Cylinder):",
                            "Request Sequence (comma-separated):", "Direction:" };
        JComponent[] ajv_inputs = { ajv_headField, ajv_sizeField, ajv_requestField, ajv_dirBox };
        for (int ajv_i = 0; ajv_i < ajv_labels.length; ajv_i++)
        {
            JLabel ajv_label = new JLabel(ajv_labels[ajv_i]);
            ajv_label.setFont(ajv_labelFont);
            ajv_c.gridy = ajv_i;
            ajv_c.gridx = 0;
            ajv_c.weightx = 0.4;
            ajv_form.add(ajv_label, ajv_c);
            ajv_c.gridx = 1;
            ajv_c.weightx = 0.6;
            ajv_form.add(ajv_inputs[ajv_i], ajv_c);
        }

        JButton ajv_run = new JButton("Run SCAN");
        ajv_run.setFont(ajv_labelFont);
        ajv_run.addActionListener(ajv_e -> onRun(ajv_frame, ajv_headField, ajv_sizeField, ajv_requestField, ajv_dirBox));
        JPanel ajv_buttonPanel = new JPanel();
        ajv_buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        ajv_buttonPanel.add(ajv_run);

        ajv_frame.add(ajv_title, BorderLayout.NORTH);
        ajv_frame.add(ajv_form, BorderLayout.CENTER);
        ajv_frame.add(ajv_buttonPanel, BorderLayout.SOUTH);
        ajv_frame.getRootPane().setDefaultButton(ajv_run);

        ajv_frame.pack();
        ajv_frame.setLocationRelativeTo(null);
        ajv_frame.setVisible(true);
    }

    public static void main(String[] ajv_args)
    {
        SwingUtilities.invokeLater(ScanDiskScheduling::createAndShowGui);
    }
}