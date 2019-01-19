package Sockets;

import Sockets.ChannelSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Representation of the MC socket
 */
public class MCSocket extends ChannelSocket {

    private static final String MC_ADDR= "224.0.0.1";

    /**
     * Constructor with default values
     */
    public MCSocket() throws IOException {
        super();
        this.port = 8000;
        this.socket =  new MulticastSocket(this.port);
        this.address = InetAddress.getByName(MC_ADDR);

        socket.joinGroup(address);
        }

     public MCSocket(int port,String address) throws IOException {
        super();
        this.port = port;
        this.socket = new MulticastSocket(this.port);
        this.address = InetAddress.getByName(address);

        socket.joinGroup(this.address);
     }




}
