
import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FileManager{

    private static final int CHUNKSSIZE = 64000;

    private String pathname;
    private List<ChunkData> chunks;

    public FileManager(String pathname) {
        this.pathname = pathname;
        this.chunks = new ArrayList<>();
    }

    public FileManager() {
        this.chunks = new ArrayList<>();
    }

    public List<ChunkData> splitFile() throws IOException{

        byte[] buffer = new byte[CHUNKSSIZE];
        List<ChunkData> chunksArray = new ArrayList<ChunkData>();


        //try-with-resources to ensure closing stream
        try (
                FileInputStream fis = new FileInputStream(pathname);
                BufferedInputStream bis = new BufferedInputStream(fis)
        )
        {

            int chunkNmb = 0;
            int size;
            while ((size = bis.read(buffer)) > 0) {

                ChunkData chunk = new ChunkData(chunkNmb);
                chunk.setData(size, buffer);
                chunkNmb += 1;
                chunksArray.add(chunk);
                buffer = new byte[CHUNKSSIZE];
            }

            File file = new File(pathname);
            if(file.length()%CHUNKSSIZE == 0) {
                ChunkData chunk = new ChunkData(chunkNmb);
                chunksArray.add(chunk);
            }
        }
        return chunksArray;

    }


    public void mergeChunks(String fileId) throws IOException {

        //File dir = new File("Peer "+ Peer.getPeerID());
        File dir = new File("Peer "+Peer.getPeerID());
        File[] foundFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(fileId);
            }
        });
        // Sort files by name
        Arrays.sort(foundFiles, new Comparator()
        {
            @Override
            public int compare(Object o1, Object o2){
                File f1 = (File) o1;
                File f2 = (File) o2;
                Integer f1N = Integer.parseInt(f1.getName().substring(65, f1.getName().length()));
                Integer f2N = Integer.parseInt(f2.getName().substring(65, f2.getName().length()));
                return f1N.compareTo(f2N);
            }
        });


        for (int i=0; i<foundFiles.length; i++) {
            System.out.println("FILENAME: "+foundFiles[i].getName());
        }
        mergeFile(foundFiles);

    }

    public void mergeFile(File[] files) throws IOException{

        File file = new File(pathname);
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        try (FileOutputStream fos = new FileOutputStream(file); BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
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

    public void saveChunk(ChunkData info) throws IOException {

        File file = new File(pathname);
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        Path path = file.toPath();
        ByteBuffer buffer = ByteBuffer.wrap(info.getData());


        AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

        CompletionHandler handler = new CompletionHandler<Integer, Object>() {

            @Override
            public void completed(Integer result, Object attachment) {

                System.out.println(attachment + " completed and " + result + " bytes were written.");
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable e, Object attachment) {

                System.out.println(attachment + " failed with exception:");
                try {
                    channel.close();
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
                e.printStackTrace();
            }
        };

        channel.write(buffer,0, "Chunk saving", handler);
    }

    public byte[] readEntireFileData() throws IOException{

        Path path = Paths.get(pathname);

        AsynchronousFileChannel channel =  AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        ByteBuffer buffer = ByteBuffer.allocate(CHUNKSSIZE);
        long position = 0;

        Future<Integer> operation = channel.read(buffer, position);

        while(!operation.isDone());

        buffer.flip();
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        buffer.clear();

        return data;
    }

    public byte[] deleteFile(String pathname){
        Path path = Paths.get(pathname);
        
        try {
            byte[] return_data = Files.readAllBytes(new File(pathname).toPath());
            Files.delete(path);
            return return_data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

        
    }

    
    

}