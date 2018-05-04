#!/bin/bash 
mkdir bin
javac -d bin Chunk/*.java Messages/*.java Peer/*.java RMI/*.java Sockets/*.java Workers/*.java
