package recall_techniques;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ActiveRecall {
  private File fileName;
  private List<String> questions;
  private List<String> userAnswers;
  private List<String> correctAnswers;
  private int currentQuestion;
  public boolean isPaused;
  private int correctAnswersCount;

  public ActiveRecall(File fileName) {
    this.fileName = fileName;
    this.questions = new ArrayList<>();
    this.userAnswers = new ArrayList<>();
    this.correctAnswers = new ArrayList<>();
    this.currentQuestion = 0;
    this.isPaused = false;
    this.correctAnswersCount = 0;
  }

  // Getter for userAnswers
  public List<String> getUserAnswers() {
    return userAnswers;
  }

  // Getter for isPaused
  public boolean isPaused() {
    return isPaused;
  }

  // Start the Active Recall session
  public void startRecallSession(Scanner scanner) {
    if (fileName == null || !fileName.exists()) {
      System.out.println("No file provided. Please ensure the file is correctly selected.");
      return;
    }

    try {
      String content = new String(Files.readAllBytes(fileName.toPath()));
      generateQuestions(content);

      // Practice Round: Collect user's answers
      System.out.println("Practice Round: Answer the following 10 questions.");
      if (collectUserAnswers(scanner))
        return; // Check if 'back' was selected

      // Active Recall Round: Ask questions again in a shuffled order
      System.out.println("\nActive Recall Round: Let's test your memory.");
      if (activeRecallRound(scanner))
        return; // Check if 'back' was selected

    } catch (IOException e) {
      System.out.println("Error loading file: " + e.getMessage());
    }
  }

  // Practice round where user provides answers to questions
  private boolean collectUserAnswers(Scanner scanner) {
    while (currentQuestion < 10 && !isPaused) {
      System.out.println("Question " + (currentQuestion + 1) + ": " + questions.get(currentQuestion));
      String answer = scanner.nextLine();
      userAnswers.add(answer); // Store user's answers
      currentQuestion++;

      // After each question, handle pause, reset, or go back
      if (currentQuestion < 10) {
        System.out.println("Type 'pause' to pause, 'reset' to reset, or 'back' to return to the homescreen:");
        String command = scanner.nextLine();
        if (handleCommand(command, scanner)) {
          return true; // Return true if 'back' is selected
        }
      }
    }
    currentQuestion = 0; // Reset question index for Active Recall round
    return false; // Return false to continue the session
  }

  // Active Recall round where the same questions are asked in shuffled order
  private boolean activeRecallRound(Scanner scanner) {
    List<Integer> shuffledIndices = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      shuffledIndices.add(i);
    }
    Collections.shuffle(shuffledIndices); // Shuffle the order of questions

    correctAnswersCount = 0; // Reset correct answers count for Active Recall round

    for (int index : shuffledIndices) {
      System.out.println("Question " + (index + 1) + ": " + questions.get(index));
      String answer = scanner.nextLine();

      // Check if the answer matches the one provided in the practice round
      if (answer.equalsIgnoreCase(userAnswers.get(index))) {
        correctAnswersCount++;
      }

      System.out.println("Your initial answer was: " + userAnswers.get(index));

      // After each question, handle pause, reset, or go back
      System.out.println("Type 'pause' to pause, 'reset' to reset, or 'back' to return to the homescreen:");
      String command = scanner.nextLine();
      if (handleCommand(command, scanner)) {
        return true; // Return true if 'back' is selected
      }
    }

    showResults(); // Show results after Active Recall round
    return false; // Return false to indicate the session ended normally
  }

  // Method to return the next question
  public String nextQuestion() {
    if (currentQuestion < questions.size()) {
      return questions.get(currentQuestion++);
    }
    return null; // No more questions
  }

  // Handle commands: pause, reset, etc.
  private boolean handleCommand(String command, Scanner scanner) {
    switch (command) {
      case "pause":
        isPaused = true;
        System.out.println("Session paused.");
        System.out.println("Type 'resume' to continue, 'reset' to reset, or 'back' to return to the homescreen:");
        String pauseCommand = scanner.nextLine();
        if (pauseCommand.equals("resume")) {
          isPaused = false; // Resume session
          continueSession(scanner); // Call continueSession to resume from where it left off
        } else if (pauseCommand.equals("reset")) {
          resetSession();
          startRecallSession(scanner); // Restart the session
        } else if (pauseCommand.equals("back")) {
          System.out.println("Returning to homescreen.");
          return true; // Return true to indicate back option was selected
        } else {
          System.out.println("Invalid command. Returning to homescreen.");
          return true; // Return true to indicate back option was selected
        }
        break;
      case "reset":
        resetSession();
        startRecallSession(scanner); // Restart the session
        break;
      case "back":
        System.out.println("Returning to homescreen.");
        return true; // Return true to indicate back option was selected
      default:
        System.out.println("Continuing to next question...");
    }
    return false; // Return false to indicate the session should continue
  }

  // Continue the session from where it was paused
  private void continueSession(Scanner scanner) {
    if (currentQuestion < 10) {
      if (collectUserAnswers(scanner))
        return; // Check if 'back' was selected
    } else {
      if (activeRecallRound(scanner))
        return; // Check if 'back' was selected
    }
  }

  // Generate questions based on the content
  private void generateQuestions(String content) {
    // Simulate Named Entity Recognition (NER) and generate 10 questions
    List<String> words = Arrays.asList(content.split("\\W+"));
    Map<String, Long> wordCount = words.stream()
        .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

    // Get the top 10 most used nouns and generate significance questions
    List<String> topWords = wordCount.entrySet().stream()
        .filter(entry -> isNoun(entry.getKey())) // Only consider nouns
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .limit(10)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    // Create questions
    for (String keyword : topWords) {
      questions.add("What is the significance of '" + keyword + "'?");
      correctAnswers.add("The significance of " + keyword + " is related to its context in the text."); // Placeholder
                                                                                                        // answer
    }
  }

  // Show the results and accuracy rate
  private void showResults() {
    double accuracyRate = ((double) correctAnswersCount / 10) * 100;
    System.out.println("Active Recall round completed. Accuracy rate: " + accuracyRate + "%");

    // Provide feedback based on accuracy
    if (accuracyRate < 50) {
      System.out.println("You need improvement in understanding the material.");
    } else {
      System.out.println("Good job! Keep practicing to improve further.");
    }

    // Option to restart or go back
    System.out.println("1. Restart");
    System.out.println("2. Back to homescreen");

    Scanner scanner = new Scanner(System.in);
    String command = scanner.nextLine();
    if (command.equals("1")) {
      resetSession();
      startRecallSession(scanner); // Restart the session
    } else {
      System.out.println("Returning to homescreen.");
    }
  }

  // Check if a word is a noun (simple check for this example)
  private boolean isNoun(String word) {
    return Character.isUpperCase(word.charAt(0)) || word.endsWith("s");
  }

  // Reset session state
  private void resetSession() {
    currentQuestion = 0;
    correctAnswersCount = 0;
    userAnswers.clear(); // Clear previous user answers
    isPaused = false;
    System.out.println("Session reset. Starting from question 1.");
  }
}
