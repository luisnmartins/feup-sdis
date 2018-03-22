import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class Protocols implements remoteInterface {

    Protocols() {}

    public void sayHello() throws RemoteException{
        System.out.println("FODASSE");
    }
    @Override
    public void backup() throws RemoteException{

    }

    @Override
    public void restores() throws RemoteException{

    }
}
