import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIHandler {

    /**
     * Sends any object that uses the remoteInterface to the registry
     * @param objectToSend 
     * @param tag identifier of the object in the registry
     */
    public <T extends remoteInterface> void sendToRegistry(T objectToSend,String tag) {
        try {

            remoteInterface stub = (remoteInterface) UnicastRemoteObject.exportObject(objectToSend,0);
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(tag,stub);
        } catch (Exception e) {
            System.err.println("Server exception: "+ e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Returns any object that extends Remote from the registry
     * @param Tag identifier of the object in the registry
     * @param host ip_address of the host of the registry
     */
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
