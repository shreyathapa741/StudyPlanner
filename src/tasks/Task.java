package tasks;

import java.time.LocalDate;

public class Task {
  private String name;
  private LocalDate dueDate;
  private boolean completed;

  public Task(String name, LocalDate dueDate) {
    this.name = name;
    this.dueDate = dueDate;
    this.completed = false;
  }

  public String getName() {
    return name;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  @Override
  public String toString() {
    return "Task: " + name + " | Due: " + dueDate + " | Completed: " + (completed ? "Yes" : "No");
  }
}
