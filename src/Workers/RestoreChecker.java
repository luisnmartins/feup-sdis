package Workers;

import Peer.Peer;

public class RestoreChecker implements Runnable{

   
    public RestoreChecker(){}

    @Override
    public void run(){
        while(true){
            if(Peer.getWindow() >= 7){
                Peer.setFlag(false);
            }else{
                Peer.setFlag(true);
            }
        }
       
         
    }
}