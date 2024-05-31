import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Wisielec extends JFrame {
    private final Game game;
    private JLabel wordLabel;
    private JTextField inputField;
    private JButton submitButton;
    private JLabel messageLabel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public Wisielec() {
        setTitle("Gra w Wisielca");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game = new Game();
        cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        mainPanel.add(createMainMenuPanel(), "MainMenu");
        mainPanel.add(createDifficultyPanel(), "ChooseDifficulty");
        mainPanel.add(createGamePanel(), "Game");
        mainPanel.add(createAddWordPanel(), "AddWord");
        mainPanel.add(createStatsPanel(), "Stats");

        add(mainPanel);
        cardLayout.show(mainPanel, "MainMenu");
        this.mainPanel = mainPanel;
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));

        JButton playButton = new JButton("Zagraj");
        JButton addWordButton = new JButton("Dodaj słowo");
        JButton statsButton = new JButton("Wyświetl statystyki");
        JButton exitButton = new JButton("Wyjdź");

        playButton.addActionListener(e -> cardLayout.show(mainPanel, "ChooseDifficulty"));

        addWordButton.addActionListener(e -> cardLayout.show(mainPanel, "AddWord"));

        statsButton.addActionListener(e -> {
            updateStatsUI();
            cardLayout.show(mainPanel, "Stats");
        });

        exitButton.addActionListener(e -> System.exit(0));

        panel.add(playButton);
        panel.add(addWordButton);
        panel.add(statsButton);
        panel.add(exitButton);

        return panel;
    }

    private JPanel createDifficultyPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        JLabel label = new JLabel("Wybierz poziom trudności:");
        panel.add(label);

        JButton easyButton = new JButton("Łatwy");
        JButton mediumButton = new JButton("Średni");
        JButton hardButton = new JButton("Trudny");

        easyButton.addActionListener(e -> {
            game.chooseDifficulty(1);
            cardLayout.show(mainPanel, "Game");
            updateGameUI();
        });

        mediumButton.addActionListener(e -> {
            game.chooseDifficulty(2);
            cardLayout.show(mainPanel, "Game");
            updateGameUI();
        });

        hardButton.addActionListener(e -> {
            game.chooseDifficulty(3);
            cardLayout.show(mainPanel, "Game");
            updateGameUI();
        });

        panel.add(easyButton);
        panel.add(mediumButton);
        panel.add(hardButton);

        return panel;
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1));
        wordLabel = new JLabel("Słowo: ");
        inputField = new JTextField(1);
        submitButton = new JButton("Sprawdź literę");
        messageLabel = new JLabel();

        JButton backButton = new JButton("Wróć do menu");
        backButton.addActionListener(e -> {
            game.resetGame();
            cardLayout.show(mainPanel, "MainMenu");
        });

        submitButton.addActionListener(e -> {
            if (!game.gameEnded()) {
                checkLetter();
            }
        });

        panel.add(wordLabel);
        panel.add(inputField);
        panel.add(submitButton);
        panel.add(messageLabel);
        panel.add(backButton);

        return panel;
    }

    private JPanel createAddWordPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 1));

        JTextField wordField = new JTextField(20);
        JComboBox<String> difficultyBox = new JComboBox<>(new String[]{"Łatwy", "Średni", "Trudny"});
        JLabel messageLabel = new JLabel();

        JButton addButton = createAddButton(wordField, difficultyBox, messageLabel);

        JButton backButton = new JButton("Wróć do menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        panel.add(new JLabel("Nowe słowo:"));
        panel.add(wordField);
        panel.add(new JLabel("Poziom trudności:"));
        panel.add(difficultyBox);
        panel.add(addButton);
        panel.add(backButton);
        panel.add(messageLabel);

        return panel;
    }

    private JButton createAddButton(JTextField wordField, JComboBox<String> difficultyBox, JLabel messageLabel) {
        JButton addButton = new JButton("Dodaj słowo");
        addButton.addActionListener(e -> {
            String newWord = wordField.getText().toLowerCase();
            int difficulty = difficultyBox.getSelectedIndex() + 1;

            List<String> targetList;
            switch (difficulty) {
                case 1:
                    targetList = game.easyWords;
                    break;
                case 2:
                    targetList = game.mediumWords;
                    break;
                case 3:
                    targetList = game.hardWords;
                    break;
                default:
                    messageLabel.setText("Nieprawidłowy wybór poziomu trudności.");
                    return;
            }

            if (!targetList.contains(newWord)) {
                targetList.add(newWord);
                messageLabel.setText("Słowo dodane do bazy.");
            } else {
                messageLabel.setText("Słowo już istnieje w bazie.");
            }
        });
        return addButton;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1));

        JLabel statsLabel = new JLabel("Statystyki gry:");
        JLabel winsLabel = new JLabel("Liczba zwycięstw: 0");
        JLabel lossesLabel = new JLabel("Liczba porażek: 0");
        JLabel totalGamesLabel = new JLabel("Liczba zagranych gier: 0");

        JButton backButton = new JButton("Wróć do menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        panel.add(statsLabel);
        panel.add(winsLabel);
        panel.add(lossesLabel);
        panel.add(totalGamesLabel);
        panel.add(backButton);

        return panel;
    }

    private void checkLetter() {
        String input = inputField.getText().toLowerCase();
        if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
            char letter = input.charAt(0);
            if (!game.guessedLetters.contains(letter)) {
                game.checkLetter(letter);
                updateGameUI();
            } else {
                messageLabel.setText("Ta litera już była podana.");
            }
        } else {
            messageLabel.setText("Podaj pojedynczą literę.");
        }
        inputField.setText("");
    }

    private void updateGameUI() {
        wordLabel.setText("Słowo: " + formatWord(game.userWord));
        if (game.lives == 0) {
            messageLabel.setText("Przegrałeś! Słowo to: " + game.word);
            game.losses++;
        } else if (game.word.equals(String.valueOf(game.userWord))) {
            messageLabel.setText("Wygrałeś!");
            game.wins++;
        } else {
            messageLabel.setText("Pozostałe życia: " + game.lives);
        }
    }

    private void updateStatsUI() {
        JLabel statsLabel = (JLabel) ((JPanel) mainPanel.getComponent(4)).getComponent(0);
        JLabel winsLabel = (JLabel) ((JPanel) mainPanel.getComponent(4)).getComponent(1);
        JLabel lossesLabel = (JLabel) ((JPanel) mainPanel.getComponent(4)).getComponent(2);
        JLabel totalGamesLabel = (JLabel) ((JPanel) mainPanel.getComponent(4)).getComponent(3);

        statsLabel.setText("Statystyki gry:");
        winsLabel.setText("Liczba zwycięstw: " + game.wins);
        lossesLabel.setText("Liczba porażek: " + game.losses);
        totalGamesLabel.setText("Liczba zagranych gier: " + (game.wins + game.losses));
    }

    private String formatWord(char[] userWord) {
        StringBuilder formatted = new StringBuilder();
        for (char c : userWord) {
            formatted.append(c).append(' ');
        }
        return formatted.toString().trim();
    }

    private static class Game {
        List<String> easyWords = new ArrayList<>(Arrays.asList("kot", "dom", "las", "pies", "oko"));
        List<String> mediumWords = new ArrayList<>(Arrays.asList("komputer", "biblioteka", "delfin", "słownik", "kwarc"));
        List<String> hardWords = new ArrayList<>(Arrays.asList("konstantynopolitanczykowianeczka", "wyimaginowana", "jeżozwierze", "szczebrzeszynie", "człekokształtny"));

        List<String> currentWords;
        String word;
        char[] userWord;
        int lives;
        int wins = 0;
        int losses = 0;
        Set<Character> guessedLetters = new HashSet<>();

        public Game() {
            resetGame();
        }

        public void chooseDifficulty(int difficulty) {
            switch (difficulty) {
                case 1:
                    currentWords = easyWords;
                    break;
                case 2:
                    currentWords = mediumWords;
                    break;
                case 3:
                    currentWords = hardWords;
                    break;
            }
            Random random = new Random();
            word = currentWords.get(random.nextInt(currentWords.size())).toLowerCase();
            userWord = new char[word.length()];
            Arrays.fill(userWord, '_');
            guessedLetters.clear();
        }

        public void checkLetter(char letter) {
            guessedLetters.add(letter);
            boolean foundLetter = false;
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == letter) {
                    userWord[i] = letter;
                    foundLetter = true;
                }
            }
            if (!foundLetter) {
                lives--;
            }
        }

        public boolean gameEnded() {
            return lives == 0 || word.equals(String.valueOf(userWord));
        }

        public void resetGame() {
            lives = 6;
            guessedLetters.clear();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Wisielec::new);
    }
}