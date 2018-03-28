import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class ChannelSocket implements Runnable {

    public static final int PACKET_SIZE = 65536;
    public MulticastSocket socket;
    public int port;
    public InetAddress address;

    ChannelSocket(){}

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

            byte[] buf = new byte[PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            try {
                this.socket.receive(packet);
                System.out.println(new String(packet.getData()));
                Runnable receiver = new MessageInterpreter(packet.getLength(), packet.getData());
                Peer.getExec().execute(receiver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}