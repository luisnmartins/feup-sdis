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
    private static State fsmState = State.START;

    public static enum Transition {
        SENDER, RECEIVER, WROTE, READ, QUIT;
    }

    public static enum State {
        START {
            @Override
            public State next(Transition transition) {
                return (transition == SENDER) ? WRITE : RECEIVE;
            }
        },
        WRITE {
            @Override
            public State next(Transition transition) {
                if (transition == WROTE) {
                    return RECEIVE;
                } else
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
                } else
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

    public MessageHandler(SSLSocket socket) throws IOException{
        this.connectedSocket = socket;

        InputStream in = this.connectedSocket.getInputStream();
        OutputStream out = this.connectedSocket.getOutputStream();

        reader = new DataInputStream(in);
        writer = new DataOutputStream(out);

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

    public void sendMessage(Message msg) {
        if (writer != null && fsmState == State.WRITE){
            byte[] textMessage = msg.getFullMessage();
            try {
                writer.write(textMessage);
                fsmState.next(WROTE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.err.println("Error: cant send message if connection hasnt been established or you  are not the one to send the message");
        }

    }

    @Override
    public void run() {

        boolean running = true;
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

    public void checkMessage() throws IOException{
        byte[] buffer = new byte[MAX_SIZE];
        byte[] response = null;
        boolean didRead = false;
        int readsize = 0;
        while ((readsize = reader.read(buffer)) > 0) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            didRead = true;
            byte[] messagePart = Arrays.copyOfRange(buffer, 0, readsize);
            outputStream.write(response);
            outputStream.write(messagePart);
            response = outputStream.toByteArray();
            buffer = new byte[MAX_SIZE];

        }

        if (didRead) {
            fsmState.next(READ);
            this.separateMessage(response.length, response);
            String messageType = this.header.substring(0,this.header.indexOf(" "));
            switch (messageType) {
                case "SUCCESS": {
                    //TODO: Success Action
                    break;
                }
                case "ERROR": {
                    //TODO: Success Action
                    break;
                }
                //TRACKER
                case "REGISTER": {
                    RegisterMessage register = new RegisterMessage(header, "444.55.66");
                    register.action();
                    break;
                }
                case "HASFILE": {                      
                    HasFileMessage hasfile = new HasFileMessage(header, "444.55.66");
                    hasfile.action();
                    break;
                }
                case "GETFILE": {                      
                    GetFileMessage getfile = new GetFileMessage(header, "444.55.66");
                    getfile.action();
                    break;
                }
                //PEER
                case "PEERINFOSIZE": {                      
                    PeerInfoSizeMessage peerinfosize = new PeerInfoSizeMessage(header);
                    peerinfosize.action();
                    break;
                }
                case "PEERINFO": {                      
                    PeerInfoMessage peerinfo = new PeerInfoMessage(header);
                    peerinfo.action();
                    break;
                }
                default:
                    break;
            }
            
        }

    }

    public void updateState(Transition transition){
        fsmState.next(transition);
    }
}