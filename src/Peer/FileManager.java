package Peer;

import Chunk.ChunkData;
import Peer.Peer;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
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

    /*public List<ChunkData> splitFile() throws IOException{

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

    }*/


    /**
     * Merges chunk files into one
     */
    /*public void mergeFile(File[] files) throws IOException{

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
    }*/

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

    /**
     * Retrieves and deletes all the file data from the file specified
     * @param pathname name of the file to delete
     * @return return deleted data or null if where's nothing to delete
     */
    public byte[] deleteFile(String pathname){
        Path path = Paths.get(pathname);
        File fileParent = new File(pathname).getParentFile();
        try {
            byte[] return_data = Files.readAllBytes(path);
            Files.delete(path);
            if(fileParent.isDirectory() && fileParent.list().length  ==  0)
                Files.delete(fileParent.toPath());
            return return_data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
        
    }

    /**
     * Retrieves the data bytes of the file specified
     */
    public byte[] getFileData(String pathname){
        try {
            return Files.readAllBytes(new File(pathname).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void writeToFileAsync(byte[] data,long offset) throws IOException{

        Path path = Paths.get(pathname);
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path,StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.APPEND,StandardOpenOption.SYNC);

        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();

        fileChannel.write(buffer, offset, buffer, new CompletionHandler<Integer,ByteBuffer>() {

            @Override
            public void completed(Integer result,ByteBuffer attachment){

            }

            @Override
            public void failed(Throwable exc,ByteBuffer attachment){

            }

        });


    }

    public boolean createFileDir(String folderName){
        File dir = new File(folderName);
        return dir.mkdir();
    }


    public byte[] readFileAsync(long offset,int dataSize) throws IOException{
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(pathname),StandardOpenOption.READ,StandardOpenOption.READ);
        
        ByteBuffer buffer = ByteBuffer.allocate(dataSize);

        Future<Integer> operation = channel.read(buffer,offset);

        try {
            operation.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
        
        byte[] returnChunk = buffer.array();

        buffer.clear();

        return returnChunk;


    }

    
    

}