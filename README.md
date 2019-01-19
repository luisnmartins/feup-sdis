# Project 1

The aim of this project was to create a distributed backup system using RMI and UDP. Whenever a file is backed up, it is split into chunks that are copied to other computers in the system according to the desired replication degree (minimum 1). It is also possible to restore and delete a backed up file as well reclaim space in the system

## How to compile?
 
 Open a terminal and run the following command:

     sh compile.sh


## Start RMI

 Open a terminal and run the following command:
    
    rmiregistry

## RUN

### Add a new Peer to the system

    Peer.Peer <version> <server id> <access_point> <MC_port> <MC_IP_address> <MDB_port> <MDB_IP_address> <MDR_port> <MDR_IP_address>

        Eg.: Peer.Peer 1.0 1 1  8000 224.0.0.1 8001 224.0.0.2 8002 224.0.0.3

### Backup a file

	RMI.Application //<host>/<peer_access_point> BACKUP <file_path> <desired_replication_degree>

		Eg.: RMI.Application //localhost/1 BACKUP /usr/users2/2015/Desktop/file.txt 3

### Restore

	RMI.Application //<host>/<peer_access_point> RESTORE <file_path>

		Eg.: Application //localhost/1 RESTORE /usr/users2/2015/Desktop/file.txt 

### Delete

	RMI.Application //<host>/<peer_access_point> DELETE <file_path>

		Eg.: RMI.Application //localhost/1 DELETE /usr/users2/2015/Desktop/file.txt

### Reclaim Protocol

This protocol allows the user to reclaim space in the system deleting some replicated chunks. It tries to minimize the number of chunks deleted as well as the number of documents without the desired replication degree

	RMI.Application //<host>/<peer_access_point> RECLAIM <max_disk_space>

		Eg.: RMI.Application //localhost/1 RECLAIM 30000

### State

	RMI.Application //<host>/<peer_access_point> STATE

		Eg.: RMI.Application //localhost/1 STATE


### Enhancements

  For each one of these protocols there's a enhanced version that makes the system more reliable

  #### Backup enhancement
  
  In this enhancement the system tries to SOMETHING

     Eg.:  RMI.Application localhost/1 ENHBACKUP /usr/users2/2015/Desktop/file.txt 3
  
  ##### Restore  enhancement
  
  In this enhancement the system tries to ask for all the chunks of the file to restore in a more reliable way. We're using an UDP connection (non reliable connection) and each file can have a lot of chunks so when a computer is asking for these chunks a lot of messages can be lost. In order to prevent this, in the enhanced version the computer send only some messages to the system at a time preventing for a big lost of messages. Each time a message is sent the computer defines a timeout. If it not receive a response for that message before a timeout it retries to send the message.

    Eg.:  RMI.Application localhost/1 ENHRESTORE /usr/users2/2015/Desktop/file.txt

  #### Delete enhancement
  
  Since it is a distributed system it relies on many different computers that are not always available on the network. In this enhanced version of the delete protocol each time the system starts it checks if a deleted file still have any chunk backed up in any computer that in anytime before had been connected to the network and resend the delete message in order to make it be deleted

    Eg.:  RMI.Application localhost/1 ENHDELETE /usr/users2/2015/Desktop/file.txt


# Project 2

Following Project 1 the aim of this project was to create a similar system but this time it is more like a shared file system. Based on torrent idea the system relies on a tracker which keep track of each computer on the network as well as the files each one is sharing with the network. So each time a new user (computer) enters the network he should inform the tracker of his presence and the choose which files he will seed. Each time a user starts seeding a file the system will automatically create a XML file so that the user can share it (pretty much how torrent system works)

In order to test it you just have to run the following commands on the App folder of the Project 2

To initialize the system you should set an instance of the tracker running on a "server" (computer that is always available on the network)

```
java -jar  Tracker.jar
```

Each time a peer want to join the system it just have to run the following command and register himself on the network and start seeding or downloading a file

```
java -jar Peer.jar
```

Note: In order to make the communications safer the system uses SSLSockets to communicate with symmetric keys encryption. So each time a Peer wants to communicate with the Tracker it should be able to use the tracker's public key. This key should be found on App/Peer folder. However, if the system is compromised this key should be regenerated as well as the corresponding private key. In order to make this easier there's a  script in DistributedSystem folder to regenerate both these keys called generatekeys.sh



