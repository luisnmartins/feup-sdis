
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.NotBoundException;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * peer
 */
public class Peer implements remoteInterface{

    private String peerID;
    private MulticastSockets multicasts;

    public Peer(String id) throws IOException {
        peerID = id ;
        this.multicasts = new MulticastSockets();

    }


    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.out.println("Error retrieving function arguments");
            return;
        }

        System.setProperty("java.net.preferIPv4Stack", "true");
        Peer peer = new Peer(args[0]);

        peer.initiateThreads();


        //So para testar
        if(peer.peerID.equals(new String("3")) ){
            System.out.println("ENTROU");
            peer.putchunk();
        }
        //FIM de teste

        RMIHandler handler = new RMIHandler();
        handler.sendToRegistry(peer,peer.peerID);

      }

    @Override
    public void backup(String pathname, int replicationDegree) throws RemoteException {

        FileManager chunks = new FileManager(pathname); //create a FileManager to get file in chunks
        List<String> messages = new ArrayList<String>(); //array to keep backup messages to send

        String fileId = chunks.generateFileID(); //get fileId according to sha256 encryption
        Message messageToSend = new Message(fileId, "1.0", peerID); //initialize message

        try {

            List<Chunk> chunksArray = chunks.splitFile(); //get an array with all the chunks

            for(int i=0; i<chunksArray.size(); i++) {

                messages.add(messageToSend.getPutChunk(chunksArray.get(i), replicationDegree));
                System.out.println(messages.get(i));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void restore(String pathname) throws RemoteException {

    }

    @Override
    public void state() throws RemoteException {

    }

    @Override
    public void reclaim(Integer memory) throws RemoteException {

    }

    @Override
    public void delete(String pathname) throws RemoteException {

    }

    @Override
    public void sayHello() throws RemoteException {
        System.out.println("HELLO WORLD");
    }

    //Manda mensagem para o canal MC, so para razoes de teste
    void putchunk() throws IOException {
        String msg = "PUTCHUNK FROM PEER" + this.peerID;

        //DatagramPacket(byte[] buf, int length, InetAddress address, int port)
        DatagramPacket packet = new DatagramPacket(msg.getBytes(),msg.getBytes().length,this.multicasts.getAddress(0),this.multicasts.getPort(0));
        this.multicasts.MC.send(packet);
        System.out.println("Sent packet");
    }

    //Inicia as threads para os 3 canais necessarios
    public void initiateThreads(){

        ExecutorService executor = Executors.newFixedThreadPool(5);
        //Thread para o canal principal MC;
        Runnable mcThread = new ChannelListener(this.multicasts.MC);
        executor.execute(mcThread);

        //Thread para o canal MDB
        Runnable mdbThread = new ChannelListener(this.multicasts.MDB);
        executor.execute(mdbThread);

        //Thread para o canal MDR
        Runnable mdrThread = new ChannelListener(this.multicasts.MDR);
        executor.execute(mdrThread);

    }

}