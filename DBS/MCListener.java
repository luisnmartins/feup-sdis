import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MCListener implements Runnable {

    private MulticastSocket socket;

    MCListener(MulticastSocket sc){
        this.socket = sc;
    }
    @Override
    public void run() {
        while(true){

            byte[] buf = new byte[64];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            try {
                this.socket.receive(packet);
                //CHAMA AQUI UMA THREAD QUE INTERPRETA O PACKET
                System.out.println(packet.getData().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
