import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.lang.management.ManagementFactory;


/**
 * 
 */

/**
 * @authors Sai Sri Mourya Gudladona
 * 			Karthikeyan Valliyurnatt 
 * 			Lakshmi J Naidu
 *
 */
public class Application {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		    			
		    Thread runner = null ;
			BufferedReader console=null;
			String[] fromConsole = null;
			String fromConsole_1 = null;
			String[] trial = ManagementFactory.getRuntimeMXBean()
					.getName().split("@");
			String WhoAmI = new String(trial[1]);
			String [] WhoAmI_small= WhoAmI.split("\\.");
			
			try {
			new ThreadContainer();
			//runner.start();
			
			Thread.sleep(8000);
			fromConsole=null;
			
				console = new BufferedReader(new FileReader(WhoAmI_small[0]+"_input.txt"));	
				
				while(true){
					fromConsole = null;
				//System.out.println(" PLEASE ENTER A COMMAND ");
					if(console!=null){
						fromConsole_1 =console.readLine();
						
					if(fromConsole_1!=null)
					 {
						fromConsole= fromConsole_1.split(":");
						System.out.println("ENTERED TEXT IS "+ fromConsole[1]);
						if((fromConsole[1].equalsIgnoreCase("End"))||(fromConsole[1].equalsIgnoreCase("read"))||(fromConsole[1].equalsIgnoreCase("write")))
						{
							String temp_string= fromConsole[0]+":"+fromConsole[1]+":"+fromConsole[2];
							//pw.write(fromConsole[0]+":"+fromConsole[1]+":"+fromConsole[2]+"\0");		
						    //pw.flush();
							System.out.println("READ LINE IS "+ temp_string);
							ThreadContainer.myAction(temp_string);
						}
						else{
							System.out.println("ENTER PROPER COMMAND");
						}
					}else{
						
							System.out.println("---------------->>>>>>>>>>>> END OF FILE REACHED <<<<<<<<<<<<--------------");
					}
					Thread.sleep(4000);
					}
							   
				}
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				runner.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(" Running Threads Finished ");
	}

}