import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.List;

public class manageFiles implements remoteInterface{

    public manageFiles() {}

    public void splitFile(File f) throws IOException{
        int partCounter = 1;
        byte[] buffer = new byte[64000];

        String fileName = f.getName();


        //try-with-resources to ensure closing stream
        try (FileInputStream fis = new FileInputStream(f); BufferedInputStream bis = new BufferedInputStream(fis)) {

            int bytesAmount = 0;
            int chunkNmb = 1;
            while ((bytesAmount = bis.read(buffer)) > 0) {
                //write each chunk of data into separate file with different number in name
                String filePartName = String.format("%s.%03d", fileName, partCounter++);
                Chunk newChunk = new Chunk(f.getParent(), filePartName);
                newChunk.setChunkNo(chunkNmb);
                try (FileOutputStream out = new FileOutputStream(newChunk)) {
                    out.write(buffer, 0, bytesAmount);
                }catch (Exception e){
                    System.err.println("Splitter exception: " + e.toString());
                    e.printStackTrace();
                }

                chunkNmb += 1;
            }
        }
    }

    public void mergeFile(List<File> files, File into) throws IOException{
        try (FileOutputStream fos = new FileOutputStream(into); BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
            for (File f : files) {
                Files.copy(f.toPath(), mergingStream);
             }   
        }
    }

    public String generateFileID(File f){
        String bitString = f.getName() + Long.toString(f.lastModified()) + Boolean.toString(f.canWrite()) + Boolean.toString(f.canRead()) + f.getPath();
        return sha256(bitString);
    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    
    

}