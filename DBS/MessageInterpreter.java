public class MessageInterpreter implements Runnable {

    private String message;

    MessageInterpreter(String msg){
        this.message = msg;
    }

    @Override
    public void run() {
        String type = this.message.trim();
        type = type.substring(0,type.indexOf(" "));
        switch (type){
            case "BACKUP": {
                break;
            }
        }

    }


}
