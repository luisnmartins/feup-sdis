package Peer;

import Messages.*;
import Peer.Peer;

import static Peer.MessageHandler.Transition.*;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocket;


import java.util.AbstractMap.SimpleEntry;

/**
 * MessageHandler implements Runnable
 */
public class MessageHandler implements Runnable {

    private static final byte CR = 0xD;
    private static final byte LF = 0xA;
    private static final int MAX_SIZE = 65435;

    private SSLSocket connectedSocket;
    private DataInputStream reader;
    private DataOutputStream writer;
    private String header;
    private byte[] body;
    private State fsmState = State.START;

    private volatile boolean running;

    public static enum Transition {
        SENDER, RECEIVER, WROTE, READ, QUIT;
    }

    public static enum State {
        START {
            @Override
            public State next(Transition transition) {
                if(transition == SENDER){
                    return WRITE;
                }else if(transition == RECEIVER){
                    return RECEIVE;

                }
                return START;
            }
        },
        WRITE {
            @Override
            public State next(Transition transition) {
                if (transition == WROTE) {
                    return RECEIVE;
                }
                return WRITE;
            }
        },
        RECEIVE {
            @Override
            public State next(Transition transition) {
                if (transition == READ) {
                    return WRITE;
                } else if (transition == QUIT) {
                    return CLOSE;
                }
                return RECEIVE;
            }
        },
        CLOSE {
            @Override
            public State next(Transition transition) {
                return CLOSE;
            }
        };

        public State next(Transition transition) {
            return null;
        }

    }

    public void updateState(Transition transition){
        fsmState = fsmState.next(transition);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            switch (fsmState) {
            case RECEIVE:
                try {
                    checkMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case CLOSE:
             try {
                    this.connectedSocket.close();         
                    
                } catch (IOException e) {
                  e.printStackTrace();
               }
                running = false;
                break;
            default:
                break;
            }
        }

    }
    
    public MessageHandler(SSLSocket socket) throws IOException{
        this.connectedSocket = socket;

        InputStream in = this.connectedSocket.getInputStream();
        OutputStream out = this.connectedSocket.getOutputStream();

        reader = new DataInputStream(in);
        writer = new DataOutputStream(out);

    }

    public DataOutputStream getWriter() {
        return writer;
    }

    public synchronized void  sendMessage(Message msg) {
        if (writer != null && fsmState == State.WRITE){
            byte[] textMessage = msg.getFullMessage();
            try {             
                writer.write(textMessage);             
                fsmState = fsmState.next(WROTE); 
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.err.println("Error: cant send message if connection hasnt been established or you  are not the one to send the message");
        }

    }

    public void separateMessage(int size, byte[] data) {
        int i = 0;
        for (; i < size; i++) {
            if (i <= size - 5) {
                if (data[i] == CR && data[i + 1] == LF && data[i + 2] == CR && data[i + 3] == LF) {
                    break;
                }
            }
        }
        byte[] headerByte = new byte[i];
        System.arraycopy(data, 0, headerByte, 0, i - 1);
        this.header = new String(headerByte);
        this.header = this.header.trim();

        if (size > i + 3) {
            this.body = new byte[size - i - 4];
            System.arraycopy(data, i + 4, this.body, 0, size - i - 4);

        } else {
            this.body = null;
        }

        System.out.println("Received: " + this.header);
    }

    public void checkMessage() throws IOException{
        byte[] buffer = new byte[MAX_SIZE];
        int readsize = 0;
        readsize = reader.read(buffer);
        byte[] response = Arrays.copyOfRange(buffer, 0, readsize);

        this.separateMessage(response.length, response);
        String messageType = this.header.substring(0,this.header.indexOf(" "));
        System.out.println("MESSAGETYPE: " + messageType);
        switch (messageType) {
            case "SUCCESS": {
                //TODO: Success Action
                break;
            }
            case "ERROR": {
                fsmState=fsmState.next(QUIT); 
                break;
            }
            case "CLOSE": {                        
                fsmState=fsmState.next(QUIT);              
                break;
            }
            //TRACKER
            case "REGISTER": {
                RegisterMessage register = new RegisterMessage(header,body);
                register.action(writer);
                running=false;
                break;
            }
            case "ONLINE": {
                OnlineMessage online = new OnlineMessage(header);
                online.action();
                break;
            }
            case "HASFILE": {                      
                HasFileMessage hasfile = new HasFileMessage(header);
                hasfile.action(writer);
                running=false;
                break;
            }
            case "NOFILE": {                      
                NoFileMessage nofile = new NoFileMessage(header);
                nofile.action();
                break;
            }
            case "GETFILE": {                      
                GetFileMessage getfile = new GetFileMessage(header);
                int res = getfile.action(writer);
                running=false;
                break;
            }
            //PEER
            case "PEERINFO": {                      
                PeerInfoMessage peerinfo = new PeerInfoMessage(header, body);
                peerinfo.action();
                break;
            }
            default:
                break;
        }

    }

}