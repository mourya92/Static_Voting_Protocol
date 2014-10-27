import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;


public class Code_Tester {
	static BufferedReader br;
	static String whoAmI;
	static ArrayList<String> File_List = new ArrayList<String>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String Id_trans;
		String Operation;
		String [] trial= ManagementFactory.getRuntimeMXBean().getName().split("@");
		whoAmI=trial[1];
		int no_of_lines = 0;
		int i=0;
		int j=0;
		int flag=0;
		try{
		br=new BufferedReader(new FileReader("Log_File_"+args[0]+".txt"));
		String infoFromConfig;
		while ((infoFromConfig = br.readLine()) != null) {
			File_List.add(infoFromConfig);
			no_of_lines++;
		}	
		for(i = 0;i<no_of_lines;i++)
		{
			
	      		if(File_List.get(i).contains("WRITE START"))
				{
					if(File_List.get(i+1).contains("WRITE END"))
					{
						System.out.println("correct order followed for the transaction");
						
					}
					else
					{
						System.out.println("Concurency test failed :(....Program will exit");
						System.out.println("the Failed lines are");
						System.out.println(File_List.get(i));
						System.out.println(File_List.get(i+1));
						System.exit(0);
					}
				
					
				
			     }
	      		else if(File_List.get(i).contains("READ START"))
	      		{
	      			int ij=0;
	      			for(ij=i+1;ij<no_of_lines;ij++)
	      			{
	      				if(File_List.get(ij).contains("READ END"))
	      				{
	      					String [] line=File_List.get(ij).split(":");
	      					String [] line1=File_List.get(i).split(":");
	      					if(line[0].equalsIgnoreCase(line1[0]))
	      					{
	      						
	      						break;
	      					}
	      				}
	      				
	      				else if(File_List.get(ij).contains("WRITE START"))
	      				{
	      					System.out.println("Concurency test failed :(....Program will exit");
							System.out.println("the Failed lines are");
							System.out.println(File_List.get(i));
							System.out.println(File_List.get(ij));
							System.exit(0);	
	      				}
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
