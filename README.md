# How to compile?
 
* sh compile.sh

# Start RMI

* rmiregistry

# RUN

## Add a new Peer

      	Peer.Peer <version> <server id> <access_point> <MC_port> <MC_IP_address> <MDB_port> <MDB_IP_address> <MDR_port> <MDR_IP_address>

         	Eg.: Peer.Peer 1.0 1 1  8000 224.0.0.1 8001 224.0.0.2 8002 224.0.0.3

## Backup

	RMI.Application //<host>/<peer_access_point> BACKUP <file_path> <desired_replication_degree>

		Eg.: RMI.Application //localhost/1 BACKUP /usr/users2/2015/Desktop/file.txt 3

## Restore

	RMI.Application //<host>/<peer_access_point> RESTORE <file_path>

		Eg.: Application //localhost/1 RESTORE /usr/users2/2015/Desktop/file.txt 

## Delete

	RMI.Application //<host>/<peer_access_point> DELETE <file_path>

		Eg.: RMI.Application //localhost/1 DELETE /usr/users2/2015/Desktop/file.txt

## Reclaim Protocol

	RMI.Application //<host>/<peer_access_point> RECLAIM <max_disk_space>

		Eg.: RMI.Application //localhost/1 RECLAIM 30000

## State

	RMI.Application //<host>/<peer_access_point> STATE

		Eg.: RMI.Application //localhost/1 STATE

  #### Os protocolos Backup, Restore, Delete podem ser chamados com ENH para que seja usada a versão enhanced

    Eg.:  RMI.Application localhost/1 ENHBACKUP /usr/users2/2015/Desktop/file.txt 3

	Application localhost/1 ENHRESTORE /usr/users2/2015/Desktop/file.txt 

	RMI.Application localhost/1 ENHDELETE /usr/users2/2015/Desktop/file.txt

Powered by Redmine © 2006-2012 Jean-Philippe Lang
