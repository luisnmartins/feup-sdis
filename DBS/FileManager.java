
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class FileManager{

    private static final int CHUNKSSIZE = 64000;

    private String pathname;
    private List<Chunk> chunks;

    public FileManager(String pathname) {
        this.pathname = pathname;
        this.chunks = new ArrayList<>();
    }

    public FileManager() {
        this.chunks = new ArrayList<>();
    }

    public List<Chunk> splitFile() throws IOException{

        byte[] buffer = new byte[CHUNKSSIZE];
        List<Chunk> chunksArray = new ArrayList<Chunk>();


        //try-with-resources to ensure closing stream
        try (
                FileInputStream fis = new FileInputStream(pathname);
                BufferedInputStream bis = new BufferedInputStream(fis)
        )
        {

            int chunkNmb = 0;
            int size;
            while ((size = bis.read(buffer)) > 0) {

                Chunk chunk = new Chunk(chunkNmb);
                chunk.setData(size, buffer);
                chunkNmb += 1;
                chunksArray.add(chunk);
                buffer = new byte[CHUNKSSIZE];
            }

            File file = new File(pathname);
            if(file.length()%CHUNKSSIZE == 0) {
                Chunk chunk = new Chunk(chunkNmb);
                chunksArray.add(chunk);
            }
        }
        return chunksArray;

    }

    public void mergeFile(List<File> files, File into) throws IOException{
        try (FileOutputStream fos = new FileOutputStream(into); BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
            for (File f : files) {
                Files.copy(f.toPath(), mergingStream);
             }   
        }
    }

    public String generateFileID(){

        File f = new File(pathname);
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