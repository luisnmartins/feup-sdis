package Peer;

import Peer.Peer;

import java.io.*;

public class LogsManager {


    public LogsManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> TerminatePeer()));

    }

    public void TerminatePeer() {
        SaveData();
    }


    public boolean SaveData() {


        String pathname = "Peer "+Peer.getPeerID()+"/data.out";

        File file = new File(pathname);
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Serialization
        try
        {
            //Saving of object in a file
            FileOutputStream fileStream = new FileOutputStream(pathname);
            OutputStream buffer = new BufferedOutputStream(fileStream);
            ObjectOutputStream out = new ObjectOutputStream(buffer);

            // Method for serialization of object
            out.writeObject(Peer.getStateManager());
            out.close();
            buffer.close();
            fileStream.close();

            System.out.println("Data has been save");
            return true;

        }

        catch(IOException ex)
        {
            System.err.println("Error saving data");
            return false;
        }


    }


    public StatusManager LoadData() {

        StatusManager state;

        String pathname = "Peer "+Peer.getPeerID()+"/data.out";

        // Deserialization
        try(
                // Reading the object from a file
                FileInputStream file = new FileInputStream(pathname);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInputStream in = new ObjectInputStream(buffer)
        )
        {

            // Method for deserialization of object
            state = (StatusManager) in.readObject();


            in.close();
            file.close();
            if(state.getBackedUpFiles() == null) {
            
            }
            System.out.println("Data has been loaded");
        }

        catch(IOException ex)
        {
            System.out.println("Error loading Status Manager or there's not any data");
            state = new StatusManager();
        }

        catch(ClassNotFoundException ex)
        {
            System.err.println("Error loading Status Manager object");
            state = null;
        }

        return state;
    }

}
