import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MDBSocket extends ChannelSocket{

    private static final String ADDR_MDB = "224.0.0.2";

    MDBSocket() throws IOException {
        this.port = 8001;
        this.socket = new MulticastSocket(port);
        this.address = InetAddress.getByName(ADDR_MDB);
        this.socket.joinGroup(address);
    }

    MDBSocket(int port,String address) throws IOException {
        super();
        this.port = port;
        this.socket = new MulticastSocket(this.port);
        this.address = InetAddress.getByName(address);

        socket.joinGroup(this.address);
    }
}
