import java.net.*;
import java.io.*;
class childServer extends Thread{
    Socket s1;
    public childServer(Socket s){
        this.s1=s;

    }
    public void run(){


        String errorMessage="404 ERROR: ";
        String successMessage="200 OK: READY";
        String successMessage2="200 OK: Closing Connection";
        BufferedReader clientBuffer= null;
        try {
            clientBuffer = new BufferedReader(new InputStreamReader(s1.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DataOutputStream serverBuffer= null;
        try {
            serverBuffer = new DataOutputStream(s1.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {

            String message="";
            String message1 = clientBuffer.readLine();//read csp message
            //check the correctness of csp message
            String[] messageArr=message1.split(" ");
            errorMessage+="Invalid Connection Setup Message";
            if(messageArr.length!=5){
                throw new Exception("The CSP length is not 5");
            }
            if(!messageArr[0].equals("s")){
                throw new Exception("The protocol phase is incorrect");
            }
            if(!messageArr[1].equals("rtt") && !messageArr[1].equals("tput")){
                throw new Exception("the measurement type is unknown");
            }
            int numberOfProbes=Integer.parseInt(messageArr[2]);
            if(numberOfProbes<=0){
                throw new Exception("probe number cannot be less than 0");
            }
            int messageSize=Integer.parseInt(messageArr[3]);
            if(messageSize<=0){
                throw new Exception("message size cannot be less than 0");
            }
            int serverDelay=Integer.parseInt(messageArr[4]);
            if(serverDelay<0){
                throw new Exception("delay cannot be less than 0");
            }
            serverBuffer.writeBytes(successMessage+"\n");
            //read mp message
            int sequenceNumber=0;
            int lastSequence=0;
            String payLoad="";
            errorMessage="404 ERROR: Invalid Measurement Message";
            while(sequenceNumber<numberOfProbes){
                String message2=clientBuffer.readLine();
                String[] messageArr1=message2.split(" ");
                if(messageArr1.length!=3){

                    throw new Exception("the MP length is not 3");
                }
                if(!messageArr1[0].equals("m")){
                    throw new Exception("MP flas issue");
                }
                sequenceNumber=Integer.parseInt(messageArr1[1]);
                if(sequenceNumber!=lastSequence+1){
                    throw new Exception("sequence is not added by 1");
                }
                lastSequence=sequenceNumber;
                Thread.sleep(serverDelay);
                serverBuffer.writeBytes(message2+'\n');

            }
            //start CTP
            String message3=clientBuffer.readLine();
            String[] messageArr2=message3.split(" ");
            errorMessage="404 ERROR: Invalid Connection Termination Message";
            if(!messageArr2[0].equals("t")){
                throw new Exception("CTP phase error");
            }
            serverBuffer.writeBytes(successMessage2+'\n');
            s1.close();
        } catch (Exception e) {

                e.printStackTrace();
            try {
                serverBuffer.writeBytes(errorMessage+"\n");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                s1.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }

    }
}
public class perfServer{


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
