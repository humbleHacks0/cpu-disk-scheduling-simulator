import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Main menu of the CPU and Disk Scheduling Simulator.
 *   SJF         - SJFScheduler         (Regina Dela Llarte)
 *   Round Robin - RoundRobinScheduler  (Matthew Aguirre)
 *   SCAN        - ScanScheduler        (AJ Valenzuela)
 *   C-LOOK      - CLookScheduler       (Maxine Basada)
 */
public class SchedulingSimulator
{
    static String sim_groupNumber = "9";   
    static String sim_group = "Bernie Bears";
    static String[] sim_members = { "Aguirre, Matthew", "Basada, Maxine", "Dela Llarte, Regina", "Valenzuela, AJ" };
    static String[] sim_algorithms = { "Round Robin", "C-LOOK", "SJF", "SCAN" };   

    static JFrame sim_menu;

    static JButton makeButton(String sim_text, ActionListener sim_action)
    {
        JButton sim_button = new JButton(sim_text);
        sim_button.setFont(new Font("SansSerif", Font.BOLD, 14));
        sim_button.addActionListener(sim_action);
        return sim_button;
    }

    static JLabel makeHeader(String sim_text)
    {
        JLabel sim_label = new JLabel(sim_text, SwingConstants.CENTER);
        sim_label.setFont(new Font("SansSerif", Font.BOLD, 16));
        sim_label.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        return sim_label;
    }

    static void showAbout()
    {
        StringBuilder sim_sb = new StringBuilder();
        sim_sb.append("CPU and Disk Scheduling App\n\n");
        sim_sb.append("Group Number: ").append(sim_groupNumber).append("\n");
        sim_sb.append("Group Name: ").append(sim_group).append("\n\nMembers:\n");
        for (int sim_i = 0; sim_i < sim_members.length; sim_i++)
            sim_sb.append("  - ").append(sim_members[sim_i]).append(" (").append(sim_algorithms[sim_i]).append(")\n");
        JOptionPane.showMessageDialog(sim_menu, sim_sb.toString(), "About", JOptionPane.INFORMATION_MESSAGE);
    }

    static void createMenu()
    {
        sim_menu = new JFrame("CPU and Disk Scheduling Simulator");
        sim_menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel sim_title = new JLabel("CPU and Disk Scheduling App", SwingConstants.CENTER);
        sim_title.setFont(new Font("SansSerif", Font.BOLD, 22));
        sim_title.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JLabel sim_subtitle = new JLabel("Choose an algorithm to run", SwingConstants.CENTER);
        sim_subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel sim_top = new JPanel(new GridLayout(2, 1));
        sim_top.add(sim_title);
        sim_top.add(sim_subtitle);

        JPanel sim_buttons = new JPanel(new GridLayout(0, 1, 0, 8));
        sim_buttons.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        // SJF and Round Robin windows
        sim_buttons.add(makeHeader("CPU Scheduling"));
        sim_buttons.add(makeButton("Shortest Job First", e -> CpuSchedulingGui.createAndShowGui(new SJFScheduler())));
        sim_buttons.add(makeButton("Round Robin", e -> CpuSchedulingGui.createAndShowGui(new RoundRobinScheduler())));

        // SCAN and C-LOOK windows
        sim_buttons.add(makeHeader("Disk Scheduling"));
        sim_buttons.add(makeButton("SCAN", e -> DiskSchedulingGui.createAndShowGui(new ScanScheduler())));
        sim_buttons.add(makeButton("C-LOOK", e -> DiskSchedulingGui.createAndShowGui(new CLookScheduler())));

        sim_buttons.add(new JLabel(""));
        sim_buttons.add(makeButton("About", e -> showAbout()));
        sim_buttons.add(makeButton("Exit", e -> System.exit(0)));

        sim_menu.add(sim_top, BorderLayout.NORTH);
        sim_menu.add(sim_buttons, BorderLayout.CENTER);

        sim_menu.pack();
        sim_menu.setLocationRelativeTo(null);
        sim_menu.setVisible(true);
    }

    public static void main(String[] sim_args)
    {
        SwingUtilities.invokeLater(SchedulingSimulator::createMenu);
    }
}
