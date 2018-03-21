

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sun.java.util.jar.pack.Package.File;

public interface remoteInterface extends Remote {

    void splitFile(File f) throws RemoteException,IOException;

    void mergeFile(List<File> files, File into) throws RemoteException,IOException;
}