package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Scanner;
import timer.PomodoroTimer;
import recall_techniques.SpacedRepetition;
import recall_techniques.ActiveRecall;
import tasks.TaskManager;
import data.DataStorage;
import shared.*;

public class StudyPlannerGui {
  private JFrame frame;
  private CardLayout cardLayout;
  private JPanel mainPanel;
  private TaskManager taskManager;
  private PomodoroTimer pomodoroTimer;
  private File selectedFile;
  private SpacedRepetition spacedRepetition;
  private ActiveRecall activeRecall;
  private JTextArea questionArea;
  private JTextField answerField;
  private JLabel timerLabel;
  private boolean isBreakTime = false;

  // Timer fields for break intervals in Spaced Repetition
  private Timer breakTimer;
  private int breakDuration; // duration in seconds

  // Constructor
  public StudyPlannerGui() {
    frame = new JFrame("Study Planner");
    frame.setSize(800, 600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());

    taskManager = new TaskManager();
    pomodoroTimer = new PomodoroTimer(25 * 60, 5 * 60, 30 * 60, 4); // 25 mins work, 5 mins short break, 30 mins long
                                                                    // break

    // Main panel with CardLayout
    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);
    frame.add(mainPanel, BorderLayout.CENTER);

    // Initialize panels for each feature
    setupHomePage();
    setupPomodoroTab();
    setupSpacedRepetitionTab();
    setupActiveRecallTab();
    setupTaskSchedulerTab();

    frame.setVisible(true);
  }

  private void setupHomePage() {
    JPanel homePanel = new JPanel();
    homePanel.setLayout(new BorderLayout());

    JLabel welcomeLabel = new JLabel("Hi! How would you like to study today :D", SwingConstants.CENTER);
    welcomeLabel.setFont(new Font("Serif", Font.BOLD, 24));
    homePanel.add(welcomeLabel, BorderLayout.CENTER);

    // Panel for buttons
    JPanel buttonPanel = new JPanel();
    JButton pomodoroButton = new JButton("Pomodoro");
    JButton spacedRepetitionButton = new JButton("Spaced Repetition");
    JButton activeRecallButton = new JButton("Active Recall");
    JButton taskSchedulerButton = new JButton("Task Scheduler");

    // Action listeners for buttons
    pomodoroButton.addActionListener(e -> cardLayout.show(mainPanel, "Pomodoro")); // Switch to Pomodoro panel
    spacedRepetitionButton.addActionListener(e -> cardLayout.show(mainPanel, "SpacedRepetition")); // Switch to Spaced
                                                                                                   // Repetition panel
    activeRecallButton.addActionListener(e -> cardLayout.show(mainPanel, "ActiveRecall")); // Switch to Active Recall
                                                                                           // panel
    taskSchedulerButton.addActionListener(e -> cardLayout.show(mainPanel, "TaskScheduler")); // Switch to Task Scheduler
                                                                                             // panel

    buttonPanel.add(pomodoroButton);
    buttonPanel.add(spacedRepetitionButton);
    buttonPanel.add(activeRecallButton);
    buttonPanel.add(taskSchedulerButton);
    homePanel.add(buttonPanel, BorderLayout.SOUTH);

    mainPanel.add(homePanel, "Home");
  }

  // Pomodoro Tab setup
  private void setupPomodoroTab() {
    JPanel pomodoroPanel = new JPanel();
    JLabel timeLabel = new JLabel("25:00", SwingConstants.CENTER);
    timeLabel.setFont(new Font("Serif", Font.BOLD, 48));

    JButton startButton = new JButton("Start");
    JButton pauseButton = new JButton("Pause");
    JButton resetButton = new JButton("Reset");
    JButton backButton = new JButton("Back to Home");

    pomodoroPanel.add(timeLabel);
    pomodoroPanel.add(startButton);
    pomodoroPanel.add(pauseButton);
    pomodoroPanel.add(resetButton);
    pomodoroPanel.add(backButton);

    mainPanel.add(pomodoroPanel, "Pomodoro");

    // Timer Logic
    startButton.addActionListener(e -> {
      if (!pomodoroTimer.isRunning()) {
        new Thread(pomodoroTimer).start(); // Start the timer in a separate thread
        pomodoroTimer.start();
        JOptionPane.showMessageDialog(frame, "Pomodoro timer started.");
      }
    });

    pauseButton.addActionListener(e -> {
      if (pomodoroTimer.isRunning()) {
        pomodoroTimer.pause();
        JOptionPane.showMessageDialog(frame, "Pomodoro timer paused.");
      }
    });

    resetButton.addActionListener(e -> {
      pomodoroTimer.reset();
      timeLabel.setText("25:00");
      JOptionPane.showMessageDialog(frame, "Pomodoro timer reset.");
    });

    backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home")); // Back to Home

    pomodoroTimer.addTimerListener(remainingTime -> {
      int minutes = remainingTime / 60;
      int seconds = remainingTime % 60;
      timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    });
  }

  // Spaced Repetition Tab setup
  private void setupSpacedRepetitionTab() {
    JPanel spacedRepetitionPanel = new JPanel(new BorderLayout());

    // Text area to display questions
    JTextArea questionArea = new JTextArea("Load a questions file to start the spaced repetition session.");
    questionArea.setEditable(false);
    questionArea.setLineWrap(true);
    questionArea.setWrapStyleWord(true);
    JScrollPane questionScrollPane = new JScrollPane(questionArea);

    // Text field for user answer input
    JTextField answerField = new JTextField();
    JLabel answerLabel = new JLabel("Your Answer:");

    // Buttons
    JButton loadFileButton = new JButton("Load Questions File");
    JButton startSessionButton = new JButton("Start Session");
    JButton submitAnswerButton = new JButton("Submit Answer");
    JButton pauseButton = new JButton("Pause");
    JButton resetButton = new JButton("Reset");
    JButton backButton = new JButton("Back to Home"); // Back button

    // Disable buttons initially
    startSessionButton.setEnabled(false);
    pauseButton.setEnabled(false);
    resetButton.setEnabled(false);
    submitAnswerButton.setEnabled(false);

    // Panel for answer input
    JPanel answerPanel = new JPanel(new BorderLayout());
    answerPanel.add(answerLabel, BorderLayout.WEST);
    answerPanel.add(answerField, BorderLayout.CENTER);

    // Panel for buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(loadFileButton);
    buttonPanel.add(startSessionButton);
    buttonPanel.add(submitAnswerButton);
    buttonPanel.add(pauseButton);
    buttonPanel.add(resetButton);
    buttonPanel.add(backButton); // Add back button to the button panel

    // Panel for both answer and buttons
    JPanel southPanel = new JPanel(new BorderLayout());
    southPanel.add(answerPanel, BorderLayout.NORTH);
    southPanel.add(buttonPanel, BorderLayout.SOUTH);

    // Add components to the main panel
    spacedRepetitionPanel.add(questionScrollPane, BorderLayout.CENTER);
    spacedRepetitionPanel.add(southPanel, BorderLayout.SOUTH); // Add the combined panel to SOUTH

    mainPanel.add(spacedRepetitionPanel, "SpacedRepetition");

    // Action listener for the back button
    backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home")); // Back to Home

    // DataStorage instance for loading files
    DataStorage dataStorage = new DataStorage();

    // Load file button action
    loadFileButton.addActionListener(e -> {
      selectedFile = dataStorage.chooseFile((JFrame) SwingUtilities.getWindowAncestor(spacedRepetitionPanel));
      if (selectedFile != null) {
        questionArea.setText("File loaded: " + selectedFile.getName() + "\nYou can now start the session.");
        startSessionButton.setEnabled(true);
      } else {
        questionArea.setText("No file selected. Please select a valid questions file.");
      }
    });

    // Define DisplayUpdater to update the questionArea
    DisplayUpdater displayUpdater = question -> SwingUtilities.invokeLater(() -> {
      questionArea.setText(question); // Update question area with the text provided by DisplayUpdater
    });

    // Start session button action
    startSessionButton.addActionListener(e -> {
      if (selectedFile != null && selectedFile.exists()) {
        // Create SpacedRepetition instance, passing the DisplayUpdater
        spacedRepetition = new SpacedRepetition(selectedFile, displayUpdater);

        // Start the session in a new thread to avoid blocking the GUI
        new Thread(() -> {
          spacedRepetition.startSpacedRepetitionSession(); // Start session without parameters
        }).start();

        // Initial GUI setup after starting the session
        SwingUtilities.invokeLater(() -> {
          questionArea.setText("Starting the session..."); // Initial message
          startSessionButton.setEnabled(false);
          submitAnswerButton.setEnabled(true);
          pauseButton.setEnabled(true);
          resetButton.setEnabled(true);

          // Show answer field for user input
          answerField.setEnabled(true);
          answerField.requestFocusInWindow(); // Set focus to the answer field for user convenience
        });
      } else {
        questionArea.setText("Please load a valid questions file first.");
      }
    });

    // Submit answer button action
    submitAnswerButton.addActionListener(e -> {
      String userAnswer = answerField.getText();
      if (!userAnswer.isEmpty()) {
        spacedRepetition.submitAnswer(userAnswer); // Submit the answer
        answerField.setText(""); // Clear the answer field

        // Check if session is complete
        if (spacedRepetition.isSessionComplete()) {
          questionArea.setText("Session complete! Thank you for participating.");
          submitAnswerButton.setEnabled(false);
          answerField.setEnabled(false); // Disable the answer field after session completion
          pauseButton.setEnabled(false); // Disable pause button after completion
          resetButton.setEnabled(false); // Disable reset button after completion
        } else {
          // If not complete, load the next question
          String nextQuestion = "Question " + (spacedRepetition.getCurrentQuestion() + 1) + ": "
              + spacedRepetition.getCurrentQuestionText();
          questionArea.setText(nextQuestion);
          answerField.requestFocusInWindow(); // Keep focus on the answer field
        }
      }
    });

    // Pause button action
    pauseButton.addActionListener(e -> {
      // Logic to pause the session
      spacedRepetition.setPausedState(true); // Assuming you have a method to set paused state
      pauseButton.setEnabled(false);
      // Optionally, show a resume button if necessary (not shown in the code)
    });

    // Reset button action
    resetButton.addActionListener(e -> {
      // Logic to reset the session
      questionArea.setText("Session reset. Load a questions file to start again.");
      submitAnswerButton.setEnabled(false);
      pauseButton.setEnabled(false);
      resetButton.setEnabled(false);
      answerField.setText(""); // Clear answer field on reset
      // Optionally, reset the spaced repetition state if necessary
    });
  }

  private void setupActiveRecallTab() {
    JPanel activeRecallPanel = new JPanel(new BorderLayout());

    JTextArea statusArea = new JTextArea("Load a questions file to start the Active Recall session.");
    statusArea.setEditable(false);
    statusArea.setLineWrap(true);
    statusArea.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(statusArea);

    JTextField answerField = new JTextField(20); // Input field for user answers
    JButton submitAnswerButton = new JButton("Submit Answer");
    submitAnswerButton.setEnabled(false); // Disable until session starts

    JButton loadFileButton = new JButton("Load Questions File");
    JButton startSessionButton = new JButton("Start Session");
    JButton pauseButton = new JButton("Pause");
    JButton resetButton = new JButton("Reset");
    JButton backButton = new JButton("Back to Home"); // Back button
    startSessionButton.setEnabled(false);
    pauseButton.setEnabled(false);
    resetButton.setEnabled(false);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(loadFileButton);
    buttonPanel.add(startSessionButton);
    buttonPanel.add(pauseButton);
    buttonPanel.add(resetButton);
    buttonPanel.add(submitAnswerButton); // Add submit button to panel
    buttonPanel.add(backButton); // Add back button to the button panel

    activeRecallPanel.add(scrollPane, BorderLayout.CENTER);
    activeRecallPanel.add(answerField, BorderLayout.NORTH); // Add answer input field above the text area
    activeRecallPanel.add(buttonPanel, BorderLayout.SOUTH);

    mainPanel.add(activeRecallPanel, "ActiveRecall");

    // Action listener for the back button
    backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home")); // Back to Home

    DataStorage dataStorage = new DataStorage();

    loadFileButton.addActionListener(e -> {
      selectedFile = dataStorage.chooseFile((JFrame) SwingUtilities.getWindowAncestor(activeRecallPanel));
      if (selectedFile != null) {
        statusArea.setText("File loaded: " + selectedFile.getName() + "\nYou can now start the session.");
        startSessionButton.setEnabled(true);
      } else {
        statusArea.setText("No file selected. Please select a valid questions file.");
      }
    });

    startSessionButton.addActionListener(e -> {
      if (selectedFile != null && selectedFile.exists()) {
        activeRecall = new ActiveRecall(selectedFile);

        // Start the session in a new thread
        new Thread(() -> {
          String firstQuestion = activeRecall.nextQuestion();

          // Update GUI in the event dispatch thread
          SwingUtilities.invokeLater(() -> {
            statusArea.setText(firstQuestion);
            answerField.setText("");
            submitAnswerButton.setEnabled(true);
            startSessionButton.setEnabled(false);
            pauseButton.setEnabled(true);
            resetButton.setEnabled(true);
          });
        }).start();
      } else {
        statusArea.setText("Please load a valid questions file first.");
      }
    });

    submitAnswerButton.addActionListener(e -> {
      String userAnswer = answerField.getText();
      if (userAnswer.trim().isEmpty()) {
        JOptionPane.showMessageDialog(frame, "Please enter an answer before submitting.");
        return;
      }

      // Store the answer using the getter method
      activeRecall.getUserAnswers().add(userAnswer);
      String nextQuestion = activeRecall.nextQuestion();

      if (nextQuestion != null) {
        statusArea.setText(nextQuestion);
        answerField.setText(""); // Clear the answer field
      } else {
        // Handle end of session
        statusArea.setText("Session completed. Review your results.");
        submitAnswerButton.setEnabled(false); // Disable submission when done
      }
    });

    pauseButton.addActionListener(e -> {
      // Handle pause functionality
      if (!activeRecall.isPaused()) { // Use the getter method
        activeRecall.isPaused = true; // Update the pause state in your logic
        JOptionPane.showMessageDialog(frame, "Session paused. You can resume later.");
      } else {
        JOptionPane.showMessageDialog(frame, "Session is already paused.");
      }
    });

    resetButton.addActionListener(e -> {
      statusArea.setText("Session reset. Load a questions file to start again.");
      answerField.setText(""); // Clear the answer field
      submitAnswerButton.setEnabled(false);
      startSessionButton.setEnabled(true);
      pauseButton.setEnabled(false);
      resetButton.setEnabled(false);
    });
  }

  // Task Scheduler Tab setup
  private void setupTaskSchedulerTab() {
    JPanel taskSchedulerPanel = new JPanel();
    taskSchedulerPanel.setLayout(new BorderLayout());

    // Area to display tasks
    JTextArea taskArea = new JTextArea();
    taskArea.setEditable(false);
    taskArea.setLineWrap(true);
    taskArea.setWrapStyleWord(true);
    JScrollPane taskScrollPane = new JScrollPane(taskArea);
    taskScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    // Button to add task
    JButton addTaskButton = new JButton("Add Task");
    JButton changeStatusButton = new JButton("Change Status");
    JButton deleteTaskButton = new JButton("Delete Task");

    // Panel for buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addTaskButton);
    buttonPanel.add(changeStatusButton);
    buttonPanel.add(deleteTaskButton);

    // Back to home button
    JButton backButton = new JButton("Back to Home");

    // Add components to the task scheduler panel
    taskSchedulerPanel.add(taskScrollPane, BorderLayout.CENTER);
    taskSchedulerPanel.add(buttonPanel, BorderLayout.SOUTH);
    taskSchedulerPanel.add(backButton, BorderLayout.NORTH); // Position it at the top

    mainPanel.add(taskSchedulerPanel, "TaskScheduler");

    // Task Manager instance
    taskManager = new TaskManager();

    // Action Listener for Add Task button
    addTaskButton.addActionListener(e -> {
      String taskName = JOptionPane.showInputDialog("Enter task name:");
      String dueDateStr = JOptionPane.showInputDialog("Enter due date (YYYY-MM-DD):");
      if (taskName != null && dueDateStr != null) {
        taskManager.addTask(taskName, dueDateStr);
        updateTaskArea(taskArea);
      }
    });

    // Action Listener for Change Status button
    changeStatusButton.addActionListener(e -> {
      String taskNumberStr = JOptionPane.showInputDialog("Enter task number to change status:");
      if (taskNumberStr != null) {
        try {
          int taskNumber = Integer.parseInt(taskNumberStr);
          String statusStr = JOptionPane.showInputDialog("Is the task completed? (yes/no):");
          boolean isCompleted = "yes".equalsIgnoreCase(statusStr);
          taskManager.changeTaskStatus(taskNumber, isCompleted);
          updateTaskArea(taskArea);
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(frame, "Invalid task number.");
        }
      }
    });

    // Action Listener for Delete Task button
    deleteTaskButton.addActionListener(e -> {
      String taskNumberStr = JOptionPane.showInputDialog("Enter task number to delete:");
      if (taskNumberStr != null) {
        try {
          int taskNumber = Integer.parseInt(taskNumberStr);
          taskManager.deleteTask(taskNumber);
          updateTaskArea(taskArea);
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(frame, "Invalid task number.");
        }
      }
    });

    // Action Listener for Back to Home button
    backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home")); // Back to Home
  }

  // Helper method to update the task area
  private void updateTaskArea(JTextArea taskArea) {
    StringBuilder tasksDisplay = new StringBuilder();
    for (int i = 0; i < taskManager.getTasks().size(); i++) {
      tasksDisplay.append((i + 1) + ". " + taskManager.getTasks().get(i) + "\n");
    }
    taskArea.setText(tasksDisplay.toString());
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(StudyPlannerGui::new);
  }
}
