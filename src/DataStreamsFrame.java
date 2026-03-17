import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsFrame extends JFrame {

    private Path selectedFilePath;

    private final JLabel fileLabel = new JLabel("Selected File: None");
    private final JTextField searchField = new JTextField(20);

    private final JTextArea originalTextArea = new JTextArea(25, 35);
    private final JTextArea filteredTextArea = new JTextArea(25, 35);

    private final JButton loadButton = new JButton("Load a File");
    private final JButton searchButton = new JButton("Search the File");
    private final JButton quitButton = new JButton("Quit");

    public DataStreamsFrame() {
        setTitle("Java Data Streams Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(topPanel(), BorderLayout.NORTH);
        add(centerPanel(), BorderLayout.CENTER);
        add(bottomPanel(), BorderLayout.SOUTH);

        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        originalTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        filteredTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        quitButton.addActionListener(e -> System.exit(0));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel topPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.add(fileLabel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search String:"));
        searchPanel.add(searchField);

        panel.add(filePanel);
        panel.add(searchPanel);

        return panel;
    }

    private JPanel centerPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JScrollPane leftScroll = new JScrollPane(originalTextArea);
        leftScroll.setBorder(new TitledBorder("Original File"));

        JScrollPane rightScroll = new JScrollPane(filteredTextArea);
        rightScroll.setBorder(new TitledBorder("Filtered Results"));

        panel.add(leftScroll);
        panel.add(rightScroll);

        return panel;
    }

    private JPanel bottomPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        panel.add(loadButton);
        panel.add(searchButton);
        panel.add(quitButton);

        return panel;
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFilePath = chooser.getSelectedFile().toPath();
            fileLabel.setText("Selected File: " + selectedFilePath.getFileName());

            try {
                List<String> lines = Files.readAllLines(selectedFilePath);
                originalTextArea.setText(String.join("\n", lines));
                originalTextArea.setCaretPosition(0);
                filteredTextArea.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error loading file: " + ex.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void searchFile() {
        if (selectedFilePath == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please load a file first.",
                    "No File Loaded",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a search string.",
                    "Missing Search String",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try (Stream<String> lines = Files.lines(selectedFilePath)) {
            String filteredOutput = lines
                    .filter(line -> line.toLowerCase().contains(searchText))
                    .collect(Collectors.joining("\n"));

            if (filteredOutput.isBlank()) {
                filteredTextArea.setText("No matching lines found.");
            } else {
                filteredTextArea.setText(filteredOutput);
            }

            filteredTextArea.setCaretPosition(0);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error searching file: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}