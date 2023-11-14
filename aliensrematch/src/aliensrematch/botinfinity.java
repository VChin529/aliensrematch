package aliensrematch;

import java.util.ArrayList;

//Snippets of Code from our project 
//two crewmember two alien
public class botinfinity {
	
	// calculate alien probabilities when the bot moves
	void botMoveAlienProbability() {

		double beta = 0.0; // to calculate normalization constant

		// scanner goes off
		if (alienScan()) {
			
			double prob_square_total = 0.0;//total probability of the cells inside our detection range
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					// alien must be in our scanner area && open(not a wall)
					if (alienScanCoord(i, j) && curr.state) {
					
						prob_square_total += curr.palien;
					} 
				}
			}
			
			
			if (prob_square_total == 0) {
				return;
			}
			

			for (cell curr : board.board) {
				if(alienScanCoord(curr.x,curr.y) &&curr.state) {
					curr.palien *= 1.0 / prob_square_total;
				}else {
					curr.palien *= (1.0-(1.0 / prob_square_total));
				}
				
				
				beta += curr.palien;
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
		

		// copying old probabilities for reference
		double[][] probs = new double[board.board.length][board.board.length];
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board.length; j++) {
				probs[i][j] = board.board[i][j].palien;
			}
		}

		double beta = 0.0; // to calculate normalization constant

		if (alienScan()) {
			
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
	
	// sets off crewmember detection beep
		// beep depends on location of both crewmembers
		boolean beep() {
			double prob = 0.0;

			//case where 2 crewmembers still on board
			if (crewmember1 != null && crewmember2 != null) {
				// find the probability of beep from crewmember1 && crewmember2
				//create key to pull distance from our current position to crew1 position in dictionary
				String current1 = createKey(x, y, crewmember1.x, crewmember1.y);
				//get distance from our dictionary
				int d1 = board.dict.get(current1);
				//calculate probability
				//probability of not detecting beep from crewmember1
				double prob1 = 1.0 - (Math.pow(Math.E, (-alpha * (d1 - 1))));
				
				//same as above, but for the second crewmember
				String current2 = createKey(x, y, crewmember2.x, crewmember2.y);
				int d2 = board.dict.get(current2);
				double prob2 = 1.0 - (Math.pow(Math.E, (-alpha * (d2 - 1))));

				// final beep probability is from one OR the other
				// 1 - (not detecting crew 1 AND not detecting crew2)
				//this is the probability of detecting crew1 OR crew2 
				prob = 1.0 - (prob1 * prob2);
				
				//case where one crew is found and are detecting beeps from remaining crewmember
			} else if (crewmember1 != null) { // crewmember2 is null, we are looking for crew1
				String current = createKey(x, y, crewmember1.x, crewmember1.y);
				int d = board.dict.get(current);
				prob = (Math.pow(Math.E, (-alpha * (d - 1))));

			} else { // crewmember1 is null, we are looking for crew2
				String current = createKey(x, y, crewmember2.x, crewmember2.y);
				int d = board.dict.get(current);
				prob = (Math.pow(Math.E, (-alpha * (d - 1))));
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
				

				// set current bot position probability to 0
				board.board[x][y].pcrew = 0;

				double beta = 0;//for normalization
				for (int i = 0; i < board.board.length; i++) {
					for (int j = 0; j < board.board.length; j++) {
						cell curr = board.board[i][j];
						if (curr.state) {
							// find the distance from us to the cell
							String current = createKey(x, y, i, j);
							int d = board.dict.get(current);
							//if it equals zero then we know crew is not there, ignore
							if(curr.pcrew==0) {
								curr.pcrew=0;

								// multiply probability of crewmember in cell * probability of beep | crewmember
							}else if (crewmember1 != null && crewmember2 != null&&d!=0) {
								
								double beepProb = Math.pow(Math.E, (-alpha * (d - 1)));
								//probability of getting a beep from either crewmate
								curr.pcrew*= 1.0-(1.0-beepProb)*(1.0-beepProb);
								
							} else if(d!=0){
								//probability of getting a beep from a single crewmate
								double beepProb = Math.pow(Math.E, (-alpha * (d - 1)));
								curr.pcrew *= (beepProb);
							}
							beta += curr.pcrew;
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
								
							}else if (crewmember1 != null && crewmember2 != null && d!=0) {
								//probability of not getting a beep from either crewmate
								double beepProb = Math.pow(Math.E, (-alpha * (d - 1)));
								curr.pcrew*= (1.0-(1.0-(1.0-beepProb)*(1.0-beepProb)));

							} else if(d != 0){
								//probability of not getting a beep from a single crewmate
								double beepProb = Math.pow(Math.E, (-alpha * (d - 1)));
								curr.pcrew *= (1.0 - beepProb);
							}
							beta += curr.pcrew;
						}
					}
				}
				
				//normalize
				for (int i = 0; i < board.board.length; i++) {
					for (int j = 0; j < board.board.length; j++) {
						cell curr = board.board[i][j];
						curr.pcrew = (1.0 / beta) * curr.pcrew;

					}
				}

			}

		}
		
		cell findMaxCrew() {
			ArrayList<cell> max = new ArrayList<>(); // to collect all cells with max probability

			// add our current destination cell to the list
			max.add(dest);
			boolean stay = true;

			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board.length; j++) {
					cell curr = board.board[i][j];
					// if we find a cell that has a higher probability than the ones we are
					// currently saving
					// remove those old cells and add this one
					if (max.get(0).pcrew < curr.pcrew) {
						// we have found a better probability, so we are no longer pathing to the
						// current destination cell
						stay = false;
						max.removeAll(max);
						max.add(curr);
						// if we find a cell that has the same probability as the ones we are currently
						// saving
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
}
