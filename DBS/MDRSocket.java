import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MDRSocket extends ChannelSocket {

    private static final String MDR_ADDR = "224.0.0.3";

    MDRSocket() throws IOException {
        this.port= 8002;
        this.socket = new MulticastSocket(port);
        this.address = InetAddress.getByName(MDR_ADDR);
        this.socket.joinGroup(address);
    }
}
