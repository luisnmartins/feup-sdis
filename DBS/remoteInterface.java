

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface remoteInterface extends Remote {

    void backup(String pathname, int replicationDegree,boolean enhanced) throws RemoteException;

    void restore(String pathname,boolean enhanced) throws RemoteException;

    void state() throws RemoteException;

    void reclaim(Integer memory) throws RemoteException;

    void delete(String pathname,boolean enhanced) throws RemoteException;

}