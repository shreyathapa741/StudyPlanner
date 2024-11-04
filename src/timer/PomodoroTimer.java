package timer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PomodoroTimer implements Runnable {
  private int originalWorkDuration; // in seconds
  private int originalShortBreakDuration; // in seconds
  private int originalLongBreakDuration; // in seconds
  private int workDuration; // in seconds
  private int shortBreakDuration; // in seconds
  private int longBreakDuration; // in seconds
  private int cycles; // number of work cycles
  private boolean running; // state of the timer
  private boolean paused; // state of the timer when paused
  private List<Consumer<Integer>> listeners = new ArrayList<>(); // List to hold timer listeners

  public PomodoroTimer(int workDuration, int shortBreakDuration, int longBreakDuration, int cycles) {
    this.originalWorkDuration = workDuration; // Store original durations
    this.originalShortBreakDuration = shortBreakDuration;
    this.originalLongBreakDuration = longBreakDuration;
    this.workDuration = workDuration;
    this.shortBreakDuration = shortBreakDuration;
    this.longBreakDuration = longBreakDuration;
    this.cycles = cycles;
    this.running = false;
    this.paused = false;
  }

  public void addTimerListener(Consumer<Integer> listener) {
    listeners.add(listener); // Add a listener to the list
  }

  private void notifyListeners(int remainingTime) {
    for (Consumer<Integer> listener : listeners) {
      listener.accept(remainingTime); // Notify each listener with the remaining time
    }
  }

  public void start() {
    running = true; // Set running state to true
    paused = false; // Ensure paused is false when starting
  }

  public void stop() {
    running = false; // Stop the timer
  }

  public void pause() {
    paused = true; // Set paused state to true
  }

  public void reset() {
    running = false; // Stop the timer
    paused = false; // Reset paused state
    // Reset durations to original values
    workDuration = originalWorkDuration;
    shortBreakDuration = originalShortBreakDuration;
    longBreakDuration = originalLongBreakDuration;
    System.out.println("Timer reset to original settings.");
  }

  public void resume() {
    paused = false; // Set paused state to false
  }

  @Override
  public void run() {
    runTimer(); // Execute the timer logic in this method
  }

  public void runTimer() {
    for (int i = 1; i <= cycles; i++) {
      if (!running)
        break;

      // Work phase
      System.out.println("Work session " + i + " started for " + (workDuration / 60) + " minutes.");
      sleep(workDuration); // Sleep for work duration

      // Check if timer is still running
      if (!running)
        break;

      System.out.println("Work session " + i + " ended. Time for a break!");

      // Short break phase
      if (i < cycles) {
        System.out.println("Short break started for " + (shortBreakDuration / 60) + " minutes.");
        sleep(shortBreakDuration); // Sleep for short break duration
        System.out.println("Short break ended.");
      } else {
        // Long break phase for the last cycle
        System.out.println("Long break started for " + (longBreakDuration / 60) + " minutes.");
        sleep(longBreakDuration); // Sleep for long break duration
        System.out.println("Long break ended.");
      }
    }
    // Reset state after completing cycles
    running = false;
    System.out.println("Pomodoro cycle completed. Ready to start again!");
  }

  private void sleep(int seconds) {
    try {
      for (int i = seconds; i > 0; i--) {
        if (paused) {
          // Wait until the timer is resumed
          synchronized (this) {
            while (paused) {
              wait(); // Wait until notified
            }
          }
        }
        notifyListeners(i); // Notify listeners with remaining seconds
        Thread.sleep(1000); // Sleep for 1 second
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // Restore interrupted status
    }
  }

  public boolean isRunning() {
    return running;
  }

  public boolean isPaused() {
    return paused;
  }
}
