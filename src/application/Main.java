package application;

import gui.StudyPlannerGui;

public class Main {
  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(StudyPlannerGui::new);
  }
}
