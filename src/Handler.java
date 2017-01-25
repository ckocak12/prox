import java.io.*;
import java.net.*;

class Handler extends Thread {
	private Socket cli = null;
	private DataOutputStream responseToBrowser = null;
	private DataInputStream requestedMessage = null;

	public Handler (Socket cli) {
		this.cli = cli;
	}

	public static boolean filterOK(String url) throws IOException {
		String word;
		BufferedReader br = 
				new BufferedReader(new FileReader("BannedInfo.txt"));
		while(br.ready()) {
			word = br.readLine();
			if(url.contains(word)) {
				br.close();
				return false;
			}
		}
		br.close();
		return true;
	}

	public void run() {
		try {
			requestedMessage = new DataInputStream(cli.getInputStream());
			responseToBrowser = new DataOutputStream(cli.getOutputStream());
			RequestHdr r = new RequestHdr(); //initialized in order to use the parse method
			ReplyHdr r1 = new ReplyHdr();//initialized in order to use response methods

			String copyReq = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(requestedMessage));
			while(br.ready()) {
				copyReq = copyReq.concat(br.readLine());
			}
			String documentMessage = r.toString(r.parse(requestedMessage));
			System.out.println("Request of "+r.url+" is handled by "+
					this.getId());
			System.out.println("");
			System.out.println("Message Sent:\n"+documentMessage);
			
			if(filterOK(r.url)) {
					Socket clientSocket = new Socket(r.hostname,80);
					DataOutputStream toWeb = new DataOutputStream(
							clientSocket.getOutputStream());
					
					DataInputStream fromWeb = new DataInputStream(
							clientSocket.getInputStream());
					
					toWeb.writeBytes(r.toString(false));
					
					byte[] response = new byte[4096];
					int bytesRead;
					while((bytesRead=fromWeb.read(response))!=-1) {
						responseToBrowser.write(response,0,bytesRead);
						responseToBrowser.flush();
					}
					responseToBrowser.flush();
					clientSocket.close();
			}
				
			else {
				responseToBrowser.writeBytes(r1.formNotAllowed());
				responseToBrowser.flush();
			}
			System.out.println("Request handled with thread " + this.getId() + " has been terminated...");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}