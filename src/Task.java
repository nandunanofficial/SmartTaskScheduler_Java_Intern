package SmartTaskScheduler.src;

import java.util.Date;

public class Task implements Comparable<Task> {
    public String title;
    public int priority; // 1 - High, 2 - Medium, 3 - Low
    public Date deadline;

    public Task(String title, int priority, Date deadline) {
        this.title = title;
        this.priority = priority;
        this.deadline = deadline;
    }

    @Override
    public int compareTo(Task other) {
        if (this.priority != other.priority)
            return Integer.compare(this.priority, other.priority);
        return this.deadline.compareTo(other.deadline);
    }

    @Override
    public String toString() {
        return "[" + title + "] - Priority: " + priority + " | Due: " + deadline;
    }
}

