package aliensrematch;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

public class board {
	int d; // dimensions of the board
	cell[][] board; // 2D array of cells. this is the board.
	int open = 0; // # of open cells in the board

	HashMap<String, Integer> dict = new HashMap<>(); // to store distances from each cell to each cell

	
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
			// make sure its other neighbors have not been opened since it was added to the
			// fringe
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
			open++;

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
		
		
		// calculate distance values using dijsktra
		dijkstra();
		
		/*for(String array: dict.keySet()) {
			System.out.println("Cell1 x: "+ array.charAt(0));
			System.out.println("Cell1 y: "+ array.charAt(1));
			System.out.println("Cell2 x: "+ array.charAt(2));
			System.out.println("Cell2 y: "+ array.charAt(3));
			System.out.println("Distance between them: "+ dict.get(array)+"\n");
			
		}*/

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
	
	
	
	// calculates distance from each cell to each cell
	public void dijkstra() {
		// loop through all cells
		for (cell[] cells : board) {
			for (cell cell : cells) {
				
				// if this cell is open, this is our starting point
				if (cell.state) {
					Queue<cell> queue = new LinkedList<cell>();
					ArrayList<cell> visited = new ArrayList<cell>();

					// add our current cell to the fringe
					queue.add(cell);
					String string1 = createKey(cell.x, cell.y, cell.x, cell.y); //key string from coords
					dict.put(string1, 0); // add to dictionary
					cell.parent=cell;
					
					
					while (!queue.isEmpty()) {
						// pick a target
						cell curr = queue.poll();
						
						// create key for this cell
						String current = createKey(cell.x, cell.y, curr.x, curr.y);
						
						// find parent from dict and get their distance
						String parent = createKey(cell.x, cell.y, curr.parent.x, curr.parent.y);
						int parentDistance = dict.get(parent);
						
						// calculate our current distance to the target
						int currentDistance = 0;
						// if the dict already has our key, but our value here is less, then currentDistance = our value
						if (dict.containsKey(current) && (dict.get(current) > parentDistance + 1)) {
							currentDistance=parentDistance+1;
						// if the dict already has our key, and its value is less, currentDistance = previous value
						}else if(dict.containsKey(current) && (dict.get(current) < parentDistance + 1)) {
							currentDistance= dict.get(current);
						// if the dict does not have our key, then currentDistance = our value
						}else {
							currentDistance=parentDistance+1;
						}
						
						// add our key & value to the dict
						dict.put(current, currentDistance);
						
						
						// add neighbors to fringe if they are valid and not already visited
						if ((!queue.contains(curr.up)) && (curr.up != null) && (!visited.contains(curr.up))&&curr.up.state) {
							queue.add(curr.up);
							curr.up.parent = curr;
						}
						if ((!queue.contains(curr.down)) && (curr.down != null) && (!visited.contains(curr.down))&& curr.down.state) {
							queue.add(curr.down);
							curr.down.parent = curr;
						}
						if ((!queue.contains(curr.left)) && (curr.left != null) && (!visited.contains(curr.left))&&curr.left.state) {
							queue.add(curr.left);
							curr.left.parent = curr;
						}
						if ((!queue.contains(curr.right)) && (curr.right != null) && (!visited.contains(curr.right))&&curr.right.state) {
							queue.add(curr.right);
							curr.right.parent = curr;
						}
						
						// add current node to the visited fringe
						visited.add(curr);
					}
				}
			}
		}
		wipeParents();
	}
	
	
	
	// utility function to start path searching again
	void wipeParents() {
		for (cell[] cellr : board) {
			for (cell cell : cellr) {
				cell.parent = null;
			}
		}
	}
	
	
	
	// utility function to create string key for our dictionary
	// it is in the format 1234, with srcx=1, srcy=2, destx=3, desty=4
	String createKey(int x1, int y1, int x2, int y2) {
		return Integer.toString(x1) + Integer.toString(y1) + Integer.toString(x2) + Integer.toString(y2);
	}

}
