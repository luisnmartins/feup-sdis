package Sockets;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.net.UnknownHostException;
import java.nio.charset.*;
import java.lang.ProcessBuilder;
import java.security.cert.X509Certificate;
import Peer.Peer;
/**
 * DRSocket
 */
public class SenderSocket extends SecureSocket implements Runnable{

    protected String host;
    protected InetAddress address;
    protected TrustManager[] trustAllCerts;
    protected SSLSocket socket;

    public SenderSocket(int port,String host)throws UnknownHostException{
        super();
        this.port = port;
        this.host = host;
        this.address = InetAddress.getByName(this.host);
        trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };
   

    }


    public void connect(String connectFrom,String connectTo){
        try{
            setupSocketKeyStore(connectFrom);
            setupPublicKeyStore(connectTo);
            setupSSLContext();

            SSLSocketFactory sf = sslContext.getSocketFactory();
            this.socket = (SSLSocket) sf.createSocket(this.host,this.port);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            din = new DataInputStream(in);
            dout = new DataOutputStream(out);

        }catch(GeneralSecurityException gse){
            gse.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /*public void setupSSLContext() throws GeneralSecurityException, IOException{
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(publicKeyStore);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(socketKeyStore,passphrase.toCharArray());

        this.sslContext = SSLContext.getInstance("TLS");
        this.sslContext.init(kmf.getKeyManagers(),
                                trustAllCerts,
                                secureRandom);
    }*/

    public void run(){
        while(true){
            
        }
    }

      

}