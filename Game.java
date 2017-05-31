package Game;
/**
 * @author Poojith Jain (poojithj@andrew.cmu.edu)
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 * Class Game that defines the 'Whack-a-mole' game.
 */
public class Game {
    /**
     * Constant for the length of the text field.
     */
    private static final int COLS = 10;
    /**
     * Main panel of the layout.
     */
    private JPanel mainPanel = new JPanel();
    /**
     * Panel for enclosing the first row of elements.
     */
    private JPanel firstRowPanel = new JPanel();
    /**
     * Panel for enclosing the second row of elements.
     */
    private JPanel secondRowPanel = new JPanel();
    /**
     * Start button to start the game.
     */
    private JButton startButton = new JButton("Start");
    /**
     * Label for the timer.
     */
    private JLabel lblTime = new JLabel();
    /**
     * Field to display the timer.
     */
    private JTextField timeField = new JTextField(COLS);
    /**
     * Label for the score.
     */
    private JLabel lblScore = new JLabel();
    /**
     * Field to display the score.
     */
    private JTextField scoreField = new JTextField(COLS);
    /**
     * Thread to keep track of the timer.
     */
    private Thread timerThread;
    /**
     * Random object to pick a button from the array.
     */
    private Random random = new Random();
    /**
     * Constant for representing a mole.
     */
    private static final String MOLE = ":-)";
    /**
     * Constant for representing a hole.
     */
    private static final String HOLE = "[+]";
    /**
     * Variable for representing the score.
     */
    private static int score;
    /**
     * Variable for the count of the timer.
     */
    private static int count;
    /**
     * Color constant for mole.
     */
    private static final Color MOLE_COLOR = Color.LIGHT_GRAY;
    /**
     * Color constant for hole.
     */
    private static final Color HOLE_COLOR = Color.cyan;
    /**
     * Array of buttons for the holes and moles.
     */
    private JButton[] buttons;
    /**
     * An array of threads for the buttons.
     */
    private ButtonThread[] threads;
    /**
     * Constructor of the game class with no parameters.
     */
    public Game() {
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 14);
        Font alertFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
        Border buttonBorder = new LineBorder(Color.darkGray, 9);

        startButton.setFocusPainted(false);
        scoreField.setEditable(false);
        timeField.setEditable(false);

        lblTime.setText("Time Left:");
        lblScore.setText("Score:");

        firstRowPanel.add(startButton);
        firstRowPanel.add(lblTime);
        firstRowPanel.add(timeField);
        firstRowPanel.add(lblScore);
        firstRowPanel.add(scoreField);

        buttons = new JButton[64];
        addButtons(font, buttonBorder);
        startConfiguration(alertFont, buttonBorder);
        GridLayout gridLayout = new GridLayout(8, 8, 4, 4);
        secondRowPanel.setLayout(gridLayout);
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                timerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        timeField.setText("00:" + count);
                        try {
                            while (count >= 0) {
                                if (count >= 10) {
                                    timeField.setText("00:" + count);
                                } else if (count == 0) {
                                    timeField.setText("TIME UP!");
                                    downConfiguration();
                                } else {
                                    timeField.setText("00:0" + count);
                                }
                                Thread.sleep(1000);
                                count--;
                            }
                            Thread.sleep(5000);
                            startConfiguration(alertFont, buttonBorder);
                            startButton.setEnabled(true);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                timerThread.start();
                threads = new ButtonThread[buttons.length];
                for (int i = 0; i < buttons.length; i++) {
                    threads[i] = new ButtonThread(buttons[i]);
                    threads[i].start();
                }
            }
        });
        mainPanel.add(firstRowPanel);
        mainPanel.add(secondRowPanel);
    }
    /**
     * @return JPanel to retrieve the main panel.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
    /**
     * Threads for all the buttons in the game.
     */
    private class ButtonThread extends Thread {
        /**
         * Button representing the button of the thread.
         */
        private JButton button;
        /**
         * Variable for keeping the time of buttons in "Up" state.
         */
        private int randomUpTime;
        /**
         * Constructor of the ButtonThread class.
         * @param b button for which a new thread needs to be created.
         */
        public ButtonThread(JButton b) {
            button = b;
        }

        @Override
        public void run() {
            while (timerThread.isAlive()) {
                button.setText(MOLE);
                button.setBackground(MOLE_COLOR);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (button.getText().equals(MOLE)) {
                            score++;
                            scoreField.setText(String.valueOf(score));
                            synchronized (button) {
                                button.setText(":-(");
                            }
                        }
                    }
                });
                try {
                    randomUpTime = 1000 * random.nextInt((int) (1 + (Math.random() * (5 - 1))));
                    Thread.sleep(randomUpTime);
                    downConfiguration(button);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * @param font to set the font for the buttons
     * @param buttonBorder to set the border for the buttons
     */
    private void addButtons(Font font, Border buttonBorder) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(HOLE);
            buttons[i].setBackground(HOLE_COLOR);
            buttons[i].setFont(font);
            buttons[i].setOpaque(true);
            buttons[i].setFocusPainted(false);
            buttons[i].setPreferredSize(new Dimension(45, 45));
            buttons[i].setBorder(buttonBorder);
            secondRowPanel.add(buttons[i]);
        }
    }

    /**
     * @param button that has to be changed to a hole.
     */
    private void downConfiguration(JButton button) {
        synchronized (button) {
            button.setText(HOLE);
            button.setBackground(HOLE_COLOR);
        }
        try {
            int randomDownTime = 1000 * random.nextInt((int) (6 + (Math.random() * (12 - 1))));
            Thread.sleep(randomDownTime);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (count < 1) {
            downConfiguration();
        }
    }

    /**
     * Method to set the down configuration of the buttons.
     */
    public void downConfiguration() {
        for (int i = 0; i < buttons.length; i++) {
            synchronized (buttons) {
                buttons[i].setText(HOLE);
                buttons[i].setBackground(HOLE_COLOR);
                buttons[i].setEnabled(false);
            }
        }
        score = 0;
     }
    /**
     * @param font to set the font for the buttons
     * @param buttonBorder to set the border for the buttons
     */
    private void startConfiguration(Font font, Border buttonBorder) {
        count = 20;
        score = 0;
        for (int i = 0; i < buttons.length; i++) {
            synchronized (buttons) {
                buttons[i].setText(HOLE);
                buttons[i].setBackground(HOLE_COLOR);
            }
            buttons[i].setEnabled(true);
            buttons[i].setFont(font);
            buttons[i].setOpaque(true);
            buttons[i].setFocusPainted(false);
            buttons[i].setPreferredSize(new Dimension(45, 45));
            buttons[i].setBorder(buttonBorder);
        }
        timeField.setText(" ");
        scoreField.setText(" ");
    }

    /**
     * Method to create and set the GUI layout.
     */
    private static void createAndShowGUI() {
        Game game = new Game();
        JFrame window = new JFrame("Whack-a-mole");
        window.setSize(400, 400);
        window.setPreferredSize(new Dimension(550, 550));
        window.setResizable(false);
        window.getContentPane().add(game.getMainPanel());
        window.pack();
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * @param args for the main method
     */
    public static void main(String[] args) {
        createAndShowGUI();
    }
}

