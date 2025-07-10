package SmartTaskScheduler.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

public class TaskUI {
    private JFrame frame;
    private DefaultListModel<Task> model;
    private JList<Task> taskList;
    private TaskManager manager = new TaskManager();
    private Timer timer;

    public TaskUI() {
        frame = new JFrame("Smart Task Scheduler");
        model = new DefaultListModel<>();
        taskList = new JList<>(model);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton addBtn = new JButton("Add");
        JButton delBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton filterTodayBtn = new JButton("Today’s Tasks");

        JPanel panel = new JPanel();
        panel.add(addBtn);
        panel.add(delBtn);
        panel.add(refreshBtn);
        panel.add(filterTodayBtn);

        frame.add(new JScrollPane(taskList), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        refreshTasks();

        addBtn.addActionListener(e -> showAddDialog());
        delBtn.addActionListener(e -> {
            Task selected = taskList.getSelectedValue();
            if (selected != null) {
                manager.removeTask(selected);
                refreshTasks();
            }
        });

        refreshBtn.addActionListener(e -> refreshTasks());

        filterTodayBtn.addActionListener(e -> showTodayTasks());

        startReminderTimer();
    }

    private void showAddDialog() {
        JTextField titleField = new JTextField();
        String[] prioOptions = {"1 - High", "2 - Medium", "3 - Low"};
        JComboBox<String> priorityBox = new JComboBox<>(prioOptions);
        JTextField dateField = new JTextField("yyyy-MM-dd");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Task Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Priority:"));
        panel.add(priorityBox);
        panel.add(new JLabel("Deadline (yyyy-MM-dd):"));
        panel.add(dateField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Task", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText();
                int priority = priorityBox.getSelectedIndex() + 1;
                Date deadline = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());
                manager.addTask(new Task(title, priority, deadline));
                refreshTasks();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input!");
            }
        }
    }

    private void refreshTasks() {
        model.clear();
        PriorityQueue<Task> tasks = manager.getAllTasks();
        for (Task t : tasks) model.addElement(t);
    }

    private void showTodayTasks() {
        model.clear();
        Calendar today = Calendar.getInstance();
        PriorityQueue<Task> tasks = manager.getAllTasks();
        for (Task t : tasks) {
            Calendar taskDay = Calendar.getInstance();
            taskDay.setTime(t.deadline);
            if (today.get(Calendar.YEAR) == taskDay.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == taskDay.get(Calendar.DAY_OF_YEAR)) {
                model.addElement(t);
            }
        }
    }

    private void startReminderTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Date now = new Date();
                for (Task t : manager.getAllTasks()) {
                    long diff = t.deadline.getTime() - now.getTime();
                    if (diff > 0 && diff < 3600000) { // Due within 1 hour
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(frame, "Reminder: " + t.title + " is due soon!")
                        );
                        break;
                    }
                }
            }
        }, 0, 300000); // every 5 minutes
    }
}

