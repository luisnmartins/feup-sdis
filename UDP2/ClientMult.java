import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class ClientMult {
    
    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Error on arguments!");
            return;
        }

        new ClientMult().StartClient(args);
        
    }
    public void StartClient(String[] args) throws IOException {
        String msg = createMessage(args);
        InetAddress group = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        MulticastSocket s = new MulticastSocket(port);
        s.joinGroup(group);

        DatagramPacket operation = new DatagramPacket(msg.getBytes(), msg.length(), group, port);
        s.send(operation);

        //get response
        byte[] buf = new byte[256];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        s.receive(recv);
        String received = new String(recv.getData());
        System.out.println(received);

        s.leaveGroup(group);

        s.close();

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