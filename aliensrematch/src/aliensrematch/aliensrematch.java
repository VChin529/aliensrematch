package aliensrematch;

import java.util.Arrays;

import aliensrematch.bot1;

public class aliensrematch {

	public static void main(String[] args) {
		System.out.println("running");

		
		bot1 bot1 = new bot1(5,0.5);
		int[] ret1 = new int[2];
		ret1 = bot1.run();
		
		
		
		bot2 bot2 = new bot2(2,0.5);
		int[] ret2 = new int[2];
		ret2 = bot2.run();
		
		
		System.out.println("Bot1 Saved:");
		System.out.println(ret1[0]);
		System.out.println("Bot1 Steps:");
		System.out.println(ret1[1]);
		
		System.out.println("Bot2 Saved:");
		System.out.println(ret2[0]);
		System.out.println("Bot2 Steps:");
		System.out.println(ret2[1]);
	}

}
