package aliensrematch;

import java.util.Arrays;

import aliensrematch.bot1;

public class aliensrematch {

	public static void main(String[] args) {
		int k = 1;
		int iters = 1;
		for(int i = 0; i < 4; i++){
			if (i == 0) {
				k = 1;
			} else if (i == 1) {
				k = 3;
			} else if (i == 2) {
				k = 5;
			} else if (i == 3) {
				k = 7;
			}
			String bot1returnSaved = "<-c(";
			String bot1returnSteps = "<-c(";
			String bot2returnSaved = "<-c(";
			String bot2returnSteps = "<-c(";
			String bot3returnSaved = "<-c(";
			String bot3returnSteps = "<-c(";
			String bot6returnSaved = "<-c(";
			String bot6returnSteps = "<-c(";
			String bot1returnSavedSuccess = "<-c(";
			String bot1returnStepsSuccess = "<-c(";
			String bot2returnSavedSuccess = "<-c(";
			String bot2returnStepsSuccess = "<-c(";
			String bot3returnSavedSuccess = "<-c(";
			String bot3returnStepsSuccess = "<-c(";
			String bot6returnSavedSuccess = "<-c(";
			String bot6returnStepsSuccess = "<-c(";
			String bot1savedPercentage = "<-c(";
			String bot2savedPercentage = "<-c(";
			String bot3savedPercentage = "<-c(";
			String bot6savedPercentage = "<-c(";

			for (double alpha = 0.0; alpha <= 0.1; alpha += 0.025) {
				//double saved1 = 0, sumsteps1 = 0;
				//double saved2 = 0, sumsteps2 = 0;
				double saved3 = 0, sumsteps3 = 0;
				double saved6 = 0, sumsteps6 = 0;
				//double saved1Success = 0, sumsteps1Success = 0;
				//double saved2Success = 0, sumsteps2Success = 0;
				double saved3Success = 0, sumsteps3Success = 0;
				double saved6Success = 0, sumsteps6Success = 0;
				for (int j = 0; j < iters; j++) {
					//bot1 bot1 = new bot1(k, alpha);
					//double[] ret1 = new double[2];
					//ret1 = bot1.run();
					//bot2 bot2 = new bot2(k, alpha);
					//double[] ret2 = new double[2];
					//ret2 = bot2.run();

					bot4 bot3 = new bot4(k, alpha);
					double[] ret3 = new double[2];
					ret3= bot3.run();


					bot5 bot6 = new bot5(k, alpha);
					double[] ret6 = new double[2];
					ret6 = bot6.run();
					/*if(ret1[0]==1){
						saved1Success+=ret1[0];
						sumsteps1Success+=ret1[1];
					}
					if(ret2[0]==1){
						saved2Success+=ret2[0];
						sumsteps2Success+=ret2[1];
					}*/
					if(ret3[0]==2){
						saved3Success+=ret3[0];
						sumsteps3Success+=ret3[1];
					}
					if(ret6[0]==2){
						saved6Success+=ret6[0];
						sumsteps6Success+=ret6[1];
					}
					/*saved1 += ret1[0];
					saved2 += ret2[0];
					sumsteps1 += ret1[1];
					sumsteps2 += ret2[1];*/

					saved3 += ret3[0];
					sumsteps3 += ret3[1];


					saved6 += ret6[0];
					sumsteps6 += ret6[1];
				}
				/*saved1/=iters;
				sumsteps1/=iters;
				saved2/=iters;
				sumsteps2/=iters;*/
				saved3/=iters;
				sumsteps3/=iters;
				saved6/=iters;
				sumsteps6/=iters;
				/*if(saved1Success ==0){
					sumsteps1Success = 0;
				}else{
					sumsteps1Success/=saved1Success;
				}
				if(saved2Success==0){
					sumsteps2Success=0;
				}else{
					sumsteps2Success/=saved2Success;
				}*/
				if(saved3Success==0){
					sumsteps3Success=0;
				}else{
					sumsteps3Success/=saved3Success;
				}
				if(saved6Success==0){
					sumsteps6Success=0;
				}else{
					sumsteps6Success/=saved6Success;
				}



				/*bot1returnSaved += Double.toString(saved1) + ",";
				bot1returnSteps += Double.toString(sumsteps1) + ",";
				bot2returnSaved += Double.toString(saved2) + ",";
				bot2returnSteps += Double.toString(sumsteps2) + ",";*/
				bot3returnSaved += Double.toString(saved3) + ",";
				bot3returnSteps += Double.toString(sumsteps3) + ",";
				bot6returnSaved += Double.toString(saved6) + ",";
				bot6returnSteps += Double.toString(sumsteps6) + ",";
				/*bot1returnStepsSuccess+=Double.toString(sumsteps1Success)+",";
				bot2returnStepsSuccess+=Double.toString(sumsteps2Success)+",";*/
				bot3returnStepsSuccess+=Double.toString(sumsteps3Success)+",";
				bot6returnStepsSuccess+=Double.toString(sumsteps6Success)+",";
				/*bot1savedPercentage+= Double.toString(saved1Success/iters)+ ",";
				bot2savedPercentage+= Double.toString(saved2Success/iters)+",";*/
				bot3savedPercentage+= Double.toString(saved3Success/iters)+ ",";
				bot6savedPercentage+= Double.toString(saved6Success/iters)+",";
			}
			bot1returnSaved = bot1returnSaved.substring(0, bot1returnSaved.length() - 1);
			bot1returnSteps = bot1returnSteps.substring(0, bot1returnSteps.length() - 1);
			bot1returnSaved+=")";
			bot1returnSteps+=")";
			bot2returnSaved = bot2returnSaved.substring(0, bot2returnSaved.length() - 1);
			bot2returnSteps = bot2returnSteps.substring(0, bot2returnSteps.length() - 1);
			bot2returnSaved+=")";
			bot2returnSteps+=")";
			bot3returnSaved = bot3returnSaved.substring(0, bot3returnSaved.length() - 1);
			bot3returnSteps = bot3returnSteps.substring(0, bot3returnSteps.length() - 1);
			bot3returnSaved+=")";
			bot3returnSteps+=")";
			bot6returnSaved = bot6returnSaved.substring(0, bot6returnSaved.length() - 1);
			bot6returnSteps = bot6returnSteps.substring(0, bot6returnSteps.length() - 1);
			bot6returnSaved+=")";
			bot6returnSteps+=")";
			bot1returnStepsSuccess = bot1returnStepsSuccess.substring(0, bot1returnStepsSuccess.length() - 1);
			bot1returnStepsSuccess+=")";
			bot2returnStepsSuccess = bot2returnStepsSuccess.substring(0, bot2returnStepsSuccess.length() - 1);
			bot2returnStepsSuccess+=")";
			bot3returnStepsSuccess = bot3returnStepsSuccess.substring(0, bot3returnStepsSuccess.length() - 1);
			bot3returnStepsSuccess+=")";
			bot6returnStepsSuccess = bot6returnStepsSuccess.substring(0, bot6returnStepsSuccess.length() - 1);
			bot6returnStepsSuccess+=")";
			bot1savedPercentage = bot1savedPercentage.substring(0, bot1savedPercentage.length() - 1);
			bot1savedPercentage+=")";
			bot2savedPercentage = bot2savedPercentage.substring(0, bot2savedPercentage.length() - 1);
			bot2savedPercentage+=")";
			bot3savedPercentage = bot3savedPercentage.substring(0, bot3savedPercentage.length() - 1);
			bot3savedPercentage+=")";
			bot6savedPercentage = bot6savedPercentage.substring(0, bot6savedPercentage.length() - 1);
			bot6savedPercentage+=")";
			//need percent success divied saved by iters remove steps for return steps
			System.out.println("K:"+k);
			System.out.println("Bot1: average number of crewmates saved");
			System.out.println(bot1returnSaved);
			System.out.println("Bot1: average number of steps taken to save all crewmember");
			System.out.println(bot1returnStepsSuccess);
			System.out.println("Bot1: Percent saved");
			System.out.println(bot1savedPercentage);
			System.out.println("Bot2: average number of crewmates saved");
			System.out.println(bot2returnSaved);
			System.out.println("Bot2: average number of steps taken to save all crewmember");
			System.out.println(bot2returnStepsSuccess);
			System.out.println("Bot2: Percent saved");
			System.out.println(bot2savedPercentage);
			System.out.println("Bot3: average number of crewmates saved");
			System.out.println(bot3returnSaved);
			System.out.println("Bot3: average number of steps taken to save all crewmember");
			System.out.println(bot3returnStepsSuccess);
			System.out.println("Bot3: Percent saved");
			System.out.println(bot3savedPercentage);
			System.out.println("Bot6: average number of crewmates saved");
			System.out.println(bot6returnSaved);
			System.out.println("Bot6: average number of steps taken to save all crewmember");
			System.out.println(bot6returnStepsSuccess);
			System.out.println("Bot6: Percent saved");
			System.out.println(bot6savedPercentage);
		}
	}
}
