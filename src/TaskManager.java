package SmartTaskScheduler.src;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.PriorityQueue;

public class TaskManager {
    private PriorityQueue<Task> taskQueue = new PriorityQueue<>();
    private final String filePath = "tasks.json";

    public TaskManager() {
        loadTasks();
    }

    public void addTask(Task task) {
        taskQueue.add(task);
        saveTasks();
    }

    public void removeTask(Task task) {
        taskQueue.remove(task);
        saveTasks();
    }

    public PriorityQueue<Task> getAllTasks() {
        return new PriorityQueue<>(taskQueue);
    }

    public void saveTasks() {
        try (Writer writer = new FileWriter(filePath)) {
            new Gson().toJson(taskQueue, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTasks() {
        try (Reader reader = new FileReader(filePath)) {
            taskQueue = new Gson().fromJson(reader, new TypeToken<PriorityQueue<Task>>(){}.getType());
            if (taskQueue == null) taskQueue = new PriorityQueue<>();
        } catch (IOException e) {
            taskQueue = new PriorityQueue<>();
        }
    }
}

