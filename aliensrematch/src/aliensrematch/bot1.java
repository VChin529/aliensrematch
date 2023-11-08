package aliensrematch;

import java.util.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class bot1 {
	int x, y, k; // coordinates
	board board; // board that the bot is on
	alien alien; // array of aliens
	crewmember crewmember; // crewmember to save
	int debug = 1; // utility for debugging. ignore.
	int debugq = 0;
	int alpha;

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
			alien = new alien(board);
		}
		double scanSize = scanRadiusBlocks();
		System.out.println("Bot is at: x: " + curr.x + " y: " + curr.y + " size:" + scanSize);
		// initialize alien probabilities
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (alienScanCoord(i, j)) {
					board.board[i][j].palien1 = 0;
					board.board[i][j].pcrew1 = 0;
				} else {
					board.board[i][j].pcrew1 = (1.0 / ((board.d * board.d) - scanSize));
					board.board[i][j].palien1 = (1.0 / ((board.d * board.d) - scanSize));
				}
			}
		}

		// initialize crewmember
		// if in the same position as bot, redo
		crewmember = new crewmember(board);
		while (x == crewmember.x && y == crewmember.y) {
			crewmember.generateCrewmember();
		}

		System.out.println("Init board:");
		printBoardP(1);
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

				if (debugq == 1) {
					System.out.println("adding" + curr.up.x + " " + curr.up.y);
				}
				curr.up.parent = curr;
			}
			if ((!queue.contains(curr.down)) && (curr.down != null) && (!visited.contains(curr.down))
					&& (curr.down.isValid(ignore))) {
				queue.add(curr.down);
				if (debugq == 1) {
					System.out.println("adding" + curr.down.x + " " + curr.down.y);
				}
				curr.down.parent = curr;
			}
			if ((!queue.contains(curr.left)) && (curr.left != null) && (!visited.contains(curr.left))
					&& (curr.left.isValid(ignore))) {
				queue.add(curr.left);
				if (debugq == 1) {
					System.out.println("adding" + curr.left.x + " " + curr.left.y);
				}
				curr.left.parent = curr;
			}
			if ((!queue.contains(curr.right)) && (curr.right != null) && (!visited.contains(curr.right))
					&& curr.right.isValid(ignore)) {
				queue.add(curr.right);
				if (debugq == 1) {
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

	// alien scanner version 1
	// checks if given coordinate falls within alien scanner range
	boolean alienScanCoord(int i, int j) {
		if ((x - k) <= i && i <= (x + k)) {
			if (((y - k) <= j && j <= (y + k))) {
				return true;
			}
		}
		return false;
	}

	// alien scanner version 2
	// checks entire scanner area
	boolean alienScan() {
		// establishes entire scanner area k up, down, left, right
		int i_start = x;
		for (int l = 0; l < k; l++) {
			i_start--;
			if (i_start <= 0) {
				break;
			}

		}

		int i_end = x;
		for (int l = 0; l < k; l++) {
			i_end++;
			if (i_end >= board.board.length - 1) {
				break;
			}

		}

		int j_start = y;
		for (int l = 0; l < k; l++) {
			j_start--;
			if (j_start <= 0) {
				break;
			}
		}

		int j_end = y;
		for (int l = 0; l < k; l++) {
			j_end++;
			if (j_end >= board.board.length - 1) {
				break;
			}
		}

		// checks bounds for alien
		for (int i = i_start; i < i_end; i++) {
			for (int j = j_start; j < j_end; j++) {
				if (i == alien.x && j == alien.y) {
					return true;
				}
			}
		}

		return false;
	}
	// finds number of blocks in the robots scan radius

	double scanRadiusBlocks() {
		// establishes entire scanner area k up, down, left, right
		int i_start = x;
		for (int l = 0; l < k; l++) {
			if (i_start <= 0) {
				break;
			}
			i_start--;

		}

		int i_end = x;
		for (int l = 0; l < k; l++) {

			if (i_end >= board.board.length - 1) {
				break;
			}
			i_end++;

		}

		int j_start = y;
		for (int l = 0; l < k; l++) {
			if (j_start <= 0) {
				break;
			}
			j_start--;

		}

		int j_end = y;
		for (int l = 0; l < k; l++) {
			if (j_end >= board.board.length - 1) {
				break;
			}
			j_end++;

		}

		double count = 0;
		// checks bounds for alien
		System.out.println("I start: " + i_start + " end: " + i_end + " J start: " + j_start + " end: " + j_end);
		for (int i = i_start; i <= i_end; i++) {
			for (int j = j_start; j <= j_end; j++) {
				count++;
			}

		}

		return count;
	}

	void botMoveProbability() {
		// detection goes off

		if (alienScan()) {

			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					if (alienScanCoord(i, j) && curr.palien1 != 0) {
						curr.palien1 = 1.0 / (2.0 * k + 1.0);
					} else {

					}
				}
			}
			// detection does not go off
		} else {
			double beta = 0;
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					if (alienScanCoord(i, j) && curr.palien1 != 0) {
						curr.palien1 = 0;
					}
					beta += curr.palien1;
				}

			}
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					curr.palien1 = (1.0 / beta) * curr.palien1;
				}
			}
		}

	}

	boolean beep() {

		return false;
	}

	void probabilityCrewmate() {
		if (beep()) {
			board.board[x][y].pcrew1 = 0;
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					//TODO add distance as a dictionary!?
					board.board[i][j].pcrew1*= Math.pow(Math.E, -alpha -1);
				}
			}
		} else {
			double beta = 0;
			board.board[x][y].pcrew1 = 0;
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

	// calculate alien probabilities
	void alienMoveProbability() {
		// copying old probabilities for reference
		double[][] probs = new double[board.board.length][board.board.length];
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				probs[i][j] = board.board[i][j].palien1;
			}
		}

		double beta = 0; // to calculate normalization constant

		// walk the board
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];

				// bot position is p = 0
				if (x == i & y == j) {
					curr.palien1 = 0;
					// if we scanned an alien but are outside of radar zone
				} else if (alienScan() && !alienScanCoord(i, j)) {
					curr.palien1 = 0;
					// probability calculation
					// no alien scan
					// OR alien scan within radar zone
				} else {
					curr.palien1 = 0;
					cell n = curr.up;
					if (n != null && n.state) {
						curr.palien1 += probs[i - 1][j] * n.neighbor_ct;
					}
					n = curr.down;
					if (n != null && n.state) {
						curr.palien1 += probs[i + 1][j] * n.neighbor_ct;
					}
					n = curr.left;
					if (n != null && n.state) {
						curr.palien1 += probs[i][j - 1] * n.neighbor_ct;
					}
					n = curr.right;
					if (n != null && n.state) {
						curr.palien1 += probs[i][j + 1] * n.neighbor_ct;
					}

				}

				beta += curr.palien1;
			}
		}

		System.out.println(beta);
		// normalize

		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];
				curr.palien1 = (1.0 / beta) * curr.palien1;
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
				printBoardP(1);
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
			botMoveProbability();
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

			alienMoveProbability();

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
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];

				if (curr.state == false) {
					System.out.print("XXX ");
					continue;
				}

				if (curr.alien == false) {
					if ((i == x && j == y) && (i == crewmember.x && j == crewmember.y)) {
						System.out.print("_BC ");
						continue;
					}
					if (i == x && j == y) {
						System.out.print("_B_ ");
						continue;
					}
					if (i == crewmember.x && j == crewmember.y) {
						System.out.print("__C ");
						continue;
					}
					System.out.print("OOO ");
					continue;

				} else {
					if ((i == x && j == y) && i == crewmember.x && j == crewmember.y) {
						System.out.print("ABC ");
						continue;
					}
					if (i == x && j == y) {
						System.out.print("AB_ ");
						continue;
					}
					if (i == crewmember.x && j == crewmember.y) {
						System.out.print("A_C ");
						continue;
					}
					System.out.print("A__ ");
					continue;
				}
			}
			System.out.println("\n");
		}
	}

	// utility function
	// print probabilities
	// 1 = alien, 2 = crewmember, 3 = both
	void printBoardP(int n) {
		DecimalFormat df = new DecimalFormat("0.0000");
		System.out.println("Alien Pos: x:" + alien.x + " y:" + alien.y);
		System.out.println("Bot Pos: x:" + this.x + " y:" + this.y);
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];
				if (n == 1) {
					System.out.print(df.format(curr.palien1) + " ");
				} else if (n == 2) {
					System.out.print(df.format(curr.pcrew1) + " ");
				} else {
					System.out.print(df.format(curr.palien1) + " " + df.format(curr.pcrew1) + "  ");
				}
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}

	void printBoardAS() {
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				if (x == i & j == y) {
					System.out.println("B ");
				} else if (alienScanCoord(i, j)) {
					System.out.println("X ");
				} else {
					System.out.println("O ");
				}
			}
		}
		System.out.println();
		System.out.println();
	}

}
