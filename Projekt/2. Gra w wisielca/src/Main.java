import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Wisielec wisielec = new Wisielec();
            wisielec.setVisible(true);
        });
    }
}