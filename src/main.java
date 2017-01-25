import java.io.*;
import java.net.*; 

public class main {
	//TODO return the website
	//TODO log file

	public static void main(String[] args) throws IOException {
		ServerSocket serv=null;

		try {
			serv = new ServerSocket(8562);
			System.out.println("Server listening clients over port 8562...");
		}
		catch(IOException e) {
			e.printStackTrace();	
		}
		while(true) {
			Socket cli = null;
			cli = serv.accept();
			Handler ch = new Handler(cli);
			ch.start();
		}
	}
}
