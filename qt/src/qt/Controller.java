package qt;

import java.util.Scanner;



public class Controller {

	static DatabaseAccessQt qt = new DatabaseAccessQt();
	static IOFormatter io=new IOFormatter();
	static BatchProcess bp= new BatchProcess();
	
	public static void  main(String[] args) throws Exception{
		
	 Scanner user_input = new Scanner( System.in );
		float startTime = 0;
		float endTime = 0;
		io.inputConString();
		System.out.println("");
		startTime = System.nanoTime();
		
		System.out.println("Connecting to Database...");
		int choice;
		boolean isQt= qt.openConnection(io.getDBN(), io.getMysqlUserName(), io.getMysqlPass());
		if(isQt)
		{
			System.out.println("Connected..");
			System.out.println("");
			System.out.println("Enter your choice\n1.Developer Review Network(Suggested By Subhajit Sir) \n2.Developer Review Comment Network(Suggested By Shilpi Mam)\n3.Developer Comment Network (Suggested by Shubhashis Sir)");
		    choice=user_input.nextInt();
			if(io.getDBN().equalsIgnoreCase("qt_new"))
			{
				
				io.batchInput();
				
			    if(choice==1 || choice==3)
			    	io.inputData();
			    else if (choice==2)
			    	io.inputData2();
			}	
			bp.getAccountList(io.getDirectoryPath());
			bp.getReviewList(io.getReviewDirectoryPath());
			
			if(choice==1){
					
				qt.generateDRMN(bp.getAccountList(io.getDirectoryPath()), bp.getReviewList(io.getReviewDirectoryPath()), io.getStartDate(), io.getEndDate());
				io.writeFile(qt.getFileContent(), io.getDirectoryPath()+"review-network.net");
			}
			if(choice==2){
				qt.generateDRCN(bp.getAccountList(io.getDirectoryPath()), bp.getReviewList(io.getReviewDirectoryPath()),io.getStartDate(), io.getEndDate());
				io.writeFile(qt.getFileContent(), io.getDirectoryPath()+"review-comment-network.net");
			}
			if(choice==3){
				qt.generateDCRN(bp.getAccountList(io.getDirectoryPath()), bp.getReviewList(io.getReviewDirectoryPath()), io.getStartDate(), io.getEndDate());
				io.writeFile(qt.getFileContent(), io.getDirectoryPath()+"comment-network.net");
			}
			
				
				endTime = System.nanoTime();
				System.out.println("Complete Execution");
				System.out.println("Total Time Elapsed: " + (((endTime - startTime)/1000000000)/60) + " minutes");
		}
		else
		{
			System.out.println("Wrong Connection String/Username/Password!");
		}
	 }

}


