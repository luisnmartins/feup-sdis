import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MCSocket implements Runnable{

    private static final String MC_ADDR= "224.0.0.1";
    private MulticastSocket socket;
    private int port;
    private InetAddress address;

    MCSocket() throws IOException {
        this.port = 8000;
        this.socket =  new MulticastSocket(this.port);
        this.address = InetAddress.getByName(MC_ADDR);

        socket.joinGroup(address);
    }

    public MulticastSocket getSocket(){
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
                //CHAMA AQUI UMA THREAD QUE INTERPRETA O PACKET
                String s = new String(packet.getData());
                Runnable receiver = new MessageInterpreter(s);
                Peer.getExec().execute(receiver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
