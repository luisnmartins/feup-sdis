import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Representation of the MC socket
 */
public class MCSocket extends ChannelSocket{

    private static final String MC_ADDR= "224.0.0.1";

    /**
     * Constructor with default values
     */
    MCSocket() throws IOException {
        super();
        this.port = 8000;
        this.socket =  new MulticastSocket(this.port);
        this.address = InetAddress.getByName(MC_ADDR);

        socket.joinGroup(address);
        }

     MCSocket(int port,String address) throws IOException {
        super();
        this.port = port;
        this.socket = new MulticastSocket(this.port);
        this.address = InetAddress.getByName(address);

        socket.joinGroup(this.address);
     }




}
