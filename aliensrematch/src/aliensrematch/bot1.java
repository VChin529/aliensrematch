package aliensrematch;

import java.util.*;
import java.text.DecimalFormat;

public class bot1 {
	int x, y; // coordinates
	int k; // dimension of alien scanner radius
	double alpha; // sensitivity of crewmember scanner
	board board; // board that the bot is on
	alien alien; // array of aliens
	crewmember crewmember; // crewmember to save
	int debug = 1; // utility for debugging. ignore.
	int debugpath = 0; // utility for debugging. ignore.
	int ct = 0;

	public bot1(int k, double alpha) {
		// initialize k and alpha values
		this.k = k;
		this.alpha = alpha;

		// generate board dimension 50x50
		board = new board(10);

		// random placement of bot
		cell curr = board.randomCell();
		this.x = curr.x;
		this.y = curr.y;


		// generate 1 alien not within bot scanner range
		alien = new alien(board);
		while (alienScanCoord(alien.x, alien.y)) {
			board.board[alien.x][alien.y].alien = false;
			alien = new alien(board);
		}

		// initialize alien probabilities
		double scanSize = scanRadiusBlocks(); // keeps track of how many open cells are in alien scan zone
		System.out.println("Bot is at: x: " + curr.x + " y: " + curr.y + " size:" + scanSize);
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell c = board.board[i][j];
				if (!c.state) { // closed cell
					c.palien1 = 0;
				} else if (alienScanCoord(i, j)) { // in alien scan radar
					c.palien1 = 0;
				} else { // equally distribute probability amongst other cells
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

		// initialize crew probabilities
		initCrewProbs();
	}



	// checks if bot position is crewmember position
	boolean isDestination() {
		return board.board[x][y] == board.board[crewmember.x][crewmember.y];
	}



	// uses dijkstra implementation in board
	// chooses the next move that brings it closer to the crewmember
	// while avoiding alien
	// BFS
	Stack<cell> findPath() {
		// create fringes
		Stack<cell> path = new Stack<>();

		// find the cell we want to go to
		// highest crewmate probability
		cell dest = findMaxCrew();

		cell curr = board.board[x][y];
		String key = createKey(x, y, dest.x, dest.y);

		// if we are at the position we want to go to, return our position
		if (board.dict.get(key) == 0) {
			path.push(curr);
			return path;
		}


		if (debug == 1) {
			System.out.println("Were pathing to: x" + dest.x+ " y:" + dest.y+ " With probability: "+ dest.pcrew1);
		}


		cell ret=curr;
		// collect all cells we can possibly move to
		// our unblocked neighbors without alien probability = 0
		// if no cells meet these conditions, we return ourself, so we stay in place
		ArrayList<cell> possCells = new ArrayList<>();
		if (curr.up!=null && curr.up.state && curr.up.palien1 == 0) {
			possCells.add(curr.up);
			if (debug == 1) {
				System.out.println("adding up");
			}
		}
		if (curr.down!=null && curr.down.state && curr.down.palien1 == 0) {
			possCells.add(curr.down);
			if (debug == 1) {
				System.out.println("adding down");
			}
		}
		if (curr.left!=null && curr.left.state && curr.left.palien1 == 0) {
			possCells.add(curr.left);
			if (debug == 1) {
				System.out.println("adding left");
			}
		}
		if (curr.right!=null && curr.right.state && curr.right.palien1 == 0) {
			possCells.add(curr.right);
			if (debug == 1) {
				System.out.println("adding right");
			}
		}


		// iterate through all possible cells
		// find the one with the shortest distance
		int minDistance = Integer.MAX_VALUE;
		for(int i=0; i<possCells.size(); i++) {
			key = createKey(possCells.get(i).x,possCells.get(i).y, dest.x, dest.y);
			if (minDistance > board.dict.get(key)) {
				if (debug == 1) {
					System.out.println(possCells.get(i).x+" "+possCells.get(i).y+ " is shortest");
				}
				ret = possCells.get(i);
				minDistance = board.dict.get(key);
			}
		}

		if (ret == curr) {
			ct++;
		}

		path.push(ret);
		return path;

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



	// finds number of cells in the alien scan radius
	// might not be the entire (2k+1)*(2k+1) area because the bot might be by an
	// edge
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

		// counts number of cells
		double count = 0;
		System.out.println("I start: " + i_start + " end: " + i_end + " J start: " + j_start + " end: " + j_end);
		for (int i = i_start; i <= i_end; i++) {
			for (int j = j_start; j <= j_end; j++) {
				if (board.board[i][j].state) {
					count++;
				}
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

		// checks within these bounds for alien
		for (int i = i_start; i <= i_end; i++) {
			for (int j = j_start; j <= j_end; j++) {
				if (i == alien.x && j == alien.y) {
					return true;
				}
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

			for (cell curr : cells) {
				/*
				 * System.out.println("cells list"); System.out.println("cell coords: " + curr.x
				 * + ", " + curr.y); System.out.println("probability before division " +
				 * curr.palien1 + " " + prob_square_total);
				 */
				curr.palien1 *= 1.0 / prob_square_total;
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

		if (alienScan()) {
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];

					if ((x==i && y==j) || !alienScanCoord(i,j)) {
						curr.palien1 = 0;
					} else if (curr.state){
						curr.palien1 = 0;
						cell n = curr.up;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien1 += probs[i - 1][j] * (1.0 / n.neighbor_ct);
						}
						n = curr.down;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien1 += probs[i + 1][j] * (1.0 / n.neighbor_ct);
						}
						n = curr.left;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien1 += probs[i][j - 1] * (1.0 / n.neighbor_ct);
						}
						n = curr.right;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien1 += probs[i][j + 1] * (1.0 / n.neighbor_ct);
						}
					}
					beta += curr.palien1;
				}
			}

		} else {
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];

					if ((x==i && y==j) || alienScanCoord(i,j)) {
						curr.palien1 = 0;
					} else if (curr.state){
						curr.palien1 = 0;
						cell n = curr.up;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien1 += probs[i - 1][j] * (1.0 / n.neighbor_ct);
						}
						n = curr.down;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien1 += probs[i + 1][j] * (1.0 / n.neighbor_ct);
						}
						n = curr.left;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien1 += probs[i][j - 1] * (1.0 / n.neighbor_ct);
						}
						n = curr.right;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien1 += probs[i][j + 1] * (1.0 / n.neighbor_ct);
						}
					}
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



	// set initial crewmember probabilities
	void initCrewProbs() {
		ArrayList<cell> cells = new ArrayList<cell>(); // to contain cells whose probability we need to update later

		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];

				// if closed cell
				if (!curr.state) {
					curr.pcrew1 = 0;

					// if bot position
				} else if (i == x && j == y) {
					curr.pcrew1 = 0;

					// open cell not in bot position
					// this is a valid cell. collect these
				} else {
					cells.add(curr);
				}
			}
		}

		// divide probability equally amongst valid cells
		for (cell curr : cells) {
			curr.pcrew1 = 1.0 / cells.size();
		}
	}



	// sets off crewmember detection beep
	boolean beep() {
		// getting distance from our position to the crewmember
		String current = createKey(x, y, crewmember.x, crewmember.y);
		int d = board.dict.get(current);

		// calculate probability of a beep
		double prob = Math.pow(Math.E, (-alpha * (d - 1)));

		// return if random number is within this probability
		double rand = (double) Math.random();
		return (rand <= prob);
	}



	// calculate crewmember probabilities
	void crewmateProbability() {
		// if we find the crewmember
		if (crewmember.x == x && crewmember.y == y) {
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					if (crewmember.x == i && crewmember.y == j) {
						board.board[i][j].pcrew1 = 1;
					} else {
						board.board[i][j].pcrew1 = 0;
					}
				}
			}
			return;
		}


		// if the bot gets a beep
		if (beep()) {
			if (debug == 1) {
				System.out.println("BEEP!");
			}

			// set current bot position probability to 0
			board.board[x][y].pcrew1 = 0;

			double beta = 0;
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					if (curr.state) {
						// find the distance from us to the cell
						// probability that the beep went off if the crewmember was in that cell
						String current = createKey(x, y, i, j);
						int d = board.dict.get(current);

						// multiply probability of crewmember in cell * probability of beep | crewmember
						curr.pcrew1 *= Math.pow(Math.E, -alpha * (d - 1));
						beta += curr.pcrew1;
					}

				}
			}

			// normalize
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					curr.pcrew1 = (1.0 / beta) * curr.pcrew1;
				}
			}



			// if the bot does not get a beep
		} else {
			// set current bot position probability to 0
			board.board[x][y].pcrew1 = 0;

			// add up all probabilities and normalize
			double beta = 0;
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					beta += board.board[i][j].pcrew1;
				}
			}

			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					curr.pcrew1 = (1.0 / beta) * curr.pcrew1;
				}
			}

		}

	}



	// finds cell with highest crewmate probability
	// this is the cell that we want to move to
	cell findMaxCrew() {
		// collect all cells with max probability
		ArrayList<cell> max = new ArrayList<>();
		max.add(board.board[0][0]);

		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];
				// if we find a cell that has a higher probability than the ones we are currently saving
				// remove those old cells and add this one
				if (max.get(0).pcrew1 < curr.pcrew1) {
					max.removeAll(max);
					max.add(curr);
					// if we find a cell that has the same probability as the ones we are currently saving
					// ad this one
				} else if (max.get(0).pcrew1 == curr.pcrew1) {
					max.add(curr);
				}
				// nothing if this cell has a lower probability
			}
		}

		// return a random cell from our list
		int pos = (int) (Math.random() * max.size());
		return max.get(pos);
	}



	// utility function to create string key for our dictionary
	// it is in the format 1234, with srcx=1, srcy=2, destx=3, desty=4
	String createKey(int x1, int y1, int x2, int y2) {
		return Integer.toString(x1) + Integer.toString(y1) + Integer.toString(x2) + Integer.toString(y2);
	}



	// run the bot
	int[] run() {
		int[] ret = new int[2];
		int step = 0; // # of steps taken


		// keep looping
		// we will break manually once we find the crewmember
		while(true){

			if (debug == 1) {
				printBoard();
				System.out.println();
			}


			// get path
			// if no path, return
			Stack<cell> path = findPath();
			if (path == null) {
				System.out.println("Path could not be found");
				ret[0]=0;
				ret[1]=step;
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

			// update crewmember probabilities
			crewmateProbability();

			if (debug == 1) {
				printBoard();
				System.out.println();
			}

			// alien check
			// if caught by alien, return
			if (curr.alien == true) {
				ret[0]=0;
				ret[1]=step;
				return ret;

			}

			// crewmember check
			// saved crewmember, generate another
			// cannot be where the bot is
			if (isDestination()) {
				ret[0]=1;
				ret[1]=step;
				return ret;
			}

			// move aliens
			alien.move();

			alienMoveAlienProbability();

			// alien check
			if (board.getCell(x, y).alien == true) {
				ret[0]=0;
				ret[1]=step;
				return ret;
			}

		}

	}



	// utility function
	// print positions of aliens, bot, crewmember, open/closed cells, and probabilities
	void printBoard() {
		DecimalFormat df = new DecimalFormat("0.000");

		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];

				if (curr.state == false) {
					System.out.print("[XXX, " + df.format(curr.palien1) + ", " + df.format(curr.pcrew1) + "]  ");
					continue;
				}

				if (curr.alien == false) {
					if ((i == x && j == y) && (i == crewmember.x && j == crewmember.y)) {
						System.out.print("[_BC, " + df.format(curr.palien1) + ", " + df.format(curr.pcrew1) + "]  ");
						continue;
					}
					if (i == x && j == y) {
						System.out.print("[_B_, " + df.format(curr.palien1) + ", " + df.format(curr.pcrew1) + "]  ");
						continue;
					}
					if (i == crewmember.x && j == crewmember.y) {
						System.out.print("[__C, " + df.format(curr.palien1) + ", " + df.format(curr.pcrew1) + "]  ");
						continue;
					}
					System.out.print("[OOO, " + df.format(curr.palien1) + ", " + df.format(curr.pcrew1) + "]  ");
					continue;

				} else {
					if ((i == x && j == y) && i == crewmember.x && j == crewmember.y) {
						System.out.print("[ABC, " + df.format(curr.palien1) + ", " + df.format(curr.pcrew1) + "]  ");
						continue;
					}
					if (i == x && j == y) {
						System.out.print("[AB_, " + df.format(curr.palien1) + ", " + df.format(curr.pcrew1) + "]  ");
						continue;
					}
					if (i == crewmember.x && j == crewmember.y) {
						System.out.print("[A_C, " + df.format(curr.palien1) + ", " + df.format(curr.pcrew1) + "]  ");
						continue;
					}
					System.out.print("[A__, " + df.format(curr.palien1) + ", " + df.format(curr.pcrew1) + "]  ");
					continue;
				}
			}
			System.out.println("\n");
		}
	}
}
