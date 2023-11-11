package aliensrematch;

import java.util.Arrays;

import aliensrematch.bot1;

public class aliensrematch {

	public static void main(String[] args) {
		System.out.println("running");


		// BOT1
		/*
		bot1 bot1 = new bot1(2,0.5);
		int[] ret1 = new int[2];
		ret1 = bot1.run();
		System.out.println("Bot1 Saved:");
		System.out.println(ret1[0]);
		System.out.println("Bot1 Steps:");
		System.out.println(ret1[1]);
		 */


		// BOT2
		/*
		bot2 bot2 = new bot2(2,0.5);
		int[] ret2 = new int[2];
		ret2 = bot2.run();
		System.out.println("Bot2 Saved:");
		System.out.println(ret2[0]);
		System.out.println("Bot2 Steps:");
		System.out.println(ret2[1]);
		System.out.println(bot2.ct);
		 */


		// BOT3
		bot3 bot3 = new bot3(4,0.5);
		int[] ret3 = new int[2];
		ret3 = bot3.run();

		System.out.println("Bot3 Saved:");
		System.out.println(ret3[0]);
		System.out.println("Bot3 Steps:");
		System.out.println(ret3[1]);


		// TEST COMPARISON
		/*
		int saved1 = 0;
		int saved2 = 0;
		int sumsteps1 = 0;
		int sumsteps2 = 0;
		int sumct = 0;

		for (int i = 0; i < 10; i ++) {
			System.out.println(i);
			bot1 bot1 = new bot1(5,0.75);
			int[] ret1 = new int[2];
			ret1 = bot1.run();


			bot2 bot2 = new bot2(2,0.5);
			int[] ret2 = new int[2];
			ret2 = bot2.run();

			saved1 += ret1[0];
			saved2 += ret2[0];
			sumsteps1 += ret1[1];
			sumsteps2 += ret2[1];
			sumct += bot2.ct;

		}

		System.out.println("Bot 1 saved " + saved1 + " crew and took an average of " + sumsteps1/100 + " steps");
		System.out.println("Bot 2 saved " + saved2 + " crew and took an average of " + sumsteps2/100 + " steps");
		System.out.println("Bot 2 took an average of " + sumct/100 + " random steps");
		*/
	}

}
