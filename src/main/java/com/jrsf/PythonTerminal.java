package com.jrsf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PythonTerminal {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Python Terminal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JTextArea inputArea = new JTextArea();
        inputArea.setBorder(BorderFactory.createTitledBorder("Python Code"));

        JTextArea outputArea = new JTextArea();
        outputArea.setBorder(BorderFactory.createTitledBorder("Output"));
        outputArea.setEditable(false);

        JButton runButton = new JButton("Run");

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pythonCode = inputArea.getText();
                String output = executePythonCode(pythonCode);
                outputArea.setText(output);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        panel.add(runButton, BorderLayout.SOUTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.EAST);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private static String executePythonCode(String code) {
        try {
            // Write the code to a temporary file
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("temp", ".py");
            java.nio.file.Files.write(tempFile, code.getBytes());

            // Run the Python script
            ProcessBuilder pb = new ProcessBuilder("python", tempFile.toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Get the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            process.waitFor();

            // Delete the temporary file
            java.nio.file.Files.delete(tempFile);

            return output.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}

