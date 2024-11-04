package recall_techniques;

import shared.DisplayUpdater;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class SpacedRepetition {
    private File fileName;
    private List<String> questions;
    private List<String> userAnswers;
    private int currentQuestion;
    private boolean isPaused;
    private int sessionCount;
    private DisplayUpdater displayUpdater;

    public SpacedRepetition(File fileName, DisplayUpdater displayUpdater) {
        this.fileName = fileName;
        this.questions = new ArrayList<>();
        this.userAnswers = new ArrayList<>();
        this.currentQuestion = 0;
        this.isPaused = false;
        this.sessionCount = 0;
        this.displayUpdater = displayUpdater;
    }

    // Start the Spaced Repetition process
    public void startSpacedRepetitionSession() {
        if (fileName == null || !fileName.exists()) {
            displayUpdater.updateDisplay("No file provided. Please ensure the file is correctly selected.");
            return;
        }

        try {
            String content = new String(Files.readAllBytes(fileName.toPath()));
            generateQuestions(content); // Ensure this method populates the questions list

            // Define break durations for each session part
            int[] breakDurations = { 5, 7, 10 }; // Break durations in minutes
            int breakIndex = 0; // To keep track of the current break duration

            for (sessionCount = 1; sessionCount <= 4; sessionCount++) {
                displayUpdater.updateDisplay("\nSession " + sessionCount + ":");

                // Conduct practice and spaced repetition rounds
                if (!practiceRound())
                    return; // Exit if back command is issued
                if (!spacedRepetitionRound())
                    return; // Exit if back command is issued

                // Check if there are breaks to take
                if (sessionCount < 4) {
                    int breakDuration = breakDurations[breakIndex];
                    takeBreak(breakDuration); // Take a break after the two rounds
                    breakIndex = (breakIndex + 1) % breakDurations.length; // Move to the next break duration
                }
            }

            // Notify the user that the session is over
            displayUpdater.updateDisplay("\nAll sessions complete! Thank you for participating.");
        } catch (IOException e) {
            displayUpdater.updateDisplay("Error loading file: " + e.getMessage());
        }
    }

    private boolean practiceRound() {
        currentQuestion = 0; // Reset question index for the practice round
        displayUpdater.updateDisplay("Practice Round: Answer the following questions.");

        for (int i = 0; i < 5 && !isPaused; i++) {
            displayUpdater.updateDisplay("Question " + (i + 1) + ": " + questions.get(i));
            // Wait for user input here; return false to indicate waiting
            while (!isSessionComplete() && !isPaused) {
                // This loop is to wait for the answer to be submitted
                // You will need to implement a mechanism to wait for user input from the GUI
            }
        }
        currentQuestion = 0; // Reset the current question index after practice
        return true; // Return true to indicate session should continue
    }

    private boolean spacedRepetitionRound() {
        displayUpdater.updateDisplay("\nSpaced Repetition Round: Let's review the questions again.");

        List<Integer> shuffledIndices = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            shuffledIndices.add(i);
        }
        Collections.shuffle(shuffledIndices); // Shuffle the order of questions

        for (int index : shuffledIndices) {
            displayUpdater.updateDisplay("Question " + (index + 1) + ": " + questions.get(index));
            // Wait for user input here; return false to indicate waiting
            while (!isSessionComplete() && !isPaused) {
                // This loop is to wait for the answer to be submitted
                // You will need to implement a mechanism to wait for user input from the GUI
            }
        }
        return true; // Return true to indicate session should continue
    }

    private void takeBreak(int minutes) {
        displayUpdater.updateDisplay("Taking a " + minutes + " minute break.");
        try {
            Thread.sleep(minutes * 60 * 1000); // Sleep for the duration of the break
        } catch (InterruptedException e) {
            displayUpdater.updateDisplay("Break interrupted.");
        }
        displayUpdater.updateDisplay("Break over! Let's continue.");
    }

    public void submitAnswer(String answer) {
        if (currentQuestion < questions.size()) {
            userAnswers.add(answer); // Store the user's answer
            currentQuestion++;
        }
    }

    public boolean isSessionComplete() {
        return currentQuestion >= questions.size();
    }

    private void generateQuestions(String content) {
        List<String> words = Arrays.asList(content.split("\\W+"));
        Map<String, Long> wordCount = words.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        List<String> topWords = wordCount.entrySet().stream()
                .filter(entry -> isNoun(entry.getKey())) // Only consider nouns
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Create questions
        for (String keyword : topWords) {
            questions.add("What is the significance of '" + keyword + "'?");
        }
    }

    private boolean isNoun(String word) {
        return Character.isUpperCase(word.charAt(0)) || word.endsWith("s");
    }

    public void setPausedState(boolean paused) {
        this.isPaused = paused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    // Get the current question index
    public int getCurrentQuestion() {
        return currentQuestion;
    }

    // Get the current question text
    public String getCurrentQuestionText() {
        return questions.get(currentQuestion); // Return the question text at the current index
    }

    // Additional methods...
}
