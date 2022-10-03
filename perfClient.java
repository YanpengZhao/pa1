import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.io.*;

public class perfClient{


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
		String measurementType="";
		int numberOfProbes=0;
		int messageSize=0;
		BufferedReader userEn =new BufferedReader(new InputStreamReader(System.in));
		Socket s = new Socket(args[0], port);

		DataOutputStream client =new DataOutputStream(s.getOutputStream());

		BufferedReader serverBuffer =new BufferedReader(new InputStreamReader(s.getInputStream()));
		//csp starts
		int mt=0;
		System.out.println("Enter measurement type [1] for rtt [2] for tput");
		while(true) {
			try {
			mt=Integer.parseInt(userEn.readLine());
			if(mt==1) {
				measurementType="rtt";
				break;
			}else if(mt==2) {
				measurementType="tput";
				break;
			}else {
				System.out.println(String.format("you enter %s is not 1 or 2,try again", mt));
				continue;
			}
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("cannot recognize, try again");
				continue;
			}
		}
		int num=0;
		System.out.println("Enter probe number, 1 or bigger");
		while(true) {
			try {
				numberOfProbes=Integer.parseInt(userEn.readLine());
				
				break;
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("cannot recognize, try again");
				continue;
			}
		}
		System.out.println("Enter message size, cannot be smaller than 2");
		while(true) {
			try {
				messageSize=Integer.parseInt(userEn.readLine());
				if(messageSize<2){
					throw new Exception();
				}
				break;
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("cannot recognize, try again");
				continue;
			}
		}
		System.out.println("Enter delay(in ms)");
		int serverDelay=0;
		while(true) {
			try {
				serverDelay=Integer.parseInt(userEn.readLine());
				break;
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("cannot recognize, try again");
				continue;
			}
		}
		String output='s'+" "+measurementType+" "+numberOfProbes+" "+messageSize+" "+serverDelay+'\n';
		client.writeBytes(output);
		String serverMessage = serverBuffer.readLine();
		System.out.println(serverMessage);
		if (!serverMessage.equals("200 OK: Ready")) {
			System.out.println("Server: " + serverMessage);
			s.close();
			return;
		}
		//csp ends
		//mp starts
		String payLoad="";
		byte[] bytes = new byte[(int)(messageSize/2)];
        SecureRandom rand = null;
		try {
			rand = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        rand.nextBytes(bytes);
        StringBuilder result = new StringBuilder();
        for (byte temp : bytes) {
		result.append(String.format("%02x",temp));
        }
		payLoad=result.toString();
		System.out.println(payLoad);
		String output2="";
		String returned="";
		double RTT=0.0;
		double TPUT=0.0;
		long start;
		long end;
		byte[] bytes1;
		for(int i=1;i<=numberOfProbes;i++) {
			output2='m'+" "+i+" "+payLoad+'\n';
			bytes1=output2.getBytes();
			System.out.println(bytes1.length);
			start=System.nanoTime();
			client.write(bytes1);
			returned=serverBuffer.readLine();
			end=System.nanoTime();
			RTT+=(end-start);
			System.out.println(returned);
			
		}
		RTT=(RTT/1000000)/numberOfProbes;
		TPUT=messageSize/RTT;
		
		if(measurementType=="rtt") {
			System.out.println("The average RTT is "+RTT);
		}else if(measurementType=="tput") {
			System.out.println("The average TPUT is "+TPUT);
		}
		//mp ends
		client.writeBytes(("t"+'\n'));
		System.out.println(serverBuffer.readLine());

		s.close();
}
}
