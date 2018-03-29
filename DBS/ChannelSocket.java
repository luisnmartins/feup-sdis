import javafx.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    public void sendMessage(Message msg){

        byte[] textMessage = msg.getFullMessage();

        DatagramPacket packet = new DatagramPacket(textMessage,textMessage.length,this.address,this.port);
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
                Pair<Integer,byte[]> pair = new Pair<>(packet.getLength(),packet.getData());
                Peer.getMessageInterpreter().putInQueue(pair);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
