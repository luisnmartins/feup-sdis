import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class ClientMult {

    private InetAddress multicastAddress;
    private int multicastPort;
    private int port;
    private MulticastSocket ms;
    private DatagramSocket socket;
    
    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Error on arguments!");
            return;
        }

        System.setProperty("java.net.preferIPv4Stack", "true");
        new ClientMult().StartClient(args);
        
    }
    public void StartClient(String[] args) throws IOException {
        
        multicastPort = Integer.parseInt(args[1]);
        
        String msg = createMessage(args);

        multicastAddress = InetAddress.getByName(args[0]);
        ms = new MulticastSocket(multicastPort);
        ms.joinGroup(multicastAddress);
                        System.out.println("JOIN");
         //get response
        byte[] buf = new byte[256];
        DatagramPacket recv = new DatagramPacket(buf, buf.length, multicastAddress,multicastPort);
        ms.receive(recv);
        System.out.println("Received port");
        String received = new String(recv.getData()).trim();
        System.out.println(received);
        port = Integer.parseInt(received);

        ms.leaveGroup(multicastAddress);

        ms.close();

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
