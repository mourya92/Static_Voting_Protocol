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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;

public class Sender implements Runnable {

	static int My_Votes = 0;

	static ArrayList<String> Dest_List = new ArrayList<String>();
	static ArrayList<String> Release_Lock_List = new ArrayList<String>();
	ArrayList<Socket> Sock_Array = new ArrayList<Socket>();
	static ArrayList<String> Read_Unlock_List= new ArrayList<String>();
	ArrayList<Integer> Coin_Toss= new ArrayList<Integer>();
	
	static String Mode = new String();
	static int Req_Votes_read;
	static int Req_Votes_write;
	static int portNumber_sender = 3334;
	static int portNumber_receiver = 3333;
	static int portNumber_unlock= 3335;
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
	static int file_name=1;
	
	FileWriter out_2= null;
	int NO_VOTES=0;
	
	public Sender() {
		try {
			serverSocket= new ServerSocket(portNumber_sender);
			Unlock_serverSocket= new ServerSocket(portNumber_unlock);
			Coin_Toss.add(0);
			Coin_Toss.add(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDest_List(ArrayList<String> Dest_List) {
		Sender.Dest_List = Dest_List;
		System.out.println(Dest_List);
	}

	public void setOp_ID(int Op_ID){
		Sender.Op_ID= Op_ID;
		System.out.println(" OPERATION ID IS "+ Sender.Op_ID);
	}
	
	public void setFile_Name(int file_name){
		Sender.file_name= file_name;
		System.out.println(" FILE NAME IS "+ Sender.file_name);
	}
	
	public void setMy_Votes(int My_Votes) {
		Sender.My_Votes = My_Votes;
		System.out.println(" MY VOTES ARE " + Sender.My_Votes);
	}

	public void setMode(String Mode) {
		Sender.Mode = Mode;
		System.out.println("MODE RECEIVED IS " + Sender.Mode);
	}

	public void setReq_Votes_read(int Req_Votes) {
		Sender.Req_Votes_read = Req_Votes;
		System.out.println("REQUIRED VOTES " + Sender.Req_Votes_read);
	}
	
	public  void setReq_Votes_write(int Req_Votes) {
		Sender.Req_Votes_write = Req_Votes;
		System.out.println("REQUIRED VOTES " + Sender.Req_Votes_write);
	}
	
	public static void setLOCK_Status(String LOCK_Status) {
		Sender.Lock_Status.setLength(0);
		Sender.Lock_Status.append(LOCK_Status);	
	}
	
	public static StringBuffer getLOCK_Status() {
		return Sender.Lock_Status ;
	}

	public void send_data() {
		System.out.println("SENDER THREAD INVOKED..... ");
		int i = 0, back_off=1;
		String send_To = new String();
		int received_Votes=0;
		int max_Version= Sender.Version_Number;
		
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
			Sender.setLOCK_Status("Shared");
		}
			 
		
		if(Mode.equalsIgnoreCase("write")){
			System.out.println("REQUIRED VOTES TO FORM "+Mode+" QUORUM "+" is "+Req_Votes_write);
			Sender.setLOCK_Status("Exclusive");
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
				Sender.setLOCK_Status("Shared");
			}
				 
			
			if(Mode.equalsIgnoreCase("write")){
				System.out.println("REQUIRED VOTES TO FORM "+Mode+" QUORUM "+" is "+Req_Votes_write);
				Sender.setLOCK_Status("Exclusive");
			}
			
		  }
			send_To = Dest_List.get(i);
			
			System.out.println(" CONNECTING TO "+send_To+" .........");

			Socket echoSocket = null;
			try {
				echoSocket = new Socket(send_To, portNumber_sender);
				echoSocket.setSoTimeout(500);
				
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						echoSocket.getInputStream()));

				System.out.println("---- OP ID IS "+Sender.Op_ID+" MODE IS "+Sender.Mode+"-----");
				out.println(Sender.Mode+":"+Sender.Op_ID+":"+Sender.file_name);

				String [] recV= in.readLine().split(":");
					
				if(recV[1].equalsIgnoreCase("NO VOTES")){
					NO_VOTES=1;
					System.out.println("*********************** NO VOTES RECEIVED FROM "+recV[0] +" FOR ID "+recV[2]+"**********************");
					System.out.println("RETRYING TO CONNECT TO "+recV[0]+" AFTER "+ back_off*2 +" SECONDS "+"......<EXPONENTIALLY BACKING OFF>.....");
					Sender.setLOCK_Status("Free");
					//max_Version=0;
					System.out.println("-------------------- OPERATION "+recV[2]+" UNSUCCESSFULL ... SENDING UNLOCK MESSAGES ---------------- \n");
					setOp_ID(Integer.parseInt(recV[2]));
					Release_Locks(1, Integer.parseInt(recV[2]));
					Thread.sleep(back_off*2000);
					back_off++;
					i=-1;
					echoSocket.close();
					continue;
				}
				else{
				    
					NO_VOTES=0;
					Sender.Op_ID=Integer.parseInt(recV[3]);
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
//				else{
//					max_Version= Sender.Version_Number;
//				}
				
					
				
				if(Mode.equalsIgnoreCase("read")&&(received_Votes>=Req_Votes_read))
				{
					
					
					
					System.out.println("RECEIVED REQUIRED VOTES TO FORM "+Mode+" QUORUM I.E., "+"--> "+received_Votes+" VOTES");
					System.out.println("--------------- OPERATION ID "+recV[3]+"---------------------"+"\n--------------- CHECK-OUT ------------"+"\n--------INITIAL VERSION NUMBER IS "+Sender.Version_Number+"----------");
					
					if(max_Version>Sender.Version_Number)
					{
						
						System.out.println("--------> VERSION NUMBER IS DIFFERENT <--------");
						
						//synchronized (this)
						{
							BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File_"+Sender.file_name+".txt",true));
							String temp_string = new String("ID "+recV[3]+": "+WhoAmI_small[0]+": READ START : INITIAL VERSION NUMBER : "+Sender.Version_Number);
							out_log.write(temp_string);
							out_log.newLine();
							//out_log.flush();
							out_log.close();
						}
						Sender.Version_Number= max_Version;						
						
						System.out.println(" ---------------OPERATION ID "+recV[3]+"----------------------"+"\n--------------- UPDATE FILE & VERSION NUMBER TO " +Sender.Version_Number+ " ------------"+"\n---------------------------------------");
						
						FileReader in_1 = new FileReader(max_Version_Dest[0]+"_"+Sender.file_name+".txt");
						FileWriter out_1 = new FileWriter(WhoAmI_small[0]+"_"+Sender.file_name+".txt");
						BufferedReader br = new BufferedReader(in_1);
						
						String line=null; 
						
						while((line=br.readLine())!=null)
							out_1.write(line+"\n");
						
						in_1.close();
						out_1.close();
						
						System.out.println(" ----------------READ OPERATION ID "+recV[3]+"------------------"+"\n--------------- CHECK-IN ------------"+"\n----------FINAL VERSION NUMBER IS "+Sender.Version_Number+"-----------------------------");		
					}
					else{
						
						System.out.println(" ---------------READ OPERATION ID "+recV[3]+"---------------------"+"\n--------------- CHECK-OUT ------------"+"\n--------INITIAL VERSION NUMBER IS "+Sender.Version_Number+"----------");

						System.out.println(" ------------READ OPERATION ID "+recV[3]+"-----------"+"\n--------------- READ ------------"+"\n---------------------------------------");

						System.out.println(" -----------------READ OPERATION ID "+recV[3]+"------------------"+"\n--------------- CHECK-IN ------------"+"\n--------FINAL VERSION NUMBER IS "+Sender.Version_Number+"----------");

						//synchronized (this)
						{
						BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File_"+Sender.file_name+".txt",true));
						String temp_string = new String("ID "+recV[3]+": "+WhoAmI_small[0]+": READ START : INITIAL VERSION NUMBER : "+Sender.Version_Number);
						out_log.write(temp_string);
						out_log.newLine();
						//out_log.flush();
						out_log.close();
						}
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
					
					
					System.out.println("RECEIVED REQUIRED VOTES TO FORM "+Mode+" QUORUM I.E., "+"--> "+received_Votes+" VOTES AND MAX VERSION IS " + max_Version);
					System.out.println("---------------WRITE OPERATION ID "+recV[3]+"---------------------"+"\n--------------- CHECK-OUT ------------"+"\n--------CURRENT VERSION NUMBER IS "+Sender.Version_Number+"----------");
					
					//synchronized (this)
					{
						
						BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File_"+Sender.file_name+".txt",true));
						String temp_string = new String("ID "+recV[3]+": "+WhoAmI_small[0]+": WRITE START : INITIAL VERSION NUMBER : "+Sender.Version_Number);
						out_log.write(temp_string);
						out_log.newLine();
						//out_log.flush();
						out_log.close();
					}
					Sender.Version_Number= max_Version+1;
					
					System.out.println("---------------WRITE OPERATION ID "+recV[3]+"---------------------"+"\n--------------- UPDATE FILE & VERSION NUMBER TO " +Sender.Version_Number+" ------------");
					
					PrintWriter writer = new PrintWriter(new FileWriter(WhoAmI_small[0]+"_"+Sender.file_name+".txt", true));
					writer.append("WRITE DATA : VERSION NUMBER "+Sender.Version_Number+"\n");
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
				
			} catch (SocketTimeoutException e) {
				NO_VOTES=1;
				System.out.println(" ***************** SOCKET TIMED OUT *****************");
				System.out.println(" **************** "+Dest_List.get(i)+ " FAILED TO RECEIVE A MESSAGE IN TIME "+" ***************** ");
				
				Sender.setLOCK_Status("Free");
				//max_Version=0;
				System.out.println("-------------------- OPERATION "+Sender.Op_ID+" UNSUCCESSFULL ... SENDING UNLOCK MESSAGES ---------------- \n");
				Release_Locks(1, Sender.Op_ID);
				
				
				try {
					Thread.sleep(1000);
					System.out.println("******* RETYRING TO CONNECT TO "+Dest_List.get(i)+" ********* ");
					echoSocket.close();
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				i=-1;
				continue;
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
		received_Votes=0;
		
		
		System.out.println("-------------------- OPERATION SUCCESSFULL ... SENDING UNLOCK MESSAGES ---------------- \n");
		Release_Locks(0, Sender.Op_ID);
		  try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		Sender.setLOCK_Status("Free");
		System.out.println("-------------------- OPERATION SUCCESSFULL ... SENT UNLOCK MESSAGES ---------------- \n");
		
		 max_Version=0;
	}	
	
	
	public void Release_Locks(int Fail, int id){
		
		int i=0;
		
		if(Fail!=1){
			try {
		
			if(Mode.equalsIgnoreCase("write")){
				//synchronized (this)
				{
					BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File_"+Sender.file_name+".txt",true));
					String temp_string = new String("ID "+id+": "+WhoAmI_small[0]+": WRITE END : FINAL VERSION NUMBER : "+Sender.Version_Number);
					out_log.write(temp_string);
					out_log.newLine();
					//out_log.flush();
					out_log.close();
				}
			}
			if(Mode.equalsIgnoreCase("read")){
				//synchronized (this)
				{
					
				BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File_"+Sender.file_name+".txt",true));
				String temp_string = new String("ID "+id+": "+WhoAmI_small[0]+": READ END : FINAL VERSION NUMBER : "+Sender.Version_Number);
				out_log.write(temp_string);
				out_log.newLine();
				//out_log.flush();
				out_log.close();
			}
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
				out.println("Write"+":"+Sender.Version_Number+":"+"WRITE DATA"+":"+WhoAmI+":"+id+":"+Sender.file_name);
			    //System.out.println(" ------------ UNLOCK MESSAGE SENT TO "+Release_Lock_List.get(i)+" ----------");
			}
			
			if(Mode.equalsIgnoreCase("read")&&(Fail==0))
				out.println("Read"+":"+"UNLOCK"+":"+id+":"+Sender.file_name);
			
			send_unlockSocket.close();
		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}
		  
		  Release_Lock_List.clear();	 
		
	}

	@Override
	public void run() {
		
		while (true) {
			
			try {
			 
			  if(Thread.currentThread().getName().equalsIgnoreCase("Receiver")){
				
				String[] mode_local=null;
				PrintWriter out_local=null;
				BufferedReader in_local =null;

				Socket clientSocket;
				

				System.out.println("RECEIVER LISTENING...");
				
					clientSocket = serverSocket.accept();
					System.out.println(" RECEIVER ACCEPTED..."+ clientSocket.getInetAddress());
                              	
                
          		    out_local = new PrintWriter(clientSocket.getOutputStream(), true);
					in_local = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

					mode_local= new String(in_local.readLine()).split(":");
					
					System.out.println("RECEIVED MODE IS " + mode_local[0]);
					
					if(mode_local[0].equalsIgnoreCase("read")){
						if(Sender.Lock_Status.toString().equalsIgnoreCase("shared")||(Sender.Lock_Status.toString().equalsIgnoreCase("free"))){
									
							if(WhoAmI_small[0].equalsIgnoreCase("net02")){
								Random randomGenerator = new Random();
								int index = randomGenerator.nextInt(Coin_Toss.size());

								if(index==1){
									Sender.setLOCK_Status("shared");
									out_local.println(WhoAmI+":"+My_Votes+":"+Sender.Version_Number+":"+mode_local[1]);
									Read_Unlock_List.add(clientSocket.getInetAddress().toString());
									clientSocket.close();
								}
								else{
									System.out.println(" **********  NET02 FAILED TO SEND REPLY MESSAGES FOR READ ********* ");
									
								}
							}
							else{
								Sender.setLOCK_Status("shared");
								out_local.println(WhoAmI+":"+My_Votes+":"+Sender.Version_Number+":"+mode_local[1]);
								Read_Unlock_List.add(clientSocket.getInetAddress().toString());
								clientSocket.close();
							}
						}
						else{
							out_local.println(WhoAmI+":"+"NO VOTES"+":"+mode_local[1]);
							clientSocket.close();
							
						}
					}
					
					if(mode_local[0].equalsIgnoreCase("write")){
						if(Sender.Lock_Status.toString().equalsIgnoreCase("free")){
							
							System.out.println(" ---------->>>>>>>>>>>> STATUS IS FREE <<<<<<<<---------- ");
							
							
							
							if(WhoAmI_small[0].equalsIgnoreCase("net02")){
								Random randomGenerator = new Random();
								int index = randomGenerator.nextInt(Coin_Toss.size());

								if(index==1){
									Sender.setLOCK_Status("Exclusive");
									out_local.println(WhoAmI+":"+My_Votes+":"+Sender.Version_Number+":"+mode_local[1]);
									clientSocket.close();
								}
								else{
									System.out.println(" **********  NET02 FAILED TO SEND REPLY MESSAGES FOR WRITE ********* ");
									
								}
							}
							else{
								Sender.setLOCK_Status("Exclusive");
								out_local.println(WhoAmI+":"+My_Votes+":"+Sender.Version_Number+":"+mode_local[1]);
								clientSocket.close();
							}
						}
						else{
							System.out.println(" ---------->>>>>>>>>>>> STATUS IS NOT FREE <<<<<<<<---------- ");
							out_local.println(WhoAmI+":"+"NO VOTES"+":"+mode_local[1]);
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
						String temp= unlockSocket.getInetAddress().toString();
						Read_Unlock_List.remove(temp);
						Sender.setLOCK_Status("Free");
						System.out.println(" ------------- "+ unlockSocket.getInetAddress() +" RELEASED LOCKS UNEXPECTEDLY FOR OPERATION "+Unlock_Message[2]+"------------------- \n ");
						unlockSocket.close();
					}
					
					if(Unlock_Message[0].equalsIgnoreCase("Write")==true)
					{
						
							/*
							BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
							String temp_string = new String("ID "+Unlock_Message[4]+": "+WhoAmI_small[0] +" : {AS A QUORUM MEMBER}"+": WRITE START : INITIAL VERSION NUMBER : "+Sender.Version_Number);
							out_log.write(temp_string);
							out_log.newLine();
							//out_log.flush();
							out_log.close();
						*/
						System.out.println(" ----------------WRITE OPERATION ID "+Unlock_Message[4]+"---------"+"\n--------------- CHECK-OUT ------------"+"\n--------------INITIAL VERSION NUMBER IS "+Sender.Version_Number+" -------------------------");
						Sender.Version_Number= Integer.parseInt(Unlock_Message[1]);
						System.out.println(" ----------------WRITE OPERATION ID "+Unlock_Message[4]+"-----------"+"\n--------------- UPDATE FILE & VERSION NUMBER TO " +Sender.Version_Number+ " ------------"+"\n---------------------------------------");
						
						String [] Command_Originator= Unlock_Message[3].split("\\.");
						
						FileReader in_1 = new FileReader(Command_Originator[0]+"_"+Unlock_Message[5]+".txt");
						System.out.println("------------ COMMAND ORIGINATED BY "+Command_Originator[0]+" ------------- \n");
						FileWriter out_1 = new FileWriter(WhoAmI_small[0]+"_"+Unlock_Message[5]+".txt");
						BufferedReader br = new BufferedReader(in_1);
						
						String line=null; 
						
						while((line=br.readLine())!=null)
							out_1.write(line+"\n");
						
						in_1.close();
						out_1.close();
						
						System.out.println(" ----------------WRITE OPERATION ID "+Unlock_Message[4]+"---------"+"\n--------------- CHECK-IN ------------"+"\n--------------FINAL VERSION NUMBER IS "+Sender.Version_Number+" -------------------------");
						
						/*
							BufferedWriter out_log_1 = new BufferedWriter(new FileWriter("Log_File.txt",true));
							String temp_string_1 = new String("ID "+Unlock_Message[4]+": "+WhoAmI_small[0] +" : {AS A QUORUM MEMBER}"+": WRITE END : FINAL VERSION NUMBER : "+Sender.Version_Number);
							out_log_1.write(temp_string_1);
							out_log_1.newLine();
							//out_log.flush();
							out_log_1.close();
						*/
						Sender.setLOCK_Status("Free");
						System.out.println("=============== UNLOCK MESSAGE FOR WRITE RECEIVED FOR ID "+Unlock_Message[4]+"============ ");
						System.out.println(" LOCK STATUS IS " + getLOCK_Status());
						unlockSocket.close();
					}
					
					if(Unlock_Message[0].equalsIgnoreCase("Read"))
					{		
							String temp= unlockSocket.getInetAddress().toString();
							Read_Unlock_List.remove(temp);
							System.out.println("=============== UNLOCK MESSAGE FOR READ RECEIVED FROM "+unlockSocket.getInetAddress()+" FOR ID "+Unlock_Message[2]+"============= ");
							
							//synchronized (this) 
							/*{
								
								BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
								String temp_string = new String("ID "+Unlock_Message[2]+": "+WhoAmI_small[0] +" : {AS A QUORUM MEMBER}"+": READ START : INITIAL VERSION NUMBER : "+Sender.Version_Number);
								out_log.write(temp_string);
								out_log.newLine();
								//out_log.flush();
								out_log.close();
							}*/
							
							//synchronized(this)
							/*{
								BufferedWriter out_log = new BufferedWriter(new FileWriter("Log_File.txt",true));
								String temp_string = new String("ID "+Unlock_Message[2]+": "+WhoAmI_small[0] +" : {AS A QUORUM MEMBER}"+": READ END : FINAL VERSION NUMBER : "+Sender.Version_Number);
								out_log.write(temp_string);
								out_log.newLine();
								//out_log.flush();
								out_log.close();
							}*/
							
							System.out.println(Read_Unlock_List.size()+" ----------UNLOCK MESSAGES TO BE RECEIVED -------- ");
							System.out.println(Read_Unlock_List);
							if(Read_Unlock_List.isEmpty()){
					
								Sender.setLOCK_Status("Free");
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

