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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileManager {

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

    /*
     * public List<ChunkData> splitFile() throws IOException{
     * 
     * byte[] buffer = new byte[CHUNKSSIZE]; List<ChunkData> chunksArray = new
     * ArrayList<ChunkData>();
     * 
     * 
     * //try-with-resources to ensure closing stream try ( FileInputStream fis = new
     * FileInputStream(pathname); BufferedInputStream bis = new
     * BufferedInputStream(fis) ) {
     * 
     * int chunkNmb = 0; int size; while ((size = bis.read(buffer)) > 0) {
     * 
     * ChunkData chunk = new ChunkData(chunkNmb); chunk.setData(size, buffer);
     * chunkNmb += 1; chunksArray.add(chunk); buffer = new byte[CHUNKSSIZE]; }
     * 
     * File file = new File(pathname); if(file.length()%CHUNKSSIZE == 0) { ChunkData
     * chunk = new ChunkData(chunkNmb); chunksArray.add(chunk); } } return
     * chunksArray;
     * 
     * }
     */

    /**
     * Merges chunk files into one
     */
    /*
     * public void mergeFile(File[] files) throws IOException{
     * 
     * File file = new File(pathname); if(!file.exists()) {
     * file.getParentFile().mkdirs(); file.createNewFile(); }
     * 
     * try (FileOutputStream fos = new FileOutputStream(file); BufferedOutputStream
     * mergingStream = new BufferedOutputStream(fos)) { for (File f : files) {
     * Files.copy(f.toPath(), mergingStream); } } }
     */
    /**
     * Randomly generates an unique file id
     */
    public String generateFileID() {

        File f = new File(pathname);
        Path path = Paths.get(pathname);
        if (!f.exists() || f.isDirectory())
            return null;
        String bitString = null;
        try {
            bitString = f.getName() + Long.toString(f.lastModified()) + Boolean.toString(f.canWrite())
                    + Boolean.toString(f.canRead()) + f.getPath() + Files.getOwner(path).getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sha256(bitString);
    }

    /**
     * Encrypts a String with sha256 without "prohibited characters"
     */
    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public byte[] readEntireFileData() throws IOException {

        Path path = Paths.get(pathname);

        AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        ByteBuffer buffer = ByteBuffer.allocate(CHUNKSSIZE);
        long position = 0;

        Future<Integer> operation = channel.read(buffer, position);

        while (!operation.isDone())
            ;

        buffer.flip();
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        buffer.clear();

        return data;

    }

    /**
     * Retrieves and deletes all the file data from the file specified
     * 
     * @param pathname name of the file to delete
     * @return return deleted data or null if where's nothing to delete
     */
    public byte[] deleteFile(String pathname) {
        Path path = Paths.get(pathname);
        File fileParent = new File(pathname).getParentFile();
        try {
            byte[] return_data = Files.readAllBytes(path);
            Files.delete(path);
            if (fileParent.isDirectory() && fileParent.list().length == 0)
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
    public byte[] getFileData(String pathname) {
        try {
            return Files.readAllBytes(new File(pathname).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeToFileAsync(byte[] data, long offset) throws IOException {

        Path path = Paths.get(pathname);
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC);

        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();

        fileChannel.write(buffer, offset, buffer, new CompletionHandler<Integer, ByteBuffer>() {

            @Override
            public void completed(Integer result, ByteBuffer attachment) {

            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

            }

        });

    }

    public boolean createFileDir(String folderName) {
        File dir = new File(folderName);
        return dir.mkdir();
    }

    public byte[] readFileAsync(long offset, int dataSize) throws IOException {
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(pathname), StandardOpenOption.READ,
                StandardOpenOption.READ);

        ByteBuffer buffer = ByteBuffer.allocate(dataSize);

        Future<Integer> operation = channel.read(buffer, offset);

        try {
            operation.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        byte[] returnChunk = buffer.array();

        buffer.clear();

        return returnChunk;

    }

    public boolean createDownloadFile(int chunkSize, int port, String address) {

        File file = new File(pathname);

        if (!file.exists()) {
            System.err.println("Can't create download file from non existing files");
            return false;
        }

        String fileName = file.getName();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("torrent");
            doc.appendChild(rootElement);

            Element trackerElement = doc.createElement("tracker");
            rootElement.appendChild(trackerElement);

            // setting attributes to tracker
            Attr ipAttr = doc.createAttribute("IP");
            ipAttr.setValue(address);
            trackerElement.setAttributeNode(ipAttr);

            Attr portAttr = doc.createAttribute("Port");
            portAttr.setValue(Integer.toString(port));
            trackerElement.setAttributeNode(portAttr);

            Element chunkElement = doc.createElement("Chunk");
            rootElement.appendChild(chunkElement);

            Attr sizeAttr = doc.createAttribute("length");
            sizeAttr.setValue(Integer.toString(chunkSize));
            chunkElement.setAttributeNode(sizeAttr);

            Element fileElement = doc.createElement("File");
            rootElement.appendChild(fileElement);

            Attr fileID = doc.createAttribute("ID");
            String id = this.generateFileID();
            fileID.setValue(id);
            fileElement.setAttributeNode(fileID);

            Attr fileNameAttr = doc.createAttribute("name");
            fileNameAttr.setValue(fileName);
            fileElement.setAttributeNode(fileNameAttr);

            Attr fileLengthAttr = doc.createAttribute("length");
            fileLengthAttr.setValue(Long.toString(file.length()));
            fileElement.setAttributeNode(fileLengthAttr);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName + ".xml"));
            transformer.transform(source, result);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }

    public boolean parseDownloadFile() {
        File toParse = new File(this.pathname);

        if (!toParse.exists()) {
            System.err.println("File does not exist");
            return false;
        }

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(toParse);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("tracker");
            
            for (int i = 0; i < nList.getLength(); i++) {
                
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String ip = eElement.getAttribute("IP");
                    int port = Integer.parseInt(eElement.getAttribute("Port"));

                    System.out.println("Tracker IP: " + ip + " port: " + port);

                }
            }

            nList = doc.getElementsByTagName("Chunk");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    int length = Integer.parseInt(eElement.getAttribute("length"));

                    System.out.println("Chunk length: " + length);
                }
            }

            nList = doc.getElementsByTagName("File");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String fileID = eElement.getAttribute("ID");
                    long fileLength = Long.parseLong(eElement.getAttribute("length"));
                    String name = eElement.getAttribute("name");

                    System.out.println("File ID: " + fileID + " length: " + fileLength + " name: " + name);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }

}