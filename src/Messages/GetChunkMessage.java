package Messages;


import Tracker.*;
import java.util.*;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class GetChunkMessage extends Message{

    private String CRLFCRLF = "\r\n\r\n";

    private String fileId;
    private int chunkNro;    

    public GetChunkMessage(String header){

        super();
        String[] headerWords = header.split(" ");
        this.fileId = headerWords[1];        
        this.chunkNro = Integer.parseInt(headerWords[2]);
    }

    public GetChunkMessage(String fileId, int chunkNro) {

        super();
        this.fileId = fileId;
        this.chunkNro = chunkNro;
    }


    public byte[] getFullMessage() {
        String header = "GETCHUNK " + this.fileId + " " + this.chunkNro + " " + this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        return headerBytes;

    }

    public int action(DataOutputStream writer) {

        
        return 0;
        
    }
}