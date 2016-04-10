import java.awt.Font;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GUI extends JFrame {

	private static final int WIDTH = 500;
	private static final int HEIGHT = 500;
	private JPanel panel = new JPanel();
	private JButton buttons[][] = new JButton[3][3];
	private int counter = 0;
	private boolean turn = true;

	private boolean isComputerPlaying = false;

	int[][] prefMoves = { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 1, 0 }, { 1, 1 }, { 1, 2 }, { 2, 0 }, { 2, 1 }, { 2, 2 } };

	public GUI() {
		setTitle("TicTacToe");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		createContent();

		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);

	}

	public void createContent() {
		createMenu();
		panel.setLayout(new GridLayout(3, 3));
		add(panel);
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				buttons[x][y] = new JButton("");
				buttons[x][y].setFont(new Font("Arial", Font.BOLD, 50));
				buttons[x][y].addActionListener(new ButtonListener(x, y));
				panel.add(buttons[x][y]);
			}
		}
	}

	public boolean checkDraw() {
		counter++;
		return (counter == 9);
	}

	public void refresh() {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				buttons[x][y].setText("");
				turn = true;
				counter = 0;

			}
		}

	}

	// ----------------------------------------------------------------------
	// Menu creation
	// ----------------------------------------------------------------------

	public void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu menu = new JMenu("Game");
		menuBar.add(menu);

		JMenuItem newGame = new JMenuItem("New Game");
		newGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();

			}

		});

		JMenuItem playAI = new JMenuItem("Play Mike");
		playAI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isComputerPlaying) {
					playAI.setText("Play Mike");
					isComputerPlaying = false;
					refresh();
				} else {
					playAI.setText("Stop Playing Mike");
					isComputerPlaying = true;
					refresh();
				}
			}

		});
		menu.add(newGame);
		menu.add(playAI);
	}
	
	/** 
	 * Upon creation, every button implements this listener which has the following properties:
	 * 1. If the button is empty, 'X' takes the spot and the turn flips to 'O' whether it is AI or not
	 * 2. TODO
	 *
	 */

	// ----------------------------------------------------------------------
	// X and O board logic
	// ----------------------------------------------------------------------

	public class ButtonListener implements ActionListener {
		private AI mike = new AI();
		private int x;
		private int y;

		public ButtonListener(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			if (button.getText() == "") {
				if (turn) {
					button.setText("X");
					turn = false;
					if (checkWinner(x, y)) {
						JOptionPane.showMessageDialog(null, "Winner!");
						refresh();
					} else if (isComputerPlaying) {
						Timer t = new Timer(1000, new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								int[] mikeWinMoves = mike.getMoves(mike.toString());
								int[] moves = mike.getMoves("X");
								if (mikeWinMoves != null) {
									buttons[mikeWinMoves[0]][mikeWinMoves[1]].setText(mike.toString());
									System.out.println(mikeWinMoves[0] + " " + mikeWinMoves[1]);
									turn = true;
									if (checkWinner(mikeWinMoves[0], mikeWinMoves[1])) {
										JOptionPane.showMessageDialog(null, "Winner!");
										refresh();

									}
								}

								else if (moves != null) {
									buttons[moves[0]][moves[1]].setText(mike.toString());
									System.out.println(moves[0] + " " + moves[1]);
								} else {
									int index = 0;
									try {
										index = (int) (Math.random() * mike.lookupBoard().size());
										moves = mike.lookupBoard().get(index);
									} catch (IndexOutOfBoundsException ex) {
										JOptionPane.showMessageDialog(null, "Draw!");
										refresh();
									}
									if (moves != null) {
										buttons[moves[0]][moves[1]].setText(mike.toString());
									}

								}
								turn = true;
								if (moves != null) {
									if (checkWinner(moves[0], moves[1])) {
										JOptionPane.showMessageDialog(null, "Winner!");
										refresh();
									}
								}

							}

						}

						);

						t.start();
						t.setRepeats(false);

					}
				} else {
					button.setText("O");
					turn = true;
					if (checkWinner(x, y)) {
						JOptionPane.showMessageDialog(null, "Winner!");
						refresh();
					}
				}
			}

			if (checkDraw()) {
				JOptionPane.showMessageDialog(null, "Draw!");
				refresh();
			}
		}
		
		//checkWinner
		
		/**
		 *  Takes parameter integers 'x' and 'y' from the button action listener
		 *  and compares it with a pre-defined winning pattern. There is probably a more elegant 
		 *  solution, but Tic-Tac-Toe has a small number of possible winning combinations
		 * 
		 * @return true if either 'player' has a matched winning combination
		 */

		public boolean checkWinner(int x, int y) {
			if (buttons[0][y].getText() == buttons[1][y].getText()
					&& buttons[2][y].getText() == buttons[1][y].getText()) {
				return true;
			} else if (buttons[x][0].getText() == buttons[x][1].getText()
					&& buttons[x][2].getText() == buttons[x][0].getText()) {
				return true;
			} else if (x == y) { // Determines a winning diagonal pattern
				if (buttons[0][0].getText() == buttons[1][1].getText()
						&& buttons[0][0].getText() == buttons[2][2].getText()) {
					return true;
				} else if (buttons[2][0].getText() == buttons[1][1].getText()
						&& buttons[2][0].getText() == buttons[0][2].getText() && buttons[2][0].getText() != "") {
					return true;
				}
			} else if ((x == 0 && y == 2) || (x == 2 && y == 0) || buttons[1][1].getText() != "") { // Determines
																									// a
																									// winning
																									// anti-diagonal
																									// pattern
				return buttons[2][0].getText() == buttons[1][1].getText()
						&& buttons[2][0].getText() == buttons[0][2].getText();
			}
			return false;

		}

		public class AI {
			/**
			 * Represents the class for the AI component of the game. The AI is able to lookup
			 * and calculate moves for itself based upon the following scenarios in order of priority:
			 * 1. If the AI has a possible winning move, it will take it and win the game
			 * 2. If the 'player' has a possible winning move, the AI will place a marker on the winning position
			 * 3. If none of the criteria are met, it will make a random move
			 */
			
			
			
			
			/**
			 * Iterates through the board and for every empty space, it is added to a list to be used
			 * in the getMoves method (which ultimately determines the AI's next move
			 * @return A list containing empty spaces for which the AI can use to place its marker
			 */
			
			// ----------------------------------------------
			// Scanning the present board
			// ----------------------------------------------
			public ArrayList<int[]> lookupBoard() {
				ArrayList<int[]> listofMoves = new ArrayList<>();
				for (int[] moves : prefMoves) {
					if (buttons[moves[0]][moves[1]].getText() == "") {
						listofMoves.add(moves);
					}
				}
				return listofMoves;
			}

			// ----------------------------------------------
			// Logic 
			// ----------------------------------------------
			
			/**
			 * Based on the parameter player, getMoves temporarily places the player's marker to determine
			 * if there is a winning pattern. 
			 * @param player : A text representing the player (i.e 'X' or 'O')
			 * @return moves : An array on integers representing a button/tile on the board
			 */

			public int[] getMoves(String player) {
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						if (buttons[x][y].getText() == "") {
							buttons[x][y].setText(player);
							if (checkWinner(x, y)) {
								int[] moves = { x, y };
								buttons[x][y].setText("");
								return moves;
							}
							buttons[x][y].setText("");
						}
					}
				}
				return null;
			}

			public String toString() {
				return "O";
			}
		}
	}

	public static void main(String[] args) {
		new GUI();

		// TODO - clean up and document code.

	}
}
