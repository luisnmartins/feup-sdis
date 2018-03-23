import java.io.IOException;

public class Application {

    private static String pathname;
    private static int replicationDegree;
    private static String peerApp;
    private static String operation;

    public static void main(String[] args) throws IOException{

        if(verifyArgs(args) == false){
            System.out.println("Error retrieving application arguments");
            return;
        }

        RMIHandler handler = new RMIHandler();
        remoteInterface peer = handler.getFromRegistry(peerApp);
        peer.sayHello();
    }

    public static Boolean verifyArgs(String[] args){

        if(args.length != 4 && args.length != 3){
            return false;
        }
        peerApp = args[0];
        if(args.length == 4){
            if(args[1] != "BACKUP"){
                return false;
            }else{
                pathname = args[2];
                replicationDegree = Integer.parseInt(args[3]);
                return true;
            }
        }else if(args.length == 3){
            if(args[1] == "BACKUP"){
                return false;
            }
            return true;
        }
        return false;
    }
}
