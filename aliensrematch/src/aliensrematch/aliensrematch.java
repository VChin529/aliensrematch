package aliensrematch;

import java.util.Arrays;

import aliensrematch.bot1;

public class aliensrematch {

	public static void main(String[] args) {
		System.out.println("running");

		/*
		// BOT1
		bot1 bot1 = new bot1(1,0.5); int[] ret1 = new int[2]; ret1 = bot1.run();
		System.out.println("Bot1 Saved:");
		System.out.println(ret1[0]);
		System.out.println("Bot1 Steps:");
		System.out.println(ret1[1]);


		// BOT2
		bot2 bot2 = new bot2(10,0.75); int[] ret2 = new int[2]; ret2 = bot2.run();
		System.out.println("Bot2 Saved:"); System.out.println(ret2[0]);
		System.out.println("Bot2 Steps:"); System.out.println(ret2[1]);
		System.out.println(bot2.ct);


		// BOT3
		bot3 bot3 = new bot3(3, 0.75); int[] ret3 = new int[2]; ret3 = bot3.run();

		System.out.println("Bot3 Saved:"); System.out.println(ret3[0]);
		System.out.println("Bot3 Steps:"); System.out.println(ret3[1]);


		// BOT4
		bot4 bot4 = new bot4(3, 0.75); int[] ret4 = new int[2]; ret4 = bot4.run();

		System.out.println("Bot4 Saved:"); System.out.println(ret4[0]);
		System.out.println("Bot4 Steps:"); System.out.println(ret4[1]);


		// BOT5
		bot5 bot5 = new bot5(4, 0.5); int[] ret5 = new int[2]; ret5 = bot5.run();

		System.out.println("Bot5 Saved:"); System.out.println(ret5[0]);
		System.out.println("Bot5 Steps:"); System.out.println(ret5[1]);
		

		// BOT6
		bot6 bot6 = new bot6(1,0.5); int[] ret6 = new int[2]; ret6 = bot6.run(); 
		System.out.println("Bot6 Saved:"); System.out.println(ret6[0]);
		System.out.println("Bot6 Steps:"); System.out.println(ret6[1]);
		// System.out.println(bot6.ct);
		*/

		
		// TEST COMPARISON
		int saved3 = 0; int sumsteps3 = 0;
		int saved4 = 0; int sumsteps4 = 0;
		int saved5=0; int sumsteps5=0; 

		for (int i = 0; i < 40; i ++) {
			System.out.println(i); 
			
			bot3 bot3 = new bot3(5,0.5); int[] ret3 = new int[2]; ret3 = bot3.run();
			saved3 += ret3[0]; sumsteps3 += ret3[1]; 

			bot4 bot4 = new bot4(5,0.5); int[] ret4 = new int[2]; ret4 = bot4.run();
			saved4 += ret4[0];  sumsteps4 += ret4[1]; 

			bot5 bot5 = new bot5(5,0.5); int[] ret5 = new int[2]; ret5 = bot5.run();
			saved5+=ret5[0]; sumsteps5+=ret5[1];

		}

		System.out.println("Bot 3 saved " + saved3 + " crew and took an average of "
				+ sumsteps3/100 + " steps"); 
		System.out.println("Bot 4 saved " + saved4 +
						" crew and took an average of " + sumsteps4/100 + " steps");
		System.out.println("Bot 5 saved " + saved5 + " crew and took an average of "
						+ sumsteps5/100 + " steps");

	}

}
