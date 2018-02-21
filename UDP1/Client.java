
import java.io.IOException;
import java.net.*;


//javac Client.java
//java Client localhost 8080 register 11-22-AA Luis
//java Client localhost 8080 lookup 11-22-AA
public class Client {


    public static void main(String[] args) throws IOException {


        if (args.length == 0 || (args[2].equals("register") && args.length != 5) || (args[2].equals("lookup") && args.length != 4)) {
            System.out.println("Error calling method");
            return;
        }

        new Client().startClient(args);

    }

    public void startClient(String[] args) throws IOException {

        String message = createMessage(args);
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(args[0]);
        byte buf[] = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(args[1]));
        socket.send(packet);


        byte newBuf[] = new byte[256];
        DatagramPacket newPacket = new DatagramPacket(newBuf, newBuf.length);
        socket.receive(newPacket);
        String received = new String(newPacket.getData());
        System.out.println(received);

        socket.close();

    }

    public String createMessage(String[] args) {

        String message;
        message = args[2].toUpperCase();
        for (int i = 3; i < args.length; i++) {
            message += " " + args[i];
        }
        return message;
    }

}
