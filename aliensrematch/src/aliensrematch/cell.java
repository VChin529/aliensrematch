package aliensrematch;

public class cell {
	cell up, down, left, right;   // pointers to neighbors
	int neighbor_ct;   // # of open neighbors
	int x, y;   // coordinates
	boolean state;   // true = open, false = closed
	boolean alien;   // true = occupied, false = unoccupied
	cell parent;   // to trace back path in BFS
	// double h;   // calculate danger from aliens
	double palien1, pcrew1;
	//double palien2, pcrew2;

	// default constructor
	public cell() {}



	// returns random neighbor that is not a wall- may be closed
	cell randomNeighbor() {
		cell ret = null;
		int pos;

		// picks a random neighbor from 0-3
		// if this neighbor is off the board,
		// it goes again until it finds one that is on the board.
		// will not be caught in infinite loop
		// because there will not be a cell that has no neighbors
		// so it will find one eventually
		while (ret == null) {
			pos = (int) (Math.random() * 4);

			if (pos == 0) { ret = up;
			} else if (pos == 1) { ret = down;
			} else if (pos == 2) { ret = left;
			} else if (pos == 3) { ret = right;
			}
		}

		return ret;
	}



	// returns if bot can move there
	// so, if the cell is open and has no aliens
	// if ignoring aliens, only checks if cell is open
	boolean isValid(boolean ignore) {
		if (ignore == true) {
			if ((state == false)) {
				return false;
			} else {
				return true;
			}
		} else {
			if ((alien == true) || (state == false)) {
				return false;
			} else {
				return true;
			}
		}

	}
	
}
