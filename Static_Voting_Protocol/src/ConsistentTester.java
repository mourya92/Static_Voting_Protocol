import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ConsistentTester {
	public static void consistencyCheck(int processes, int files){		


		try{
			int max=0;
			for(int j = 1; j <= files; j++){
				max=0;
				ArrayList<String> latestFiles=new ArrayList<String>();
				ArrayList<String> versionInfo= new ArrayList<String>();
				for (int i = 1; i <= processes; i++) {
					
					String fileName="net0"+i+"_"+j+".txt";
					String FileContent;
					String temp[],temp1[];		
					ArrayList<Integer> version_number= new ArrayList<Integer>();	
					
					try{
						BufferedReader br_log = new BufferedReader(new FileReader("net0"+i+"_"+j+".txt"));

						while((FileContent=br_log.readLine())!=null){
							temp=FileContent.trim().split(":");
							if(!FileContent.equals("")){
								temp1=temp[1].split("\\s+");				
								if(!version_number.contains(temp1[3]))
									version_number.add(Integer.parseInt(temp1[3]));
							}

						}

						//Highest version in the file:
						if(version_number.get(version_number.size()-1)>max){
							max=version_number.get(version_number.size()-1);
							latestFiles.add(fileName+":"+max);

						}else
						latestFiles.add(fileName+":"+version_number.get(version_number.size()-1));
						

						br_log.close();
					}catch(FileNotFoundException fe){
						fe.printStackTrace();
					}catch(IOException io){
						io.printStackTrace();
					}
				}
				System.out.println("The highest version number is:"+max);
				for (int k = 0; k < latestFiles.size(); k++) {
					String s=latestFiles.get(k);
					String v[]=s.split(":");
					System.out.println("File:"+v[0]+"::::Version:"+v[1]);
					if(Integer.parseInt(v[1])==max){
						versionInfo.add(v[0]);
					}

				}
				String line1,line2;
				String name=versionInfo.get(0);
				
				BufferedReader br1 = new BufferedReader(new FileReader(name));
				name=versionInfo.get(1);
				BufferedReader br2 = new BufferedReader(new FileReader(name));
				int flag=0;
				while (((line1 = br1.readLine()) != null)&&((line2 = br2.readLine()) != null)) 
				{
					if (!line1.equalsIgnoreCase(line2)) 
					{ 
						flag=1; 
						break;
					}
				}
				if(flag==1){
					System.out.println("Results for File "+j+": Consitency not maintained!!");
				}else{
					System.out.println("Results for File "+j+": Consitency maintained!!");
				}
				br1.close();
				br2.close();


			}
		}catch(FileNotFoundException fe){
			fe.printStackTrace();
		}catch(IOException io){
			io.printStackTrace();
		}
	}
	public static void main(String[] args) {
		//command line arguments:#_of_processes #_of_files
		consistencyCheck(Integer.parseInt(args[0]),Integer.parseInt(args[1]));

	}

}
