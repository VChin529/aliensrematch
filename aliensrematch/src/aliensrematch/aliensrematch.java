package aliensrematch;

import java.util.Arrays;

import aliensrematch.bot1;

public class aliensrematch {

	public static void main(String[] args) {
		System.out.println("running");

		/*
		bot1 bot1 = new bot1(5,0.5);
		int[] ret = new int[2];
		ret1 = bot1.run();
		*/
		
		
		bot2 bot2 = new bot2(10,0.5);
		int[] ret = new int[2];
		ret = bot2.run();
		
		
		System.out.println("Bot Saved:");
		System.out.println(ret[0]);
		System.out.println("Bot Steps:");
		System.out.println(ret[1]);
	}

}
