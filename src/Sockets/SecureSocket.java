package Sockets;

import javax.net.ssl.*;
import java.io.*;
import java.util.*;
import java.security.*;
import Messages.Message;

import Peer.Peer;

/**
 * SecureSocket
 */
public abstract class SecureSocket implements Runnable{

    protected int port;
    protected SSLSocket socket;

    protected static final int PACKET_SIZE = 65536;

    protected DataInputStream din;
    protected DataOutputStream dout;


    protected KeyStore socketKeyStore;
    protected KeyStore publicKeyStore;

    protected SSLContext sslContext;


    static protected String passphrase;
    
    /**
     * A source of secure random numbers
    */
    static protected SecureRandom secureRandom;


    SecureSocket(){createSecureRandom();setPassphrase(Peer.getPeerID());}

    public SSLSocket getSocket(){
        return socket;
    }

    public void setPassphrase(String peerName){
        this.passphrase = peerName + "pw";
    }

    public boolean generatePublicKey(String peerName){
        setPassphrase(peerName);
        System.out.println("Generating " + peerName + " public and private keys");
        String commandtoCreate = "keytool -genkey -alias " + peerName + "private -keystore " + peerName + ".private -storetype JKS -keyalg rsa -dname 'CN=Your Name, OU=Your Organizational Unit, O=Your Organization, L=Your City, S=Your State, C=Your Country' -storepass " + peerName + "pw -keypass "+ peerName + "pw";
        String commandtoExportPublic = "keytool -export -alias " + peerName + "private -keystore " + peerName + ".private -file temp.key -storepass " + peerName + "pw";
        String commandtoImportPublic = "keytool -import -noprompt -alias " +peerName + "public -keystore " + peerName + ".public -file temp.key -storepass public";

        try{
            String[] args = {"/bin/bash","-c",commandtoCreate + ";" + commandtoExportPublic + ";" + commandtoImportPublic + ";rm -f temp.key"};
            Process proc = new ProcessBuilder(args).start();
            proc.waitFor();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Keys generated succesfully");
        return true;
    }

    public void setupSocketKeyStore(String peerName) throws GeneralSecurityException, IOException{
        this.socketKeyStore = KeyStore.getInstance("JKS");
        this.socketKeyStore.load(new FileInputStream(peerName+".private"),
                                    this.passphrase.toCharArray());
    }

    public void setupPublicKeyStore(String peerName) throws GeneralSecurityException, IOException{
        this.publicKeyStore = KeyStore.getInstance("JKS");
        String publicpw = "public";
        this.publicKeyStore.load(new FileInputStream(peerName + ".public"),publicpw.toCharArray());
    }

    public void setupSSLContext() throws GeneralSecurityException, IOException{
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(publicKeyStore);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(socketKeyStore,passphrase.toCharArray());

        this.sslContext = SSLContext.getInstance("TLS");
        this.sslContext.init(kmf.getKeyManagers(),
                                tmf.getTrustManagers(),
                                secureRandom);
    }

    public void run(){

    }


    public void createSecureRandom(){
        System.out.println( "Wait while secure random numbers are initialized...." );
        secureRandom = new SecureRandom();
        secureRandom.nextInt();
        System.out.println( "Done." );
    }


    public void SendMessage(Message msg){
        if(dout != null){
            byte[] textMessage = msg.getFullMessage();
            try{
                 dout.write(textMessage);
            }catch(IOException e){
                e.printStackTrace();
            }
           
        }else{
            System.err.println("Error: cant send message if connection hasnt been established");
        }
    }


}