import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

class ConnectFourGUI extends JFrame {
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private static final char EMPTY = '.';
    private static final char PLAYER = 'P';
    private static final char COMPUTER = 'C';
    private static final char WIN = 'W';
    private char[][] board = new char[ROWS][COLUMNS];
    private JButton[] buttons = new JButton[COLUMNS];
    private JPanel boardPanel = new JPanel(new GridLayout(ROWS, COLUMNS));
    private JLabel statusLabel = new JLabel("Player's turn");
    private Timer timer;
    private boolean isBlinking = false;

    public ConnectFourGUI() {
        setTitle("Connect Four");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, COLUMNS));
        for (int i = 0; i < COLUMNS; i++) {
            buttons[i] = new JButton(String.valueOf((char) ('A' + i)));
            int column = i;
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    playerMove(column);
                }
            });
            buttonPanel.add(buttons[i]);
        }
        add(buttonPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        initializeBoard();
        updateBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    private void updateBoard() {
        boardPanel.removeAll();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                JPanel cell = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(Color.BLUE);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        g.setColor(Color.WHITE);
                        g.fillOval(5, 5, getWidth() - 10, getHeight() - 10);
                    }
                };
                if (board[i][j] == PLAYER) {
                    cell.add(new Disk(Color.RED));
                } else if (board[i][j] == COMPUTER) {
                    cell.add(new Disk(Color.YELLOW));
                } else if (board[i][j] == WIN) {
                    cell.add(new Disk(Color.GREEN));
                } else {
                    cell.add(new Disk(Color.WHITE));
                }
                boardPanel.add(cell);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void playerMove(int column) {
        if (isBlinking || board[0][column] != EMPTY) {
            return;
        }
        placeDisk(column, PLAYER);
        updateBoard();
        if (checkWin(PLAYER)) {
            statusLabel.setText("Player wins!");
            highlightWinningDisks();
            disableButtons();
            JOptionPane.showMessageDialog(this, "Player wins!");
            return;
        }
        if (isBoardFull()) {
            statusLabel.setText("It's a draw!");
            disableButtons();
            JOptionPane.showMessageDialog(this, "It's a draw!");
            return;
        }
        statusLabel.setText("Computer's turn");
        computerMove();
    }

    private void computerMove() {
        Random random = new Random();
        int column;
        while (true) {
            column = random.nextInt(COLUMNS);
            if (board[0][column] == EMPTY) {
                break;
            }
        }
        placeDisk(column, COMPUTER);
        updateBoard();
        if (checkWin(COMPUTER)) {
            statusLabel.setText("Computer wins!");
            highlightWinningDisks();
            disableButtons();
            JOptionPane.showMessageDialog(this, "Computer wins!");
            return;
        }
        if (isBoardFull()) {
            statusLabel.setText("It's a draw!");
            disableButtons();
            JOptionPane.showMessageDialog(this, "It's a draw!");
            return;
        }
        statusLabel.setText("Player's turn");
    }

    private void placeDisk(int column, char disk) {
        for (int i = ROWS - 1; i >= 0; i--) {
            if (board[i][column] == EMPTY) {
                board[i][column] = disk;
                break;
            }
        }
    }

    private boolean checkWin(char disk) {
        // Check horizontal
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j <= COLUMNS - 4; j++) {
                if (board[i][j] == disk && board[i][j + 1] == disk && board[i][j + 2] == disk && board[i][j + 3] == disk) {
                    markWinningDisks(new int[][]{{i, j}, {i, j + 1}, {i, j + 2}, {i, j + 3}});
                    return true;
                }
            }
        }

        // Check vertical
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (board[i][j] == disk && board[i + 1][j] == disk && board[i + 2][j] == disk && board[i + 3][j] == disk) {
                    markWinningDisks(new int[][]{{i, j}, {i + 1, j}, {i + 2, j}, {i + 3, j}});
                    return true;
                }
            }
        }

        // Check diagonal (bottom-left to top-right)
        for (int i = 3; i < ROWS; i++) {
            for (int j = 0; j <= COLUMNS - 4; j++) {
                if (board[i][j] == disk && board[i - 1][j + 1] == disk && board[i - 2][j + 2] == disk && board[i - 3][j + 3] == disk) {
                    markWinningDisks(new int[][]{{i, j}, {i - 1, j + 1}, {i - 2, j + 2}, {i - 3, j + 3}});
                    return true;
                }
            }
        }

        // Check diagonal (top-left to bottom-right)
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j <= COLUMNS - 4; j++) {
                if (board[i][j] == disk && board[i + 1][j + 1] == disk && board[i + 2][j + 2] == disk && board[i + 3][j + 3] == disk) {
                    markWinningDisks(new int[][]{{i, j}, {i + 1, j + 1}, {i + 2, j + 2}, {i + 3, j + 3}});
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int j = 0; j < COLUMNS; j++) {
            if (board[0][j] == EMPTY) {
                return false;
            }
        }
        return true;
    }

    private void disableButtons() {
        for (JButton button : buttons) {
            button.setEnabled(false);
        }
    }

    private void markWinningDisks(int[][] winningDisks) {
        for (int[] disk : winningDisks) {
            board[disk[0]][disk[1]] = WIN;
        }
    }

    private void highlightWinningDisks() {
        updateBoard();
    }

    private class Disk extends JPanel {
        private Color color;

        public Disk(Color color) {
            this.color = color;
            setPreferredSize(new Dimension(60, 60)); // Larger disks
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            g.fillOval(5, 5, getWidth() - 10, getHeight() - 10);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConnectFourGUI frame = new ConnectFourGUI();
            frame.setVisible(true);
        });
    }
}
