import java.awt.*;
import javax.swing.*;

/**
 * User interface for the disk scheduling modules (SCAN and C-LOOK).
 * Contains no algorithm logic: it validates the input, asks a DiskScheduler for
 * the seek sequence and shows it in the results window.
 */
class DiskSchedulingGui
{
    static int ajv_min_request = 5;
    static int ajv_max_request = 20;

    static void showError(Component ajv_parent, String ajv_message)
    {
        JOptionPane.showMessageDialog(ajv_parent, ajv_message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    // checks if valid number then runs the chosen algorithm
    static void onRun(JFrame ajv_frame, JTextField ajv_headField, JTextField ajv_sizeField,
                      JTextField ajv_requestField, JComboBox<String> ajv_dirBox, DiskScheduler ajv_scheduler)
    {
        int ajv_head, ajv_diskSize;

        // input diskSize
        String ajv_sizeText = ajv_sizeField.getText().trim();
        if (ajv_sizeText.isEmpty())
        {
            showError(ajv_frame, "Please enter the disk size.");
            return;
        }
        try
        {
            ajv_diskSize = Integer.parseInt(ajv_sizeText);
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

        // input initial head
        String ajv_headText = ajv_headField.getText().trim();
        if (ajv_headText.isEmpty())
        {
            showError(ajv_frame, "Please enter the initial head position.");
            return;
        }
        try
        {
            ajv_head = Integer.parseInt(ajv_headText);
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

        // run the chosen algorithm, then show the results window (with graph)
        String ajv_direction = (String) ajv_dirBox.getSelectedItem();
        int[] ajv_seekSequence = ajv_scheduler.schedule(ajv_requests, ajv_head, ajv_direction, ajv_diskSize);
        DiskResult ajv_result = new DiskResult(ajv_requests, ajv_head, ajv_direction, ajv_diskSize, ajv_seekSequence);
        new DiskResultsDialog(ajv_frame, ajv_scheduler, ajv_result).setVisible(true);
    }

    // GUI
    static void createAndShowGui(DiskScheduler ajv_scheduler)
    {
        JFrame ajv_frame = new JFrame(ajv_scheduler.getName() + " Disk Scheduling Input");
        ajv_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Font ajv_labelFont = new Font("SansSerif", Font.BOLD, 14);

        JLabel ajv_title = new JLabel(ajv_scheduler.getName() + " Disk Scheduling Parameters", SwingConstants.CENTER);
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

        // Reset clears every field
        JButton ajv_reset = new JButton("Reset");
        ajv_reset.setFont(ajv_labelFont);
        ajv_reset.addActionListener(ajv_e ->
        {
            ajv_headField.setText("");
            ajv_sizeField.setText("");
            ajv_requestField.setText("");
            ajv_dirBox.setSelectedIndex(0);
        });

        // Load Sample Data fills every field with the sample problem
        JButton ajv_load = new JButton("Load Sample Data");
        ajv_load.setFont(ajv_labelFont);
        ajv_load.addActionListener(ajv_e ->
        {
            ajv_headField.setText(String.valueOf(SampleData.DISK_HEAD));
            ajv_sizeField.setText(String.valueOf(SampleData.DISK_SIZE));
            StringBuilder ajv_sb = new StringBuilder();
            for (int ajv_i = 0; ajv_i < SampleData.DISK_REQUESTS.length; ajv_i++)
            {
                if (ajv_i > 0)
                    ajv_sb.append(", ");
                ajv_sb.append(SampleData.DISK_REQUESTS[ajv_i]);
            }
            ajv_requestField.setText(ajv_sb.toString());
            ajv_dirBox.setSelectedItem(SampleData.DISK_DIRECTION);
        });

        JButton ajv_run = new JButton("Run " + ajv_scheduler.getName());
        ajv_run.setFont(ajv_labelFont);
        ajv_run.addActionListener(ajv_e -> onRun(ajv_frame, ajv_headField, ajv_sizeField, ajv_requestField, ajv_dirBox, ajv_scheduler));
        JPanel ajv_buttonPanel = new JPanel();
        ajv_buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        ajv_buttonPanel.add(ajv_reset);
        ajv_buttonPanel.add(ajv_load);
        ajv_buttonPanel.add(ajv_run);

        ajv_frame.add(ajv_title, BorderLayout.NORTH);
        ajv_frame.add(ajv_form, BorderLayout.CENTER);
        ajv_frame.add(ajv_buttonPanel, BorderLayout.SOUTH);
        ajv_frame.getRootPane().setDefaultButton(ajv_run);

        ajv_frame.pack();
        ajv_frame.setLocationRelativeTo(null);
        ajv_frame.setVisible(true);
    }
}
