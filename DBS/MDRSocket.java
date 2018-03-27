import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MDRSocket implements Runnable {

    private static final String MDR_ADDR = "224.0.0.3";
    private MulticastSocket socket;
    private int port;
    private InetAddress address;

    MDRSocket() throws IOException {
        this.port= 8002;
        this.socket = new MulticastSocket(port);
        this.address = InetAddress.getByName(MDR_ADDR);
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

    public void sendMessage(String msg){
        DatagramPacket packet = new DatagramPacket(msg.getBytes(),msg.getBytes().length,this.address,this.port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
