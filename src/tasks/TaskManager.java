package tasks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
  private List<Task> tasks;

  public TaskManager() {
    this.tasks = new ArrayList<>();
  }

  // Add task method
  public void addTask(String taskName, String dueDateStr) {
    LocalDate dueDate = LocalDate.parse(dueDateStr);
    Task task = new Task(taskName, dueDate);
    tasks.add(task);
    System.out.println("Task added: " + task);
  }

  // View tasks method
  public void viewTasks() {
    if (tasks.isEmpty()) {
      System.out.println("No tasks available.");
      return;
    }
    for (int i = 0; i < tasks.size(); i++) {
      System.out.println((i + 1) + ". " + tasks.get(i));
    }
  }

  // Get tasks method
  public List<Task> getTasks() {
    return tasks; // Return the list of tasks
  }

  // Delete task method
  public void deleteTask(int taskNumber) {
    if (!hasTasks()) {
      System.out.println("No tasks available to delete.");
      return;
    }
    if (taskNumber < 1 || taskNumber > tasks.size()) {
      System.out.println("Invalid task number. Please try again.");
    } else {
      Task removedTask = tasks.remove(taskNumber - 1); // Adjust for 1-based input
      System.out.println("Task deleted: " + removedTask);
    }
  }

  // Change task status method
  public void changeTaskStatus(int taskNumber, boolean isCompleted) {
    if (!hasTasks()) {
      System.out.println("No tasks available to change status.");
      return;
    }
    if (taskNumber < 1 || taskNumber > tasks.size()) {
      System.out.println("Invalid task number. Please try again.");
    } else {
      Task task = tasks.get(taskNumber - 1);
      task.setCompleted(isCompleted);
      System.out.println("Task status updated: " + task);
    }
  }

  // Helper method to check if tasks exist
  public boolean hasTasks() {
    return !tasks.isEmpty();
  }
}
