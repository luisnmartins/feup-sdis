import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.channels.Channel;

public class ChannelListener implements Runnable {

    private MulticastSocket socket;

    ChannelListener(MulticastSocket sc){
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
