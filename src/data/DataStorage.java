package data;

import javax.swing.*;
import java.io.File;

public class DataStorage {

  public File chooseFile(JFrame parentFrame) {
    final File[] selectedFile = new File[1]; // To store the selected file

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select a Text File");
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
    parentFrame.setAlwaysOnTop(true);

    int userSelection = fileChooser.showOpenDialog(parentFrame);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
      selectedFile[0] = fileChooser.getSelectedFile();
    } else {
      selectedFile[0] = null; // Handle no file selected
    }

    return selectedFile[0];
  }
}
