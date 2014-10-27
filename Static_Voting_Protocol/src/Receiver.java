import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable {

	int portNumber;
	ServerSocket serverSocket;
	String WhoAmI = null;
	static StringBuffer LOCK_Status = new StringBuffer("free");
	static int MyVotes=0;

	public Receiver(int portNumber) {

		this.portNumber = portNumber;
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Receiver() {
		// TODO Auto-generated constructor stub
	}

	public void setLOCK_Status(StringBuffer LOCK_Status) {
		Receiver.LOCK_Status = LOCK_Status;
	}
	
	public StringBuffer getLOCK_Status() {
		return Receiver.LOCK_Status ;
	}
	
	
	public void setVotes(int MyVotes){
		Receiver.MyVotes= MyVotes;
	}

	@Override
	public void run() {

		while (true) {

			Socket clientSocket;
			try {

				System.out.println("Receiver Listening...");
				
					clientSocket = serverSocket.accept();
					System.out.println(" Receiver Accepted..."
							+ clientSocket.getInetAddress());

					PrintWriter out = new PrintWriter(
							clientSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));

					System.out.println("RECEIVED MODE IS" + in.readLine());

					String[] trial = ManagementFactory.getRuntimeMXBean()
							.getName().split("@");
					WhoAmI = trial[1];

					out.println(WhoAmI+" WILL GIVE "+MyVotes+" VOTES ");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
