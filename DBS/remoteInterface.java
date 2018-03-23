

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface remoteInterface extends Remote {

    void backup() throws RemoteException;

    void restore() throws RemoteException;

    void state() throws RemoteException;

    void reclaim() throws RemoteException;

    void delete() throws RemoteException;

    void sayHello() throws RemoteException;
}