package Messages;

import Peer.*;
import Sockets.SenderSocket;
import Tracker.*;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class ChunkMessage extends Message{

    private String CRLFCRLF = "\r\n\r\n";

    private String fileId;
    private int chunkNr; 
    private byte[] body;

    public ChunkMessage(String header, byte[] body){

        String[] headerWords = header.split(" ");
        this.fileId = headerWords[1];
        this.chunkNr = Integer.parseInt(headerWords[2]);
        this.body = body; 
               
    }

    public ChunkMessage(String fileId, int chunkNr, byte[] body){

        this.fileId = fileId;
        this.chunkNr = chunkNr;
        this.body = body;
    
    }

    public byte[] getFullMessage() {
        String header = "CHUNK " + this.fileId + " " + this.chunkNr + " " +this.CRLFCRLF;
        System.out.println("Sent: " + "CHUNK " + this.fileId + " " + this.chunkNr );        
        byte[] headerBytes = header.getBytes();
        byte[] finalByteArray = new byte[headerBytes.length+this.body.length];
        System.arraycopy( headerBytes, 0, finalByteArray, 0, headerBytes.length);
        System.arraycopy( body, 0, finalByteArray, headerBytes.length, body.length );        
        return finalByteArray;
    }

    public int action() {
      
        return 0;
    }
}