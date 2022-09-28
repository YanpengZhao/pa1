import java.net.*;
import java.io.*;
class childServer extends Thread{
	Socket s1;
	public childServer(Socket s){
		this.s1=s;
		
	}
	public void run(){
		
		try {
			
			String message="";
			BufferedReader clientBuffer;
			clientBuffer = new BufferedReader(new InputStreamReader(s1.getInputStream()));
			DataOutputStream serverBuffer=new DataOutputStream(s1.getOutputStream());
			message = clientBuffer.readLine();
			serverBuffer.writeBytes(message);
			System.out.println(message);
			s1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
public class EchoServer{


public static void main(String [] args) throws IOException{
	if (args.length != 1) {
			System.out.println("Enter in right format");
			return;
		}
	int port=0;
	try {
		port=Integer.parseInt(args[0]);
	}catch(Exception e) {
		e.printStackTrace();
	}
	
	ServerSocket s = new ServerSocket(port);
	System.out.println(InetAddress.getLocalHost());
	while(true){
		Socket s1=s.accept();
		new childServer(s1).start();
	}
	
	

}
}
