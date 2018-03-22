
import java.rmi.NotBoundException;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * peer
 */
public class Peer {

    protected  int port;

    public Peer() {}


    public static void main(String[] args) throws IOException {
        //splitFile(new File("/home/carlosfr/Documents/GitKraken/feup-sdis/Distributed_Backup_Service/Spring-Lamb.-Image-shot-2-011.jpg"));
        if(args.length != 1){
            System.out.println("Error retrieving function arguments");
            return;
        }
        Peer peer = new Peer();
        peer.port = Integer.parseInt(args[0]);
        LocateRegistry.createRegistry(peer.port);
        manageFiles manager = new manageFiles();
        Protocols prot = new Protocols();
        peer.sendToRegistry(prot,"hello");

        


      }

      public <T extends remoteInterface> void sendToRegistry(T objectToSend,String tag) {
          try {

              remoteInterface stub = (remoteInterface) UnicastRemoteObject.exportObject(objectToSend,0);
              Registry reg = LocateRegistry.getRegistry();
              reg.bind(tag,stub);
          } catch (Exception e) {
            System.err.println("Server exception: "+ e.toString());
            e.printStackTrace();
          }
      }

      public <T extends remoteInterface> T getFromRegistry(String Tag){

          try {
              Registry reg = LocateRegistry.getRegistry(this.port);
              remoteInterface stub = (remoteInterface) reg.lookup(Tag);
              return (T) stub;
          } catch (Exception e) {
              System.err.println("Client exception: " + e.toString());
              e.printStackTrace();
          }

          return null;
      }

}