

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.util.List;


public interface remoteInterface extends Remote {

    void splitFile(File f) throws IOException;

    void mergeFile(List<File> files, File into) throws IOException;
}