package aliensrematch;

import java.util.Arrays;

import aliensrematch.bot1;

public class aliensrematch {

	public static void main(String[] args) {
		System.out.println("running");

		bot1 bot1 = new bot1(1);
		int[] ret1 = new int[2];
		ret1 = bot1.run();
		
		
		System.out.println ("Bot 1 Saved:");
		System.out.println(ret1[0]);
		System.out.println("Bot 1 Steps:");
		System.out.println(ret1[1]);
		System.out.println("\n");
	}

}
