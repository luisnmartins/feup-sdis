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

    public void sendMessage(byte[] msg){
        DatagramPacket packet = new DatagramPacket(msg,msg.length,this.address,this.port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){

            byte[] buf = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            try {
                this.socket.receive(packet);
                Runnable receiver = new MessageInterpreter(packet.getLength(), packet.getData());
                Peer.getExec().execute(receiver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
