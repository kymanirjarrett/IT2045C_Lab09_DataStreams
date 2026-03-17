import javax.swing.SwingUtilities;

/**
 * Launches the GUI.
 */
public class DataStreamsRunner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DataStreamsFrame::new);
    }
}