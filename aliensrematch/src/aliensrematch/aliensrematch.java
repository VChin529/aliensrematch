package aliensrematch;

import java.util.Arrays;

import aliensrematch.bot1;

public class aliensrematch {

	public static void main(String[] args) {
		System.out.println("running");

		/*
		 * // BOT1 bot1 bot1 = new bot1(1,0.5); int[] ret1 = new int[2]; ret1 =
		 * bot1.run(); System.out.println("Bot1 Saved:"); System.out.println(ret1[0]);
		 * System.out.println("Bot1 Steps:"); System.out.println(ret1[1]);
		 * 
		 * 
		 * // BOT2 bot2 bot2 = new bot2(10,0.75); int[] ret2 = new int[2]; ret2 =
		 * bot2.run(); System.out.println("Bot2 Saved:"); System.out.println(ret2[0]);
		 * System.out.println("Bot2 Steps:"); System.out.println(ret2[1]);
		 * System.out.println(bot2.ct);
		 * 
		 * 
		 * // BOT3 bot3 bot3 = new bot3(3, 0.75); int[] ret3 = new int[2]; ret3 =
		 * bot3.run();
		 * 
		 * System.out.println("Bot3 Saved:"); System.out.println(ret3[0]);
		 * System.out.println("Bot3 Steps:"); System.out.println(ret3[1]);
		 * 
		 * 
		 * // BOT4 bot4 bot4 = new bot4(3, 0.75); int[] ret4 = new int[2]; ret4 =
		 * bot4.run();
		 * 
		 * System.out.println("Bot4 Saved:"); System.out.println(ret4[0]);
		 * System.out.println("Bot4 Steps:"); System.out.println(ret4[1]); /*
		 * 
		 * 
		 * // BOT5 bot5 bot5 = new bot5(5, 0.5); int[] ret5 = new int[2]; ret5 =
		 * bot5.run();
		 * 
		 * System.out.println("Bot5 Saved:"); System.out.println(ret5[0]);
		 * System.out.println("Bot5 Steps:"); System.out.println(ret5[1]);
		 * 
		 * 
		 * /* // BOT6 bot6 bot6 = new bot6(1,0.5); int[] ret6 = new int[2]; ret6 =
		 * bot6.run(); System.out.println("Bot6 Saved:"); System.out.println(ret6[0]);
		 * System.out.println("Bot6 Steps:"); System.out.println(ret6[1]); //
		 * System.out.println(bot6.ct);
		 */

		// TEST COMPARISON
		int saved6 = 0;
		int sumsteps6 = 0;
		int sumct = 0;
		int saved7 = 0;
		int sumsteps7 = 0;
		int saved8 = 0, sumsteps8 = 0;

		for (int i = 0; i < 5; i++) {
			System.out.println(i);
			bot6 bot6 = new bot6(5, 0.5);
			int[] ret6 = new int[2];
			ret6 = bot6.run();

			bot7 bot7 = new bot7(5, 0.5);
			int[] ret7 = new int[2];
			ret7 = bot7.run();
			bot8 bot8 = new bot8(5, 0.5);
			int[] ret8 = new int[2];
			ret8 = bot8.run();

			saved6 += ret6[0];
			saved7 += ret7[0];
			sumsteps6 += ret6[1];
			sumsteps7 += ret7[1];
			saved8 += ret8[0];
			sumsteps8 += ret8[1];

		}

		System.out.println("Bot 6 saved " + saved6 + " crew and took an average of " + sumsteps6 / 100 + " steps");
		System.out.println("Bot 7 saved " + saved7 + " crew and took an average of " + sumsteps7 / 100 + " steps");
		System.out.println("Bot 8 saved " + saved8 + " crew and took an average of " + sumsteps8 / 100 + " steps");

	}

}
