import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * peer
 */
public class peer {

    public static void splitFile(File f) throws IOException {
        
        int partCounter = 1;
        byte[] buffer = new byte[64000];

        String fileName = f.getName();

        //try-with-resources to ensure closing stream
        try (FileInputStream fis = new FileInputStream(f); BufferedInputStream bis = new BufferedInputStream(fis)) {

            int bytesAmount = 0;
            while ((bytesAmount = bis.read(buffer)) > 0) {
                //write each chunk of data into separate file with different number in name
                String filePartName = String.format("%s.%03d", fileName, partCounter++);
                File newFile = new File(f.getParent(), filePartName);
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, bytesAmount);
                }
            }
        }
    }

    public static void mergeFiles(List<File> files, File into) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(into); BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
            for (File f : files) {
                Files.copy(f.toPath(), mergingStream);
             }   
        }
}

    public static void main(String[] args) throws IOException {
        splitFile(new File("/home/carlosfr/Documents/GitKraken/feup-sdis/Distributed_Backup_Service/Spring-Lamb.-Image-shot-2-011.jpg"));
      

      }
}