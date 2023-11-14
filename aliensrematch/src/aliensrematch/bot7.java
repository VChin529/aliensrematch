package aliensrematch;

import java.util.*;
import java.text.DecimalFormat;

// 2 crewmembers 2 aliens
// BANDAID FIX RN. REDO
public class bot7 {
	int x, y; // coordinates
	int k; // dimension of alien scanner radius
	double alpha; // sensitivity of crewmember scanner
	board board; // board that the bot is on
	alien alien1, alien2; // array of aliens
	crewmember crewmember1, crewmember2; // crewmember to save
	cell dest; // cell that we are moving towards. Highest crewmate probability
	int debug = 0; // utility for debugging. ignore.
	int debugpath = 0; // utility for debugging. ignore.
	boolean ct = false;

	public bot7(int k, double alpha) {
		// initialize k and alpha values
		this.k = k;
		this.alpha = alpha;

		// generate board dimension 50x50
		board = new board(50);

		// random placement of bot
		cell curr = board.randomCell();
		this.x = curr.x;
		this.y = curr.y;


		// generate 2 aliens not within bot scanner range
		alien1 = new alien(board);
		while (alienScanCoord(alien1.x, alien1.y)) {
			board.board[alien1.x][alien1.y].alien = false;
			alien1 = new alien(board);
		}
		
		alien2 = new alien(board);
		while ((alien2.x == alien1.x && alien2.y == alien1.y) || (alienScanCoord(alien2.x, alien2.y))) {
			board.board[alien2.x][alien2.y].alien = false;
			alien2 = new alien(board);
		}

		// initialize alien probabilities
		double scanSize = scanRadiusBlocks(); // keeps track of how many open cells are in alien scan zone
		if (debug == 1) {
			System.out.println("Bot is at: x: " + curr.x + " y: " + curr.y + " size:" + scanSize);
		}
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell c = board.board[i][j];
				if (!c.state) { // closed cell
					c.palien = 0;
				} else if (alienScanCoord(i, j)) { // in alien scan radar
					c.palien = 0;
				} else { // equally distribute probability amongst other cells
					c.palien = (1.0 / ((board.open) - scanSize));
				}
			}
		}


		// generate crewmembers
		// if in the same position as bot, redo
		crewmember1 = new crewmember(board);
		while (x == crewmember1.x && y == crewmember1.y) {
			crewmember1.generateCrewmember();
		}

		crewmember2 = new crewmember(board);
		while ((x == crewmember2.x && y == crewmember2.y) || 
				(crewmember1.x == crewmember2.x && crewmember1.y == crewmember2.y)) {
			crewmember2.generateCrewmember();
		}

		// initialize crew probabilities
		initCrewProbs();


		// set destination cell to a random position on the board
		dest = board.randomCell();
		while (dest.x == x && dest.y == y) {
			dest = board.randomCell();
		}
	}



	// checks if bot position is crewmember position
	boolean isDestination() {
		return (crewmember1!= null && board.board[x][y] == board.board[crewmember1.x][crewmember1.y])
				|| (crewmember2 != null && board.board[x][y] == board.board[crewmember2.x][crewmember2.y]);
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
		dest = findMaxCrew();

		cell curr = board.board[x][y];
		String key = createKey(x, y, dest.x, dest.y);

		// if we are at the position we want to go to, return our position
		if (board.dict.get(key) == 0) {
			path.push(curr);
			return path;
		}


		if (debug == 1) {
			System.out.println("Were pathing to: x" + dest.x+ " y:" + dest.y+ " With probability: "+ dest.pcrew);
		}


		cell ret=curr;
		// collect all cells we can possibly move to
		// our unblocked neighbors without alien probability = 0
		// if no cells meet these conditions, we return ourself, so we stay in place
		ArrayList<cell> possCells = new ArrayList<>();
		if (curr.up!=null && curr.up.state && curr.up.palien == 0) {
			possCells.add(curr.up);
		}
		if (curr.down!=null && curr.down.state && curr.down.palien == 0) {
			possCells.add(curr.down);
		}
		if (curr.left!=null && curr.left.state && curr.left.palien == 0) {
			possCells.add(curr.left);
		}
		if (curr.right!=null && curr.right.state && curr.right.palien == 0) {
			possCells.add(curr.right);
		}


		// iterate through all possible cells
		// find the one with the shortest distance
		int minDistance = Integer.MAX_VALUE;
		for(int i=0; i<possCells.size(); i++) {
			key = createKey(possCells.get(i).x,possCells.get(i).y, dest.x, dest.y);
			if (minDistance > board.dict.get(key)) {
				ret = possCells.get(i);
				minDistance = board.dict.get(key);
			}
		}

		if (debug == 1) {
			System.out.println(ret.x+" "+ret.y+ " is next step");
		}

		path.push(ret);
		return path;

	}



	// trace back parent pointers to return the shortest path as a stack
	Stack<cell> getPath() {
		Stack<cell> path = new Stack<>();

		// get current coordinates
		int currx = dest.x;
		int curry = dest.y;

		// get parent of current node
		cell next = board.board[dest.x][dest.y].parent;

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
		if (debug == 1) {
			System.out.println("I start: " + i_start + " end: " + i_end + " J start: " + j_start + " end: " + j_end);
		}
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
				if ((i == alien1.x && j == alien1.y) || (i == alien2.x && j == alien2.y)) {
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
			if (debug == 1) {
				System.out.println("SCANNER!");
			}
			
			double prob_square_total = 0.0;//the sum of the probabilities inside the detection square
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					// alien must be in this area && open
					if (alienScanCoord(i, j) && curr.state) {
						prob_square_total += curr.palien;
					} 
					
				}
			}
			
			
			if (prob_square_total == 0) {
				return;
			}
			

			for (cell[] currline : board.board) {
				for(cell curr: currline) {
				
				if(alienScanCoord(curr.x,curr.y)&&curr.state) {
					curr.palien *= 1.0 / prob_square_total;
				}else {
					curr.palien *= (1.0-(1.0 / prob_square_total));
				}
				
				beta += curr.palien;
				}
			}
			if (beta == 0) {
				return;
			}

			// scanner does not go off
		} else {
			// the new cells we just moved into now have a probability of 0
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					if (!alienScanCoord(i, j)) {
						beta += curr.palien;
					}
				}
			}
			
			if (beta == 0) {
				return;
			}
			
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					if (alienScanCoord(i, j)) {
						curr.palien = 0;
					}
				}
			}
		}

		// normalize
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];
				curr.palien = (1.0 / beta) * curr.palien;
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
				probs[i][j] = board.board[i][j].palien;
			}
		}

		double beta = 0.0; // to calculate normalization constant

		if (alienScan()) {
			if (debug == 1) {
				System.out.println("SCANNER!");
			}
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];

					if ((x==i && y==j)) {
						curr.palien = 0;
						
					} else if (curr.state){
						curr.palien = 0;
						cell n = curr.up;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien += probs[i - 1][j] * (1.0 / n.neighbor_ct);
						}
						n = curr.down;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien += probs[i + 1][j] * (1.0 / n.neighbor_ct);
						}
						n = curr.left;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien += probs[i][j - 1] * (1.0 / n.neighbor_ct);
						}
						n = curr.right;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien += probs[i][j + 1] * (1.0 / n.neighbor_ct);
						}
					}
					beta += curr.palien;
				}
			}

		} else {
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];

					if ((x==i && y==j)) {
						curr.palien = 0;
					} else if (curr.state){
						curr.palien = 0;
						cell n = curr.up;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien += probs[i - 1][j] * (1.0 / n.neighbor_ct);
						}
						n = curr.down;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien += probs[i + 1][j] * (1.0 / n.neighbor_ct);
						}
						n = curr.left;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien += probs[i][j - 1] * (1.0 / n.neighbor_ct);
						}
						n = curr.right;
						if (n != null && n.state && n.neighbor_ct != 0) {
							curr.palien += probs[i][j + 1] * (1.0 / n.neighbor_ct);
						}
					}
					beta += curr.palien;
				}
			}
		}


		// normalize
		if (beta == 0) {
			return;
		}
		
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];
				curr.palien = (1.0 / beta) * curr.palien;
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
					curr.pcrew = 0;

					// if bot position
				} else if (i == x && j == y) {
					curr.pcrew = 0;

					// open cell not in bot position
					// this is a valid cell. collect these
				} else {
					cells.add(curr);
				}
			}
		}

		// divide probability equally amongst valid cells
		for (cell curr : cells) {
			curr.pcrew = 1.0 / cells.size();
		}
	}



	// sets off crewmember detection beep
	boolean beep() {
		double prob = 0.0;
		
		// getting distance from our position to the crewmember
		if (crewmember1 != null && crewmember2 != null) {
			// find the probability of beep from crewmember1 && crewmember2
			String current1 = createKey(x, y, crewmember1.x, crewmember1.y);
			int d1 = board.dict.get(current1);
			double prob1 = Math.pow(Math.E, (-alpha * (d1 - 1)));
			
			String current2 = createKey(x, y, crewmember2.x, crewmember2.y);
			int d2 = board.dict.get(current2);
			double prob2 = Math.pow(Math.E, (-alpha * (d2 - 1)));
			
			// final beep probability is from one OR the other
			// 1 - and
			prob = 1 - (prob1 * prob2);
			
			
		} else if (crewmember1 != null){ //crewmember2 is null, we are looking for crew1
			String current = createKey(x, y, crewmember1.x, crewmember1.y);
			int d = board.dict.get(current);
			prob = Math.pow(Math.E, (-alpha * (d - 1)));
			
			
		} else { // crewmember1 is null, we are looking for crew2
			String current = createKey(x, y, crewmember2.x, crewmember2.y);
			int d = board.dict.get(current);
			prob = Math.pow(Math.E, (-alpha * (d - 1)));
		}
		
		// return if random number is within this probability
		double rand = (double) Math.random();
		return (rand <= prob);
	}



	// calculate crewmember probabilities
	void crewmateProbability() {
		// if we find the crewmember
		// there is now no crewmember in this cell, so p = 0
		// continue on with recalculating probabilities
		if (isDestination()) {
			board.board[x][y].pcrew = 0.0;
		}


		// if the bot gets a beep
		if (beep()) {
			if (debug == 1) {
				System.out.println("BEEP!");
			}

			// set current bot position probability to 0
			board.board[x][y].pcrew = 0;

			double beta = 0;
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					if (curr.state) {
						// find the distance from us to the cell
						// probability that the beep went off if the crewmember was in that cell
						String current = createKey(x, y, i, j);
						int d = board.dict.get(current);
						if(curr.pcrew==0) {
							curr.pcrew=0;
							
				
						
						
						// multiply probability of crewmember in cell * probability of beep | crewmember
						}else if (crewmember1 != null && crewmember2 != null&&d!=0) {
							// multiply by probability of both beeps
							String destKey = createKey(x, y, dest.x, dest.y);
							int dtoDest = board.dict.get(destKey);
							double beepProbDest = (1.0-Math.pow(Math.E, (-alpha * (d - 1))));
							
							double beepProb = Math.pow(Math.E, (-alpha * (d - 1)));
							curr.pcrew*= (1.0-beepProb)*(1.0-beepProb);
						} else if(d!=0){
							double beepProb = Math.pow(Math.E, (-alpha * (d - 1)));
							curr.pcrew *= (beepProb);
						}
					}

				}
			}

			// normalize
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					curr.pcrew = (1.0 / beta) * curr.pcrew;
				}
			}



			// if the bot does not get a beep
		} else {
			// set current bot position probability to 0
			board.board[x][y].pcrew = 0;

			// add up all probabilities and normalize
			double beta = 0;
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					if (curr.state) {
						String current = createKey(x, y, i, j);
						int d = board.dict.get(current);
						if(curr.pcrew==0) {
							curr.pcrew=0;
						}else if (crewmember1 != null && crewmember2 != null&&d!=0) {
							// multiply by probability of both beeps
							double beepProb = Math.pow(Math.E, (-alpha * (d - 1)));
							curr.pcrew*= (1.0-(1.0-beepProb)*(1.0-beepProb));

						} else if(d!=0){
							double beepProb = Math.pow(Math.E, (-alpha * (d - 1)));
							curr.pcrew *= (1.0 - beepProb);
						}
						beta += curr.pcrew;
					}
				}
			}

			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					curr.pcrew = (1.0 / beta) * curr.pcrew;
				}
			}

		}

	}



	// finds cell with highest crewmate probability
	// this is the cell that we want to move to
	// breaks ties at random
	cell findMaxCrew() {
		ArrayList<cell> max = new ArrayList<>(); // to collect all cells with max probability

		// add our current destination cell to the list
		max.add(dest);
		boolean stay = true;

		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];
				// if we find a cell that has a higher probability than the ones we are currently saving
				// remove those old cells and add this one
				if (max.get(0).pcrew < curr.pcrew) {
					// we have found a better probability, so we are no longer pathing to the current destination cell
					stay = false;
					max.removeAll(max);
					max.add(curr);
					// if we find a cell that has the same probability as the ones we are currently saving
					// ad this one
				} else if (max.get(0).pcrew == curr.pcrew) {
					max.add(curr);
				}
				// nothing if this cell has a lower probability
			}
		}


		// if we never found a better cell, keep going to our current destination cell
		if (stay == true) {
			return dest;
		}

		// we have a better probability somewhere else
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
		int saved = 0; // # of crewmembers saved
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
				ret[0]=saved;
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

			if (debug == 1) {
				printBoard();
				System.out.println();
			}

			// alien check
			// if caught by alien, return
			if (curr.alien == true) {
				ret[0]=saved;
				ret[1]=step;
				return ret;

			}

			// crewmember check
			// saved crewmember, increment saved count
			// if we have saved both crewmembers, return
			if (isDestination()) {
				saved++;
				
				if (saved == 2) {
					ret[0]=saved;
					ret[1]=step;
					return ret;
				}
				
				// turn off the crewmember we just saved
				if (crewmember1 != null && x==crewmember1.x && y==crewmember1.y) {
					crewmember1 = null;
				} else {
					crewmember2 = null;
				}
			}

			
			// move aliens randomly
			int mv = (int)(Math.random() * 2);
			if (mv == 0) {
				alien1.move();
				alien2.move();
			} else {
				alien2.move();
				alien1.move();
			}

			alienMoveAlienProbability();

			// update crewmember probabilities
			crewmateProbability();

			// alien check
			if (board.getCell(x, y).alien == true) {
				ret[0]=saved;
				ret[1]=step;
				return ret;
			}
			
			if (board.board[alien1.x][alien1.y].palien == 0 || board.board[alien2.x][alien2.y].palien == 0) {
				ct=true;
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
					System.out.print("[XXX, " + df.format(curr.palien) + ", " + df.format(curr.pcrew) + "]  ");
					continue;
				}

				if (curr.alien == false) {
					if ((i == x && j == y) && ((crewmember1 != null && i == crewmember1.x && j == crewmember1.y)||(crewmember2 != null && i == crewmember2.x && j == crewmember2.y))) {
						System.out.print("[_BC, " + df.format(curr.palien) + ", " + df.format(curr.pcrew) + "]  ");
						continue;
					}
					if (i == x && j == y) {
						System.out.print("[_B_, " + df.format(curr.palien) + ", " + df.format(curr.pcrew) + "]  ");
						continue;
					}
					if ((crewmember1 != null && i == crewmember1.x && j == crewmember1.y)||(crewmember2 != null && i == crewmember2.x && j == crewmember2.y)) {
						System.out.print("[__C, " + df.format(curr.palien) + ", " + df.format(curr.pcrew) + "]  ");
						continue;
					}
					System.out.print("[OOO, " + df.format(curr.palien) + ", " + df.format(curr.pcrew) + "]  ");
					continue;

				} else {
					if ((i == x && j == y) && ((crewmember1 != null && i == crewmember1.x && j == crewmember1.y) || (crewmember2 != null && i == crewmember2.x && j == crewmember2.y))) {
						System.out.print("[ABC, " + df.format(curr.palien) + ", " + df.format(curr.pcrew) + "]  ");
						continue;
					}
					if (i == x && j == y) {
						System.out.print("[AB_, " + df.format(curr.palien) + ", " + df.format(curr.pcrew) + "]  ");
						continue;
					}
					if ((crewmember1 != null && i == crewmember1.x && j == crewmember1.y)||(crewmember2 != null && i == crewmember2.x && j == crewmember2.y)) {
						System.out.print("[A_C, " + df.format(curr.palien) + ", " + df.format(curr.pcrew) + "]  ");
						continue;
					}
					System.out.print("[A__, " + df.format(curr.palien) + ", " + df.format(curr.pcrew) + "]  ");
					continue;
				}
			}
			System.out.println("\n");
		}
	}
}