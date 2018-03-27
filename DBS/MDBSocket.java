import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MDBSocket implements Runnable {

    private static final String ADDR_MDB = "224.0.0.1";
    private MulticastSocket socket;
    private int port;
    private InetAddress address;

    MDBSocket() throws IOException {
        this.port = 8001;
        this.socket = new MulticastSocket(port);
        this.address = InetAddress.getByName(ADDR_MDB);
        this.socket.joinGroup(address);
    }

    public MulticastSocket getSocket() {
        return socket;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        while(true){

            byte[] buf = new byte[64];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            try {
                this.socket.receive(packet);
                //CHAMA AQUI UMA THREAD QUE INTERPRETA O PACKET
                String s = new String(packet.getData());
                System.out.println("Ola " + s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
