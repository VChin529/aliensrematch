package aliensrematch;

import java.util.*;
import java.text.DecimalFormat;

public class bot1 {
	int x, y; // coordinates
	int k; // dimension of alien scanner radius
	int alpha; // sensitivity of crewmember scanner
	board board; // board that the bot is on
	alien alien; // array of aliens
	crewmember crewmember; // crewmember to save
	int debug = 1; // utility for debugging. ignore.
	int debugpath = 0; // utility for debugging. ignore.

	public bot1(int k) {
		// generate board dimension 50x50
		board = new board(5);

		// random placement of bot
		cell curr = board.randomCell();
		this.x = curr.x;
		this.y = curr.y;
		this.k = k;
		this.alpha=2;

		// generate 1 alien
		alien = new alien(board);
		while (alienScanCoord(alien.x, alien.y)) {
			board.board[alien.x][alien.y].alien = false;
			alien = new alien(board);
		}

		// initialize alien probabilities
		// keep track of open cells
		double scanSize = scanRadiusBlocks();
		System.out.println("Bot is at: x: " + curr.x + " y: " + curr.y + " size:" + scanSize);
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				cell c = board.board[i][j];
				if (!c.state) {
					c.palien1 = 0;
					c.pcrew1= 0;
				} else if (alienScanCoord(i, j)) {
					c.palien1 = 0;
					c.pcrew1 = 0;
				} else {
					c.pcrew1 = (1.0 / ((board.open) - scanSize));
					c.palien1 = (1.0 / ((board.open) - scanSize));
				}
			}
		}

		// generate crewmember
		// if in the same position as bot, redo
		crewmember = new crewmember(board);
		while (x == crewmember.x && y == crewmember.y) {
			crewmember.generateCrewmember();
		}

		// TODO
		// initialize crewmember probabilities

	}

	// checks if bot position is crewmember position
	boolean isDestination() {
		return board.board[x][y] == board.board[crewmember.x][crewmember.y];
	}

	// BFS to return the shortest path
	// ignore is for the situation where aliens are blocking by the path (per the TA
	// in zulip)
	Stack<cell> findPath(boolean ignore) {
		// create fringes
		Queue<cell> queue = new LinkedList<cell>();
		ArrayList<cell> visited = new ArrayList<cell>();

		// add our current cell to the fringe
		queue.add(board.board[x][y]);
		while (!queue.isEmpty()) {
			// check if we are at the crewmate
			cell curr = queue.poll();
			if ((curr.x == crewmember.x) && curr.y == crewmember.y) {
				return getPath();
			}

			// add neighbors to fringe if they are valid and not already visited
			if ((!queue.contains(curr.up)) && (curr.up != null) && (!visited.contains(curr.up))
					&& (curr.up.isValid(ignore))) {
				queue.add(curr.up);

				if (debugpath == 1) {
					System.out.println("adding" + curr.up.x + " " + curr.up.y);
				}
				curr.up.parent = curr;
			}
			if ((!queue.contains(curr.down)) && (curr.down != null) && (!visited.contains(curr.down))
					&& (curr.down.isValid(ignore))) {
				queue.add(curr.down);
				if (debugpath == 1) {
					System.out.println("adding" + curr.down.x + " " + curr.down.y);
				}
				curr.down.parent = curr;
			}
			if ((!queue.contains(curr.left)) && (curr.left != null) && (!visited.contains(curr.left))
					&& (curr.left.isValid(ignore))) {
				queue.add(curr.left);
				if (debugpath == 1) {
					System.out.println("adding" + curr.left.x + " " + curr.left.y);
				}
				curr.left.parent = curr;
			}
			if ((!queue.contains(curr.right)) && (curr.right != null) && (!visited.contains(curr.right))
					&& curr.right.isValid(ignore)) {
				queue.add(curr.right);
				if (debugpath == 1) {
					System.out.println("adding" + curr.right.x + " " + curr.right.y);
				}
				curr.right.parent = curr;
			}
			// add current node to the visited fringe
			visited.add(curr);

		}

		// we will only reach here if the aliens are blocking the path, thus run the
		// algorithm again, this time ignoring the aliens
		return findPath(true);
	}

	// trace back parent pointers to return the shortest path as a stack
	Stack<cell> getPath() {
		Stack<cell> path = new Stack<>();

		// get current coordinates
		int currx = crewmember.x;
		int curry = crewmember.y;

		// get parent of current node
		cell next = board.board[crewmember.x][crewmember.y].parent;

		do {
			// add parent to stack
			path.push(board.board[currx][curry]);
			// get next parent
			next = board.board[currx][curry].parent;
			currx = next.x;
			curry = next.y;
			// set parent to null so we don't run into problems the next runs

		} while (board.board[currx][curry].parent != null);

		return path;
	}

	// every step the bot takes, we want a new path
	// so the parents must be undone
	void wipeParents() {
		for (cell[] cellr : board.board) {
			for (cell cell : cellr) {
				cell.parent = null;
			}
		}
	}

	// finds number of cells in the alien scan radius
	// might not be the entire (2k+1)*(2k+1) area because the bot might be by an edge
	double scanRadiusBlocks() {
		// establishes entire scanner area k up, down, left, right
		int i_start = x;
		for (int l = 0; l < k; l++) {
			if (i_start <= 0) {break;}
			i_start--;
		}
		int i_end = x;
		for (int l = 0; l < k; l++) {
			if (i_end >= board.board.length - 1) {break;}
			i_end++;
		}

		int j_start = y;
		for (int l = 0; l < k; l++) {
			if (j_start <= 0) {break;}
			j_start--;
		}
		int j_end = y;
		for (int l = 0; l < k; l++) {
			if (j_end >= board.board.length - 1) {break;}
			j_end++;
		}

		// counts number of cells
		double count = 0;
		System.out.println("I start: " + i_start + " end: " + i_end + " J start: " + j_start + " end: " + j_end);
		for (int i = i_start; i <= i_end; i++) {
			for (int j = j_start; j <= j_end; j++) {
				if (board.board[i][j].state) {count++;}
			}
		}

		return count;
	}

	// alien scanner version 1
	// checks if given coordinate falls within alien scanner range
	boolean alienScanCoord(int i, int j) {
		if ((x - k) <= i && i <= (x + k)) {
			if (((y - k) <= j) && (j <= (y + k))) {
				return true;
			}
		}
		return false;
	}

	// alien scanner version 2
	// checks entire scanner area for alien
	boolean alienScan() {
		// establishes entire scanner area k up, down, left, right
		int i_start = x;
		for (int l = 0; l < k; l++) {
			if (i_start <= 0) {break;}
			i_start--;
		}
		int i_end = x;
		for (int l = 0; l < k; l++) {
			if (i_end >= board.board.length - 1) {break;}
			i_end++;
		}

		int j_start = y;
		for (int l = 0; l < k; l++) {
			if (j_start <= 0) {break;}
			j_start--;
		}
		int j_end = y;
		for (int l = 0; l < k; l++) {
			if (j_end >= board.board.length - 1) {break;}
			j_end++;
		}

		// checks within these bounds for alien
		for (int i = i_start; i <= i_end; i++) {
			for (int j = j_start; j <= j_end; j++) {
				if (i == alien.x && j == alien.y) {return true;}
			}
		}
		return false;
	}

	// calculate alien probabilities when the bot moves
	void botMoveAlienProbability() {
		if (debug == 1) {
			System.out.println("BOT MOVE");
		}
		
		double beta = 0.0; // to calculate normalization constant
		
		// scanner goes off
		if (alienScan()) {
			ArrayList<cell> cells = new ArrayList<cell>(); // to contain cells whose probability we need to update later
			double prob_square_total = 0.0;
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					// alien must be in this area && open
					if (alienScanCoord(i, j) && curr.state) {
						cells.add(curr);
						prob_square_total += curr.palien1;
					} else { // everything else is 0
						curr.palien1 = 0;
					}
				}
			}
			
			for (cell curr:cells) {
				/*
				System.out.println("cells list");
				System.out.println("cell coords: " + curr.x + ", " + curr.y);
				System.out.println("probability before division " + curr.palien1 + " " + prob_square_total);
				*/
				curr.palien1 *= 1.0/prob_square_total;
				// System.out.println("probability after division:" + curr.palien1);
				beta += curr.palien1;
			}


		// scanner does not go off
		} else {
			// the new cells we just moved into now have a probability of 0
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					if (alienScanCoord(i, j)) {
						curr.palien1 = 0;
					}
					System.out.println(curr.palien1);
					beta += curr.palien1;
				}
			}
		}

		// normalize
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];
				curr.palien1 = (1.0 / beta) * curr.palien1;
			}
		}

	}

	// calculate alien probabilities when aliens move
	void alienMoveAlienProbability() {
		if (debug == 1) {
			System.out.println("ALIEN MOVE");
		}
		
		// copying old probabilities for reference
		double[][] probs = new double[board.board.length][board.board.length];
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				probs[i][j] = board.board[i][j].palien1;
			}
		}

		double beta = 0.0; // to calculate normalization constant

		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];

				// bot position has p = 0
				if (x == i & y == j) {
					curr.palien1 = 0;

					// if we scanned an alien but are outside of radar zone
				} else if (alienScan() && !alienScanCoord(i, j)) {
					curr.palien1 = 0;

					// no alien scan
					// OR alien scan within radar zone
					// for each neighbor, probability that alien was in that cell * probability alien moved into current cell
				} else if (curr.state){
					curr.palien1 = 0;
					cell n = curr.up;
					if (n != null && n.state && n.neighbor_ct !=0) {
						curr.palien1 += probs[i - 1][j] * (1.0/n.neighbor_ct);
					}
					n = curr.down;
					if (n != null && n.state && n.neighbor_ct !=0) {
						curr.palien1 += probs[i + 1][j] * (1.0/n.neighbor_ct);
					}
					n = curr.left;
					if (n != null && n.state && n.neighbor_ct !=0) {
						curr.palien1 += probs[i][j - 1] * (1.0/n.neighbor_ct);
					}
					n = curr.right;
					if (n != null && n.state && n.neighbor_ct !=0) {
						curr.palien1 += probs[i][j + 1] * (1.0/n.neighbor_ct);
					}

				}
				beta += curr.palien1;
			}
		}


		// normalize
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];
				curr.palien1 = (1.0 / beta) * curr.palien1;
			}
		}

	}

	// sets off crewmember detection beep
	boolean beep() {

		return false;
	}

	// calculate crewmember probabilities
	void probabilityCrewmate() {
		// if the bot gets a beep
		if (beep()) {
			board.board[x][y].pcrew1 = 0;
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					//TODO add distance as a dictionary!?
					board.board[i][j].pcrew1*= Math.pow(Math.E, -alpha -1);
				}
			}

			// if the bot does not get a beep
		} else {
			board.board[x][y].pcrew1 = 0;
			double beta = 0;
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					beta += board.board[i][j].pcrew1;
				}
			}

			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					curr.palien1 = (1.0 / beta) * curr.pcrew1;
				}
			}

		}

	}


	// run the bot
	int[] run() {
		int[] ret = new int[2];
		int saved = 0; // # of crewmembers saved
		int step = 0; // # of steps taken

		// go for 1000 steps
		while (step < 1000) {

			if (debug == 1) {
				printBoard();
				System.out.println();
			}

			// get path
			// if no path, return
			Stack<cell> path = findPath(false);
			if (path == null) {
				System.out.println("Path could not be found");
				ret[0] = saved;
				ret[1] = step;
				return ret;
			}

			// look at next move ONLY
			cell curr = path.pop();
			// advance bot
			x = curr.x;
			y = curr.y;
			step++;

			// update alien probabilities
			botMoveAlienProbability();
			
			if (debug==1) {
				printBoard();
				System.out.println();
			}

			// alien check
			// if caught by alien, return
			if (curr.alien == true) {
				ret[0] = saved;
				ret[1] = step;
				return ret;
			}

			// crewmember check
			// saved crewmember, generate another
			// cannot be where the bot is
			if (isDestination()) {
				saved++;
				crewmember.generateCrewmember();
				while (x == crewmember.x && y == crewmember.y) {
					crewmember.generateCrewmember();
				}
			}

			// move aliens
			alien.move();

			alienMoveAlienProbability();

			// alien check
			if (board.getCell(x, y).alien == true) {
				ret[0] = saved;
				ret[1] = step;
				return ret;
			}

			// wipe parent pointers in preparation for a new path
			// must do this every time because we recalculate path every step
			wipeParents();

		}

		// completed 1000 steps without dying
		ret[0] = saved;
		ret[1] = step;
		return ret;
	}

	// utility function
	// print positions of aliens, bot, crewmembers, and open/closed cells
	void printBoard() {
		DecimalFormat df = new DecimalFormat("0.000");
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];

				if (curr.state == false) {
					System.out.print("[XXX, " + df.format(curr.palien1) + "]  ");
					continue;
				}

				if (curr.alien == false) {
					if ((i == x && j == y) && (i == crewmember.x && j == crewmember.y)) {
						System.out.print("[_BC, " + df.format(curr.palien1) + "]  ");
						continue;
					}
					if (i == x && j == y) {
						System.out.print("[_B_, " + df.format(curr.palien1) + "]  ");
						continue;
					}
					if (i == crewmember.x && j == crewmember.y) {
						System.out.print("[__C, " + df.format(curr.palien1) + "]  ");
						continue;
					}
					System.out.print("[OOO, " + df.format(curr.palien1) + "]  ");
					continue;

				} else {
					if ((i == x && j == y) && i == crewmember.x && j == crewmember.y) {
						System.out.print("[ABC, " + df.format(curr.palien1) + "]  ");
						continue;
					}
					if (i == x && j == y) {
						System.out.print("[AB_, " + df.format(curr.palien1) + "]  ");
						continue;
					}
					if (i == crewmember.x && j == crewmember.y) {
						System.out.print("[A_C, " + df.format(curr.palien1) + "]  ");
						continue;
					}
					System.out.print("[A__, " + df.format(curr.palien1) + "]  ");
					continue;
				}
			}
			System.out.println("\n");
		}
	}
}
