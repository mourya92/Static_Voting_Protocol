import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;


public class Test_prgram {
	
	
	
	public static void main(String[] args)
	{
		File file = new File("test.txt");
		FileChannel channel = null;
		FileLock lock = null;
		try {
			channel = new RandomAccessFile(file, "rw").getChannel();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Get an exclusive lock on the whole file
		
		try {
			lock = channel.lock();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
		    try {
				if((lock = channel.tryLock())==null)
					System.out.println(" LOCK ALREADY EXISTS");
				else
					System.out.println(" LOCK SUCCESSFULLY OBTAINED ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    // Ok. You get the lock
		} catch (OverlappingFileLockException e) {
			e.printStackTrace();
			System.out.println(" FILE ALREADY OPENED ");
		    // File is open by someone else
		} finally {
		    try {
				lock.release();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
}
