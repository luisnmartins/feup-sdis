package Messages;

public class Message {

        public Message(){}

        public byte[] getFullMessage(){ String s = "Ola" ; return s.getBytes();}

        public int action(){return 0;};
}