package aliensrematch;

import java.util.*;

public class bot1 {
	int x, y, k;   // coordinates
	board board;   // board that the bot is on
	alien alien;  // array of aliens
	crewmember crewmember;   // crewmember to save
	int debug = 0;   // utility for debugging. ignore.

	public bot1(int k) {
		// generate board dimension 50x50
		board = new board(50);

		// random placement of bot
		cell curr = board.randomCell();
		this.x = curr.x;
		this.y = curr.y;
		
		this.k = k;
		
		// generate 1 alien
		alien = new alien(board);
		while (alienScan(alien.x, alien.y)) {
			alien = new alien (board);
		}

		// initialize crewmember
		// if in the same position as bot, redo
		crewmember = new crewmember(board);
		while (x == crewmember.x && y == crewmember.y) {
			crewmember.generateCrewmember();
		}

		if (debug == 1) {
			printBoard();
		}

	}



	// checks if bot position is crewmember position
	boolean isDestination() {
		return board.board[x][y] == board.board[crewmember.x][crewmember.y];
	}




	// BFS to return the shortest path
	// ignore is for the situation where aliens are blocking by the path (per the TA in zulip)
	Stack<cell> findPath(boolean ignore) {
		// error check
		if (debug ==1 && !board.board[x][y].isValid(ignore)) {
			System.out.println("We blocked " + board.board[x][y].x + board.board[x][y].y);
		}

		//create fringes
		Queue<cell> queue = new LinkedList<cell>();
		ArrayList<cell> visited = new ArrayList<cell>();

		//add our current cell to the fringe
		queue.add(board.board[x][y]);
		while (!queue.isEmpty()) {
			//check if we are at the crewmate
			cell curr = queue.poll();
			if ((curr.x == crewmember.x) && curr.y == crewmember.y) {
				if (debug == 1) {
					System.out.println("we made it");
				}
				return getPath();
			}

			//add neighbors to fringe if they are valid and not already visited
			if ((!queue.contains(curr.up)) && (curr.up != null) && (!visited.contains(curr.up))
					&& (curr.up.isValid(ignore))) {
				queue.add(curr.up);

				if (debug == 1) {
					System.out.println("adding" + curr.up.x + " " + curr.up.y);
				}
				curr.up.parent = curr;
			}
			if ((!queue.contains(curr.down)) && (curr.down != null) && (!visited.contains(curr.down))
					&& (curr.down.isValid(ignore))) {
				queue.add(curr.down);
				if (debug == 1) {
					System.out.println("adding" + curr.down.x + " " + curr.down.y);
				}
				curr.down.parent = curr;
			}
			if ((!queue.contains(curr.left)) && (curr.left != null) && (!visited.contains(curr.left))
					&& (curr.left.isValid(ignore))) {
				queue.add(curr.left);
				if (debug == 1) {
					System.out.println("adding" + curr.left.x + " " + curr.left.y);
				}
				curr.left.parent = curr;
			}
			if ((!queue.contains(curr.right)) && (curr.right != null) && (!visited.contains(curr.right))
					&& curr.right.isValid(ignore)) {
				queue.add(curr.right);
				if (debug == 1) {
					System.out.println("adding" + curr.right.x + " " + curr.right.y);
				}
				curr.right.parent = curr;
			}
			//add current node to the visited fringe
			visited.add(curr);

		}

		//we will only reach here if the aliens are blocking the path, thus run the algorithm again, this time ignoring the aliens 
		return findPath(true);
	}

	
	
	// trace back parent pointers to return the shortest path as a stack
	Stack<cell> getPath() {
		Stack<cell> path = new Stack<>();

		//get current coordinates
		int currx = crewmember.x;
		int curry = crewmember.y;

		//get parent of current node
		cell next = board.board[crewmember.x][crewmember.y].parent;

		do {
			if (debug == 1) {
				System.out.println("cell x " + currx + " cell y " + curry);
			}
			//add parent to stack
			path.push(board.board[currx][curry]);
			//get next parent
			next = board.board[currx][curry].parent;
			currx = next.x;
			curry = next.y;
			//set parent to null so we don't run into problems the next runs

		} while (board.board[currx][curry].parent != null);

		return path;
	}
	
	
	
	// every step the bot takes, we want a new path
	// so the parents must be undone
	void wipeParents()
	{
		for(cell[] cellr : board.board) {
			for(cell cell: cellr) {
				cell.parent=null;
			}
		}
	}

	

	// alien scanner version 1
	// checks if given coordinate falls within alien scanner range
	boolean alienScanCoord(int i, int j) {
		if ((x-k)< i && i < (x+k)) {
			if (((y-k) < j && j < (y+k))) {
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
			if (i_start < 0) {break;}
		}
		
		int i_end = x;
		for (int l = 0; l < k; l++) {
			i_end++;
			if (i_start >= board.board.length) {break;}
		}
		
		int j_start = y;
		for (int l = 0; l < k; l++) {
			j_start--;
			if (j_start < 0) {break;}
		}
		
		int j_end = x;
		for (int l = 0; l < k; l++) {
			j_end++;
			if (j_start >= board.board.length) {break;}
		}
		
		
		// checks bounds for alien
		for (int i = i_start; i < i_end; i++) {
			for (int j = j_start; j < j_end; j++) {
				if (i==alien.x && j==alien.y) {return true;}
			}
		}
		
		return false;
	}
	
	
	
	// calculate alien probabilities
	void calculateAlienProbabilities() {
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				cell curr = board.board[i][j];
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


			// alien check
			// if caught by alien, return
			if (curr.alien == true) {
				if (debug == 1) {
					System.out.println("Caught by alien");
				}
				ret[0] = saved;
				ret[1] = step;
				return ret;
			}


			// crewmember check
			// saved crewmember, generate another
			// cannot be where the bot is
			if (isDestination()) {
				saved++;
				if (debug == 1) {
					System.out.println("Found crewmate");
				}
				crewmember.generateCrewmember();
				while (x == crewmember.x && y == crewmember.y) {
					crewmember.generateCrewmember();
				}
			}


			// move aliens
			alien.move();

			// alien check
			if (board.getCell(x, y).alien == true) {
				if (debug == 1) {
					System.out.println("Caught by alien");
				}
				ret[0] = saved;
				ret[1] = step;
				return ret;
			}

			// wipe parent pointers in preparation for a new path
			// must do this every time because we recalculate path every step
			wipeParents();

		}


		// completed 1000 steps without dying
		if (debug == 1) {System.out.println("Out of time");}
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

}
