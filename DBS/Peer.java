
import javafx.util.Pair;
import org.omg.CORBA.TIMEOUT;

import java.util.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.concurrent.*;


/**
 * peer
 */
public class Peer implements remoteInterface{

    private static String peerID;
    private static MCSocket MC;
    private static MDBSocket MDB;
    private static MDRSocket MDR;
    private static MessageInterpreter messageInterpreter;

    private static ScheduledThreadPoolExecutor exec;
    private static StatusManager stateManager;



    public Peer(String id) throws IOException {
        peerID = id ;
        this.initiateSocketThreads();
        this.stateManager = new StatusManager();

    }


    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.out.println("Error retrieving function arguments");
            return;
        }

        System.setProperty("java.net.preferIPv4Stack", "true");
        Peer peer = new Peer(args[0]);

        RMIHandler handler = new RMIHandler();
        handler.sendToRegistry(peer,peerID);

      }

    @Override
    public void backup(String pathname, int replicationDegree) throws RemoteException {


        FileManager chunks = new FileManager(pathname); //create a FileManager to get file in chunks
        
        String fileId = chunks.generateFileID(); //get fileId according to sha256 encryption


        if(this.stateManager.deleteFile(pathname) != null){

            //TODO
            //REMOVE TODAS AS CHUNKS QUE EXISTEM DESSE FICHEIRO EM TODOS OS PEER PARA FAZER UPDATE

        }
        this.stateManager.addFile(pathname,fileId);
        try {

            List<ChunkData> chunksArray = chunks.splitFile(); //get an array with all the chunks


            for(int i=0; i<chunksArray.size(); i++) {

                System.out.println("CHUNK SEND: "+chunksArray.get(i).getChunkNo());
                Message messageToSend = new PutChunkMessage(fileId, "1.0", peerID, chunksArray.get(i), replicationDegree);
                Runnable thread = new MessageCarrier(messageToSend, "MDB",chunksArray.get(i).getChunkNo());
                Random rand = new Random();
                int randint = rand.nextInt(1000);
                Peer.getExec().schedule(thread, randint, TimeUnit.MILLISECONDS);

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

        System.out.println("PEER FILES:   ");
        Hashtable<String, String> peerFiles = stateManager.getFilesTables();
        Set<String> s = peerFiles.keySet();
        Set<String> chunksKeys = stateManager.getChunkTable().keySet();
        for(String pathname: s){
            String fileId = peerFiles.get(pathname);
            System.out.println("FILE: ");
            System.out.println("   Pathname: "+pathname);
            System.out.println("   File Id: "+fileId);
            for(String fileIdKey: chunksKeys){
                if(fileIdKey.contains(fileId)){
                    System.out.println("   Chunk: " + stateManager.getChunkTable().get(fileIdKey).getChunkNo() + " ReplicationDegree: " + stateManager.getChunkTable().get(fileIdKey).getCurrentReplicationDegree());

                }
            }

        }

        for(int i = 0; i< stateManager.getBackupedUpFiles().size(); i++){
            String string_aux = stateManager.getBackupedUpFiles().get(i);
            System.out.println("Chunk:  " + stateManager.getBackupedUpFiles().get(i));
            System.out.println("   CurrentReplicationDegree: " + stateManager.getChunkTable().get(string_aux).getCurrentReplicationDegree());
            System.out.println("   Chunk size: " + stateManager.getChunkTable().get(string_aux).getSize() + " Bytes");
        }


    }

    @Override
    public void reclaim(Integer memory) throws RemoteException {

    }

    @Override
    public void delete(String pathname) throws RemoteException {

        String fileId;
        if((fileId = Peer.getStateManager().deleteFile(pathname)) == null) {
            System.err.println("You didn't backup up this file so you can't delete it");
            return;

        } else {
            //stateManager.getFilesTables().remove(pathname);
            Message deleteMessage = new DeleteMessage(fileId, "1.0", peerID);
            Runnable thread = new MessageCarrier(deleteMessage, "MC");
            Peer.getExec().execute(thread);

            stateManager.removeChunks(fileId);
            stateManager.getFilesTables().remove(pathname);
        }

    }

    @Override
    public void sayHello() throws RemoteException {
        System.out.println("HELLO WORLD");
    }

    //Inicia as threads para os 3 canais necessarios
    public void initiateSocketThreads() throws IOException {


        this.exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(20);
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

        messageInterpreter = new MessageInterpreter();
        Runnable interpreterThread = messageInterpreter;
        this.exec.execute(interpreterThread);

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

    public static String getPeerID() {
        return peerID;
    }

    public static StatusManager getStateManager() {
        return stateManager;
    }

    public static MessageInterpreter getMessageInterpreter() {
        return messageInterpreter;
    }
}