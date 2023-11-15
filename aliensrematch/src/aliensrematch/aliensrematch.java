package aliensrematch;

import java.util.Arrays;

import aliensrematch.bot1;

public class aliensrematch {

	public static void main(String[] args) {
		System.out.println("running");
		
		int saved1 = 0, sumsteps1 = 0;
		int saved2 = 0, sumsteps2 = 0;
		int saved3 = 0, sumsteps3 = 0;
		int saved4 = 0, sumsteps4 = 0;
		int saved5 = 0, sumsteps5 = 0;
		int saved6 = 0, sumsteps6 = 0;
		int saved7 = 0, sumsteps7 = 0;
		int ran2 = 0;

		int iters=30;
		for (int i = 0; i < iters; i++) {
			System.out.println(i);
			System.out.println("bot1");
			bot1 bot1 = new bot1(10, 0.05);
			int[] ret1 = new int[2];
			ret1 = bot1.run(); 
			System.out.println("bot2");
			bot2 bot2 = new bot2(10, 0.05);
			int[] ret2 = new int[2];
			ret2 = bot2.run();
			/*System.out.println("bot3");
			bot3 bot3 = new bot3(10, 0.05);
			int[] ret3 = new int[2];
			ret3 = bot3.run();
			System.out.println("bot4");
			bot4 bot4 = new bot4(10, 0.05);
			int[] ret4 = new int[2];
			ret4 = bot4.run();
			System.out.println("bot5");
			//bot5 bot5 = new bot5(25, 0.03);
			int[] ret5 = new int[2];
			//ret5 = bot5.run();
			
			System.out.println("bot6");
			bot6 bot6 = new bot6(25, 0.05);
			int[] ret6 = new int[2];
			ret6 = bot6.run(); 
			System.out.println("bot7");
			//bot7 bot7 = new bot7(25, 0.03);
			int[] ret7 = new int[2];
			//ret7 = bot7.run();*/
			

			saved1 += ret1[0];
			saved2 += ret2[0];
			sumsteps1 += ret1[1];
			sumsteps2 += ret2[1];
			ran2+=bot2.ct;

			/*saved3 += ret3[0];
			sumsteps3 += ret3[1];
			saved4 += ret4[0];
			sumsteps4 += ret4[1];
			saved5 += ret5[0];
			sumsteps5 += ret5[1];
			
			saved6 += ret6[0];
			sumsteps6 += ret6[1];
			
			saved7 += ret7[0];
			sumsteps7 += ret7[1];*/
			

		}
		System.out.println("Bot 1 saved " + saved1 + " crew and took an average of " + sumsteps1 / iters + " steps");
		System.out.println("Bot 2 saved " + saved2 + " crew and took an average of " + sumsteps2 / iters + " steps and took an average of "
				+ ran2 / iters + " random moves");
		System.out.println("Bot 3 saved " + saved3 + " crew and took an average of " + sumsteps3 / iters + " steps");
		System.out.println("Bot 4 saved " + saved4 + " crew and took an average of " + sumsteps4 / iters + " steps");
		System.out.println("Bot 5 saved " + saved5 + " crew and took an average of " + sumsteps5 / iters + " steps");
		System.out.println("Bot 6 saved " + saved6 + " crew and took an average of " + sumsteps6 / iters + " steps");
		System.out.println("Bot 7 saved " + saved7 + " crew and took an average of " + sumsteps7 / iters + " steps");

	}

}
