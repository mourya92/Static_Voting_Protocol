import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

public class CopyOfThreadContainer implements Runnable {
	PipedReader pr;
	BufferedReader br;
	char pipe_data;
	int totalVotes;
	int readVotes;
	int writeVotes;
	int myVote;
	String whoAmI;
	String infoFromConfig,infoFromMyvotes;
	ArrayList<String> myList = new ArrayList<String>(); 
	Sender sender_object= new Sender();
	
	PipedWriter pw_1= new PipedWriter();
	PipedReader pr_1= new PipedReader();
	
	
	CopyOfThreadContainer(PipedReader pr){
		this.pr= pr;
		System.out.println("Constructor entered" + this.pr);
		
		Thread receiver= new Thread(sender_object,"Receiver");
		Thread unlock= new Thread(sender_object,"Unlock");
		
		receiver.start();
		unlock.start();
		
		String [] trial= ManagementFactory.getRuntimeMXBean().getName().split("@");
		whoAmI=trial[1];
		 try {
			br=new BufferedReader(new FileReader("config.txt"));
			while ((infoFromConfig = br.readLine()) != null) {
				String [] line=infoFromConfig.split(":");
				if (line[0].equals("Total_Votes"))
					totalVotes=Integer.parseInt(line[1]);
				else if(line[0].equals("Write_Quorum"))
					writeVotes=Integer.parseInt(line[1]);
				else
					readVotes=Integer.parseInt(line[1]);
					
			}
			System.out.println("who am I"+whoAmI);
			//String[] server=whoAmI.split("\\.");
			//System.out.println("Server[0]"+server);
			
			br=	new BufferedReader(new FileReader("MyVotes.txt"));
			
			while((infoFromMyvotes = br.readLine()) != null){
				String[] details=infoFromMyvotes.split(":");
				
				if(whoAmI.equals(details[0])){
					myVote=Integer.parseInt(details[1]);
				}			
				else
			     myList.add(details[0]); 
				
			}
			
			System.out.println(" LIST OF DESTINATIONS ARE ");
			System.out.println(myList);
			
			System.out.println("total votes:"+totalVotes+" \n read vote:"+readVotes+" \n write vote:"+writeVotes+" \n my vote :"+myVote+" \n Who am I:"+whoAmI);
			sender_object.setDest_List(myList);
			sender_object.setMy_Votes(myVote);
			} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException io){
			io.printStackTrace();
		}
		 
	}
	public void myAction(StringBuffer action){
		int requestVoteCount=0;
		String[] Temp = new String(action.toString()).split(":"); 
		
		if(Temp[1].equalsIgnoreCase("read")){
			requestVoteCount=readVotes-myVote;
			if(requestVoteCount>0){
				System.out.println("Connect to other processes for reading");	
				sender_object.setMode(Temp[1]);
				sender_object.setOp_ID(Integer.parseInt(Temp[0]));
				sender_object.setReq_Votes_read(requestVoteCount);
		    	System.out.println("SENDER THREAD STARTED TO DO READ OPERATION.....");
		    	sender_object.send_data();
		    	System.out.println("SENDER THREAD COMPLETED");
			}
		}
		else{
			requestVoteCount=writeVotes-myVote;
			if(requestVoteCount>0){
				System.out.println("Connect to other processes for writing");
				sender_object.setMode(Temp[1]);
				sender_object.setOp_ID(Integer.parseInt(Temp[0]));
				sender_object.setReq_Votes_write(requestVoteCount);
		    	System.out.println("SENDER THREAD STARTED TO DO WRITE OPERATION.....");
		    	sender_object.send_data();
		    	System.out.println("SENDER THREAD COMPLETED");
			}
		}
	}
	
	public void run(){
		
		System.out.println(" Entered run");
		System.out.println("Outside constructor" + this.pr); 
		
		try {
			StringBuffer action= new StringBuffer("");
			
			while(true){
				action.setLength(0);
				
				while(true){
				pipe_data = (char)pr.read();
				//System.out.print(pipe_data);
				if (pipe_data != 0){
					action.append(pipe_data);
					//System.out.print(action);
				}
				else{
					System.out.println("-------->>>>>>>> NO DATA TO READ FROM PIPE <<<<<<<<------");
					break;
				}
					
			}	
			System.out.println("action recorded is " + action);
			
			myAction(action);
			
			}
			
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(" ------------->>>>>>END OF PIPE DATA<<<<<<<<<----------");
		}finally
		{
			try {
				pr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
