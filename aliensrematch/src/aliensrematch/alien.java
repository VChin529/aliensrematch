package aliensrematch;

import java.util.ArrayList;

public class alien {
	int x, y;   // coordinates
	board board;   // board that the alien is on
	
	public alien (board b) {
		board = b;
		
		// put alien in random cell that does not already contain an alien
		cell curr = board.randomCell();
		while (curr.alien == true) {
			curr = board.randomCell();
		}
		
		// update coords
		this.x = curr.x;
		this.y = curr.y;
		curr.alien = true;
	}
	
	
	
	// moves alien to a random neighbor cell that is not already filled with an alien
	// sets that cell's alien attribute to true (alien is there)
	// sets parent cell's alien attribute to false (alien is not there)
	// if the alien cannot move (blocked by walls and other aliens), it stays in place
	public void move () {
		cell curr = board.getCell(x, y);
		
		ArrayList<cell> neighbors = new ArrayList<cell>();
		
		// find neighbors that are not closed and not already filled with aliens
		if (curr.up != null && curr.up.state != false && curr.up.alien != true) {
			neighbors.add(curr.up);
		}
		if (curr.down != null && curr.down.state != false && curr.down.alien != true) {
			neighbors.add(curr.down);
		}
		if (curr.left != null && curr.left.state != false && curr.left.alien != true) {
			neighbors.add(curr.left);
		}
		if (curr.right != null && curr.right.state != false && curr.right.alien != true) {
			neighbors.add(curr.right);
		}
		
		// if there are no possible neighbors to move to, stay in place
		if (neighbors.isEmpty()) { return;}
		
		// move to random neighbor in list
		cell next = neighbors.remove((int)(Math.random() * neighbors.size()));
		
		// update alien position
		x = next.x;
		y = next.y;
		next.alien = true;
		curr.alien = false;
	}
}
