import java.net.*;
import java.io.*;

public class EchoClient{


public static void main(String [] args) throws IOException{
		if (args.length != 2 ) {
			System.out.println("Enter in right format");
			return;
		}
		String message;
		String echoMessage;
		int port=0;
		try {
			port=Integer.parseInt(args[1]);
		}catch(Exception e) {
			e.printStackTrace();
		}
		BufferedReader userEn =new BufferedReader(new InputStreamReader(System.in));

		Socket s = new Socket(args[0], port);

		DataOutputStream client =new DataOutputStream(s.getOutputStream());

		BufferedReader serverBuffer =new BufferedReader(new InputStreamReader(s.getInputStream()));

		System.out.println("Enter your message:");

		message = userEn.readLine();

		client.writeBytes(message + '\n');

		echoMessage= serverBuffer.readLine();

		System.out.println("Echo: " + echoMessage);

		s.close();
}
}