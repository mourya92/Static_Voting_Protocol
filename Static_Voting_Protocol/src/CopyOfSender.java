import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class CopyOfSender implements Runnable {

	static int My_Votes = 0;

	static ArrayList<String> Dest_List = new ArrayList<String>();
	static ArrayList<String> Release_Lock_List = new ArrayList<String>();
	ArrayList<Socket> Sock_Array = new ArrayList<Socket>();
	static ArrayList<String> Read_Unlock_List= new ArrayList<String>();
	
	
	static String Mode = new String();
	static int Req_Votes_read;
	static int Req_Votes_write;
	static int portNumber_sender = 3324;
	static int portNumber_receiver = 3323;
	static int portNumber_unlock= 3325;
	static StringBuffer Lock_Status = new StringBuffer("Free");
	static int Version_Number=1;
	String [] max_Version_Dest=null;
	static String[] trial = ManagementFactory.getRuntimeMXBean()
			.getName().split("@");
	static String WhoAmI = new String(trial[1]);
	static String [] WhoAmI_small= WhoAmI.split("\\.");
	
	static ServerSocket Unlock_serverSocket=null;
	
	static ServerSocket serverSocket= null;
	static int Op_ID=0;
	
	FileWriter out_2= null;
	int NO_VOTES=0;
	
	public CopyOfSender() {
		try {
			serverSocket= new ServerSocket(portNumber_sender);
			Unlock_serverSocket= new ServerSocket(portNumber_unlock);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDest_List(ArrayList<String> Dest_List) {
		CopyOfSender.Dest_List = Dest_List;
		System.out.println(Dest_List);
	}

	public void setOp_ID(int Op_ID){
		CopyOfSender.Op_ID= Op_ID;
		System.out.println(" OPERATION ID IS "+ CopyOfSender.Op_ID);
	}
	public void setMy_Votes(int My_Votes) {
		CopyOfSender.My_Votes = My_Votes;
		System.out.println(" MY VOTES ARE " + CopyOfSender.My_Votes);
	}

	public void setMode(String Mode) {
		CopyOfSender.Mode = Mode;
		System.out.println("MODE RECEIVED IS " + CopyOfSender.Mode);
	}

	public void setReq_Votes_read(int Req_Votes) {
		CopyOfSender.Req_Votes_read = Req_Votes;
		System.out.println("REQUIRED VOTES " + CopyOfSender.Req_Votes_read);
	}
	
	public  void setReq_Votes_write(int Req_Votes) {
		CopyOfSender.Req_Votes_write = Req_Votes;
		System.out.println("REQUIRED VOTES " + CopyOfSender.Req_Votes_write);
	}
	
	public static void setLOCK_Status(String LOCK_Status) {
		CopyOfSender.Lock_Status.setLength(0);
		CopyOfSender.Lock_Status.append(LOCK_Status);	
	}
	
	public static StringBuffer getLOCK_Status() {
		return CopyOfSender.Lock_Status ;
	}

	public void send_data() {
		System.out.println("SENDER THREAD INVOKED..... ");
		int i = 0, back_off=1;
		String send_To = new String();
		int received_Votes=0;
		int max_Version= CopyOfSender.Version_Number;
		
		if(Lock_Status.toString().equalsIgnoreCase("Exclusive"))
		{
			while(true)
				if(Lock_Status.toString().equalsIgnoreCase("Free"))
					break;
		}
		
		if(Mode.equalsIgnoreCase("write")&&(Lock_Status.toString().equalsIgnoreCase("Shared"))){
			while(true)
				if(Lock_Status.toString().equalsIgnoreCase("Free"))
					break;
		}
		
		if(Mode.equalsIgnoreCase("read")){
			System.out.println("REQUIRED VOTES TO FORM "+Mode+" QUORUM "+" is "+Req_Votes_read);
			CopyOfSender.setLOCK_Status("Shared");
		}
			 
		
		if(Mode.equalsIgnoreCase("write")){
			System.out.println("REQUIRED VOTES TO FORM "+Mode+" QUORUM "+" is "+Req_Votes_write);
			CopyOfSender.setLOCK_Status("Exclusive");
		}
		
		for (i = 0; i < Dest_List.size(); i++) {
		
			if(NO_VOTES==1)
			{
			if(Lock_Status.toString().equalsIgnoreCase("Exclusive"))
			{
				while(true)
					if(Lock_Status.toString().equalsIgnoreCase("Free"))
						break;
			}
			
			if(Mode.equalsIgnoreCase("write")&&(Lock_Status.toString().equalsIgnoreCase("Shared"))){
				while(true)
					if(Lock_Status.toString().equalsIgnoreCase("Free"))
						break;
			}
			
			if(Mode.equalsIgnoreCase("read")){
				System.out.println("REQUIRED VOTES TO FORM "+Mode+" QUORUM "+" is "+Req_Votes_read);
				CopyOfSender.setLOCK_Status("Shared");
			}
				 
			
			if(Mode.equalsIgnoreCase("write")){
				System.out.println("REQUIRED VOTES TO FORM "+Mode+" QUORUM "+" is "+Req_Votes_write);
				CopyOfSender.setLOCK_Status("Exclusive");
			}
		  }
			send_To = Dest_List.get(i);
			
			System.out.println(" CONNECTING TO "+send_To+" .........");

			Socket echoSocket = null;
			try {
				echoSocket = new Socket(send_To, portNumber_sender);
				
				
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						echoSocket.getInputStream()));

				System.out.println("---- OP ID IS "+CopyOfSender.Op_ID+" MODE IS "+CopyOfSender.Mode+"-----");
				out.println(CopyOfSender.Mode+":"+CopyOfSender.Op_ID);

				String [] recV= in.readLine().split(":");
					
				if(recV[1].equalsIgnoreCase("NO VOTES")){
					NO_VOTES=1;
					System.out.println("*********************** NO VOTES RECEIVED FROM "+recV[0] +" FOR ID "+recV[2]+"**********************");
					System.out.println("RETRYING TO CONNECT TO "+recV[0]+" AFTER "+ back_off*2 +" SECONDS "+"......<EXPONENTIALLY BACKING OFF>.....");
					CopyOfSender.setLOCK_Status("Free");
					System.out.println("-------------------- OPERATION "+recV[2]+" UNSUCCESSFULL ... SENDING UNLOCK MESSAGES ---------------- \n");
					CopyOfSender.Release_Locks(1, Integer.parseInt(recV[2]));
					Thread.sleep(back_off*2000);
					back_off++;
					i=-1;
					echoSocket.close();
					continue;
				}
				else{
				    
					NO_VOTES=0;
					CopyOfSender.Op_ID=Integer.parseInt(recV[3]);
					Release_Lock_List.add(recV[0]);
				}
				
				back_off=1;
				Sock_Array.add(echoSocket);
				
				
				System.out.println("SENT MODE: " + Mode
						+ " RECEIVED MESSAGE--> " + recV[0]+" GAVE "+recV[1]+" VOTES AND VERSION NUMBER IS "+ recV[2]+" FOR ID "+recV[3]);
	            		
				received_Votes+=Integer.parseInt(recV[1]);
				
				if(max_Version<=Integer.parseInt(recV[2])){
					max_Version=Integer.parseInt(recV[2]);
					max_Version_Dest= recV[0].split("\\.");
				}
					
				
				if(Mode.equalsIgnoreCase("read")&&(received_Votes>=Req_Votes_read))
				{
					
					
					
					System.out.println("RECEIVED REQUIRED VOTES TO FORM "+Mode+" QUORUM I.E., "+"--> "+received_Votes+" VOTES");
					System.out.println("--------------- OPERATION ID "+recV[3]+"---------------------"+"\n--------------- CHECK-OUT ------------"+"\n--------INITIAL VERSION NUMBER IS "+CopyOfSender.Version_Number+"----------");
					
					if(max_Version>CopyOfSender.Version_Number)
					{
						
						System.out.println("--------> VERSION NUMBER IS DIFFERENT <--------");
						
						BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
						String temp_string = new String("ID "+recV[3]+": "+WhoAmI_small[0]+": READ START : INITIAL VERSION NUMBER : "+CopyOfSender.Version_Number);
						out_log.write(temp_string);
						out_log.newLine();
						//out_log.flush();
						out_log.close();
						
						CopyOfSender.Version_Number= max_Version;						
						
						System.out.println(" ---------------OPERATION ID "+recV[3]+"----------------------"+"\n--------------- UPDATE FILE & VERSION NUMBER TO " +CopyOfSender.Version_Number+ " ------------"+"\n---------------------------------------");
						
						FileReader in_1 = new FileReader(max_Version_Dest[0]+".txt");
						FileWriter out_1 = new FileWriter(WhoAmI_small[0]+".txt");
						BufferedReader br = new BufferedReader(in_1);
						
						String line=null; 
						
						while((line=br.readLine())!=null)
							out_1.write("UPDATED DATA : "+line+"\n");
						
						in_1.close();
						out_1.close();
						
						System.out.println(" ----------------READ OPERATION ID "+recV[3]+"------------------"+"\n--------------- CHECK-IN ------------"+"\n----------FINAL VERSION NUMBER IS "+CopyOfSender.Version_Number+"-----------------------------");		
					}
					else{
						
						System.out.println(" ---------------READ OPERATION ID "+recV[3]+"---------------------"+"\n--------------- CHECK-OUT ------------"+"\n--------INITIAL VERSION NUMBER IS "+CopyOfSender.Version_Number+"----------");

						System.out.println(" ------------READ OPERATION ID "+recV[3]+"-----------"+"\n--------------- READ ------------"+"\n---------------------------------------");

						System.out.println(" -----------------READ OPERATION ID "+recV[3]+"------------------"+"\n--------------- CHECK-IN ------------"+"\n--------FINAL VERSION NUMBER IS "+CopyOfSender.Version_Number+"----------");

						
						BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
						String temp_string = new String("ID "+recV[3]+": "+WhoAmI_small[0]+": READ START : INITIAL VERSION NUMBER : "+CopyOfSender.Version_Number);
						out_log.write(temp_string);
						out_log.newLine();
						//out_log.flush();
						out_log.close();
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				}
				if(Mode.equalsIgnoreCase("write")&&(received_Votes>=Req_Votes_write))
				{
					
					
					System.out.println("RECEIVED REQUIRED VOTES TO FORM "+Mode+" QUORUM I.E., "+"--> "+received_Votes+" VOTES");
					System.out.println("---------------WRITE OPERATION ID "+recV[3]+"---------------------"+"\n--------------- CHECK-OUT ------------"+"\n--------CURRENT VERSION NUMBER IS "+CopyOfSender.Version_Number+"----------");
					
					BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
					String temp_string = new String("ID "+recV[3]+": "+WhoAmI_small[0]+": WRITE START : INITIAL VERSION NUMBER : "+CopyOfSender.Version_Number);
					out_log.write(temp_string);
					out_log.newLine();
					//out_log.flush();
					out_log.close();
					
					CopyOfSender.Version_Number= max_Version+1;
					
					System.out.println("---------------WRITE OPERATION ID "+recV[3]+"---------------------"+"\n--------------- UPDATE FILE & VERSION NUMBER TO " +CopyOfSender.Version_Number+" ------------");
					
					PrintWriter writer = new PrintWriter(new FileWriter(WhoAmI_small[0]+".txt", true));
					writer.append("WRITE DATA : VERSION NUMBER "+CopyOfSender.Version_Number+"\n");
					writer.flush();
					writer.close();
					
					System.out.println("---------------WRITE OPERATION ID "+recV[3]+"-------------"+"\n--------------- CHECK-IN ------------"+"\n---------------------------------------");		
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
		received_Votes=0;
		max_Version=0;
		
		System.out.println("-------------------- OPERATION SUCCESSFULL ... SENDING UNLOCK MESSAGES ---------------- \n");
		CopyOfSender.Release_Locks(0, CopyOfSender.Op_ID);
		CopyOfSender.setLOCK_Status("Free");
		System.out.println("-------------------- OPERATION SUCCESSFULL ... SENT UNLOCK MESSAGES ---------------- \n");
	}	
	
	
	public static void Release_Locks(int Fail, int id){
		
		int i=0;
		
		if(Fail!=1){
			try {
		
			if(Mode.equalsIgnoreCase("write")){
				BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
				String temp_string = new String("ID "+id+": "+WhoAmI_small[0]+": WRITE END : FINAL VERSION NUMBER : "+CopyOfSender.Version_Number);
				out_log.write(temp_string);
				out_log.newLine();
				//out_log.flush();
				out_log.close();
			}
			if(Mode.equalsIgnoreCase("read")){
				BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
				String temp_string = new String("ID "+id+": "+WhoAmI_small[0]+": READ END : FINAL VERSION NUMBER : "+CopyOfSender.Version_Number);
				out_log.write(temp_string);
				out_log.newLine();
				//out_log.flush();
				out_log.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		  for (i = 0; i < Release_Lock_List.size(); i++)
			{
			
			try {
				Socket send_unlockSocket = new Socket(Release_Lock_List.get(i), portNumber_unlock);
				PrintWriter out;
				
				out = new PrintWriter(send_unlockSocket.getOutputStream(),true);
			
			if(Fail==1){
				out.println("Release"+":"+"UNLOCK"+":"+id);
			}
			
			if(Mode.equalsIgnoreCase("write")&&(Fail==0)){
				out.println("Write"+":"+CopyOfSender.Version_Number+":"+"WRITE DATA"+":"+WhoAmI+":"+id);
			    //System.out.println(" ------------ UNLOCK MESSAGE SENT TO "+Release_Lock_List.get(i)+" ----------");
			}
			
			if(Mode.equalsIgnoreCase("read")&&(Fail==0))
				out.println("Read"+":"+"UNLOCK"+":"+id);
			
			send_unlockSocket.close();
		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}
		  
		  Release_Lock_List.clear();
		  
		  try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
		
	}

	@Override
	public void run() {
		
		while (true) {
			
			try {
			 
			  if(Thread.currentThread().getName().equalsIgnoreCase("Receiver")){
				
				String[] mode_local=null;
				PrintWriter out=null;
				BufferedReader in =null;

				Socket clientSocket;
				

				System.out.println("RECEIVER LISTENING...");
				
					clientSocket = serverSocket.accept();
					System.out.println(" RECEIVER ACCEPTED..."+ clientSocket.getInetAddress());
                              	
                
          		    out = new PrintWriter(clientSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

					mode_local= new String(in.readLine()).split(":");
					
					System.out.println("RECEIVED MODE IS " + mode_local[0]);
                
					if(mode_local[0].equalsIgnoreCase("read")){
						if(CopyOfSender.Lock_Status.toString().equalsIgnoreCase("shared")||(CopyOfSender.Lock_Status.toString().equalsIgnoreCase("free"))){
							
							BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
							String temp_string = new String("ID "+mode_local[1]+": "+WhoAmI_small[0] +" : {AS A QUORUM MEMBER}"+": READ START : INITIAL VERSION NUMBER : "+CopyOfSender.Version_Number);
							out_log.write(temp_string);
							out_log.newLine();
							//out_log.flush();
							out_log.close();
							
							CopyOfSender.setLOCK_Status("shared");
							out.println(WhoAmI+":"+My_Votes+":"+CopyOfSender.Version_Number+":"+mode_local[1]);
							Read_Unlock_List.add(clientSocket.getInetAddress().toString());
							clientSocket.close();
							
						}
						else{
							out.println(WhoAmI+":"+"NO VOTES"+":"+mode_local[1]);
							clientSocket.close();
							
						}
					}
					
					if(mode_local[0].equalsIgnoreCase("write")){
						if(CopyOfSender.Lock_Status.toString().equalsIgnoreCase("free")){
							
							System.out.println(" ---------->>>>>>>>>>>> STATUS IS FREE <<<<<<<<---------- ");
							BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
							String temp_string = new String("ID "+mode_local[1]+": "+WhoAmI_small[0] +" : {AS A QUORUM MEMBER}"+": WRITE START : INITIAL VERSION NUMBER : "+CopyOfSender.Version_Number);
							out_log.write(temp_string);
							out_log.newLine();
							//out_log.flush();
							out_log.close();
							
							CopyOfSender.setLOCK_Status("Exclusive");
							out.println(WhoAmI+":"+My_Votes+":"+CopyOfSender.Version_Number+":"+mode_local[1]);
							clientSocket.close();
							
						}
						else{
							System.out.println(" ---------->>>>>>>>>>>> STATUS IS NOT FREE <<<<<<<<---------- ");
							out.println(WhoAmI+":"+"NO VOTES"+":"+mode_local[1]);
							clientSocket.close();
							
						}
							
					}
			  }
			  else{
				  
					Socket unlockSocket=null;
					System.out.println(" WAITING ON UNLOCK MESSAGE ");
					
					unlockSocket = Unlock_serverSocket.accept();
					
					BufferedReader in = new BufferedReader(new InputStreamReader(unlockSocket.getInputStream()));
					String [] Unlock_Message= in.readLine().split(":");
					
					if(Unlock_Message[0].equalsIgnoreCase("Release"))
					{
						CopyOfSender.setLOCK_Status("Free");
						System.out.println(" ------------- "+ unlockSocket.getInetAddress() +" RELEASED LOCKS UNEXPECTEDLY FOR OPERATION "+Unlock_Message[2]+"------------------- \n ");
						unlockSocket.close();
					}
					
					if(Unlock_Message[0].equalsIgnoreCase("Write")==true)
					{
						System.out.println(" ----------------WRITE OPERATION ID "+Unlock_Message[4]+"---------"+"\n--------------- CHECK-OUT ------------"+"\n--------------INITIAL VERSION NUMBER IS "+CopyOfSender.Version_Number+" -------------------------");
						CopyOfSender.Version_Number= Integer.parseInt(Unlock_Message[1]);
						System.out.println(" ----------------WRITE OPERATION ID "+Unlock_Message[4]+"-----------"+"\n--------------- UPDATE FILE & VERSION NUMBER TO " +CopyOfSender.Version_Number+ " ------------"+"\n---------------------------------------");
						
						String [] Command_Originator= Unlock_Message[3].split("\\.");
						
						FileReader in_1 = new FileReader(Command_Originator[0]+".txt");
						System.out.println("------------ COMMAND ORIGINATED BY "+Command_Originator[0]+" ------------- \n");
						FileWriter out_1 = new FileWriter(WhoAmI_small[0]+".txt");
						BufferedReader br = new BufferedReader(in_1);
						
						String line=null; 
						
						while((line=br.readLine())!=null)
							out_1.write(line+"\n");
						
						in_1.close();
						out_1.close();
						
						System.out.println(" ----------------WRITE OPERATION ID "+Unlock_Message[4]+"---------"+"\n--------------- CHECK-IN ------------"+"\n--------------FINAL VERSION NUMBER IS "+CopyOfSender.Version_Number+" -------------------------");
						
						BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
						String temp_string = new String("ID "+Unlock_Message[4]+": "+WhoAmI_small[0] +" : {AS A QUORUM MEMBER}"+": WRITE END : FINAL VERSION NUMBER : "+CopyOfSender.Version_Number);
						out_log.write(temp_string);
						out_log.newLine();
						//out_log.flush();
						out_log.close();
						
						CopyOfSender.setLOCK_Status("Free");
						System.out.println("=============== UNLOCK MESSAGE FOR WRITE RECEIVED FOR ID "+Unlock_Message[4]+"============ ");
						System.out.println(" LOCK STATUS IS " + getLOCK_Status());
						unlockSocket.close();
					}
					
					if(Unlock_Message[0].equalsIgnoreCase("Read")==true)
					{		
							String temp= unlockSocket.getInetAddress().toString();
							Read_Unlock_List.remove(temp);
							System.out.println("=============== UNLOCK MESSAGE FOR READ RECEIVED FROM "+unlockSocket.getInetAddress()+" FOR ID "+Unlock_Message[2]+"============= ");
							System.out.println(Read_Unlock_List.size()+" ----------UNLOCK MESSAGES TO BE RECEIVED -------- ");
							
							BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
							String temp_string = new String("ID "+Unlock_Message[2]+": "+WhoAmI_small[0] +" : {AS A QUORUM MEMBER}"+": READ END : FINAL VERSION NUMBER : "+CopyOfSender.Version_Number);
							out_log.write(temp_string);
							out_log.newLine();
							//out_log.flush();
							out_log.close();
							
							if(Read_Unlock_List.isEmpty()){
					
								CopyOfSender.setLOCK_Status("Free");
								System.out.println("===============ALL UNLOCK MESSAGES RECEIVED ============= ");
								unlockSocket.close();
								}
					
					}
					
			  }	
			} 
		catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}	
   }

