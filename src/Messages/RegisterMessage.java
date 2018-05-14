package Messages;


import Tracker.*;

public class RegisterMessage{

    private String CRLFCRLF = "\r\n\r\n";

    private String senderId;
    private String address;
    private String receiverPort;

    public RegisterMessage(String header) {

        String[] headerWords = header.split(" ");
        this.senderId = headerWords[1];
        this.address = headerWords[2];
        this.receiverPort = headerWords[3];
    }

    public RegisterMessage(String senderId, String address, String receiverPort) {

        this.senderId = senderId;
        this.address = address;
        this.receiverPort = receiverPort;

    }

    public byte[] getFullMessage() {
        String header = "REGISTER " + this.senderId + " " + this.address + this.receiverPort + this.CRLFCRLF;
        byte[] headerBytes = header.getBytes();
        return headerBytes;

    }

    public void action() {

        Tracker.addOnlinePeer(this.senderId, this.address, this.receiverPort);

        
    }
}