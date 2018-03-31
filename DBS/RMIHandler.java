import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIHandler {

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

    public <T extends Remote> T getFromRegistry(String Tag,String host){

        try {
            Registry reg;
            if(host == null){
                reg = LocateRegistry.getRegistry();
            }else{
                reg = LocateRegistry.getRegistry(host);
            }
            
            T stub = (T) reg.lookup(Tag);
            return stub;
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        return null;
    }
}
