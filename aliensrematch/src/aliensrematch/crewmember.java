package aliensrematch;

public class crewmember {
	int x,y;   // coordinates
	board board;   // board that the crewmember is on
	
	public crewmember(board b) {
		board = b;
		
		// put crewmember in a random cell
		// update coords
		cell curr = board.randomCell();
		this.x = curr.x;
		this.y = curr.y;
	}
	
	
	
	// puts ("new") crewmember at a random position on the board
	// to be used when crewmember is saved and we want a new one
	// technically the same object
	// but didn't want to waste time initializing a new one
	public void generateCrewmember () {
		cell curr = board.randomCell();
		x = curr.x;
		y = curr.y;
	}
}
