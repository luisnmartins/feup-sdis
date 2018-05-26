/*package Sockets;

import Messages.Message;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.AbstractMap.SimpleEntry;
import Peer.Peer;

public abstract class ChannelSocket implements Runnable {

    protected static final int PACKET_SIZE = 65536;
    protected MulticastSocket socket;
    protected int port;
    protected SSLSocket tcpSocket;
    protected InetAddress address;

    public ChannelSocket(){}

    public MulticastSocket getSocket() {
        return socket;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    /**
     * Sends a datagram packet
     */
    /*public void sendMessage(Message msg){

        byte[] textMessage = msg.getFullMessage();
        DatagramPacket packet = new DatagramPacket(textMessage,textMessage.length,this.address,this.port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receives the message and inserts it in the queueu to be interpret after
     */
    /*@Override
    public void run() {
        while(true){

            byte[] buf = new byte[PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            try {
                this.socket.receive(packet);
                SimpleEntry<Integer,byte[]> pair = new SimpleEntry<>(packet.getLength(),packet.getData());
                Peer.getMessageInterpreter().putInQueue(pair);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}*/
