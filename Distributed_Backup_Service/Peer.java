
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import manageFiles.java;
import remoteInterface.java;


/**
 * peer
 */
public class Peer {

    public Peer() {};

    public String sayHello(){
        return "Hello World";    
    }

    public static void main(String[] args) throws IOException {
        splitFile(new File("/home/carlosfr/Documents/GitKraken/feup-sdis/Distributed_Backup_Service/Spring-Lamb.-Image-shot-2-011.jpg"));
        LocateRegistry.createRegistry(Integer.parseInt(arg[0]));
        try {
            manageFiles manager = new manageFiles();
            remoteInterface stub = (remoteInterface) UnicastRemoteObject.exportObject(manager,0);   
            
            Registry reg = LocateRegistry.getRegistry();
            reg.bind("name", stub);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        


      }

}