package aliensrematch;

import java.util.ArrayList;

public class board {
	int d;   // dimensions of the board
	cell[][] board;   //2D array of cells. this is the board.

	public board(int d) {
		this.d = d;
		board = new cell[d][d];

		// set neighbor pointers
		for (int i = 0; i < d; i++) {
			for (int j = 0; j < d; j++) {
				cell curr = new cell();
				curr.x = i;
				curr.y = j;
				board[i][j] = curr;
				if (i > 0) {
					cell neighbor = board[i - 1][j];
					curr.up = neighbor;
					neighbor.down = curr;
				}

				if (j > 0) {
					cell neighbor = board[i][j - 1];
					curr.left = neighbor;
					neighbor.right = curr;
				}
			}
		}

		// generating pathways
		ArrayList<cell> fringe = new ArrayList<cell>(); // cells to open
		ArrayList<cell> dead_ends = new ArrayList<cell>(); // dead ends, open half randomly

		// start with random cell
		int i = (int) (Math.random() * d);
		int j = (int) (Math.random() * d);
		cell curr = board[i][j];

		fringe.add(curr);

		while (!fringe.isEmpty()) {
			// pick random cell from fringe to continue
			// make sure its other neighbors have not been opened since it was added to the fringe
			curr = fringe.remove((int) (Math.random() * fringe.size()));
			while (curr.neighbor_ct > 1) {
				if (fringe.isEmpty()) {
					break;
				} else {
					curr = fringe.remove((int) (Math.random() * fringe.size()));
				}
			}
			
			// open the cell
			curr.state = true;

			// collect all of its neighbors
			// increment their neighbor_ct because their neighbor curr has been opened
			ArrayList<cell> neighbors = new ArrayList<cell>();
			if (curr.up != null) {
				neighbors.add(curr.up);
				curr.up.neighbor_ct++;
			}
			if (curr.down != null) {
				neighbors.add(curr.down);
				curr.down.neighbor_ct++;
			}
			if (curr.left != null) {
				neighbors.add(curr.left);
				curr.left.neighbor_ct++;
			}
			if (curr.right != null) {
				neighbors.add(curr.right);
				curr.right.neighbor_ct++;
			}

			// add neighbors to fringe if their only open neighbor is curr
			// if fringe is not changed, no more cells to open on this path
			// this means it is a dead end. add to dead_ends
			boolean changed_fringe = false;
			for (cell neighbor : neighbors) {
				if (neighbor.neighbor_ct == 1) {
					fringe.add(neighbor);
					changed_fringe = true;
				}
			}
			if (changed_fringe == false) {
				dead_ends.add(curr);
			}
		}

		// opened all paths, have to open half of the dead ends now
		for (cell de : dead_ends) {
			// check if cell de is still a dead end- might have opened since it was added
			// random number. 0 = open, 1 = close. About half will be opened
			if (de.neighbor_ct == 1 && (int) (Math.random() * 2) == 0) {
				// open random neighbor
				de.randomNeighbor().state = true;
			}
		}

	}

	
	
	// returns a random cell that is not closed
	// used to spawn location for alien, bot, crewmember
	cell randomCell() {
		// cell in array dimensions
		int x = (int) (Math.random() * d);
		int y = (int) (Math.random() * d);

		// check for closed cell
		while (board[x][y].state == false) {
			x = (int) (Math.random() * d);
			y = (int) (Math.random() * d);
		}

		return board[x][y];
	}

	
	
	// returns cell at given position
	cell getCell(int x, int y) {
		return board[x][y];
	}

}
