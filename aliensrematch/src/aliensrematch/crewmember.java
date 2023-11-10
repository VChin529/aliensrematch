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



	// puts crewmember at a random position on the board
	// to be used when finding intial position for crewmember- must not be in bot cell
	public void generateCrewmember () {
		cell curr = board.randomCell();
		x = curr.x;
		y = curr.y;
	}
}
