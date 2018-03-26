import java.io.IOException;
import java.rmi.RemoteException;

public class Application {

    private static String pathname;
    private static int replicationDegree;
    private static String peerApp;
    private static String operation;
    private static RMIHandler handler;

    public static void main(String[] args) throws IOException{

        handler = new RMIHandler();
        if(verifyArgs(args) == false){
            return;
        }
    }

    public static Boolean verifyArgs(String[] args){

        if(args.length != 4 && args.length != 3 && args.length != 2){
            System.err.println("Incorrect number of arguments");
            return false;
        }
        remoteInterface peer = handler.getFromRegistry(args[0]);


        switch(args[1]) {
            case "BACKUP":
                if (args.length != 4) {
                    System.err.println("BACKUP call is incorrect. It must be: java <PEER_ID> BACKUP <FILENAME> <REPLICATION_DEGREE>");
                    return false;

                } else {
                    try {
                        peer.backup(args[2], Integer.parseInt(args[3]));
                        return true;

                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            case "RECOVERY":
                if(args.length != 3) {
                    System.err.println("RECOVERY call is incorrect. It must be: java <PEER_ID> RECOVERY <FILENAME>");
                    return false;

                } else {
                    try{
                        peer.restore(args[2]);
                        return true;

                    } catch(RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            case "RECLAIM":
                if(args.length != 3) {
                    System.err.println("RECLAIM call is incorrect. It must be: java <PEER_ID> RECLAIM <MEMORY>");
                    return false;

                } else {
                    try{
                        peer.reclaim(Integer.parseInt(args[2]));
                        return true;

                    } catch(RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            case "DELETE":
                if(args.length != 3) {
                    System.err.println("DELETE call is incorrect. It must be: java <PEER_ID> DELETE <FILENAME>");
                    return false;

                } else {
                    try{
                        peer.delete(args[2]);
                        return true;

                    } catch(RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            case "STATE":
                if(args.length != 2) {
                    System.err.println("STATE call is incorrect. It must be: java <PEER_ID> STATE");
                    return false;

                } else {
                    try{
                        peer.state();
                        return true;

                    } catch(RemoteException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            default:
                System.err.println("Your call is not valid!");
                return false;

        }

    }
}
