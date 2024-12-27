package _experiments;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PokerGameExample {
    private static JTextArea logArea;
    private static JPanel cardDisplayPanel;
    private static JPanel actionPanel;
    private static JSlider betSlider;
    private static JLabel betLabel;
    private static final String[] CARDS = {
            "Ace of Spades", "2 of Hearts", "3 of Diamonds", "4 of Clubs", "5 of Spades",
            "6 of Hearts", "7 of Diamonds", "8 of Clubs", "9 of Spades", "10 of Hearts",
            "Jack of Diamonds", "Queen of Clubs", "King of Spades"
    };
    private static final Map<String, String> CARD_TEXT_REPRESENTATIONS = new HashMap<>();
    private static Random random = new Random();
    private static int currency = 1000;

    public static void main(String[] args) {
        defineCardTextRepresentations();

        // Create the main window
        JFrame frame = new JFrame("Poker Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Create a panel for the card display
        cardDisplayPanel = new JPanel();
        cardDisplayPanel.setBackground(Color.WHITE);
        cardDisplayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cardDisplayPanel.setLayout(new BorderLayout());

        // Create a panel for buttons and actions
        actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(2, 1, 10, 10));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create a slider for betting
        betSlider = new JSlider(0, currency, 0);
        betSlider.setMajorTickSpacing(100);
        betSlider.setMinorTickSpacing(20);
        betSlider.setPaintTicks(true);
        betSlider.setPaintLabels(true);
        betLabel = new JLabel("Bet Amount: $0", JLabel.CENTER);
        actionPanel.add(betSlider);
        actionPanel.add(betLabel);

        // Create action buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton betButton = new JButton("Bet");
        styleButton(betButton);
        buttonPanel.add(betButton);

        JButton checkButton = new JButton("Check");
        styleButton(checkButton);
        buttonPanel.add(checkButton);

        JButton raiseButton = new JButton("Raise");
        styleButton(raiseButton);
        buttonPanel.add(raiseButton);

        JButton foldButton = new JButton("Fold");
        styleButton(foldButton);
        buttonPanel.add(foldButton);

        // Create the log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.WHITE);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Log"));
        logScrollPane.setPreferredSize(new Dimension(300, 600));

        // Add components to the frame
        frame.add(cardDisplayPanel, BorderLayout.CENTER);
        frame.add(actionPanel, BorderLayout.EAST);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(logScrollPane, BorderLayout.WEST);

        // Redirect System.out to the log area
        PrintStream logStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                SwingUtilities.invokeLater(() -> {
                    logArea.append(String.valueOf((char) b));
                    logArea.setCaretPosition(logArea.getDocument().getLength()); // Scroll to end
                });
            }
        });
        System.setOut(logStream);

        // Display the window
        frame.setVisible(true);

        // Test logging
        System.out.println("Welcome to the Poker Game!");

        // Set up the betting slider listener
        betSlider.addChangeListener(e -> betLabel.setText("Bet Amount: $" + betSlider.getValue()));

        // Set up action listeners
        betButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int betAmount = betSlider.getValue();
                if (betAmount > 0 && betAmount <= currency) {
                    currency -= betAmount;
                    System.out.println("You bet $" + betAmount + ". Remaining currency: $" + currency);
                } else {
                    System.out.println("Invalid bet amount.");
                }
            }
        });

        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("You checked.");
            }
        });

        raiseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int raiseAmount = betSlider.getValue();
                if (raiseAmount > 0 && raiseAmount <= currency) {
                    currency -= raiseAmount;
                    System.out.println("You raised by $" + raiseAmount + ". Remaining currency: $" + currency);
                } else {
                    System.out.println("Invalid raise amount.");
                }
            }
        });

        foldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("You folded.");
            }
        });

        // Set up the periodic task to ask for card choices
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cardChoice = (String) JOptionPane.showInputDialog(
                        frame,
                        "Choose a card:",
                        "Card Selection",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        CARDS,
                        CARDS[0]
                );
                if (cardChoice != null) {
                    displayCard(cardChoice);
                    System.out.println("User chose: " + cardChoice);
                }
            }
        });

        // Start the timer when the button is pressed
        JButton startButton = new JButton("Start Choosing Cards");
        styleButton(startButton);
        frame.add(startButton, BorderLayout.NORTH);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.start();
                startButton.setEnabled(false); // Disable the button after starting the timer
            }
        });
    }

    private static void defineCardTextRepresentations() {
        for (String card : CARDS) {
            // Create a simple text representation of each card
            CARD_TEXT_REPRESENTATIONS.put(card, generateCardTextRepresentation(card));
        }
    }

    private static String generateCardTextRepresentation(String cardName) {
        String[] parts = cardName.split(" of ");
        String rank = parts[0];
        String suit = parts[1];
        return String.format(
                "+-----------------------+\n" +
                        "| %2s %s          |\n" +
                        "|                       |\n" +
                        "|                       |\n" +
                        "|                       |\n" +
                        "|            %s         |\n" +
                        "|                       |\n" +
                        "|                       |\n" +
                        "| %2s %s          |\n" +
                        "+-----------------------+",
                rank, suit,
                suit,
                rank, suit
        );
    }

    private static void displayCard(String cardName) {
        cardDisplayPanel.removeAll();
        JTextArea cardTextArea = new JTextArea(CARD_TEXT_REPRESENTATIONS.get(cardName));
        cardTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        cardTextArea.setBackground(Color.WHITE);
        cardTextArea.setForeground(Color.BLACK);
        cardTextArea.setEditable(false);
        cardDisplayPanel.add(cardTextArea, BorderLayout.CENTER);
        cardDisplayPanel.revalidate();
        cardDisplayPanel.repaint();
    }

    private static void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(50, 150, 250));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Sans Serif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
