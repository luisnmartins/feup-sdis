import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSockets {

    public MulticastSocket MC;
    public MulticastSocket MDB;
    public MulticastSocket MDR;

    private int[] ports;
    private InetAddress[] addresses = new InetAddress[3];

    MulticastSockets() throws IOException {

        this.ports = new int[]{8000, 8001, 8002};
        this.addresses[0] = InetAddress.getByName("224.0.0.1");
        this.addresses[1] = InetAddress.getByName("224.0.0.2");
        this.addresses[2] = InetAddress.getByName("224.0.0.3");

        MC = new MulticastSocket(this.ports[0]);
        MDB = new MulticastSocket(this.ports[1]);
        MDR = new MulticastSocket(this.ports[2]);

        MC.joinGroup(this.addresses[0]);
        MDB.joinGroup(this.addresses[1]);
        MDR.joinGroup(this.addresses[2]);

       // MC.connect(this.addresses[0],this.ports[0]);
        //MDB.connect(this.addresses[1],this.ports[1]);
        //MDR.connect(this.addresses[2],this.ports[2]);
    }

    MulticastSockets(int[] ports,String[] hostsNames) throws IOException {
        this.ports = ports;
        MulticastSocket[] sockets = {this.MC,this.MDB,this.MDR};
        for(int i = 0; i < 3; i++){
            sockets[i] = new MulticastSocket(this.ports[i]);
            this.addresses[i] = InetAddress.getByName(hostsNames[i]);
            sockets[i].joinGroup(this.addresses[i]);
        }
    }

    public InetAddress[] getAddresses() {
        return addresses;
    }

    public int[] getPorts(){
        return ports;
    }

    public InetAddress getAddress(int i){
        return addresses[i];
    }

    public int getPort(int i){
        return ports[i];
    }
}
