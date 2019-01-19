package RMI;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface used by objects send to rmi registry
 */
public interface remoteInterface extends Remote {

    /**
     * Function that initiates the backup subprotocol
     * @param pathname represent the path to the file to backup
     * @param replicationDegree replication degree desired
     * @param enhanced if it is to use the enhancememt
     */
    void backup(String pathname, int replicationDegree,boolean enhanced) throws RemoteException;

    /**
     * Function that initiates the restore subprotocol
     * @param pathname path of the file to restore
     * @param enhanced if it is to use the enhancement version
     */
    void restore(String pathname,boolean enhanced) throws RemoteException;

    /**
     * Show the state of system
     */
    void state() throws RemoteException;

    /**
     * Initiates the reclaim subprotocol
     * @param memory maximum space that the peer can use
     */
    void reclaim(Integer memory) throws RemoteException;

    /**
     * Initiates the delete subprotocol
     * @param pathname path to the backedUp file to delete
     * @param enhanced flag to see if it is to use the enhanced version
     */
    void delete(String pathname,boolean enhanced) throws RemoteException;

}