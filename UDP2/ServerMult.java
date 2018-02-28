
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Calendar;
import javax.imageio.IIOException;
import java.net.InetAddress;
import java.util.Hashtable;
import javax.smartcardio.Card;

public class ServerMult {

    private InetAddress mcadrres;
    private MulticastSocket mcsocket;
    private int port;
    private int mcport;
    private Hashtable<String, String> table;
    private DatagramSocket socket;
    private MyThread thread;

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        if (args.length != 3) {
            System.out.println("Error calling method");
            return;
        }

        ServerMult server = new ServerMult();
        server.createSockets(args);

    }

    public void createSockets(String[] args) throws IOException {
        port = Integer.parseInt(args[0]);
        mcport = Integer.parseInt(args[2]);

        //Multicast Socket
        mcadrres = InetAddress.getByName(args[1]);
        mcsocket = new MulticastSocket(mcport);
        mcsocket.setSoTimeout(1);

        //Datagram Socket
        socket = new DatagramSocket(port);
        thread = new MyThread();
        thread.start();

    }

    public void run() throws IOException {
        boolean running = true;
        table = new Hashtable<String, String>();
        while (running) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            processRequest(packet);
        }

    }

    public void processRequest(DatagramPacket packet) throws IOException {

        InetAddress address = packet.getAddress();
        int port = packet.getPort();

        String receivedMessage = new String(packet.getData());

        String response = null;
        int result = -1;

        response = parseMessage(receivedMessage);

        if (response != null) {
            result = table.size();
        }

        if (response != null)
            response = result + "\n" + response;
        else
            response = "" + result;

        //send response
        byte buf[] = response.getBytes();
        DatagramPacket newPacket = new DatagramPacket(buf, buf.length, address, port);
        socket.send(newPacket);

    }

    public String parseMessage(String receivedMessage) {

        String variables[] = receivedMessage.split(" ");
        String response;

        for (int i = 0; i < variables.length; i++) {
            variables[i] = variables[i].trim();
            System.out.println(variables[i]);
        }

        if (variables[0].equals("REGISTER")) {

            if (table.containsKey(variables[1]) == false) {
                table.put(variables[1], variables[2]);
                response = variables[1] + " " + variables[2];
                return response;

            } else {

                return null;
            }

        } else if (variables[0].equals("LOOKUP")) {

            String name;

            if (table.containsKey(variables[1]) == false) {

                return null;

            } else {

                response = variables[1] + " " + table.get(variables[1]);
                return response;
            }

        } else {

            System.out.println("Error parsing message");
            return null;
        }
    }

    private class MyThread extends Thread {
        public void run() {
            while (true) {
                String portStr = Integer.toString(port);
                DatagramPacket packet = new DatagramPacket(portStr.getBytes(), portStr.getBytes().length, mcadrres,
                        mcport);

                try {
                    mcsocket.send(packet);
                    Thread.sleep(1000);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}