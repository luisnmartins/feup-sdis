
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.imageio.IIOException;

public class ServerMult {

    static String INET_ADDR;
    static int port;

    public static void main(String[] args) throws UnknownHostException, InterruptedException{
        if(args.length != 3){
            System.out.println("Error calling method");
            return;
        }
        INET_ADDR = args[1];
        port = args[0];

        try(DatagramSocket serverSocket = new DatagramSocket(port)){
            
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }
}