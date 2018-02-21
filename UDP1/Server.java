import java.io.IOException;
import java.net.*;
import java.util.Hashtable;

//javac Server.java
//java Server 8080
public class Server{

    private  DatagramSocket socket;

    private Hashtable<String, String> table;


    public static void main(String[] args) throws IOException{


        if(args.length != 1) {
            System.out.println("Error calling method");
            return;
        }


        new Server().run(Integer.parseInt(args[0]));

    }

    public void run(int port) throws IOException {


        boolean running = true;
        socket = new DatagramSocket(port);
        table = new Hashtable<String, String>();

        while(running) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            processRequest(packet);

        }

    }

    public void processRequest(DatagramPacket packet) throws IOException{

        InetAddress address = packet.getAddress();
        int port = packet.getPort();

        String receivedMessage = new String(packet.getData());


        String response = null;
        int result = -1;

        response = parseMessage(receivedMessage);

        if(response != null) {
            result = table.size();
        }

        if(response != null)
            response = result + "\n"+ response;
        else
            response = ""+ result;

        //send response
        byte buf[] = response.getBytes();
        DatagramPacket newPacket = new DatagramPacket(buf, buf.length, address, port);
        socket.send(newPacket);


    }

    public String parseMessage(String receivedMessage) {

        String variables[] = receivedMessage.split(" ");
        String response;

        for(int i=0; i<variables.length; i++) {
            variables[i] = variables[i].trim();
            System.out.println(variables[i]);
        }


        if(variables[0].equals("REGISTER")) {

            if(table.containsKey(variables[1]) == false) {
                table.put(variables[1], variables[2]);
                response = variables[1] + " " + variables[2];
                return response;

            } else {

                return null;
            }

        } else if(variables[0].equals("LOOKUP")) {

            String name;

            if(table.containsKey(variables[1]) == false) {

                return null;

            } else {

                response = variables[1]+ " " + table.get(variables[1]);
                return response;
            }

        } else {

            System.out.println("Error parsing message");
            return null;
        }
    }

}
