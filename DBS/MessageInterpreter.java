public class MessageInterpreter implements Runnable {

    private String message;

    MessageInterpreter(String msg){
        this.message = msg;
    }

    @Override
    public void run() {
        String type = this.message.trim();
        type = type.substring(0,type.indexOf(" "));
        Message receivedMessage;
        switch (type){
            case "PUTCHUNK": {
                receivedMessage = new PutChunkMessage(this.message);
                receivedMessage.action();
                break;
            }
        }

    }


}
