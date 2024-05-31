package com.jrsf;

import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.*;

public class PythonTerminalEnd {
    private static RSyntaxTextArea inputArea;
    private static JTextArea outputArea;
    private static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("Python Terminal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        inputArea = new RSyntaxTextArea(20, 60);
        inputArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        inputArea.setCodeFoldingEnabled(true);

        // Add zoom in and zoom out functionality
        inputArea.addMouseWheelListener(new MouseWheelListener() {
            private final int FONT_SIZE_STEP = 2;
            private final int MIN_FONT_SIZE = 8;
            private final int MAX_FONT_SIZE = 36;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    int notches = e.getWheelRotation();
                    int newFontSize = inputArea.getFont().getSize() - (notches * FONT_SIZE_STEP);
                    newFontSize = Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, newFontSize));
                    inputArea.setFont(new Font(inputArea.getFont().getFamily(), Font.PLAIN, newFontSize));
                }
            }
        });

        // Add auto-complete
        CompletionProvider provider = createCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
        ac.install(inputArea);

        outputArea = new JTextArea();
        outputArea.setBorder(BorderFactory.createTitledBorder("Output"));
        outputArea.setEditable(false);

        JButton runButton = new JButton("Run");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");
        JButton themeButton = new JButton("Toggle Theme");

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pythonCode = inputArea.getText();
                String output = executePythonCode(pythonCode);
                outputArea.setText(output);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveScript();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadScript();
            }
        });

        themeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    toggleTheme();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new RTextScrollPane(inputArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        buttonPanel.add(runButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(themeButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

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

    private static void saveScript() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                inputArea.write(writer);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to save the script: " + e.getMessage());
            }
        }
    }

    private static void loadScript() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                inputArea.read(reader, null);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to load the script: " + e.getMessage());
            }
        }
    }

    private static void toggleTheme() throws IOException {
        Theme theme;
        if (inputArea.getBackground().equals(Color.WHITE)) {
            theme = Theme.load(PythonTerminal.class.getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
        } else {
            theme = Theme.load(PythonTerminal.class.getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/default.xml"));
        }
        theme.apply(inputArea);
    }

    private static CompletionProvider createCompletionProvider() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        String[] keywords = {
                "and", "as", "assert", "break", "class", "continue", "def", "del", "elif", "else", "except",
                "False", "finally", "for", "from", "global", "if", "import", "in", "is", "lambda", "None", "nonlocal",
                "not", "or", "pass", "raise", "return", "True", "try", "while", "with", "yield"
        };
        for (String keyword : keywords) {
            provider.addCompletion(new BasicCompletion(provider, keyword));
        }
        return provider;
    }
}
