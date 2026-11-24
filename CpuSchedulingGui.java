import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * User interface for the CPU scheduling modules (SJF and Round Robin).
 * Contains no algorithm logic: it only collects and validates input, then asks a
 * CPUScheduler for the result and displays it.
 * Window flow: input window -> process table window -> results window.
 */
class CpuSchedulingGui
{
    // allowed number of processes
    static int rdl_min_process = 10;
    static int rdl_max_process = 20;

    // largest allowed arrival time, burst time and time quantum
    static int rdl_max_time = 1000;

    // shows an error popup
    static void showError(Component rdl_parent, String rdl_message)
    {
        JOptionPane.showMessageDialog(rdl_parent, rdl_message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    // shows the gantt chart, result table, and the averages
    static void showResults(CPUScheduler rdl_scheduler, int[] rdl_at, int[] rdl_bt, int rdl_quantum)
    {
        int rdl_n = rdl_at.length;
        CPUResult rdl_result = rdl_scheduler.schedule(rdl_at, rdl_bt, rdl_quantum);

        // result table
        DefaultTableModel rdl_model = new DefaultTableModel(
            new String[] { "Process", "Arrival Time", "Burst Time", "Completion Time",
                           "Turnaround Time", "Waiting Time", "Response Time" }, 0)
        {
            public boolean isCellEditable(int rdl_row, int rdl_col)
            {
                return false;
            }
        };
        for (int rdl_i = 0; rdl_i < rdl_n; rdl_i++)
            rdl_model.addRow(new Object[] { "P" + (rdl_i + 1), rdl_at[rdl_i], rdl_bt[rdl_i],
                                            rdl_result.completion[rdl_i], rdl_result.turnaround[rdl_i],
                                            rdl_result.waiting[rdl_i], rdl_result.response[rdl_i] });

        JFrame rdl_frame = new JFrame(rdl_scheduler.getName() + " Results");
        rdl_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel rdl_title = new JLabel(rdl_scheduler.getResultsHeading(rdl_quantum), SwingConstants.CENTER);
        rdl_title.setFont(new Font("SansSerif", Font.BOLD, 22));
        rdl_title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        // gantt chart
        JScrollPane rdl_ganttScroll = new JScrollPane(new GanttPanel(rdl_result.gantt),
            ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rdl_ganttScroll.setBorder(BorderFactory.createTitledBorder("Gantt Chart (time scale)"));
        rdl_ganttScroll.setPreferredSize(new Dimension(900, 140));

        JTable rdl_table = new JTable(rdl_model);
        rdl_table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rdl_table.setRowHeight(24);
        rdl_table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        rdl_table.setPreferredScrollableViewportSize(new Dimension(900, 260));

        // averages, CPU utilization and throughput
        Font rdl_avgFont = new Font("SansSerif", Font.BOLD, 16);
        String[] rdl_avgTexts = {
            "Average Completion Time: " + String.format("%.2f", rdl_result.avgCompletion),
            "Average Turnaround Time: " + String.format("%.2f", rdl_result.avgTurnaround),
            "Average Waiting Time: " + String.format("%.2f", rdl_result.avgWaiting),
            "Average Response Time: " + String.format("%.2f", rdl_result.avgResponse),
            "CPU Utilization: " + String.format("%.2f", rdl_result.utilization) + "%  (busy "
                + rdl_result.busyTime + " of " + rdl_result.totalTime + " time units)",
            "Throughput: " + String.format("%.4f", rdl_result.throughput) + " processes per time unit  ("
                + rdl_n + " processes in " + rdl_result.totalTime + " time units)"
        };
        JPanel rdl_avgPanel = new JPanel(new GridLayout(rdl_avgTexts.length, 1, 0, 5));
        rdl_avgPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        for (int rdl_i = 0; rdl_i < rdl_avgTexts.length; rdl_i++)
        {
            JLabel rdl_label = new JLabel(rdl_avgTexts[rdl_i]);
            rdl_label.setFont(rdl_avgFont);
            rdl_avgPanel.add(rdl_label);
        }

        // layout
        JPanel rdl_center = new JPanel(new BorderLayout(0, 10));
        rdl_center.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        rdl_center.add(rdl_ganttScroll, BorderLayout.NORTH);
        rdl_center.add(new JScrollPane(rdl_table), BorderLayout.CENTER);

        rdl_frame.add(rdl_title, BorderLayout.NORTH);
        rdl_frame.add(rdl_center, BorderLayout.CENTER);
        rdl_frame.add(rdl_avgPanel, BorderLayout.SOUTH);

        rdl_frame.pack();
        rdl_frame.setLocationRelativeTo(null);
        rdl_frame.setVisible(true);
    }

    // reads one whole-number cell, returns -1 (after showing an error) if it is not valid
    static int readCell(JFrame rdl_frame, DefaultTableModel rdl_model, int rdl_row, int rdl_col,
                        String rdl_name, String rdl_field, int rdl_lowest)
    {
        Object rdl_obj = rdl_model.getValueAt(rdl_row, rdl_col);
        String rdl_text = (rdl_obj == null) ? "" : rdl_obj.toString().trim();
        if (rdl_text.isEmpty())
        {
            showError(rdl_frame, rdl_name + ": " + rdl_field + " cannot be empty.");
            return -1;
        }
        int rdl_value;
        try
        {
            rdl_value = Integer.parseInt(rdl_text);
        }
        catch (NumberFormatException rdl_e)
        {
            showError(rdl_frame, rdl_name + ": " + rdl_field + " must be a whole number (you entered \"" + rdl_text + "\").");
            return -1;
        }
        if (rdl_value < 0)
        {
            showError(rdl_frame, rdl_name + ": " + rdl_field + " cannot be negative.");
            return -1;
        }
        if (rdl_value < rdl_lowest)
        {
            showError(rdl_frame, rdl_name + ": " + rdl_field + " must be at least " + rdl_lowest + ".");
            return -1;
        }
        if (rdl_value > rdl_max_time)
        {
            showError(rdl_frame, rdl_name + ": " + rdl_field + " must not be more than " + rdl_max_time + ".");
            return -1;
        }
        return rdl_value;
    }

    // checks every cell in the table is a valid whole number, then runs the algorithm
    static void onRun(JFrame rdl_frame, JTable rdl_table, DefaultTableModel rdl_model,
                      int rdl_quantum, CPUScheduler rdl_scheduler)
    {
        if (rdl_table.isEditing())
            rdl_table.getCellEditor().stopCellEditing();

        int rdl_n = rdl_model.getRowCount();
        int[] rdl_at = new int[rdl_n];
        int[] rdl_bt = new int[rdl_n];

        for (int rdl_i = 0; rdl_i < rdl_n; rdl_i++)
        {
            String rdl_name = "P" + (rdl_i + 1);

            // arrival time: not empty, whole number, not negative
            rdl_at[rdl_i] = readCell(rdl_frame, rdl_model, rdl_i, 1, rdl_name, "Arrival Time", 0);
            if (rdl_at[rdl_i] < 0)
                return;

            // burst time: not empty, whole number, at least 1
            rdl_bt[rdl_i] = readCell(rdl_frame, rdl_model, rdl_i, 2, rdl_name, "Burst Time", 1);
            if (rdl_bt[rdl_i] < 0)
                return;
        }

        // all inputs are valid, show the results
        showResults(rdl_scheduler, rdl_at, rdl_bt, rdl_quantum);
    }

    // table GUI where the user edits arrival time and burst time
    static void showTableGui(CPUScheduler rdl_scheduler, int rdl_count, int rdl_firstQuantum)
    {
        // holder so Load Sample Data can change the quantum used by Run
        int[] rdl_quantum = { rdl_firstQuantum };

        JFrame rdl_frame = new JFrame(rdl_scheduler.getName() + " Process Input");
        rdl_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel rdl_title = new JLabel("Enter Arrival Time and Burst Time", SwingConstants.CENTER);
        rdl_title.setFont(new Font("SansSerif", Font.BOLD, 22));
        rdl_title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        JLabel rdl_quantumLabel = new JLabel(" ", SwingConstants.CENTER);
        rdl_quantumLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        if (rdl_scheduler.needsQuantum())
            rdl_quantumLabel.setText("Time Quantum: " + rdl_quantum[0]);

        JPanel rdl_north = new JPanel(new GridLayout(2, 1));
        rdl_north.add(rdl_title);
        rdl_north.add(rdl_quantumLabel);

        // only the arrival time and burst time columns can be edited
        DefaultTableModel rdl_model = new DefaultTableModel(new String[] { "Process", "Arrival Time", "Burst Time" }, 0)
        {
            public boolean isCellEditable(int rdl_row, int rdl_col)
            {
                return rdl_col != 0;
            }
        };

        // one row per process, the cells start empty so the user must fill them in
        for (int rdl_i = 0; rdl_i < rdl_count; rdl_i++)
            rdl_model.addRow(new Object[] { "P" + (rdl_i + 1), "", "" });

        // table design
        JTable rdl_table = new JTable(rdl_model);
        rdl_table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rdl_table.setRowHeight(26);
        rdl_table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        rdl_table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        rdl_table.setPreferredScrollableViewportSize(new Dimension(420, 300));

        JPanel rdl_tablePanel = new JPanel(new BorderLayout());
        rdl_tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        rdl_tablePanel.add(new JScrollPane(rdl_table), BorderLayout.CENTER);

        // buttons: back, reset, load sample data & run
        Font rdl_labelFont = new Font("SansSerif", Font.BOLD, 14);
        JButton rdl_back = new JButton("Back");
        rdl_back.setFont(rdl_labelFont);
        rdl_back.addActionListener(rdl_e ->
        {
            rdl_frame.dispose();
            createAndShowGui(rdl_scheduler);
        });

        // reset clears every Arrival Time and Burst Time cell
        JButton rdl_reset = new JButton("Reset");
        rdl_reset.setFont(rdl_labelFont);
        rdl_reset.addActionListener(rdl_e ->
        {
            // stop the cell that is being typed in first
            if (rdl_table.isEditing())
                rdl_table.getCellEditor().cancelCellEditing();
            for (int rdl_i = 0; rdl_i < rdl_model.getRowCount(); rdl_i++)
            {
                rdl_model.setValueAt("", rdl_i, 1);
                rdl_model.setValueAt("", rdl_i, 2);
            }
        });

        // load sample data replaces the table with the sample process set
        JButton rdl_load = new JButton("Load Sample Data");
        rdl_load.setFont(rdl_labelFont);
        rdl_load.addActionListener(rdl_e ->
        {
            if (rdl_table.isEditing())
                rdl_table.getCellEditor().cancelCellEditing();
            rdl_model.setRowCount(0);
            for (int rdl_i = 0; rdl_i < SampleData.CPU_PROCESSES.length; rdl_i++)
                rdl_model.addRow(new Object[] { "P" + (rdl_i + 1),
                                                String.valueOf(SampleData.CPU_PROCESSES[rdl_i][0]),
                                                String.valueOf(SampleData.CPU_PROCESSES[rdl_i][1]) });
            if (rdl_scheduler.needsQuantum())
            {
                rdl_quantum[0] = SampleData.CPU_QUANTUM;
                rdl_quantumLabel.setText("Time Quantum: " + rdl_quantum[0]);
            }
        });

        JButton rdl_run = new JButton("Run " + rdl_scheduler.getName());
        rdl_run.setFont(rdl_labelFont);
        rdl_run.addActionListener(rdl_e -> onRun(rdl_frame, rdl_table, rdl_model, rdl_quantum[0], rdl_scheduler));

        JPanel rdl_buttonPanel = new JPanel();
        rdl_buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        rdl_buttonPanel.add(rdl_back);
        rdl_buttonPanel.add(rdl_reset);
        rdl_buttonPanel.add(rdl_load);
        rdl_buttonPanel.add(rdl_run);

        rdl_frame.add(rdl_north, BorderLayout.NORTH);
        rdl_frame.add(rdl_tablePanel, BorderLayout.CENTER);
        rdl_frame.add(rdl_buttonPanel, BorderLayout.SOUTH);
        rdl_frame.getRootPane().setDefaultButton(rdl_run);

        rdl_frame.pack();
        rdl_frame.setLocationRelativeTo(null);
        rdl_frame.setVisible(true);
    }

    // checks if number of processes (and time quantum for Round Robin) is valid
    static void onNext(JFrame rdl_frame, JTextField rdl_countField, JTextField rdl_quantumField, CPUScheduler rdl_scheduler)
    {
        // cannot be empty
        String rdl_text = rdl_countField.getText().trim();
        if (rdl_text.isEmpty())
        {
            showError(rdl_frame, "Please enter the number of processes.");
            return;
        }

        // must be a whole number
        int rdl_count;
        try
        {
            rdl_count = Integer.parseInt(rdl_text);
        }
        catch (NumberFormatException rdl_e)
        {
            showError(rdl_frame, "Number of processes must be a whole number.");
            return;
        }

        // must be within the allowed range (10 to 20)
        if (rdl_count < rdl_min_process || rdl_count > rdl_max_process)
        {
            showError(rdl_frame, "Enter between " + rdl_min_process + " and " + rdl_max_process
                             + " processes (you entered " + rdl_count + ").");
            return;
        }

        // time quantum is only needed for Round Robin (cannot be empty, whole number, at least 1)
        int rdl_quantum = 0;
        if (rdl_scheduler.needsQuantum())
        {
            String rdl_qText = rdl_quantumField.getText().trim();
            if (rdl_qText.isEmpty())
            {
                showError(rdl_frame, "Please enter the time quantum.");
                return;
            }
            try
            {
                rdl_quantum = Integer.parseInt(rdl_qText);
            }
            catch (NumberFormatException rdl_e)
            {
                showError(rdl_frame, "Time quantum must be a whole number.");
                return;
            }
            if (rdl_quantum < 1)
            {
                showError(rdl_frame, "Time quantum must be at least 1.");
                return;
            }
            if (rdl_quantum > rdl_max_time)
            {
                showError(rdl_frame, "Time quantum must not be more than " + rdl_max_time + ".");
                return;
            }
        }

        // valid, close this window and open the table
        rdl_frame.dispose();
        showTableGui(rdl_scheduler, rdl_count, rdl_quantum);
    }

    // GUI (first window: asks for the number of processes, and the time quantum for Round Robin)
    static void createAndShowGui(CPUScheduler rdl_scheduler)
    {
        JFrame rdl_frame = new JFrame(rdl_scheduler.getName() + " Scheduling Input");
        rdl_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Font rdl_labelFont = new Font("SansSerif", Font.BOLD, 14);

        JLabel rdl_title = new JLabel(rdl_scheduler.getFullName() + " Scheduling", SwingConstants.CENTER);
        rdl_title.setFont(new Font("SansSerif", Font.BOLD, 22));
        rdl_title.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JTextField rdl_countField = new JTextField(10);
        JTextField rdl_quantumField = new JTextField(10);

        // Round Robin has one extra row for the time quantum
        boolean rdl_isRR = rdl_scheduler.needsQuantum();
        String rdl_countLabel = "Number of Processes (" + rdl_min_process + "-" + rdl_max_process + "):";
        String[] rdl_labels = rdl_isRR
            ? new String[] { rdl_countLabel, "Time Quantum:" }
            : new String[] { rdl_countLabel };
        JComponent[] rdl_inputs = rdl_isRR
            ? new JComponent[] { rdl_countField, rdl_quantumField }
            : new JComponent[] { rdl_countField };

        JPanel rdl_form = new JPanel(new GridBagLayout());
        rdl_form.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        GridBagConstraints rdl_c = new GridBagConstraints();
        rdl_c.insets = new Insets(8, 5, 8, 5);
        rdl_c.fill = GridBagConstraints.HORIZONTAL;

        for (int rdl_i = 0; rdl_i < rdl_labels.length; rdl_i++)
        {
            JLabel rdl_label = new JLabel(rdl_labels[rdl_i]);
            rdl_label.setFont(rdl_labelFont);
            rdl_c.gridy = rdl_i;
            rdl_c.gridx = 0;
            rdl_c.weightx = 0.6;
            rdl_form.add(rdl_label, rdl_c);
            rdl_c.gridx = 1;
            rdl_c.weightx = 0.4;
            rdl_form.add(rdl_inputs[rdl_i], rdl_c);
        }

        // Reset clears the fields, Load Sample Data fills them with the sample values
        JButton rdl_reset = new JButton("Reset");
        rdl_reset.setFont(rdl_labelFont);
        rdl_reset.addActionListener(rdl_e ->
        {
            rdl_countField.setText("");
            rdl_quantumField.setText("");
        });

        JButton rdl_load = new JButton("Load Sample Data");
        rdl_load.setFont(rdl_labelFont);
        rdl_load.addActionListener(rdl_e ->
        {
            rdl_countField.setText(String.valueOf(SampleData.CPU_PROCESSES.length));
            if (rdl_isRR)
                rdl_quantumField.setText(String.valueOf(SampleData.CPU_QUANTUM));
        });

        // Next button
        JButton rdl_next = new JButton("Next");
        rdl_next.setFont(rdl_labelFont);
        rdl_next.addActionListener(rdl_e -> onNext(rdl_frame, rdl_countField, rdl_quantumField, rdl_scheduler));
        JPanel rdl_buttonPanel = new JPanel();
        rdl_buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        rdl_buttonPanel.add(rdl_reset);
        rdl_buttonPanel.add(rdl_load);
        rdl_buttonPanel.add(rdl_next);

        rdl_frame.add(rdl_title, BorderLayout.NORTH);
        rdl_frame.add(rdl_form, BorderLayout.CENTER);
        rdl_frame.add(rdl_buttonPanel, BorderLayout.SOUTH);
        rdl_frame.getRootPane().setDefaultButton(rdl_next);

        rdl_frame.pack();
        rdl_frame.setLocationRelativeTo(null);
        rdl_frame.setVisible(true);
    }
}
