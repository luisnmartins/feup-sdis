
import ChunkInfo.ChunkInfo;

import javax.xml.crypto.Data;
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
import java.util.concurrent.*;


/**
 * peer
 */
public class Peer implements remoteInterface{

    private String peerID;
    private static MCSocket MC;
    private static MDBSocket MDB;
    private static MDRSocket MDR;

    private static ScheduledThreadPoolExecutor exec;

    private static Hashtable<String,String> filesTables;
    private static Hashtable<String, ChunkInfo> chunkTable;

    public Peer(String id) throws IOException {
        peerID = id ;
        filesTables = new Hashtable<>();
        this.initiateSocketThreads();

    }


    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.out.println("Error retrieving function arguments");
            return;
        }

        System.setProperty("java.net.preferIPv4Stack", "true");
        Peer peer = new Peer(args[0]);

        //So para testar
        if(peer.peerID.equals(new String("3")) ){
            System.out.println("ENTROU");
          //  peer.putchunk();
        }
        //FIM de teste

        RMIHandler handler = new RMIHandler();
        handler.sendToRegistry(peer,peer.peerID);

      }

    @Override
    public void backup(String pathname, int replicationDegree) throws RemoteException {


        FileManager chunks = new FileManager(pathname); //create a FileManager to get file in chunks
        
        String fileId = chunks.generateFileID(); //get fileId according to sha256 encryption


        try {

            List<Chunk> chunksArray = chunks.splitFile(); //get an array with all the chunks


            for(int i=0; i<chunksArray.size(); i++) {

                Message messageToSend = new PutChunkMessage(fileId, "1.0", peerID, chunksArray.get(i), replicationDegree);
                Runnable thread = new MessageCarrier(messageToSend, "MDB");
                this.exec.execute(thread);

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

    //Inicia as threads para os 3 canais necessarios
    public void initiateSocketThreads() throws IOException {


        this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(50);
        //Thread para o canal principal MC;
        MC = new MCSocket();
        Runnable mcThread = MC;
        this.exec.execute(mcThread);

        //Thread para o canal MDB
        MDB = new MDBSocket();
        Runnable mdbThread = MDB;
        this.exec.execute(mdbThread);

        //Thread para o canal MDR
        MDR = new MDRSocket();
        Runnable mdrThread = MDR;
        this.exec.execute(mdrThread);

    }

    public static ScheduledExecutorService getExec() {
        return exec;
    }

    public static MCSocket getMC() {
        return MC;
    }

    public static MDBSocket getMDB() {
        return MDB;
    }

    public static MDRSocket getMDR() {
        return MDR;
    }
}